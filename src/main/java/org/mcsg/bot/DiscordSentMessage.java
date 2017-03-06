package org.mcsg.bot;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotSentMessage;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordSentMessage implements BotSentMessage{

	private IMessage message;
	private DiscordBot bot;
	
	public DiscordSentMessage(IMessage message, DiscordBot bot) {
		this.message = message;
		this.bot = bot;
	}
	
	
	
	@Override
	public String getMessage() {
		return message.getContent();
	}

	@Override
	public BotChannel getChat() {
		return new DiscordChannel(message.getChannel(), new DiscordServer(message.getGuild(), bot));
	}

	@Override
	public void edit(String msg) {
		try {
			message.edit(msg);
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void delete() {
		try {
			message.delete();
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	@Override
	public BotSentMessage append(String msg) {
		String str = message.getContent();
		str += "\n" + msg;
		edit(str);
		return this;
	}
	
	

}
