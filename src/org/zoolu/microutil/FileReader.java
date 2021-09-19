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
import java.io.InputStreamReader;
import java.io.Reader;

import javax.microedition.io.Connector;


/** FileReader is an input stream for reading data from a file.
  */
public class FileReader extends Reader {
	
	Reader in;
	
	/** Creates a new FileReader. */ 
	public FileReader(String filename) throws FileNotFoundException {
		try {
			in=new InputStreamReader(Connector.openInputStream("file://"+filename));
		}
		catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}
	
	/** Close the stream. */
	public void close() throws IOException {
		in.close();
	}

	/** Mark the present position in the stream. */
	public void mark(int readAheadLimit) throws IOException {
		in.mark(readAheadLimit);
	}

	/** Tell whether this stream supports the mark() operation. */
	public boolean markSupported() {
		return in.markSupported();
	}

	/** Read a single character. */
	public int read() throws IOException {
		return in.read();
	}

	/** Read characters into an array. */
	public int read(char[] cbuf) throws IOException {
		return in.read(cbuf);
	}

	/** Read characters into a portion of an array. */
	public int read(char[] cbuf, int off, int len) throws IOException {
		return in.read(cbuf,off,len);
	}

	/** Tell whether this stream is ready to be read. */
	public boolean ready() throws IOException {
		return in.ready();
	}

	/** Reset the stream. */
	public void reset() throws IOException {
		in.reset();
	}

	/** Skip characters. */
	public long skip(long n) throws IOException {
		return in.skip(n);
	}
}
