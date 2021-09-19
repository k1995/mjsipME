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




/** TLS security context.
  */
public class TlsContext {
	

	/** Default password */
	public static final char[] DEFAULT_PASSWORD=("TLS_CONTEXT_PASSWD").toCharArray();

	/** KeyStore */
	//KeyStore ks;

	/** Password */
	char[] passwd=DEFAULT_PASSWORD;

	/** Whether all certificates should be considered trusted.
	  * By default, trust_all_certificates=false. */
	boolean trust_all_certificates=false;

	/** Counter of trusted certificates. */
	int trust_count=0;



	
	/** Creates a new TlsContext. */
	public TlsContext() {
		//TODO
	}


	/** Creates a new TlsContext. */
	public TlsContext(char[] passwd) {
		//TODO
	}


	/** Sets key certificate. */
	public void setKeyCert(String key_file, String cert_file) {
		//TODO
	}


	/** Sets key certificate. */
	/*public void setKeyCert(Key key, Certificate cert) throws java.security.KeyStoreException {
		//TODO
	}*/


	/** Adds a trusted certificate. */
	public void addTrustCert(String cert_file) {
		//TODO
	}


	/** Adds a trusted certificate. */
	/*public void addTrustCert(Certificate cert) throws java.security.KeyStoreException {
		//TODO
	}*/


	/** Adds all trusted certificates from the specified folder. */
	public void addTrustFolder(String cert_folder) {
		//TODO
	}


	/** Sets trust-all mode */
	public void setTrustAll(boolean trust) {
		trust_all_certificates=trust;
		printOut("trust all: "+((trust)? "yes" : "no"));
					
	}


	/** Wheather it's in trust-all mode */
	public boolean isTrustAll() {
		return trust_all_certificates;
	}   


	/** Gets KeyStore */
	/*public KeyStore getKeyStore() {
		//return ks;
	}*/


	/** Prints out a message. */
	static void printOut(String str) {
		System.out.println("TlsContext: "+str);
	}
		
}
