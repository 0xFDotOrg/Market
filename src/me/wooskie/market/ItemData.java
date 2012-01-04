package me.wooskie.market;
//Contains all item data in hashmaps.

import java.util.HashMap;

import org.bukkit.material.MaterialData;

public class ItemData {

	public static HashMap<MaterialData, String> name = new HashMap<MaterialData, String>();
	public static HashMap<MaterialData, Double> marketPrice = new HashMap<MaterialData, Double>();
	public static HashMap<MaterialData, Object[]> components = new HashMap<MaterialData, Object[]>();
	public static HashMap<MaterialData, Integer> transactionLog = new HashMap<MaterialData, Integer>();
	public static HashMap<MaterialData, Double[]> marketPriceHistory = new HashMap<MaterialData, Double[]>();
	
	public static Double marketStabilityFactor = 1280D;
	
	public static HashMap<MaterialData, Double> ssRatio = new HashMap<MaterialData, Double>();
	public static HashMap<MaterialData, Integer> ssBundleSize = new HashMap<MaterialData, Integer>();
	public static HashMap<MaterialData, Double> ssSellPrice = new HashMap<MaterialData, Double>();
	public static HashMap<MaterialData, Double> ssBuyPrice = new HashMap<MaterialData, Double>();
	
	public ItemData() {
		
		LoadDefaultItemData();
		
		for( MaterialData matData : name.keySet() ) {
			if (!transactionLog.containsKey(matData)) {
				transactionLog.put(matData, 0);
				ssRatio.put(matData, 3D);
			}
		}
		
		Market.fileIO.LoadMarketPrices();
		Market.fileIO.LoadSSRatios();
		Market.fileIO.LoadTransactionLog();
		
		for (MaterialData matData : marketPrice.keySet() ) {
			Double[] itemPriceHistory = new Double[100];
			int ii = 0;
			while (ii < 100) {
				itemPriceHistory[ii] = marketPrice.get(matData);
				ii++;
			}
			marketPriceHistory.put(matData, itemPriceHistory);
		}
		
		Market.fileIO.LoadMarketPriceHistory();
		
		// Set server store prices
		for (MaterialData itemMatData : ssRatio.keySet()) {
			Market.publicActions.UpdateServerStorePrice(itemMatData);
		}
		
	}
	
	// Default item data will be overwritten by data in text files. This data is to start things out initially.
	public void LoadDefaultItemData() {

		Def(1, "Stone", new Object[]{1D, new MaterialData(4), 1D, new MaterialData(263)});
		Def(2, "Grass", 3D);
		Def(3, "Dirt", 0.2);
		Def(4, "Cobblestone", 0.25);
		Def(5, "Wooden Planks", new Object[]{0.25D, new MaterialData(17)});
		Def(6, "Sapling", 0.2);
		Def(6, (byte) 1, "Sapling (Pine)", new Object[]{1D, new MaterialData(6)});
		Def(6, (byte) 2, "Sapling (Birch)", new Object[]{1D, new MaterialData(6)});
		Def(12, "Sand", 0.25);
		Def(13, "Gravel", 0.1);
		Def(14, "Gold Ore", 99D);
		Def(15, "Iron Ore", 19D);
		Def(16, "Coal Ore", 5D);
		Def(17, "Wood", 1D);
		Def(17, (byte) 1, "Wood (Pine)", new Object[]{1D, new MaterialData(17)});
		Def(17, (byte) 2, "Wood (Birch)", new Object[]{1D, new MaterialData(17)});
		Def(18, "Leaves", 0.1);
		Def(20, "Glass", new Object[]{1D, new MaterialData(12), 0.125D, new MaterialData(263)});
		Def(21, "Lapis Lazuli Ore", 50D);
		Def(22, "Lapis Lazuli Block", new Object[]{9D, new MaterialData(351, (byte) 4)});
		Def(24, "Sandstone", new Object[]{4D, new MaterialData(12)});
		Def(25, "Note Block", new Object[]{8D, new MaterialData(5), 1D, new MaterialData(331)});
		Def(50, "Torch",  new Object[]{0.25D, new MaterialData(118), 0.25D, new MaterialData(263, (byte) 1)});
		Def(76, "Redstone Torch", new Object[]{1D, new MaterialData(280), 1D, new MaterialData(331)});
		Def(256, "Iron Shovel", new Object[]{2D, new MaterialData(280), 1D, new MaterialData(265)});
		Def(257, "Iron Pickaxe", new Object[]{2D, new MaterialData(280), 3D, new MaterialData(265)});
		Def(258, "Iron Axe", new Object[]{2D, new MaterialData(280), 3D, new MaterialData(265)});
		Def(259, "Flint and Steel", new Object[]{1D, new MaterialData(318), 1D, new MaterialData(265)});
		Def(260, "Red Apple", 100D);
		Def(261, "Bow", new Object[]{3D, new MaterialData(280), 3D, new MaterialData(287)});
		Def(262, "Arrow", 1D);
		Def(263, "Coal", new Object[]{1D, new MaterialData(263, (byte) 1)});
		Def(263, (byte) 1, "Charcoal", new Object[]{1.143D, new MaterialData(17)});
		Def(264, "Diamond", 600D);
		Def(265, "Iron Ingot", new Object[]{1D, new MaterialData(15), 0.125, new MaterialData(263)});
		Def(266, "Gold Ingot", new Object[]{1D, new MaterialData(14), 0.125, new MaterialData(263)});
		Def(267, "Iron Sword", new Object[]{1D, new MaterialData(280), 2D, new MaterialData(263)});
		Def(268, "Wooden Sword", new Object[]{1D, new MaterialData(280), 2D, new MaterialData(5)});
		Def(269, "Wooden Shovel", new Object[]{2D, new MaterialData(280), 1D, new MaterialData(5)});
		Def(270, "Wooden Pickaxe", new Object[]{2D, new MaterialData(280), 3D, new MaterialData(5)});
		Def(271, "Wooden Axe", new Object[]{2D, new MaterialData(280), 3D, new MaterialData(5)});
		Def(280, "Stick", new Object[]{0.5D, new MaterialData(5)});
		Def(287, "String", 1D);
		Def(318, "Flint", 0.2);
		Def(331, "Redstone", 4D);
		Def(332, "Snowball", 4D);
		Def(333, "Boat", new Object[]{5D, new MaterialData(5)});
		Def(334, "Leather", 15D);
		Def(351, (byte) 4, "Lapis Lazuli", 10D);
		Def(356, "Redstone Repeater", new Object[]{3D, new MaterialData(1), 2D, new MaterialData(76), 1D, new MaterialData(331)});
	}
	
	public void Def(Integer itemID, String itemName, Double itemMarketPrice) {
		Def(itemID, (byte) 0, itemName, itemMarketPrice);
	}
	
	public void Def(Integer itemID, Byte itemDamage, String itemName, Double itemMarketPrice) {
		MaterialData matData = new MaterialData(itemID, itemDamage);
		name.put(matData, itemName);
		marketPrice.put(matData, itemMarketPrice);
	}
	
	public void Def(Integer itemID, String itemName, Object[] itemComponents) {
		Def(itemID, (byte) 0, itemName, itemComponents);
	}
	
	public void Def(Integer itemID, Byte itemDamage, String itemName, Object[] itemComponents) {
		MaterialData matData = new MaterialData(itemID, itemDamage);
		name.put(matData, itemName);
		components.put(matData, itemComponents);
	}
	
}