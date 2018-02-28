package org.mcsg.bot;

import java.util.ArrayList;
import java.util.List;

import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

public class DiscordUser implements BotUser{

	IUser user;
	
	public DiscordUser(IUser user) {
		this.user = user;
	}
	
	@Override
	public String getId() {
		return user.getStringID();
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

	@Override
	public List<String> getGroups(BotServer server) {
		List<IRole> roles = user.getRolesForGuild(((DiscordServer)server).getHandle());
		List<String> sroles = new ArrayList<>();
		
		for(IRole role : roles) {
			String str = role.getStringID();
			sroles.add(str);
		}
		return sroles;
	}
	
	public void removeRole(IRole role) {
		RequestBuffer.request(() -> {
			user.removeRole(role);
		});
	}
	
	public void addRole(IRole role) {
		RequestBuffer.request(() -> {
			user.addRole(role);
		});
	}
	public IUser getHandle() {
		return user;
	}
	

}
