package com.au_craft.GGHitBy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.au_craft.GGHitBy.UpdateChecker;

public final class GGHitBy extends JavaPlugin implements Listener {
  	public String[] jarVersion = {"0.1"};
		public String hitMessage;
		protected UpdateChecker updateChecker;
		
		@Override
		public void onDisable(){
		}
		
		@Override
		public void onEnable(){
			loadConfiguration();
			this.updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/server-mods/ggdamagechanger/files.rss");
			if (this.updateChecker.updateNeeded() && getConfig().getBoolean("checkForUpdates")){
				getLogger().info("A new version is available: "+this.updateChecker.getVersion());
				getLogger().info("Get it from: " + this.updateChecker.getLink());
			}
			if (!getConfig().getBoolean("checkForUpdates")){
				getLogger().warning("Update Checking is Disabled. It is advised to be enabled. Change settings in config.yml");
			}
			getServer().getPluginManager().registerEvents(this, this);
			getLogger().info("Enabled");
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
				if (true && attacked.hasPermission("GGHB.see")){//if player is opted in to messages & perms
				    String hitMessagePlayer = hitMessage.replaceAll("attacker", attacker.getName());
					attacked.sendMessage(hitMessagePlayer);
				}
			}
		}
	}
