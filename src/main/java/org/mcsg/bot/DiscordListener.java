package org.mcsg.bot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mcsg.bot.command.CommandHandler;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
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
		

		//todo: actually configure what its connecting to somehow
		
		DiscordVoiceChannel voice = new DiscordVoiceChannel(
				event.getClient().getGuilds().get(0).getVoiceChannels().get(0), 
				new DiscordServer(event.getClient().getGuilds().get(0), bot));
		
		bot.addVoice(event.getClient().getGuilds().get(0), voice);

		voice.connnect();

	}

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) { 	
		String msg = event.getMessage().getContent();

		IChannel channel = event.getMessage().getChannel();
		IGuild guild = event.getMessage().getGuild();

		DiscordServer server = new DiscordServer(guild, bot);
		DiscordChannel chat = new DiscordChannel(channel, server);

		DiscordUser user = new DiscordUser(event.getMessage().getAuthor());

		handler.executeCommand(msg, chat, user);
	}

}
