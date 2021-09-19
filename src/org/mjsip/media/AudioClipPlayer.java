/*
 * Copyright (C) 2006 Luca Veltri - University of Parma - Italy
 * 
 * This source code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package org.mjsip.media;


import java.io.IOException;
import java.net.URL;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;


/** Plays an audio file.
  */
public class AudioClipPlayer {
	

	/** Audio file types */
	final static String[] AUDIO_TYPES={ "audio/x-wav" };

	/** Corresponding audio file extensions */
	final static String[] FILE_EXTS={ "wav" };


	/** Default volume (in the range [0-100]) */
	static int DEFAUL_VOLUME=30;
	
	

	/** The audio player */
	Player clip;
	
	/** The audio volume */
	int volume=100;

	/** The player listener */
	AudioClipPlayerListener listener=null; 


	/** Creates a new AudioClipPlayer.
	  * param@ filename the name of a valid audio file,
	  * param@ listener the AudioClipPlayer listener. */
	public AudioClipPlayer(String filename, AudioClipPlayerListener listener) {
		init(filename,null,listener);
	}


	/** Creates a new AudioClipPlayer.
	  * param@ filename the name of a valid audio file,
	  * param@ listener the AudioClipPlayer listener. */
	public AudioClipPlayer(String filename, String type, AudioClipPlayerListener listener) {
		init(filename,type,listener);
	}


	/** Creates a new AudioClipPlayer.
	  * param@ url URL specifing the audio file,
	  * param@ listener the AudioClipPlayer listener. */
	public AudioClipPlayer(URL url, AudioClipPlayerListener listener) {
		throw new RuntimeException("Method not supported");
	}

	/** Inits the AudioClipPlayer. */
	private void init(String filename, String type, AudioClipPlayerListener listener) {
		this.listener=listener;     
		if (type==null) {
			// guess the file type
			type=AUDIO_TYPES[0];
			for (int i=0; i<FILE_EXTS.length; i++) if (filename.substring(filename.length()-FILE_EXTS[i].length()).equalsIgnoreCase(FILE_EXTS[i])) type=AUDIO_TYPES[i];
		}
		try {
			clip=Manager.createPlayer(getClass().getResourceAsStream(filename),type);
			clip.prefetch();
		}
		catch (MediaException e) {  e.printStackTrace();  }
		catch (IOException e) {  e.printStackTrace();  }
	}


	/** Sets the volume level */
	public void setVolumeGain(double gain) {
		volume=DEFAUL_VOLUME+(int)(gain*DEFAUL_VOLUME);
	}


	/** Gets the volume level */
	public int getVolume() {
		return volume;
	}


	/** Sets loop counter.
	  * @param n The number of loops (0 for playing continuously). */
	public AudioClipPlayer setLoopCount(int n) {
		if (clip!=null) {
			if (n<=0) clip.setLoopCount(-1); else clip.setLoopCount(n-1);
		} 
		return this;
	}


	/** Loops the sound until stopped. */
	public AudioClipPlayer setLoop() {
		return setLoopCount(0);
	}


	/** Plays it. */
	public AudioClipPlayer play() {
		if (clip!=null) {
			try {
				VolumeControl vc=(VolumeControl)clip.getControl("VolumeControl");
				if(vc!=null) vc.setLevel(volume);
			}
			catch (Exception e) {  e.printStackTrace();  } // getControl may throw IllegalArgumentException, IllegalStateException
			try {
				clip.start();
			}
			catch (MediaException e) { e.printStackTrace(); }
		}
		return this;
	}


	/** Plays it n times.
	  * @param n The number of plays (0 for playing continuously). */
	public AudioClipPlayer play(int n) {
		setLoopCount(n).play();
		return this;
	}


	/** Stops and rewinds the sound */
	public AudioClipPlayer stop() {
		if (clip!=null) try {  clip.stop(); } catch (MediaException e) { e.printStackTrace(); }
		return this;
	}


	/** Rewinds the sound */
	public void rewind() {
		
	}


	/** Goes to a time position */
	public void goTo(long millisec) {
		
	}


	/** Plays the sound from begining (restart) */
	public void replay() {
		play();
	}

}


