/*
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
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


//import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;


/** TcpSocket provides a uniform interface to TCP transport protocol,
  * regardless J2SE or J2ME is used.
  */
public class TcpSocket {
	

	/* TCP client socket */
	SocketConnection socket=null;

	/* TCP socket InputStream */
	InputStream is=null;

	/* TCP socket OutputStream */
	OutputStream os=null;


	/** Creates a new TcpSocket */ 
	TcpSocket() {
		socket=null;
	}

	/** Creates a new TcpSocket */ 
	TcpSocket(SocketConnection sock_conn) {
		socket=sock_conn;
	}

	/** Creates a new TcpSocket */ 
	public TcpSocket(String host, int port) throws java.io.IOException {
		init(new IpAddress(host),port);
	}

	/** Creates a new TcpSocket */ 
	public TcpSocket(String host, int port, IpAddress local_ipaddr, int local_port) throws java.io.IOException {
		init(new IpAddress(host),port);
	}

	/** Creates a new TcpSocket */ 
	public TcpSocket(IpAddress ipaddr, int port) throws java.io.IOException {
		init(ipaddr,port);
	}

	/** Creates a new TcpSocket */ 
	public TcpSocket(IpAddress ipaddr, int port, IpAddress local_ipaddr, int local_port) throws java.io.IOException {
		init(ipaddr,port);
	}

	/** Inits the TcpSocket */ 
	private void init(IpAddress ipaddr, int port) throws java.io.IOException {
		//this.ipaddr=ipaddr;
		//this.port=port;
		socket=(SocketConnection)Connector.open("socket://"+ipaddr.toString()+":"+port);
	}

	/** Whether the socket is connected. */
	public boolean isConnected() {
		return is!=null;
	}
	
	/** Closes this socket. */
	public void close() throws java.io.IOException {
		if (is!=null) is.close();
		if (os!=null) os.close();
		if (socket!=null) socket.close();
	}
	
	/** Gets an input stream for this socket. */
	public InputStream getInputStream() throws java.io.IOException {
		if (is==null) is=socket.openInputStream();
		return is;
	}
	
	/** Gets an output stream for this socket. */
	public OutputStream getOutputStream() throws java.io.IOException {
		if (os==null) os=socket.openOutputStream();
		return os;
	}
	
	/** Gets the local address to which the socket is bound. */
	public IpAddress getLocalAddress() {
		try {  if (socket!=null) return new IpAddress(socket.getLocalAddress());  } catch (Exception e) {}
		return null;
	}
	
	/** Gets the local port to which this socket is bound. */
	public int getLocalPort() {
		try {  if (socket!=null) return socket.getLocalPort();  } catch (Exception e) {}
		return 0;
	}
	
	/** Gets the address to which the socket is connected. */
	public IpAddress getAddress() {
		try {  if (socket!=null) return new IpAddress(socket.getAddress());  } catch (Exception e) {}
		return null;
	}
	
	/** Gets the remote port to which this socket is connected. */
	public int getPort() {
		try {  if (socket!=null) return socket.getPort();  } catch (Exception e) {}
		return 0;
	}
	
	/** Gets the socket timeout. */
	public int getSoTimeout() {
		return 0;
	}
	
	/** Enables/disables the socket timeou, in milliseconds. */
	public void setSoTimeout(int timeout) {
		
	}
	
	/** Converts this object to a String. */
	public String toString() {
		return "tcp:"+getLocalAddress()+":"+getPort();
	}

}
