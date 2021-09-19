package org.mjsip.media;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;

import org.zoolu.net.IpAddress;
import org.zoolu.net.UdpKeepAlive;
import org.zoolu.net.UdpPacket;
import org.zoolu.net.UdpProvider;
import org.zoolu.net.UdpProviderListener;
import org.zoolu.net.UdpSocket;
import org.zoolu.util.LogLevel;
import org.zoolu.util.Logger;



/** Push-To-Talk (PTT) application. 
  */
public class PttStreamer implements MediaStreamer, UdpProviderListener {
	
	/** Maximum datagram size (MTU). Bigger datagrams are fragmented. */
	static final int MAX_PKT_SIZE=1012;

	/** Minimum datagram size. Smaller datagrams are silently discarded) */
	static final int MIN_PKT_SIZE=4;
	
	/** Time between two keepalive datagrams [millisecs] */
	static final long KEPPALIVE_TIME=30000;

	/** Log */
	Logger log=null;

	/** Recorder */
	Player recorder=null;
	
	/** Record controller */
	RecordControl rc=null;
	
	/** Audio type */
	String audio_type="audio/x-wav";

	/** Encoding */
	//String encoding="encoding=pcm&rate=8000&bits=16&channels=1";

	/** Buffer */
	ByteArrayOutputStream buffer;

	/** Whether it is recording */
	boolean is_recording=false;
	
	/** Player */
	Player player=null;

	/** Remote address */
	IpAddress remote_addr;
	
	/** Remote port */
	int remote_port;

	/** UDP socket */
	UdpSocket udp_socket=null;

	/** UDP layer */
	UdpProvider udp=null;

	/** UDP keepalive */
	UdpKeepAlive udp_keepalive=null;

	/** Last Push2TalkApp instance */
	static PttStreamer INSTANCE=null;



	/** Gets last Push2TalkApp instance. */
	public static PttStreamer getLastInstance() {
		return INSTANCE;
	}


	/** Creates a new PttStreamer. */
	public PttStreamer(FlowSpec flow_spec, Logger log) {
		this.log=log;
		int local_port=flow_spec.getLocalPort();
		remote_addr=new IpAddress(flow_spec.getRemoteAddress());
		remote_port=flow_spec.getRemotePort();
		try {
			udp=new UdpProvider(udp_socket=new UdpSocket(local_port),this);
			//udp=new UdpProvider(udp_socket=new JumboUdpSocket(local_port,MAX_PKT_SIZE),this);
		}
		catch(Exception e) {  e.printStackTrace();  printLog(e.toString());  }
		
		INSTANCE=this;
	}


	/** Starts media streamer. */
	public boolean start() {
		//udp_keepalive=new UdpKeepAlive(udp_socket,new SocketAddress(remote_addr,remote_port),KEPPALIVE_TIME);
		return true;     
	}


	/** Stops media streamer. */
	public boolean halt() {
		if (udp_keepalive!=null) udp_keepalive.halt();
		udp_keepalive=null;
		udp.halt();
		udp=null;
		udp_socket.close();
		is_running=false;
		return true;
	}


	/** Whether the PTT is running. */
	boolean is_running;


	/** Thread run method. */
	public void run() {
		is_running=true;
		while (is_running) {
			record();
			pause(500);
			send();
			pause(4000);
		}
		udp.halt();
		udp=null;
	}


	/** Waits for <i>millisecs</i>. */
	static void pause(long millisecs) {
		try {  Thread.sleep(millisecs);  } catch(Exception e) {}
	}




	/** Starts recording. */
	public void record() {
		if (!is_recording) {
			try {
				//recorder=Manager.createPlayer("capture://audio?"+encoding);
				recorder=Manager.createPlayer("capture://audio");
				recorder.realize();
				rc=(RecordControl)recorder.getControl("RecordControl");
				buffer=new ByteArrayOutputStream();
				rc.setRecordStream(buffer);
				printLog("record");
				rc.startRecord();
				recorder.start();
				is_recording=true;
			}
			catch (MediaException e) {  e.printStackTrace();  printLog(e.toString());  }
			catch (IOException e) {  e.printStackTrace();  printLog(e.toString());  }
		}
	}


	/** Stops recording and sends captured audio. */
	public void send() {
		if (is_recording) {
			try {
				rc.stopRecord();
				rc.commit();
				recorder.stop();
				recorder.close();
				is_recording=false;
				//play(buffer.toByteArray());
				send(buffer.toByteArray());
			}
			catch (MediaException e) {  e.printStackTrace();  printLog(e.toString());  }
			catch (IOException e) {  e.printStackTrace();  printLog(e.toString());  }   
		}
	}


	/** Sends the given audio buffer. */
	void send(byte[] buffer) {
		printLog("send ["+buffer.length+"B]");
		if (udp!=null)
		try  {
			UdpPacket packet=new UdpPacket(buffer,buffer.length,remote_addr,remote_port);
			udp.send(packet);
		}
		catch (IOException e) {  e.printStackTrace();  printLog(e.toString());  } 
		else printLog("WARNING: null socket found: impossible to send packet."); 
	}


	/** Plays the given audio buffer. */
	void play(byte[] buffer, int offset, int length) {
		if (length<MIN_PKT_SIZE) return;
		// else
		printLog("play ["+length+"B]");
		try {
			if (player!=null) {
				player.stop();
				player.close();
			}
			player=Manager.createPlayer(new ByteArrayInputStream(buffer,offset,length),audio_type);
			player.prefetch();
			player.start();
		}
		catch (MediaException e) {  e.printStackTrace();  printLog(e.toString());  }
		catch (IOException e) {  e.printStackTrace();  printLog(e.toString());  }   
	}


	// ************************ Callback methods ***********************

	/** From UdpProvider. When a new UDP datagram is received. */
	public void onReceivedPacket(UdpProvider udp, UdpPacket packet) {
		play(packet.getData(),packet.getOffset(),packet.getLength());
	}


	/** From UdpProvider. When UdpProvider terminates. */
	public void onServiceTerminated(UdpProvider udp, Exception error) {
		if (error!=null) printLog("UDP socket: "+error.toString());
	}


	// ****************************** Logs *****************************

	/** Adds a new string to the default Log */
	private void printLog(String str) {
		printLog(str,LogLevel.INFO);
	}


	/** Adds a new string to the default Log */
	private void printLog(String str, LogLevel level) {
		if (log!=null) log.log(level,this.getClass(),str);  
		else System.out.println("PttStreamer: "+str);
	}


}
