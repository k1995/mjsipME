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

package org.zoolu.microutil;


import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;


/** FileOutputStream is an output stream for writing data to a file.
  */
public class FileOutputStream extends OutputStream {
	
	OutputStream os;
	
	/** Creates a new FileOutputStream. */ 
	public FileOutputStream(String filename) throws IOException {
		os=Connector.openOutputStream("file://"+filename);
	}
	
	/** Creates a new FileOutputStream. */ 
	public FileOutputStream(String filename, boolean append) throws IOException {
		os=Connector.openOutputStream("file://"+filename);
	}

	/** Creates a new FileOutputStream. */ 
	public FileOutputStream(FileOutputStream fileoutstream) {
		os=fileoutstream.os;
	}

	/** Closes this output stream and releases any system resources associated with this stream. */
	public void close() throws IOException {
		os.close();
	}

	/** Flushes this output stream and forces any buffered output bytes to be written out. */
	public void flush() throws IOException {
		os.flush();
	}

	/** Writes b.length bytes from the specified byte array to this output stream. */
	public void write(byte[] b) throws IOException {
		os.write(b);
	}

	/** Writes len bytes from the specified byte array starting at offset off to this output stream. */
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b,off,len);
	}

	/** Writes the specified byte to this output stream. */
	public void write(int b) throws IOException {
		os.write(b);
	}
	
	
}
