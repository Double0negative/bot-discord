package org.mcsg.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotSentMessage;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

public class DiscordChannel implements BotChannel {

	private static final Set<String> muted = new HashSet<>();

	private IChannel channel;
	private BotServer server;

	private List<String> queue;

	public DiscordChannel(IChannel channel, BotServer server) {
		this.channel = channel;
		this.server = server;

		this.queue = new ArrayList<>();
	}

	@Override
	public String getId() {
		return channel.getID();
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
	public BotServer getServer() {
		return server;
	}

	@Override
	public BotSentMessage sendMessage(String msg) {
		if(muted.contains(getId())) return null;
		BotSentMessage bsm = null;
		try {
			IMessage im = channel.sendMessage(limit(msg));
			bsm = new DiscordSentMessage(im, (DiscordBot)getServer().getBot());
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bsm;
	}

	public void sendMessageBuffered(String msg) {
		RequestBuffer.request(() -> {
			channel.sendMessage(msg);
		}).get();
	}

	@Override
	public BotSentMessage sendError(String error) {
		if(muted.contains(getId())) return null;

		BotSentMessage bsm = null;
		try {
			IMessage im = channel.sendMessage(limit("```Error: \n\n " + error + " ```"));
			bsm = new DiscordSentMessage(im, (DiscordBot) getServer().getBot());
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bsm;
	}

	@Override
	public void sendThrowable(Throwable throwable) {
		if(muted.contains(getId())) return;

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
		if(muted.contains(getId())) return null;

		StringBuilder sb = new StringBuilder();
		for(String msg : queue)
			sb.append(msg).append("\n");
		return sendMessage(sb.toString());
	}

	@Override
	public void sendFile(File file) throws Exception{
		channel.sendFile(file);
	}

	@Override
	public String getName() {
		return channel.getName();
	}


	public String limit(String str) {
		if(str.length() >= 2000) {
			return str.substring(0, 1999);
		}
		return str;
	}

	public void mute(boolean mute) {
		if(mute) {
			muted.add(getId());
		} else {
			muted.remove(getId());
		}
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
		try{
			channel.bulkDelete();
		}catch (Exception e){
			e.printStackTrace();;
		}
	}

	public DiscordSentMessage sendMessage(EmbedObject obj) {
		//channel.sendMessage(null, obj);
		return null;
	}

	@Override
	public List<BotSentMessage> getMessages() {
		List<BotSentMessage> messages = new ArrayList<>();

		for(IMessage msg : channel.getMessageHistory()) {
			messages.add(new DiscordSentMessage(msg, (DiscordBot) getServer().getBot()));
		}
		return messages;
	}
	
	public IChannel getHandle() {
		return channel;
	}


}
