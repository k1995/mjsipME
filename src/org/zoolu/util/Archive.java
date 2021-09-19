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

package org.zoolu.util;




/** Collection of static methods for handling files and jar archives.
  * Dummy implementation for JME.
*/
public class Archive {
	

	/** Gets a String URL from a String (dummy method). */
	public static String getURL(String url) {
		return url;
	}


	/** Gets the complete URL of a file included within a jar archive (dummy method). */
	public static String getJarURL(String jar_archive, String file_name) {
		return file_name;
	}


	/** Gets the complete URL of a file (dummy method). */
	public static String getFileURL(String file_name) {
		return file_name; 
	}

}
