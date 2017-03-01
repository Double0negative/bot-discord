package org.mcsg.bot;

import org.mcsg.bot.api.BotChat;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

public class DiscordBot extends GenericBot{

	private static DiscordBot instance;
	
	public static DiscordBot get() {
		return instance;
	}
	
	public static void main(String args[]) {
		instance = new DiscordBot();
	}
	
	
	private IDiscordClient client;
	
	
	private DiscordBot() {
		super();
		ClientBuilder builder = new ClientBuilder();
		builder.withToken(getSettings().get("discord.token"));
		
		try {
			client = builder.login();
			client.getDispatcher().registerListener(new DiscordListener(this.getCommandHandler(), this));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public BotUser getUser(String id) {
		return new DiscordUser(client.getUserByID(id));
	}

	@Override
	public BotChat getChat(String id) {
		IChannel channel = client.getChannelByID(id);
		
		return new DiscordChat(channel, new DiscordServer(channel.getGuild(), this));
	}

	@Override
	public BotServer getServer(String id) {
		return new DiscordServer(client.getGuildByID(id), this);
	}

	@Override
	public String getClientName() {
		return "Discord Bot (Double0negative)";
	}

	@Override
	public String getRepo() {
		return "https://github.com/Double0negative/bot-discord";
	}

	@Override
	public String getVersion() {
		return "Phoenix";
	}
}
