package org.mcsg.bot;

import java.util.List;

import org.mcsg.bot.api.BotChannel;
import org.mcsg.bot.api.BotCommand;
import org.mcsg.bot.api.BotSentMessage;
import org.mcsg.bot.api.BotServer;
import org.mcsg.bot.api.BotUser;

public class ClearCommand implements BotCommand{

	private static final int DEFAULT_DELETE_COUNT = 10;
	
	@Override
	public void execute(String cmd, BotServer server, BotChannel chat, BotUser user, String[] args, String input)
			throws Exception {
		
		if(args.length == 1 && args[0].equalsIgnoreCase("all")) {
			chat.clear();
		}
		
		List<BotSentMessage> msgs = chat.getMessages();
		
		
		
		int amt = DEFAULT_DELETE_COUNT;
		if(args.length == 1) {
			int a = Integer.parseInt(args[0]);
			if(a == -1) {
				amt = msgs.size();
			} else {
				amt = a;
			}
		}
		
		for(int i = 0; i < amt; i++) {
			BotSentMessage msg = msgs.get(i);
			msg.delete();
			
		}
		
	}

	@Override
	public String getPermission() {
		return "clear";
	}

	@Override
	public String[] getCommand() {
		return a("clear");
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
