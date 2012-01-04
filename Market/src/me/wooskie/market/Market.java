package me.wooskie.market;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Market extends JavaPlugin {

	// Sets logger for outputting to minecraft server console
	static Logger log = Logger.getLogger("Minecraft");
	
	// Defines plugin classes:
	static ItemData itemData;
	static FileIO fileIO;
	static AdminActions adminActions;
	static PublicActions publicActions;
	static MarketMaintenance marketMaintenance;
	
	public static HashMap<MaterialData, Object[]> marketData = new HashMap<MaterialData, Object[]>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		MaterialData itemMatData = player.getItemInHand().getData();
		if (commandLabel.equalsIgnoreCase("price")) {
			publicActions.PriceQuery(player, itemMatData, args);
			return true;
		} if (commandLabel.equalsIgnoreCase("market")) {
			return adminActions.Handler(player, itemMatData, args);
		}
		return false;
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		log.info("Market 0.1 enabled.");
		
	fileIO = new FileIO(this); // should come first
	adminActions = new AdminActions();
	publicActions = new PublicActions();
	itemData = new ItemData();
	marketMaintenance = new MarketMaintenance();
	
	this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable() {
		public void run() { 
			marketMaintenance.PriceStability();
			marketMaintenance.SaveAllData(); 
		}
	}, 100L, 10000L);
	
	}
	
}
