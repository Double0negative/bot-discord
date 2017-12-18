package org.mcsg.bot.commands

import static java.awt.Color.GREEN

import org.mcsg.bot.DiscordChannel
import org.mcsg.bot.api.BotChannel
import org.mcsg.bot.api.BotCommand
import org.mcsg.bot.api.BotServer
import org.mcsg.bot.api.BotUser
import org.mcsg.bot.util.StringUtils

import groovy.json.JsonSlurper
import sx.blah.discord.util.EmbedBuilder

class WeatherCommand implements BotCommand {

	private static final String CURRENT = "http://api.wunderground.com/api/{0}/conditions/q/{1}.json"
	private static final String HOURLY = "http://api.wunderground.com/api/{0}/hourly/q/{1}.json"
	private static final String RADAR = "http://api.wunderground.com/api/{0}/animatedradar/q/{1}.gif?newmaps=1&timelabel=1&timelabel.y=10&num=5&delay=50&noclutter=1&smooth=1&rainsnow=1&height=400&width=700"
	private static final String RADAR_STATIC = "http://api.wunderground.com/api/{0}/radar/q/{1}.png?newmaps=1&noclutter=1&smooth=1&rainsnow=1&height=400&width=500"

	private JsonSlurper slurp = new JsonSlurper()

	@Override
	void execute(String cmd, BotServer server, BotChannel chat, BotUser user, String[] args, String input)
	throws Exception {
		def key = server.bot.settings.get "wunderground.apikey"
		current((DiscordChannel) chat, key, input)
	}


	def current(DiscordChannel dchat, key, query) {
		def url = StringUtils.replaceVars(CURRENT, key, query)
		def json = slurp.parse(new URL(url))

		def radarUrl = StringUtils.replaceVars(RADAR, key, query)
		if (!json || !json.current_observation) return

		try {
			def current = json.current_observation

			dchat.handle.sendMessage new EmbedBuilder().with {
				withColor GREEN
				withAuthorName "Current Conditions"
				withTitle current.display_location.full

				withThumbnail current.icon_url

				appendField "Conditions", "**$current.weather**", true
				appendField "Temperature", "Real: **$current.temperature_string**\nFeels: **$current.feelslike_string**", true
				appendField "Wind", "**$current.wind_string**", false

				withImage radarUrl
			}
		} catch (e) {
			e.printStackTrace()
		}
	}

	def radar(BotChannel chat, key, query) {
		def url = StringUtils.replaceVars(RADAR, key, query)
		File file = new File("${url[url.lastIndexOf('/')..-1]}.gif")
		file.withOutputStream { out ->
			out << new URL(url).openStream()
		}

		chat.sendFile(file)
	}

	@Override
	String getPermission() { "weather" }

	@Override
	String[] getCommand() { ["weather", "w"] }

	@Override
	String getHelp() { null }

	@Override
	String getUsage() { null }
}
