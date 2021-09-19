/*
 * Copyright (C) 2008 Luca Veltri - University of Parma - Italy
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
import java.io.Writer;
import java.util.Vector;

import javax.microedition.lcdui.StringItem;



/** Write text to a StringItem.
  */
public class StringItemWriter extends Writer {
	
	/** The inner StringItem. */
	StringItem si;

	/** Maximum number of lines. */
	int max_num_of_lines=-1;

	/** Lines. */
	Vector si_lines=null;



	/** Costructs a new StringItemWriter. */
	public StringItemWriter(StringItem si) {
		super();
		this.si=si;
	}

	/** Costructs a new StringItemWriter. */
	public StringItemWriter(StringItem si, int max_num_of_lines) {
		super();
		this.si=si;
		this.max_num_of_lines=max_num_of_lines;
		si_lines=new Vector(max_num_of_lines);
	}

	/** Close the stream. */
	public void close() throws IOException {
		// DO-NOTHING.
	}

	/** Flush the stream. */
	public void flush() throws IOException {
		// DO-NOTHING.
	}

	/** Write a portion of an array of characters. */
	public void write(char[] cbuf, int off, int len) throws IOException {
		String str=new String(cbuf,off,len);
		if (max_num_of_lines>0) {
			if (si_lines.size()>max_num_of_lines) si_lines.removeElementAt(0);
			si_lines.addElement(str);
			StringBuffer sb=new StringBuffer();
			for (int i=0; i<si_lines.size(); i++) sb.append((String)si_lines.elementAt(i));
			si.setText(sb.toString());     
		}
		else si.setText(si.getText()+str);
	}
}
