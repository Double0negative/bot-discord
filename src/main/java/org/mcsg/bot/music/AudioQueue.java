package org.mcsg.bot.music;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.mcsg.bot.DiscordBot;
import org.mcsg.bot.DiscordChannel;
import org.mcsg.bot.api.Bot;
import org.mcsg.bot.api.BotChannel;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import sx.blah.discord.util.EmbedBuilder;

public class AudioQueue extends AudioEventAdapter {
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
//	private final AudioTrack filler;
	
	private DiscordChannel chat; 
	private Bot bot;

	/**
	 * @param player The audio player this scheduler uses
	 */
	public AudioQueue(Bot bot, AudioPlayer player, BotChannel chat) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.chat = (DiscordChannel)chat;
		this.bot = bot;
		//this.filler = filler;
	}

	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 */
	public void queue(AudioTrack track) {
		// Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
		// something is playing, it returns false and does nothing. In that case the player was already playing so this
		// track goes to the queue instead.
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.withThumbnail("https://cdn3.iconfinder.com/data/icons/glypho-music-and-sound/64/music-note-sound-circle-64.png");
		//builder.withAuthorIcon("https://lh3.googleusercontent.com/dUsfnDQJZt2v9d1n2tWsPZiYLLmOQkjv3R4rbsTw83lYGo2cQe8u2z-0YQPxmmcgkL8d=w300");
		String title = "";
		if (!player.startTrack(track, true)) {
			queue.offer(track);
			title  =  "#" + queue.size() + " in queue";
			builder.withColor(Color.decode("#ffbb00"));
		} else {
			builder.withColor(Color.GREEN);
			title = "Playing";
			bot.setStatus(track.getInfo().title);
		}
		builder.withTitle(track.getInfo().title);
		builder.withUrl(track.getInfo().uri);
		//builder.appendField(track.getInfo().title, "**Song:** "+track.getInfo().title+"\n**Time:** " + longToTime(track.getInfo().length), false);
		builder.appendField(track.getInfo().author, title + " - " + longToTime(track.getInfo().length), false);


	
		
		chat.getHandle().sendMessage(builder.build());
	}

	
	private String longToTime(long time) {
		long minutes =  (time / 1000)  / 60;
		long seconds = (time / 1000) % 60;
		
		return minutes + ":" + seconds;
	}
	
	/**
	 * Start the next track, stopping the current one if it is playing.
	 */
	public void nextTrack() {
		// Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
		// giving null to startTrack, which is a valid argument and will simply stop the player.
		AudioTrack track = queue.poll();
		if(track != null) {
			player.startTrack(track, false);
			//chat.sendMessage("Playing " + track.getInfo().title);
			bot.setStatus(track.getInfo().title);
		} else {
			bot.setStatus("Some wall riding beats");
			player.stopTrack();
			//player.startTrack(filler, true)
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
		if (endReason.mayStartNext) {
			nextTrack();
		}
	}

	public void onPlaylistEnd(Runnable callback) {
		callback.run();
	}
}


