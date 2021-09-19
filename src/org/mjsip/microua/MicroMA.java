package org.mjsip.microua;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import org.mjsip.sip.address.NameAddress;
import org.mjsip.sip.address.SipURI;
import org.mjsip.sip.header.StatusLine;
import org.mjsip.sip.message.SipMessage;
import org.mjsip.sip.message.SipMessageFactory;
import org.mjsip.sip.message.SipMethods;
import org.mjsip.sip.message.SipResponses;
import org.mjsip.sip.provider.MethodId;
import org.mjsip.sip.provider.SipProvider;
import org.mjsip.sip.provider.SipProviderListener;
import org.mjsip.sip.provider.SipStack;
import org.mjsip.sip.transaction.TransactionClient;
import org.mjsip.sip.transaction.TransactionClientListener;
import org.mjsip.sip.transaction.TransactionServer;
import org.zoolu.net.IpAddress;


/** Very simple Message Agent for sending and/or receiving text messages.
  */
public class MicroMA extends MIDlet implements /*Runnable,*/ CommandListener, SipProviderListener, TransactionClientListener {
	
	String local_user="user";  
	String host_addr=IpAddress.getLocalHostAddress().toString();
	int host_port=SipStack.default_port; // 5060
	
	Display display;
	Form f;
	TextField tf_port=new TextField("Local SIP port:",String.valueOf(host_port),30,TextField.ANY);
	TextField tf_proxy=new TextField("Outbound proxy:","",30,TextField.ANY);
	TextField tf_url=new TextField("Remote URL:","echo@mjsip.org",30,TextField.ANY);
	TextField tf_msg=new TextField("Send:","hello",30,TextField.ANY);
	StringItem si_dialog=new StringItem("Dialog:"," ");
	Command exitCommand=new Command("Exit", Command.EXIT, 1);
	Command sendCommand=new Command("Send", Command.ITEM, 1);
	Command initCommand=new Command("Start", Command.ITEM, 1);
	
	boolean isPaused=true;
	SipMessage message=null;
	SipProvider sip_provider=null;
	NameAddress local_url;


	public MicroMA() {
		display=Display.getDisplay(this);
		f=new Form("MicroMA");
		f.append(tf_port);
		f.append(tf_proxy);
		//f.append(tf_url);
		//f.append(tf_msg);
		//f.append(si_dialog);
		f.addCommand(exitCommand);
		f.addCommand(initCommand);
		f.setCommandListener(this);
		display.setCurrent(f);
		
		// start the local thread
		//Thread t=new Thread(this);
		//t.start();
	}


	public void startApp() {
		isPaused=false;
	}
	
	
	public void pauseApp() {
		isPaused=true;
	}
	
	
	public void destroyApp(boolean unconditional) {
		
	}


	public void commandAction(Command c, Displayable s) {
		if (c==exitCommand) {
			destroyApp(true);
			notifyDestroyed();
		}
		else
		if (c==initCommand && !isPaused) {
			try {
				host_port=Integer.parseInt(tf_port.getString());
				(new Thread() {  public void run() {  initSip();  } }).start();
			}
			catch (Exception e) {}
		}
		else
		if (c==sendCommand && !isPaused) {
			String text=tf_msg.getString();
			String target=tf_url.getString();
			if (text!=null && text.length()>0 && target!=null && target.length()>0) {
				send(new NameAddress(target),local_url,text);
				tf_msg.setString("");
				si_dialog.setText(si_dialog.getText()+"\nsent: "+text);
			}
		}
	}

	private void initSip() {
		String[] protocols={ SipProvider.PROTO_UDP };
		String outbound_proxy=tf_proxy.getString().trim();
		sip_provider=new SipProvider(host_addr,host_port,protocols);
		if (outbound_proxy!=null && outbound_proxy.length()>0) sip_provider.setOutboundProxy(new SipURI(outbound_proxy));
		sip_provider.addSelectiveListener(new MethodId(SipMethods.MESSAGE),this);
		//local_url=sip_provider.completeNameAddress(local_user);
		local_url=new NameAddress(local_user,new SipURI(local_user,sip_provider.getViaAddress(),sip_provider.getPort()));
		f.deleteAll();
		f.append(tf_url);
		f.append(tf_msg);
		f.append(si_dialog);
		f.removeCommand(initCommand);
		f.addCommand(sendCommand);
	}


	public void onReceivedMessage(SipProvider provider, SipMessage msg) {
		if (msg.isRequest() && msg.isMessage()) {
			tf_url.setString(msg.getFromHeader().getNameAddress().toString());
			si_dialog.setText(si_dialog.getText()+"\nreceived: "+msg.getStringBody());
			TransactionServer t=new TransactionServer(sip_provider,msg,null);
			t.respondWith(SipMessageFactory.createResponse(msg,200,SipResponses.reasonOf(200),null));
		}
	}


	public void send(NameAddress to, NameAddress from, String text) {
		//System.err.println("DEBUG: send message");
		message=SipMessageFactory.createMessageRequest(to,from,sip_provider.pickCallId(),null,"application/text",text.getBytes());
		TransactionClient t=new TransactionClient(sip_provider,message,this);
		t.request();
	}


	public void onTransProvisionalResponse(TransactionClient tc, SipMessage msg) {
		onTransResponse(msg.getStatusLine());
		
	}
	public void onTransSuccessResponse(TransactionClient tc, SipMessage msg) {
		onTransResponse(msg.getStatusLine());
		
	}
	public void onTransFailureResponse(TransactionClient tc, SipMessage msg) {
		onTransResponse(msg.getStatusLine());
	}
	public void onTransTimeout(TransactionClient tc) {
		onTransResponse(null);
	}
	
	private void onTransResponse(StatusLine resp) {
		String result;
		if (resp==null) result="Timout"; else result=resp.getCode()+" "+resp.getReason();
		si_dialog.setText(si_dialog.getText()+"\n"+result);
	}


/*
	public synchronized void send(NameAddress target, NameAddress from, String text) {
		System.err.println("DEBUG: send message (1)");
		message=MessageFactory.createMessageRequest(sip_provider,target,from,null,"application/text",text);
		notify();
	}


	public synchronized void run() {
		while (true) {
			
			// If no client to deal, wait until one connects
			if (message==null) {
				try {  wait();  } catch (InterruptedException e) {}
			}
			System.err.println("DEBUG: send message (2)");
			sip_provider.sendMessage(message);
			message=null;
		}
	}
*/
}
