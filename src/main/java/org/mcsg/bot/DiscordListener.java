package org.mcsg.bot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.mcsg.bot.command.CommandHandler;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordListener {

	private CommandHandler handler;
	private DiscordBot bot;
	
	public DiscordListener(CommandHandler handler, DiscordBot bot) {
		this.handler = handler;
		this.bot = bot;
	}
	
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) { // This method is called when the ReadyEvent is dispatched


	}

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) { 	
		String msg = event.getMessage().getContent();
		
		IChannel channel = event.getMessage().getChannel();
		IGuild guild = event.getMessage().getGuild();
		
		DiscordServer server = new DiscordServer(guild, bot);
		DiscordChat chat = new DiscordChat(channel, server);
		
		DiscordUser user = new DiscordUser(event.getMessage().getAuthor());
		
		handler.executeCommand(msg, chat, user);
	}

}
