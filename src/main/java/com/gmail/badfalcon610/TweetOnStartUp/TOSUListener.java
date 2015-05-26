package com.gmail.badfalcon610.TweetOnStartUp;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class TOSUListener implements Listener {

	TweetOnStartUp plugin;

	public TOSUListener(TweetOnStartUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getName().equals("TweetOnStartUp")) {
			Bukkit.getLogger().info("pluginenable");
			// new Tweet();



		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		Bukkit.getLogger().info("worldload");
	}

	@EventHandler
	public void onServiceRegister(ServiceRegisterEvent event) {
		Bukkit.getLogger().info("ServiceRegister");
	}





}
