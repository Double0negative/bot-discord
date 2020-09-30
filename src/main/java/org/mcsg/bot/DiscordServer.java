package org.mcsg.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mcsg.bot.api.Bot;
import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

import discord4j.core.object.entity.Guild;

public class DiscordServer implements BotServer {

	private Guild guild;
	private DiscordBot bot;
	
	public DiscordServer(Guild guild, DiscordBot bot) {
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
		return guild.getMembers().buffer().blockFirst().stream().map((dm) -> new DiscordUser(dm)).collect(Collectors.toList());
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
	public Bot getBot() {
		return bot;
	}
	
	public Guild getHandle() {
		return guild;
	}


	@Override
	public String getId() {
		return guild.getId().asString();
	}


	@Override
	public String getName() {
		return guild.getName();
	}

	
	
}
