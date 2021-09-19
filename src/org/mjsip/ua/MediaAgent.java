/*
 * Copyright (C) 2008 Luca Veltri - University of Parma - Italy
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

package org.mjsip.ua;



import java.util.Hashtable;

import org.mjsip.media.AudioStreamer;
import org.mjsip.media.FlowSpec;
import org.mjsip.media.LoopbackMediaStreamer;
import org.mjsip.media.MediaStreamer;
import org.mjsip.media.PttStreamer;
import org.zoolu.util.ExceptionPrinter;
import org.zoolu.util.LogLevel;
import org.zoolu.util.Logger;



/** Media agent.
  * A media agent is used to start and stop multimedia sessions
  * (e.g. audio and/or video), by means of embedded media applications.
  */
public class MediaAgent {
	

	/** Log */
	Logger log;
	
	/** Audio application */
	UserAgentProfile ua_profile;

	/** Active media applications, as table of: (String)media-->(MediaStreamer)media_app */
	Hashtable media_streamers=new Hashtable();



	/** Creates a new MediaAgent. */
	public MediaAgent(UserAgentProfile ua_profile, Logger log) {
		this.ua_profile=ua_profile;
		this.log=log;

		// ################# patch to make audio working with javax.sound.. #################
		// currently ExtendedAudioSystem must be initialized before any AudioClipPlayer is initialized..
		// this is caused by a problem with the definition of the audio format
		/*if (!ua_profile.use_rat && !ua_profile.use_jmf) {
			if (ua_profile.audio && !ua_profile.loopback && ua_profile.send_file==null && !ua_profile.recv_only && !ua_profile.send_tone) org.zoolu.sound.ExtendedAudioSystem.initAudioInputLine();
			if (ua_profile.audio && !ua_profile.loopback && ua_profile.recv_file==null && !ua_profile.send_only) org.zoolu.sound.ExtendedAudioSystem.initAudioOutputLine();
		}*/
	}

	
	/** Starts a media session */
	public boolean startMediaSession(FlowSpec flow_spec) {
		printLog("start("+flow_spec.getMediaSpec()+")");
		printLog("new flow: "+flow_spec.getLocalPort()+((flow_spec.getDirection()==FlowSpec.SEND_ONLY)? "=-->" : ((flow_spec.getDirection()==FlowSpec.RECV_ONLY)? "<--=" : "<-->" ))+flow_spec.getRemoteAddress()+":"+flow_spec.getRemotePort());

		String media=flow_spec.getMediaSpec().getType();
		
		// stop previous media_app (just in case something was wrong..)
		if (media_streamers.containsKey(media)) {
			((MediaStreamer)media_streamers.get(media)).halt();
			media_streamers.remove(media);
		}
		 
		// start new media_app
		MediaStreamer media_streamer=null;

		if (ua_profile.loopback) media_streamer=new LoopbackMediaStreamer(flow_spec,log);
		else
		if (flow_spec.getMediaSpec().getType().equals("audio")) media_streamer=newAudioStreamer(flow_spec);
		else
		if (flow_spec.getMediaSpec().getType().equals("video")) media_streamer=null;
		else
		if (flow_spec.getMediaSpec().getType().equals("ptt")) media_streamer=newPttStreamer(flow_spec);

		if (media_streamer!=null) {
			if (media_streamer.start()) {
				media_streamers.put(media,media_streamer);
				return true;
			}
			else return false;
		}
		else {
			printLog("WARNING: no "+media+" application has been found: "+media+" not started",LogLevel.WARNING);
			return false;
		}
	}
 
	
	/** Stops a media session.  */
	public void stopMediaSession(String media) {
		printLog("stop("+media+")");

		if (media_streamers.containsKey(media)) {
			((MediaStreamer)media_streamers.get(media)).halt();
			media_streamers.remove(media);
		}
		else {
			printLog("WARNING: no running "+media+" application has been found.",LogLevel.WARNING);
		}
		/*
		MBox mbox=MBox.getLastInstance();
		if (mbox!=null && mbox.isMediaActive()) {
			mbox.stopApp();
		}*/
	}


	// ********************** media applications *********************

	/** Force this audio codec */
	static final String STATIC_AUDIO_CODEC=null;

	/** Default audio codec */
	static final String DEFAULT_AUDIO_CODEC="iLBC"; // iLBC , gsm610 , amr 

	/** MBox */
	//MBox mbox;



	/** Creates a new audio application. */
	private MediaStreamer newAudioStreamer(FlowSpec flow_spec) {
		
		String audio_codec=flow_spec.getMediaSpec().getCodec();
		String remote_address=flow_spec.getRemoteAddress();
		int remote_port=flow_spec.getRemotePort();
		// @@@@@@@ force the audio codec @@@@@@@
		if (STATIC_AUDIO_CODEC!=null) audio_codec=STATIC_AUDIO_CODEC;
		// change codec name
		if (audio_codec.equalsIgnoreCase("gsm")) audio_codec="gsm610";
		else
		if (audio_codec.equalsIgnoreCase("amr")) audio_codec="amr475";
		// start media
		/*
		MBox mbox=MBox.getLastInstance();
		if (mbox!=null && !mbox.isMediaActive()) {
			mbox.startMedia("audio",audio_codec,0,remote_address,remote_port);
			return true;
		}*/
		return new AudioStreamer(flow_spec,log);
	}
	
	
	/** Creates a new ptt streamer. */
	private MediaStreamer newPttStreamer(final FlowSpec flow_spec) {
		MediaStreamer ptt_streamer=null;
		new Thread(new Runnable(){
			public void run() {
				new PttStreamer(flow_spec,log);
			}		
		}).start();
		return ptt_streamer;
	}



	// ***************************** logs ****************************

	/** Adds a new string to the default Log */
	private void printLog(String str) {
		printLog(str,LogLevel.INFO);
	}

	/** Adds a new string to the default Log */
	private void printLog(String str, LogLevel level) {
		if (log!=null) log.log(level,"MediaAgent: "+str);
	}

	/** Adds the Exception message to the default Log */
	private final void printException(Exception e, LogLevel level) {
		printLog("Exception: "+ExceptionPrinter.getStackTraceOf(e),level);
	}


}
