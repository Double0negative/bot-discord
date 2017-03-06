package org.mcsg.bot;

import java.util.List;

import org.mcsg.bot.api.Bot;
import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

import sx.blah.discord.handle.obj.IGuild;

public class DiscordServer implements BotServer {

	IGuild guild;
	DiscordBot bot;
	
	public DiscordServer(IGuild guild, DiscordBot bot) {
		this.guild = guild;
		this.bot = bot;
	}
	
	
	@Override
	public List<BotChannel> getChats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BotUser> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Bot getBot() {
		return bot;
	}
	
	public IGuild getHandle() {
		return guild;
	}

	
	
}
