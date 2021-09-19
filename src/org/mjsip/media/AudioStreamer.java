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
import org.zoolu.util.Timer;
import org.zoolu.util.TimerListener;



/** Audio application based on MMAPI.
  */
public class AudioStreamer implements MediaStreamer, UdpProviderListener, TimerListener {
	
	/** Maximum datagram size (MTU). Bigger datagrams are fragmented. */
	static final int MAX_PKT_SIZE=1012;

	/** Minimum datagram size. Smaller datagrams are silently discarded) */
	static final int MIN_PKT_SIZE=4;
	
	/** Time between two keepalive datagrams [millisecs] */
	static final long KEPPALIVE_TIME=30000;
	
	/** Number of recording buffers */
	static final int LOOP=4;

	/** Frame time length */
	static final long FRAME_TIME=2000;


	/** Log */
	Logger log=null;

	/** Audio type */
	String audio_type="audio/x-wav";

	/** Encoding */
	//String encoding="encoding=pcm&rate=8000&bits=16&channels=1";

	/** Index of array recorders */
	int index=0;

	/** Array of recorders */
	Player[] recorders=new Player[LOOP];
	
	/** Array of record controls */
	RecordControl[] rec_controls=new RecordControl[LOOP];
	
	/** Array of buffers */
	ByteArrayOutputStream[] buffers=new ByteArrayOutputStream[LOOP];

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

	/** Whether the PTT is running. */
	boolean is_running;

	/** Last AudioApp instance */
	static AudioStreamer INSTANCE=null;



	/** Gets last AudioApp instance. */
	public static AudioStreamer getLastInstance() {
		return INSTANCE;
	}


	/** Creates a new AudioApp. */
	public AudioStreamer(FlowSpec flow_spec, Logger log) {
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


	/** Starts media application. */
	public boolean start() {
		//udp_keepalive=new UdpKeepAlive(udp_socket,new SocketAddress(remote_addr,remote_port),KEPPALIVE_TIME);
		is_running=true;
		startRecord(index);
		(new Timer(FRAME_TIME,this)).start();
		return true;
	}


	/** Stops media application. */
	public boolean halt() {
		if (udp_keepalive!=null) udp_keepalive.halt();
		udp_keepalive=null;
		udp.halt();
		udp=null;
		udp_socket.close();
		is_running=false;
		return true;
	}


	/** Waits for <i>millisecs</i>. */
	static void pause(long millisecs) {
		try {  Thread.sleep(millisecs);  } catch(Exception e) {}
	}


	/** Starts recording. */
	void startRecord(int index) {
		try {
			//recorders[index]=Manager.createPlayer("capture://audio?"+encoding);
			recorders[index]=Manager.createPlayer("capture://audio");
			recorders[index].realize();
			rec_controls[index]=(RecordControl)recorders[index].getControl("RecordControl");
			buffers[index]=new ByteArrayOutputStream();
			rec_controls[index].setRecordStream(buffers[index]);
			printLog("record");
			rec_controls[index].startRecord();
			recorders[index].start();
		}
		catch (MediaException e) {  e.printStackTrace();  printLog(e.toString());  }
		catch (IOException e) {  e.printStackTrace();  printLog(e.toString());  }
	}


	/** Stops recording. */
	void stopRecord(int index) {
		try {
			rec_controls[index].stopRecord();
			rec_controls[index].commit();
			recorders[index].stop();
			recorders[index].close();
		}
		catch (MediaException e) {  e.printStackTrace();  printLog(e.toString());  }
		catch (IOException e) {  e.printStackTrace();  printLog(e.toString());  }   
	}


	/** Sends captured audio. */
	void sendRecord(int index) {
		//play(buffers[index].toByteArray());
		 send(buffers[index].toByteArray());  
	}


	/** Sends captured audio. */
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


	/** From TimerListener. When the Timer exceeds. */
	/*public void onTimeout(Timer t) {
		stopRecord(index);
		if (is_running) {
			sendRecord(index);
			int next=(index+1)%LOOP;
			index=next;
			startRecord(next);
			(new Timer(FRAME_TIME,this)).start();
		}
	}*/


	/** Whether it is recording */
	boolean rec=true;

	/** From TimerListener. When the Timer exceeds. */
	public void onTimeout(Timer t) {
		if (rec) {
			stopRecord(index);
			if (is_running) {
				sendRecord(index);
				(new Timer(FRAME_TIME,this)).start();
			}
		}
		else {
			if (is_running) {
				index=(index+1)%LOOP;
				startRecord(index);
				(new Timer(FRAME_TIME,this)).start();
			}
		}
		rec=!rec;      
	}


	// ****************************** Logs *****************************

	/** Adds a new string to the default Log */
	private void printLog(String str) {
		printLog(str,LogLevel.SEVERE);
	}


	/** Adds a new string to the default Log */
	private void printLog(String str, LogLevel level) {
		if (log!=null) log.log(level,"AudioApp: "+str);  
	}

}
