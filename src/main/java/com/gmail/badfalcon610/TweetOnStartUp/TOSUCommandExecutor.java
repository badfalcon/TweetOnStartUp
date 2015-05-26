package com.gmail.badfalcon610.TweetOnStartUp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import twitter4j.auth.RequestToken;

public class TOSUCommandExecutor implements CommandExecutor {

	TweetOnStartUp plugin;
	RequestToken requestToken;

	public TOSUCommandExecutor(TweetOnStartUp plugin, RequestToken requestToken) {
		this.plugin = plugin;
		this.requestToken = requestToken;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("auth")) {
			try {
				plugin.reloadConfig();
				return plugin.getAccessTokenR(TweetOnStartUp.twitter,
						requestToken);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return false;
	}

}
