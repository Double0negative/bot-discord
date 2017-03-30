package org.mcsg.bot;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotCommand;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

public class MuteCommand implements BotCommand{

	@Override
	public void execute(String cmd, BotServer server, BotChannel chat, BotUser user, String[] args, String input)
			throws Exception {
		System.out.println(cmd);
		if(cmd.equalsIgnoreCase("mute")){
			chat.sendMessage("Muted");
			((DiscordChannel)chat).mute(true);
		}else{
			((DiscordChannel)chat).mute(false);
			chat.sendMessage("Unmuted");
		}

	}

	@Override
	public String getPermission() {
		return "mute";
	}

	@Override
	public String[] getPrefix() {
		return a(".");
	}

	@Override
	public String[] getCommand() {
		return a("mute", "unmute");
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

}
