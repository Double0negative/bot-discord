package org.mcsg.bot;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotSentMessage;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class DiscordSentMessage implements BotSentMessage{

	private Message message;
	private MessageChannel channel;
	private DiscordBot bot;
	
	public DiscordSentMessage(Message message, MessageChannel channel, DiscordBot bot) {
		this.message = message;
		this.channel = channel;
		this.bot = bot;
	}

	
	@Override
	public String getMessage() {
		return message.getContent();
	}

	@Override
	public BotChannel getChat() {
		return new DiscordChannel(this.channel, new DiscordServer(message.getGuild().block(), bot));
	}

	@Override
	public void edit(String msg) {
		message.edit(spec -> spec.setContent(msg));
	}

	@Override
	public void delete() {
		message.delete();
	}



	@Override
	public BotSentMessage append(String msg) {
		String str = message.getContent();
		str += "\n" + msg;
		edit(str);
		return this;
	}
	
	

}
