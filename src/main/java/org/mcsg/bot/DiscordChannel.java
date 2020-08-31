package org.mcsg.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotSentMessage;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;

public class DiscordChannel implements BotChannel {

	private static final Set<String> muted = new HashSet<>();

	private MessageChannel channel;
	private BotServer server;

	private List<String> queue;

	public DiscordChannel(MessageChannel channel, BotServer server) {
		this.channel = channel;
		this.server = server;

		this.queue = new ArrayList<>();
	}

	@Override
	public String getId() {
		return channel.getId().asString();
	}

	@Override
	public List<BotUser> getUsers() {
		return new ArrayList<>();
	}

	@Override
	public BotServer getServer() {
		return server;
	}

	@Override
	public BotSentMessage sendMessage(String msg) {
		if (muted.contains(getId()))
			return null;
		BotSentMessage bsm = null;
		Message im = channel.createMessage(limit(msg)).block();
		bsm = new DiscordSentMessage(im, channel, (DiscordBot) getServer().getBot());

		return bsm;
	}

	@Override
	public BotSentMessage sendError(String error) {
		if (muted.contains(getId()))
			return null;

		return this.sendMessage(limit("```Error: \n\n " + error + " ```"));
	}

	@Override
	public void sendThrowable(Throwable throwable) {
		if (muted.contains(getId()))
			return;

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		// stack trace as a string
		this.sendError(sw.toString());
	}

	@Override
	public void queueMessage(String msg) {
		this.queue.add(msg);
	}

	@Override
	public BotSentMessage commitMessage() {
		if (muted.contains(getId()))
			return null;

		StringBuilder sb = new StringBuilder();
		for (String msg : queue)
			sb.append(msg).append("\n");
		return sendMessage(sb.toString());
	}

	@Override
	public void sendFile(File file) throws Exception {
		System.out.println("Sending " + file.getAbsolutePath());
		channel.createMessage(spec -> {
			try {
				spec.addFile(file.getName(), new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}).subscribe();
	}

	@Override
	public String getName() {
		return channel.getId().asString();
	}

	public String limit(String str) {
		if (str.length() >= 2000) {
			return str.substring(0, 1999);
		}
		return str;
	}

	public void mute(boolean mute) {
		if (mute) {
			muted.add(getId());
		} else {
			muted.remove(getId());
		}
	}

	@Override
	public BotUser getUserByName(String name) {
		BotUser user = getUserByNameExact(name);
		if (user != null) {
			return user;
		}

		// fuzzy search from bukkit
		String lowerName = name.toLowerCase(java.util.Locale.ENGLISH);
		int delta = Integer.MAX_VALUE;
		for (BotUser player : getUsers()) {
			if (player.getUsername().toLowerCase(java.util.Locale.ENGLISH).startsWith(lowerName)) {
				int curDelta = Math.abs(player.getUsername().length() - lowerName.length());
				if (curDelta < delta) {
					user = player;
					delta = curDelta;
				}
				if (curDelta == 0)
					break;
			}
		}
		return user;
	}

	public BotUser getUserByNameExact(String name) {
		for (BotUser user : getUsers()) {
			if (user.getUsername().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<BotSentMessage> getMessages() {
		throw new UnsupportedOperationException();
//		List<BotSentMessage> messages = new ArrayList<>();
//
//		for (Message msg : channel.getMessageHistory()) {
//			messages.add(new DiscordSentMessage(msg, (DiscordBot) getServer().getBot()));
//		}
//		return messages;
	}

	public Channel getHandle() {
		return channel;
	}

	@Override
	public BotSentMessage debug(String msg) {
		System.out.println("DEUBG" + server.getBot().getSettings().getBoolean("bot.debug", false));

		if (server.getBot().getSettings().getBoolean("bot.debug", false)) {
			return sendMessage(msg);
		} else {
			return null;
		}
	}

}
