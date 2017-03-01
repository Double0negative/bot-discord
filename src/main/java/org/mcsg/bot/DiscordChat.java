package org.mcsg.bot;

import java.util.ArrayList;
import java.util.List;

import org.mcsg.bot.api.BotChat;
import org.mcsg.bot.api.BotSentMessage;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordChat implements BotChat {

	private IChannel channel;
	private BotServer server;
	
	private List<String> queue;
	
	public DiscordChat(IChannel channel, BotServer server) {
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
		BotSentMessage bsm = null;
		try {
			IMessage im = channel.sendMessage(msg);
			bsm = new DiscordSentMessage(im, (DiscordBot)getServer().getBot());
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bsm;
	}

	@Override
	public BotSentMessage sendError(String error) {
		BotSentMessage bsm = null;
		try {
			IMessage im = channel.sendMessage("``` \n Error: \n " + error);
			bsm = new DiscordSentMessage(im, (DiscordBot) getServer().getBot());
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bsm;
	}

	@Override
	public void sendThrowable(Throwable throwable) {
		throwable.printStackTrace();
	}

	@Override
	public void queueMessage(String msg) {
		this.queue.add(msg);
	}

	@Override
	public BotSentMessage commitMessage() {
		StringBuilder sb = new StringBuilder();
		for(String msg : queue)
			sb.append(msg).append("\n");
		return sendMessage(sb.toString());
	}


}
