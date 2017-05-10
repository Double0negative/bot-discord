package org.mcsg.bot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotVoiceChannel;
import org.mcsg.bot.command.CommandHandler;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;

public class DiscordListener {

	private CommandHandler handler;
	private DiscordBot bot;

	public DiscordListener(CommandHandler handler, DiscordBot bot) {
		this.handler = handler;
		this.bot = bot;
	}


	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {		
		bot.setDefaultChannel(bot.getChat(bot.getSettings().getString("discord.default-channel")));
		
		bot.log("----------------------------------");
		bot.log("System", "Bot started...");
		
		bot.started();
				
		List<Map> con = bot.getSettings().getList("discord.voice");
		
		for(Map<String, String> values : con) {
			bot.connectVoiceChannel(values.get("voice"), values.get("chat"));
		}
	

	}
	
	

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) { 	
		String msg = event.getMessage().getContent();

		IChannel channel = event.getMessage().getChannel();
		IGuild guild = event.getMessage().getGuild();

		DiscordServer server = new DiscordServer(guild, bot);
		DiscordChannel chat = new DiscordChannel(channel, server);

		DiscordUser user = new DiscordUser(event.getMessage().getAuthor());
//		
//		EmbedBuilder builder = new EmbedBuilder();
//		builder.withColor(Color.GREEN);
//		builder.withTitle("Test");
//		builder.appendField("test", "test", true);
//		builder.appendField("test", "test", true);
//		builder.appendField("test", "test", true);
//
//		builder.withImage("http://i.imgur.com/naVkaEt.jpg");
//		channel.sendMessage(builder.build());

		handler.executeCommand(msg, chat, user);
	}
	


}
