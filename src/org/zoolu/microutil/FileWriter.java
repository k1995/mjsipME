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
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.microedition.io.Connector;


/** FileWriter is an output stream for writing data to a file.
  */
public class FileWriter extends Writer {
	
	Writer out;
	
	/** Creates a new FileWriter. */ 
	public FileWriter(String filename) throws FileNotFoundException {
		try {
			out=new OutputStreamWriter(Connector.openOutputStream("file://"+filename));
		}
		catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}
	
	/** Close the stream. */
	public void close()throws IOException {
		out.close();
	}

	/** Flush the stream. */
	public void flush()throws IOException {
		out.flush();
	}

	/** Write an array of characters. */
	public void write(char[] cbuf)throws IOException {
		out.write(cbuf);
	}

	/** Write a portion of an array of characters. */
	public void write(char[] cbuf, int off, int len)throws IOException {
		out.write(cbuf,off,len);
	}

	/** Write a single character. */
	public void write(int c)throws IOException {
		out.write(c);
	}

	/** Write a string. */
	public void write(String str)throws IOException {
		out.write(str);
	}

	/** Write a portion of a string. */
	public void write(String str, int off, int len)throws IOException {
		out.write(str,off,len);
	}

}
