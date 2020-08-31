package org.mcsg.bot.commands;
//
//import java.awt.Color
//
//import org.mcsg.bot.DiscordChannel
//import org.mcsg.bot.api.BotChannel
//import org.mcsg.bot.api.BotCommand
//import org.mcsg.bot.api.BotServer
//import org.mcsg.bot.api.BotUser
//import org.mcsg.bot.util.StringUtils
//
//import groovy.json.JsonSlurper
//import sx.blah.discord.util.EmbedBuilder
//
//class WeatherCommand implements BotCommand{
//
//	private static final String CURRENT = "http://api.wunderground.com/api/{0}/conditions/q/{1}.json"
//	private static final String HOURLY = "http://api.wunderground.com/api/{0}/hourly/q/{1}.json"
//	private static final String RADAR = "http://api.wunderground.com/api/{0}/animatedradar/q/{1}.gif?newmaps=1&timelabel=1&timelabel.y=10&num=5&delay=50&noclutter=1&smooth=1&rainsnow=1&height=400&width=700"
//	private static final String RADAR_STATIC = "http://api.wunderground.com/api/{0}/radar/q/{1}.png?newmaps=1&noclutter=1&smooth=1&rainsnow=1&height=400&width=500"
//
//	private JsonSlurper slurp = new JsonSlurper()
//
//
//
//	@Override
//	public void execute(String cmd, BotServer server, BotChannel chat, BotUser user, String[] args, String input)
//	throws Exception {
//		def key = server.getBot().getSettings().get("wunderground.apikey")
//		current(chat, key,  input.replace(" ", "%20"));
//	}
//
//
//	def current(chat, key, query) {
//		def dchat = chat as DiscordChannel
//
//		def url = StringUtils.replaceVars(CURRENT, key,query)
//		println url
//		def json = slurp.parse(new URL(url))
//
//
//		def radarurl = StringUtils.replaceVars(RADAR, key, query)
//		if(json && json.current_observation) {
//			try{
//				def current = json.current_observation
//
//				EmbedBuilder builder = new EmbedBuilder()
//				builder.withColor(Color.GREEN)
//				builder.withAuthorName("Current Conditions")
//				builder.withTitle(current.display_location.full)
//				
//				builder.withThumbnail(current.icon_url)
//
//				builder.appendField("Conditions", "**${current.weather}**", true)
//				builder.appendField("Tempature", "Real: **${current.temperature_string}** \nfeels: **${current.feelslike_string}**", true)
//				builder.appendField("Wind", "**${current.wind_string}**", false)
//
//				builder.withImage(radarurl)
//
//				dchat.getHandle().sendMessage(builder.build())
//			}catch(e) {
//				e.printStackTrace()
//			}
//		} else {
//			chat.sendMessage("Location not found")
//		}
//
//
//	}
//
//	def radar(BotChannel chat, key, query) {
//		def url = StringUtils.replaceVars(RADAR, key, query)
//		File file = new File("${url.tokenize('/')[-1]}.gif");
//		file.withOutputStream { out ->
//			out << new URL(url).openStream()
//		}
//
//		chat.sendFile(file);
//	}
//
//
//
//	@Override
//	public String getPermission() {
//		"weather"
//	}
//
//
//
//	@Override
//	public String[] getCommand() {
//		["weather", "w"]
//	}
//
//
//
//	@Override
//	public String getHelp() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public String getUsage() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//}
