package org.mcsg.bot;

import java.util.ArrayList;
import java.util.List;

import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;

public class DiscordUser implements BotUser{

	Member user;
	
	public DiscordUser(Member user) {
		this.user = user;
	}
	
	@Override
	public String getId() {
		System.out.println(user.getId().asString());
		return user.getId().asString();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public void sendMessage(String msg) {
		this.user.getPrivateChannel().subscribe(channel -> channel.createMessage(msg));
	}

	@Override
	public List<String> getGroups(BotServer server) {
		Guild guild = ((DiscordServer)server).getHandle();
		return new ArrayList<>();
	}
	
	public void removeRole(Role role) {
		user.removeRole(role.getId());
	}
	
	public void addRole(Role role) {
		user.addRole(role.getId());
	}
	public Member getHandle() {
		return user;
	}
	

}
