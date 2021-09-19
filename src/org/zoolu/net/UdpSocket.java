/*
 * Copyright (C) 2006 Luca Veltri - University of Parma - Italy
 * 
 * This file is part of MjSip (http://www.mjsip.org)
 * 
 * MjSip is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * MjSip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MjSip; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package org.zoolu.net;


import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

import org.zoolu.util.Parser;


/** UdpSocket provides a uniform interface to UDP transport protocol,
  * regardless J2SE or J2ME is used.
  */
public class UdpSocket {
	
	/** Whether printing debugging information on standard error output. */
	public static boolean DEBUG=false;

	/** Maximum packet size */
	//static final int MAX_PKT_SIZE=8000;
	static final int MAX_PKT_SIZE=32000;

	/** DatagramSocket */
	UDPDatagramConnection socket;
	
	/** Receiving Datagram */
	Datagram recv_dgram;

	/** Sending Datagrams (one for each remote socket address) */
	Hashtable send_dgrams;

	/** Sender packet counter */
	long sender_packet_count=0;   

	/** Sender octect counter */
	long sender_octect_count=0;   

	/** Receiver packet counter */
	long receiver_packet_count=0;   

	/** Receiver octect counter */
	long receiver_octect_count=0;   


	// ************************** costructors **************************

	/** Creates a new void UdpSocket */ 
	protected UdpSocket() {
		socket=null;
		recv_dgram=null;
	}

	/** Creates a new UdpSocket */ 
	public UdpSocket(int port) {
		init(port,null);
	}

	/** Creates a new UdpSocket */ 
	public UdpSocket(int port, IpAddress ipaddr) {
		init(port,ipaddr);
	}

	/** Inits the UdpSocket */ 
	private void init(int port, IpAddress ipaddr) {
		try {
			socket=(UDPDatagramConnection)Connector.open("datagram://:"+port);
			recv_dgram=socket.newDatagram(MAX_PKT_SIZE);
		}
		catch (java.io.IOException e) {  e.printStackTrace();  }
		send_dgrams=new Hashtable();
	}

  
	// ************************ public methods *************************

	/** Closes this datagram socket. */
	public void close() {
		try {
			socket.close();
		}
		catch (java.io.IOException e) {  e.printStackTrace();  }
		recv_dgram=null;
		send_dgrams=null;
	}


	/** Gets the local address to which the socket is bound. */
	public IpAddress getLocalAddress() {
		try {
			return new IpAddress(socket.getLocalAddress());
		}
		catch (java.io.IOException e) {  e.printStackTrace(); return null;  }
	}

	
	/** Gets the port number on the local host to which this socket is bound. */
	public int getLocalPort() {
		try {
			return socket.getLocalPort();
		}
		catch (java.io.IOException e) {  e.printStackTrace(); return 0;  }
	}

  
	/** Gets the socket timeout. */
	public int getSoTimeout() {
		return 0;
	}

	
	/** Enables/disables socket timeout with the specified timeout, in milliseconds. */
	public void setSoTimeout(int timeout) {
		
	}


	/** Receives a datagram packet from this socket. */
	public void receive(UdpPacket pkt) throws java.io.IOException {
		// receive a new datagram
		byte[] buffer=new byte[MAX_PKT_SIZE];
		recv_dgram.setData(buffer,0,buffer.length);
		socket.receive(recv_dgram);
		// set packet data
		pkt.setData(recv_dgram.getData(),recv_dgram.getOffset(),recv_dgram.getLength());
		// set packet address
		String url=recv_dgram.getAddress(); // datagram://addr:port
		if (DEBUG) {
			if (recv_dgram.getLength()>2) printLog("received "+recv_dgram.getLength()+"B from: "+url);
		}
		int begin=url.indexOf("://")+3;
		int end=url.indexOf(":",begin);
		String addr=url.substring(begin,end);
		// invert arpa address format 
		if (addr.indexOf(".in-addr.arpa")>=0) {
			Parser par=new Parser(addr);
			char[] delim={'.'};
			for (int i=0; i<4; i++) addr=(i==0)? par.getWord(delim) : par.getWord(delim)+"."+addr;
		}
		//if (DEBUG) printLog("src addr: "+addr);
		int port=Integer.parseInt(url.substring(end+1,url.length()));
		//if (DEBUG) printLog("src port: "+port);
		pkt.setIpAddress(new IpAddress(addr));
		pkt.setPort(port);
		receiver_packet_count++;
		receiver_octect_count+=pkt.getLength();
	}

  
	/** Sends an UDP packet from this socket. */ 
	public void send(UdpPacket pkt) throws java.io.IOException {
		SocketAddress dest_soaddr=new SocketAddress(pkt.getIpAddress(),pkt.getPort());
		Datagram dgram;
		if (send_dgrams.containsKey(dest_soaddr)) {
			dgram=(Datagram)send_dgrams.get(dest_soaddr);
			dgram.setData(pkt.getData(),pkt.getOffset(),pkt.getLength());
		}
		else {
			String address="datagram://"+pkt.getIpAddress().toString()+":"+pkt.getPort();
			dgram=socket.newDatagram(pkt.getData(),pkt.getLength(),address);
		}
		socket.send(dgram);
		String url=dgram.getAddress(); // datagram://addr:port
		if (DEBUG) {
			if (dgram.getLength()>2) printLog("sent "+dgram.getLength()+"B to: "+url);
		}
		sender_packet_count++;
		sender_octect_count+=pkt.getLength();
	}

	
	/** Converts this object to a String. */
	public String toString() {
		return "udp:"+getLocalPort();
	}

	/** Gets the total number of sent packets. */
	public long getSenderPacketCounter() {
		return sender_packet_count;
	}

	/** Gets the total number of sent octects. */
	public long getSenderOctectCounter() {
		return sender_octect_count;
	}

	/** Gets the total number of received packets. */
	public long getReceiverPacketCounter() {
		return receiver_packet_count;
	}

	/** Gets the total number of received octects. */
	public long getReceiverOctectCounter() {
		return receiver_octect_count;
	}

	/** Prints a log message. */
	static protected void printLog(String str) {
		//local.microua.MicroUI.log.println("DBG: UdpSocket: "+str);
	}

}
