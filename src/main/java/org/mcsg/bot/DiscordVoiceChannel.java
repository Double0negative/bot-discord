package org.mcsg.bot;

import java.io.File;
import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.naming.OperationNotSupportedException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mcsg.bot.api.BotSentMessage;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;
import org.mcsg.bot.api.BotVoiceChannel;
import org.mcsg.bot.audio.TrackInfo;

import com.sedmelluq.discord.lavaplayer.demo.d4j.AudioProvider;
import com.sedmelluq.discord.lavaplayer.demo.d4j.AudioQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordVoiceChannel implements BotVoiceChannel{

	private static final String DEFAULT_QUEUE = "_default";
	
	private IVoiceChannel channel;
	private DiscordChannel chat;
	private DiscordServer server;

	private AudioPlayerManager manager;
	private AudioPlayer player;
	private AudioProvider provider;
	
	private AudioQueue queue;

	public DiscordVoiceChannel(IVoiceChannel channel,DiscordChannel chat, DiscordServer server, AudioPlayerManager manager) {
		this.channel = channel;
		this.server = server;
		this.chat = chat;
		this.manager = manager;
	}

	@Override
	public String getId() {
		return channel.getID();
	}

	@Override
	public BotServer getServer() {
		return server;
	}

	@Override
	public List<BotUser> getUsers() {
		List<IUser> users =  channel.getUsersHere();
		List<BotUser> u = new ArrayList<>();

		for(IUser user : users) {
			u.add(new DiscordUser(user));
		}
		return u;
	}

	@Override
	public BotSentMessage sendMessage(String msg) {
		return chat.sendMessage(msg);
	}

	@Override
	public void queueMessage(String msg) {
		chat.queueMessage(msg);
	}

	@Override
	public BotSentMessage commitMessage() {
		return chat.commitMessage();
	}

	@Override
	public BotSentMessage sendError(String error) {
		return chat.sendError(error);
	}

	@Override
	public void sendThrowable(Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void connnect() {
		try {
			channel.join();
			channel.changeBitrate(96000);			
			this.player = manager.createPlayer();
			this.provider = new AudioProvider(player);
			
			server.getHandle().getAudioManager().setAudioProvider(provider);
			
			setupAudio();
		} catch (MissingPermissionsException | DiscordException | RateLimitException e) {
			e.printStackTrace();
		}
	}
	
	private void setupAudio() {
		this.queue = new AudioQueue(player, chat);
		this.player.addListener(this.queue);
	}

	
	@Override
	public void disconnect() {
		channel.leave();
	}
	@Override
	public void skip() {
		queue.nextTrack();
	}

	@Override
	public void pause() {
		this.player.setPaused(true);
	}

	public void resume() {
		this.player.setPaused(false);
	}

	@Override
	public void sendFile(File file) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return channel.getName();
	}

	@Override
	public BotUser getUserByName(String name) {
		BotUser user = getUserByNameExact(name);
		if(user != null) {
			return user;
		}
		
		
		//fuzzy search from bukkit
		String lowerName = name.toLowerCase(java.util.Locale.ENGLISH);
		int delta = Integer.MAX_VALUE;
		for (BotUser player : getUsers()) {
			if (player.getUsername().toLowerCase(java.util.Locale.ENGLISH).startsWith(lowerName)) {
				int curDelta = Math.abs(player.getUsername().length() - lowerName.length());
				if (curDelta < delta) {
					user = player;
					delta = curDelta;
				}
				if (curDelta == 0) break;
			}
		}
		return user;
	}

	public BotUser getUserByNameExact(String name) {
		for(BotUser user : getUsers()) {
			if(user.getUsername().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public void clear() {
		chat.clear();
	}

	@Override
	public List<BotSentMessage> getMessages() {
		return chat.getMessages();
	}

	@Override
	public void play(String query) {
		String queryString = "ytsearch: " + query;
		
		if(query.contains("http")) {
			queryString = query;
		}
		
		Future<Void> future = manager.loadItem(queryString, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(AudioTrack track) {
				queue.queue(track);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist list) {
				queue.queue(list.getTracks().get(0));
			}
			
			@Override
			public void noMatches() {
				chat.sendMessage("Could not play " + query + ". No tracks found.");
			}
			
			@Override
			public void loadFailed(FriendlyException arg0) {
				arg0.printStackTrace();

				System.out.println("Loading failed");
				chat.sendMessage("Could not play " + query + ". And error occured while loading the track.");
			}
		});
	}



}
