package org.mcsg.bot;

import java.util.HashMap;
import java.util.Map;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;
import org.mcsg.bot.api.BotVoiceChannel;
import org.mcsg.bot.util.StringUtils;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class DiscordBot extends GenericBot{

	private static DiscordBot instance;
	
	public static DiscordBot get() {
		return instance;
	}
	
	public static void main(String args[]) {
		instance = new DiscordBot();
	}
	
	
	private IDiscordClient client;
	private Map<IGuild, DiscordVoiceChannel> voices;
	
	
	private DiscordBot() {
		super();
		ClientBuilder builder = new ClientBuilder();
		builder.withToken(getSettings().get("discord.token"));
		
		this.voices = new HashMap<>();
		
		try {
			client = builder.login();
			client.getDispatcher().registerListener(new DiscordListener(this.getCommandHandler(), this));
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addVoice(IGuild guild, DiscordVoiceChannel channel) {
		this.voices.put(guild, channel);
	}

	@Override
	public BotUser getUser(String id) {
		return new DiscordUser(client.getUserByID(id));
	}

	@Override
	public BotChannel getChat(String id) {
		IChannel channel = client.getChannelByID(id);
		
		return new DiscordChannel(channel, new DiscordServer(channel.getGuild(), this));
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

	@Override
	public String getBrandingString() {
		return StringUtils.replaceVars(
				"{0} version {1} [{2}] implementing {3} version {4} [{5}]",
				this.getClientName(),
				this.getVersion(),
				this.getRepo(),
				super.getClientName(),
				super.getVersion(),
				super.getRepo());
	}

	@Override
	public BotVoiceChannel getVoiceChannel(BotChannel channel) {
		BotVoiceChannel voice =  voices.get(((DiscordServer)channel.getServer()).getHandle());
		return voice;
	}
}
