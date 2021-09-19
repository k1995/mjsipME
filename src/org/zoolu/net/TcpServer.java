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



import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;



/** TcpServer implements a TCP server wainting for incoming connection.
  */
public class TcpServer extends Thread {
	

	/* TCP server socket */
	ServerSocketConnection server_socket;

	/** Whether it has been halted */
	boolean stop; 

	/** Whether it is running */
	boolean is_running; 

	/** TcpServer listener */
	TcpServerListener listener;




	/** Costructs a new TcpServer */
	public TcpServer(ServerSocketConnection server_socket, TcpServerListener listener)  throws java.io.IOException {
		this.listener=listener;
		this.server_socket=server_socket;
		this.stop=false; 
		this.is_running=true;
		start();
	}


	/** Costructs a new TcpServer */
	public TcpServer(int port, TcpServerListener listener) throws java.io.IOException {
		init(port,null,0,listener);
		start();
	}
	
	
	/** Costructs a new TcpServer */
	public TcpServer(int port, IpAddress bind_ipaddr, TcpServerListener listener) throws java.io.IOException {
		init(port,bind_ipaddr,0,listener);
		start();
	}


	/** Costructs a new TcpServer */
	public TcpServer(int port, IpAddress bind_ipaddr, long alive_time, TcpServerListener listener) throws java.io.IOException {
		init(port,bind_ipaddr,alive_time,listener);
		start();
	}


	/** Inits the TcpServer */
	private void init(int port, IpAddress bind_ipaddr, long alive_time, TcpServerListener listener) throws java.io.IOException {
		this.listener=listener;
		this.server_socket=(ServerSocketConnection)Connector.open("socket://:"+port);
		this.stop=false; 
		this.is_running=true;
	}


	/** Gets server port */
	public int getPort() {
		try {  return server_socket.getLocalPort();  } catch (java.io.IOException e) {  return 0;  }
	}


	/** Whether the service is running */
	public boolean isRunning() {
		return false;
	}


	/** Stops running */
	public void halt() {
		stop=true;
	}


	/** Runs the server */
	public void run() {
		Exception err=null;
		try {
			// loop
			while (!stop) {
				TcpSocket socket=null;
				socket=new TcpSocket((SocketConnection)server_socket.acceptAndOpen());
				if (listener!=null) listener.onIncomingConnection(this,socket);
			}
		}
		catch (Exception e) {
			err=e;
			e.printStackTrace();
			stop=true;
		}
		is_running=false;
		try {
			server_socket.close();
		}
		catch (java.io.IOException e) {}
		server_socket=null;
		
		if (listener!=null) listener.onServerTerminated(this,err);
		listener=null;
	}  


	/** Gets a String representation of the Object */
	public String toString() {
		return "tcp:server:"+getPort();
	}   

}
