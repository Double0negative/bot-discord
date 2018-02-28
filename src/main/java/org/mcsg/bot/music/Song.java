package org.mcsg.bot.music;

import org.mcsg.bot.api.BotUser;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Song{

	private AudioTrack track;
	private BotUser player;
	private String query;
	
	private int likes;
	private int dislikes;
	
	private long length;
	
	
	public Song(AudioTrack track, String query, BotUser player) {
		this.track = track;
		this.query = query;
		this.player = player;
	}
	 
	public AudioTrack getTrack() {
		return this.track;
	}
	
	public BotUser getUser() {
		return player;
	}
	
	
}
