package org.mcsg.bot;

import org.mcsg.bot.api.BotUser;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordUser implements BotUser{

	IUser user;
	
	public DiscordUser(IUser user) {
		this.user = user;
	}
	
	@Override
	public String getId() {
		return user.getID();
	}

	@Override
	public String getUsername() {
		return user.getName();
	}

	@Override
	public void sendMessage(String msg) {
		try {
			user.getOrCreatePMChannel().sendMessage(msg);
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			e.printStackTrace();
		}
	}
	
	

}
