package org.mcsg.bot;

import java.io.File;
import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mcsg.bot.api.BotSentMessage;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;
import org.mcsg.bot.api.BotVoiceChannel;

import sx.blah.discord.handle.audio.impl.AudioManager;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;

public class DiscordVoiceChannel implements BotVoiceChannel{

	private IVoiceChannel channel;
	private DiscordChannel chat;
	private DiscordServer server;

	private List<File> queue;
	private AudioPlayer audio;

	public DiscordVoiceChannel(IVoiceChannel channel,DiscordChannel chat, DiscordServer server) {
		this.channel = channel;
		this.server = server;
		this.queue = new ArrayList<File>();
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
			audio = AudioPlayer.getAudioPlayerForAudioManager(server.getHandle().getAudioManager());
		} catch (MissingPermissionsException | DiscordException | RateLimitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		channel.leave();
	}

	@Override
	public void playFile(File file) {
		if(!channel.isConnected())
			throw new NotYetConnectedException();

		audio.clear();
		try {
			audio.queue(file);
		} catch (IOException | UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void queueFile(File file) {
		try {
			audio.queue(file);
		} catch (IOException | UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void skip() {
		audio.skip();
	}

	@Override
	public void pause() {
		audio.setPaused(true);
	}

	public void play() {
		audio.setPaused(false);
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


}
