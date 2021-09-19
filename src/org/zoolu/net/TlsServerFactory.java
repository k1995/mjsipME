/*
 * Copyright (C) 2007 Luca Veltri - University of Parma - Italy
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



/** TLS server factory.
  */
public class TlsServerFactory {
	

	/** Whether using client mode in first TLS handshake */
	boolean client_mode=false;
	
	/** Whether requiring client authentication */
	boolean client_auth=false;



	/** Creates a new TlsServerFactory */
	public TlsServerFactory(TlsContext tls_context) {
		//TODO
	}

	/** Sets whether using client (or server) mode in its first handshake.
	  * Servers normally authenticate themselves, and clients are not required to do so. */
	public void setUseClientMode(boolean flag) {
		client_mode=flag;
	}

	/** Whether using client (or server) mode in its first handshake.
	  * Servers normally authenticate themselves, and clients are not required to do so. */
	public boolean getUseClientMode() {
		return client_mode;
	}

	/** Sets whether requiring client authentication. */
	public void setNeedClientAuth(boolean flag) {
		client_auth=flag;
	}

	/** Whether requiring client authentication. */
	public boolean getNeedClientAuth() {
		return client_auth;
	}

	/** Creates a new TlsServer */
	public TlsServer createTlsServer(int port, TcpServerListener listener) throws java.io.IOException {
		return new TlsServer((ServerSocketConnection)Connector.open("ssl://:"+port),listener);
	}

	/** Creates a new TlsServer */
	public TlsServer createTlsServer(int port, IpAddress bind_ipaddr, TcpServerListener listener) throws java.io.IOException {
		return new TlsServer((ServerSocketConnection)Connector.open("ssl://:"+port),listener);
	}


}
