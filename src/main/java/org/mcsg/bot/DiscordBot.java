package org.mcsg.bot;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;
import org.mcsg.bot.api.BotVoiceChannel;
import org.mcsg.bot.api.PermissionManager;
import org.mcsg.bot.plugin.PluginManager;
import org.mcsg.bot.util.DelayedActionMessage;
import org.mcsg.bot.util.StringUtils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.Image;

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
	
	private Map<String, DiscordServer> servers;
	private Map<String, DiscordChannel> channels;
	private Map<String, DiscordUser> users;

	private BotChannel defaultChannel;
	private PermissionManager permissionsManager;
	private AudioPlayerManager audioManager;
	private PluginManager pluginManager;

	private DiscordBot() {
		super();
		try{
			ClientBuilder builder = new ClientBuilder();
			builder.withToken(getSettings().getString("discord.token"));

			this.voices = new HashMap<>();

			client = builder.login();
			client.getDispatcher().registerListener(new DiscordListener(this.getCommandHandler(), this));

			
		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void started() {
		try{ 
			audioManager = new DefaultAudioPlayerManager();
			
			AudioSourceManagers.registerRemoteSources(audioManager);
			AudioSourceManagers.registerLocalSource(audioManager);
			
			this.permissionsManager = new GenericPermissionManager(this);
			getCommandHandler().registerCommand(new MuteCommand());
			getCommandHandler().registerCommand(new ClearCommand());

			this.pluginManager = new PluginManager(this);
			this.pluginManager.load();
			this.pluginManager.enableAll();
			
		} catch(Exception e) {
			e.printStackTrace();
			throwable( e);
		}
	}

	public BotVoiceChannel connectVoiceChannel(String id, String chatid) {
		System.out.println(id + " " + chatid);
		IVoiceChannel vchannel = client.getVoiceChannelByID(id);
		DiscordChannel channel = (DiscordChannel)getChat(chatid);
		DiscordServer server = (DiscordServer) channel.getServer();

		DiscordVoiceChannel voice = new DiscordVoiceChannel(vchannel,channel, server, audioManager);

		addVoice(vchannel.getGuild(), voice);

		voice.connnect();

		log("Voice", "Connected to #" + channel.getName() + " in server " + server.getName());


		return voice;
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

	public void setDefaultChannel(BotChannel botChannel) {
		this.defaultChannel = botChannel;
	}

	@Override
	public BotChannel getDefaultChat() {
		return defaultChannel;
	}

	private DelayedActionMessage logger;

	@Override
	public void log(String log) {
		if(logger == null) {
			logger = new DelayedActionMessage(getDefaultChat());
		}
		logger = logger.append(log);
	}

	@Override
	public void log(String prefix, String log) {
		log("[" + prefix + "] " + log);
	}

	@Override
	public PermissionManager getPermissionManager() {
		return permissionsManager;
	}

	@Override
	public void err(String prefix, String log) {
		log("[ERR][" + prefix + "]", log);
	}

	@Override
	public void err(String log) {
		log("[ERR]", log);
	}

	@Override
	public void throwable(Throwable t) {
		getDefaultChat().sendThrowable(t);
	}

	@Override
	public String getAdminId() {
		return getSettings().getString("adminid", null);
	}

	@Override
	public void stop() {
		log("System", "Shutting down...");
		
		try{
			Thread.sleep(1000);
			System.exit(0);
		} catch(Exception e){}
	}


}
