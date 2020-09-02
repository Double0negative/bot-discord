package org.mcsg.bot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;
import org.mcsg.bot.api.BotVoiceChannel;
import org.mcsg.bot.api.PermissionManager;
import org.mcsg.bot.commands.MuteCommand;
import org.mcsg.bot.plugin.PluginManager;
import org.mcsg.bot.util.DelayedActionMessage;
import org.mcsg.bot.util.StringUtils;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;

public class DiscordBot extends GenericBot {

	private static DiscordBot instance;

	public static DiscordBot get() {
		return instance;
	}

	public static void main(String args[]) {
		instance = new DiscordBot();
	}

	private GatewayDiscordClient client;
	private DiscordClient discord;
//	private Map<IGuild, DiscordVoiceChannel> voices;

	private Map<String, DiscordServer> servers;
	private Map<String, DiscordChannel> channels;
	private Map<String, DiscordUser> users;

	private BotChannel defaultChannel;
	private PermissionManager permissionsManager;
//	private AudioPlayerManager audioManager;
	private PluginManager pluginManager;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private DiscordBot() {
//		super();
		try {

			this.client = DiscordClientBuilder.create(getSettings().getString("discord.token")).build().login().block();

			client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
				this.started();
			});

			client.onDisconnect().block();

//			client.getDispatcher().registerListener(new DiscordListener(this.getCommandHandler(), this));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void started() {
		System.out.println("started");
		try {
//			audioManager = new DefaultAudioPlayerManager();
//
//			AudioSourceManagers.registerRemoteSources(audioManager);
//			AudioSourceManagers.registerLocalSource(audioManager);

			
			this.setDefaultChannel(this.getChat(this.getSettings().getString("discord.default-channel")));
			this.permissionsManager = new GenericPermissionManager(this);
			getCommandHandler().registerCommand(null, new MuteCommand());
			getCommandHandler().registerCommand(null, new ClearCommand());
//			getCommandHandler().registerCommand(new WeatherCommand());
			getCommandHandler().disableNullPluginRegistration();

			this.pluginManager = new PluginManager(this);
			this.pluginManager.load();
			this.pluginManager.enableAll(this.getDefaultChat());
		    
			this.client.on(MessageCreateEvent.class)
		        .filter(message -> message.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false))
		        .subscribe(event -> messageReceivedHandler(event));
		} catch (Exception e) {
			e.printStackTrace();
			throwable(e);
		}
	}
	
	public void messageReceivedHandler(MessageCreateEvent event) { 	
		String msg = event.getMessage().getContent();

		MessageChannel channel = event.getMessage().getChannel().block();
		Guild guild = event.getMessage().getGuild().block();

		DiscordServer server = new DiscordServer(guild, this);
		DiscordChannel chat = new DiscordChannel(channel, server);

		DiscordUser user = new DiscordUser(event.getMember().get());

		this.getCommandHandler().executeCommand(msg, chat, user);
	}


	public BotVoiceChannel connectVoiceChannel(String id, String chatid) {
		System.out.println(id + " " + chatid);
//		IVoiceChannel vchannel = client.getVoiceChannelByID(parseLong(id));
		DiscordChannel channel = (DiscordChannel) getChat(chatid);
		DiscordServer server = (DiscordServer) channel.getServer();

//		DiscordVoiceChannel voice = new DiscordVoiceChannel(vchannel,channel, server, audioManager);
//
//		addVoice(vchannel.getGuild(), voice);
//
//		voice.connnect();
//
//		log("Voice", "Connected to #" + channel.getName() + " in server " + server.getName());
//
//
//		return voice;
		return null;
	}

//	public void addVoice(IGuild guild, DiscordVoiceChannel channel) {
//		this.voices.put(guild, channel);
//	}

	@Override
	public BotUser getUser(String id) {
//		IUser user = client.getUserByID(parseLong(id));
//		if(user == null) 
//			return null;
//		else
//			return new DiscordUser(user);
		return null;
	}

	@Override
	public BotChannel getChat(String id) {
		GuildChannel channel = client.getChannelById(Snowflake.of(id)).ofType(GuildChannel.class).block();
		Guild guild = channel.getGuild().block();
		return new DiscordChannel((MessageChannel)channel, new DiscordServer(guild, this));
	}

	@Override
	public BotServer getServer(String id) {
//		return new DiscordServer(client.getGuildByID(parseLong(id)), this);
		return null;
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
		return "8 Years Later";
	}

	@Override
	public String getBrandingString() {
		return StringUtils.replaceVars("{0} version {1} [{2}] implementing {3} version {4} [{5}]", this.getClientName(),
				this.getVersion(), this.getRepo(), super.getClientName(), super.getVersion(), super.getRepo());
	}

	@Override
	public BotVoiceChannel getVoiceChannel(BotChannel channel) {
//		BotVoiceChannel voice =  voices.get(((DiscordServer)channel.getServer()).getHandle());
//		return voice;
		return null;
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
		if (logger == null) {
			logger = new DelayedActionMessage(getDefaultChat());
		}
		logger = logger.append(log);
	}

	@Override
	public void log(String prefix, String log) {
		log("[" + sdf.format(new Date()) + "] [" + prefix + "] " + log);
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
		t.printStackTrace();
//		getDefaultChat().sendThrowable(t);
	}

	@Override
	public String getAdminId() {
		return getSettings().getString("adminid", null);
	}

	@Override
	public void stop() {
		log("System", "Shutting down...");

		try {
			Thread.sleep(1000);
			System.exit(0);
		} catch (Exception e) {
		}
	}

	@Override
	public void setStatus(String status) {
//		client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, status);
	}

	public GatewayDiscordClient getClient() {
		return this.client;
	}

	@Override
	public PluginManager getPluginManager() {
		return this.pluginManager;
	}

}
