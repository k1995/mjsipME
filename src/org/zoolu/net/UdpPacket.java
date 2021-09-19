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




/** UdpPacket provides a uniform interface to UDP packets,
  * regardless J2SE or J2ME is used.
  */
public class UdpPacket {
	
	/** Address */
	IpAddress ipaddr;
	
	/** Port */
	int port; // does it get error when trying to set the ip address with port 0?
	
	/** Data buffer */   
	byte[] buf;
	
	/** Data offset */   
	int offset;

	/** Data length */   
	int length;


	/** Creates a new UdpPacket */ 
	public UdpPacket(byte[] data) {
		init(data,0,data.length,null,0);
	}

	/** Creates a new UdpPacket */ 
	public UdpPacket(byte[] data, IpAddress ipaddr, int port) {
		init(data,0,data.length,ipaddr,port);
	}

	/** Creates a new UdpPacket */ 
	public UdpPacket(byte[] buf, int length) {
		init(buf,0,length,null,0);
	}

	/** Creates a new UdpPacket */ 
	public UdpPacket(byte[] buf, int length, IpAddress ipaddr, int port) {
		init(buf,0,length,ipaddr,port);
	}

	/** Creates a new UdpPacket */ 
	public UdpPacket(byte[] buf, int offset, int length) {
		init(buf,offset,length,null,0);
	}

	/** Creates a new UdpPacket */ 
	public UdpPacket(byte[] buf, int offset, int length, IpAddress ipaddr, int port) {
		init(buf,offset,length,ipaddr,port);
	}


	/** Inits the UdpPacket */ 
	private void init(byte[] buf, int offset, int length, IpAddress ipaddr, int port) {
		this.buf=buf;
		this.offset=offset;
		this.length=length;
		this.ipaddr=ipaddr;
		this.port=port;
	}


	/** Gets the IP address of the machine to which this datagram is being sent or from which the datagram was received. */
	public IpAddress getIpAddress() {
		return ipaddr;
	}

	/** Gets the data received or the data to be sent. */
	public byte[] getData() {
		return buf;
	}

	/** Gets the length of the data to be sent or the length of the data received. */
	public int getLength() {
		return length;
	}

	/** Gets the offset of the data to be sent or the offset of the data received. */
	public int getOffset() {
		return offset;
	}

	/** Gets the port number on the remote host to which this datagram is being sent or from which the datagram was received. */
	public int getPort() {
		return port;
	}

	/** Sets the data buffer for this packet. */
	public void setData(byte[] buf) {
		this.buf=buf;
		this.offset=0;
		this.length=buf.length;
	}

	/** Sets the data buffer for this packet. */
	public void setData(byte[] buf, int offset, int length) {
		this.buf=buf;
		this.offset=offset;
		this.length=length;
	}

	/** Sets the length for this packet. */
	public void setLength(int length) {
		this.length=length;
	}

	/** Sets the IP address of the machine to which this datagram is being sent. */
	public void setIpAddress(IpAddress ipaddr) {
		this.ipaddr=ipaddr;
	}

	/** Sets the port number on the remote host to which this datagram is being sent. */
	public void setPort(int port) {
		this.port=port;
	}

	/** Makes a copy of a given UdpPacket. */
	public void copy(UdpPacket udp_pkt) {
		setData(udp_pkt.getData(),udp_pkt.getOffset(),udp_pkt.getLength());
		setLength(udp_pkt.getLength());
		setIpAddress(udp_pkt.getIpAddress());
		setPort(udp_pkt.getPort());
	}

}
