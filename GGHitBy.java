package com.au_craft.GGHitBy;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.au_craft.GGHitBy.UpdateChecker;

public final class GGHitBy extends JavaPlugin implements Listener {
	public String[] jarVersion = {"0.1","0.1.1","0.2"};
	public String hitMessage;
	protected UpdateChecker updateChecker;
	public DataStore messageToggle;
	
		@Override
	public void onDisable(){
		messageToggle.save();
		
	}
	
		@Override
	public void onEnable(){
		loadConfiguration();
		
		String pluginFolder = this.getDataFolder().getAbsolutePath();
		(new File(pluginFolder)).mkdirs();
		messageToggle = new DataStore(new File(pluginFolder + File.separator + "message-toggle-false.txt"));
		messageToggle.load();
		
		updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/server-mods/gghitby/files.rss");
		if (getConfig().getBoolean("checkForUpdates") && this.updateChecker.updateNeeded()){
			getLogger().info("A new version is available: "+this.updateChecker.getVersion());
			getLogger().info("Get it from: " + this.updateChecker.getLink());
		}
		if (!getConfig().getBoolean("checkForUpdates")){
			getLogger().warning("Update Checking is Disabled. It is advised to be enabled. Change settings in config.yml");
		}
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Enabled");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){ //Check for Player as Sender
			sender.sendMessage("[GGHitBy] You need to be a player in game to access this command!");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("hb") || cmd.getName().equalsIgnoreCase("hitby")){
			if (args.length == 0){
				sender.sendMessage("[GGHitBy]");
				return false;
			} else if (args.length == 1){
				if (args[0].equalsIgnoreCase("reload")){
					if (sender.hasPermission("GGHB.admin.reload")){
						messageToggle.save();
						reloadConfig();
						messageToggle.load();
						sender.sendMessage("[GGHitBy] Reload Complete");
					} else{
						sender.sendMessage("[GGHitBy] You have insufficient Permissions");
					}
				}else{
					sender.sendMessage("[GGHitBy] Incorrect Syntax");
				}
			} else if (args.length == 2){
				if (args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("m")){
					if (sender.hasPermission("GGHB.use.toggle")){
						switch (args[1].toLowerCase()){
							case "t":
							case "true":
								//set show message true
								messageToggle.remove(sender.getName());
								sender.sendMessage("[GGHitBy] Messages Enabled");
								break;
							case "f":
							case "false":
								//set show message false
								messageToggle.add(sender.getName());
								sender.sendMessage("[GGHitBy] Messages Disabled");
								break;
							default:{
								sender.sendMessage("[GGHitBy] " + args[1] + " is not a valid parameter. Enter either true or false.");
								return true;
							}
						}
					} else {
						sender.sendMessage("[GGHitBy] You have insufficient Permissions");
					}
				}else{
					sender.sendMessage("[GGHitBy] Incorrect Syntax");
				}
			} else {
				sender.sendMessage("[GGHitBy] Too many Arguments");
				return false;
			}
		} else {
			sender.sendMessage("[GGHitBy] Incorrect Syntax");
		}
		return true;
	}
	
	public void loadConfiguration(){
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		checkConfigCompatibility();
		getMessageSettings();
	}
	
	public void reloadConfiguration(){
		this.reloadConfig();
		checkConfigCompatibility();
		getMessageSettings();
	}
	
	public void checkConfigCompatibility(){
		String configVersion = getConfig().getString("Version");
		boolean configIsCompatible = false;
		for (String compatibleVersion: jarVersion){
			if (configVersion.equals(compatibleVersion)){
				configIsCompatible = true;
			}
		}
		if (!configIsCompatible){
			getLogger().warning("Config is incompatible. Please delete config and Reload!");
			getLogger().warning("Using Default values!");
		}	
	}
	
	public void getMessageSettings(){
		hitMessage = getConfig().getString("messageOnHit");
	}
	
		@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		if ((event.getDamager() instanceof Player && event.getEntity() instanceof Player)){
			Player attacked = (Player) event.getEntity();
			Player attacker = (Player) event.getDamager();
			if (attacked.hasPermission("GGHB.use.see") && !messageToggle.getValues().contains(attacked.getName())){
			    String hitMessagePlayer = hitMessage.replaceAll("attacker", attacker.getName());
				attacked.sendMessage(hitMessagePlayer);
			}
		}
	}
}
