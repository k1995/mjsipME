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
import java.io.Reader;


/** Read text from a character-input stream.
  */
public class BufferedReader extends Reader {
	
	Reader in;

	/** Costructs a new BufferedReader. */
	public BufferedReader(Reader in) {
		super(in);
		this.in=in;
	}
	
	/** Close the stream. */
	public void close()  throws IOException {
		in.close();
	}

	/** Read characters into a portion of an array. */
	public int read(char[] cbuf, int off, int len) throws IOException {
		return in.read(cbuf,off,len);
	}

	/** Read a line of text.
	  * A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
	  * @return A String containing the contents of the line, not including any line-termination characters, or null if the end of the stream has been reached.
	  * @throw IOException If an I/O error occurs
	*/
	public String readLine() throws IOException {
		String str="";
		int c=in.read();
		while (c>=0 && c!='\r' && c!='\n') {
			str+=(char)c;
		}
		if (str.length()==0 && c<0) return null;
		else return str;
	}
}
