package me.wooskie.market;
// Administrator only actions, such as changing item names or market value.

import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class AdminActions {
	
	public boolean Handler(Player player, MaterialData itemMatData, String[] args) {
		
		if (args[0].equalsIgnoreCase("set")) {
			SetItemData(player, itemMatData, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("query")) {
			QueryItem(player, itemMatData);
			return true;
		}
		if (args[0].equalsIgnoreCase("trans") || args[0].equalsIgnoreCase("transaction")) {
			AdminTransaction(player, itemMatData, args);
			return true;
		}
		return false;
	}

	private void SetItemData(Player player, MaterialData itemMatData, String[] args) {
		
		if (!(ItemData.name.containsKey(itemMatData))) {
			Market.publicActions.MarketMessage(player, ("Selected item not in item database."));
			return;
		}
		String itemName = ItemData.name.get(itemMatData);
		
		if (args[1].equalsIgnoreCase("price") || args[1].equalsIgnoreCase("marketprice")) {
			if (ItemData.marketPrice.containsKey(itemMatData)) {
				ItemData.marketPrice.put(itemMatData, Double.parseDouble(args[2]));
				Market.fileIO.SaveMarketPrices();
				Market.publicActions.UpdateServerStorePrice(itemMatData);
				Market.publicActions.MarketMessage(player, ("Market price for " + itemName + " set to " + args[2] + "."));
			} else {
				Market.publicActions.MarketMessage(player, ("Item not listed as primary item."));
			}	
		} else if (args[1].equalsIgnoreCase("ssratio")) {
				ItemData.ssRatio.put(itemMatData, Double.parseDouble(args[2]));
				Market.fileIO.SaveServerStoreRatios();
				Market.publicActions.MarketMessage(player, ("Server store buy/sell ratio for " + itemName + " set to " + args[2] + "."));
		}
		
	}
	
	public void QueryItem(Player player, MaterialData itemMatData) {
		
		if (!(ItemData.name.containsKey(itemMatData))) {
			Market.publicActions.MarketMessage(player, ("Selected item not in database."));
			return;
		}
		
		String itemName = ItemData.name.get(itemMatData);
		
		if (ItemData.marketPrice.containsKey(itemMatData)) {
			String itemMarketPrice = Double.toString(ItemData.marketPrice.get(itemMatData));
			Market.publicActions.MarketMessage(player, (itemName + " is a primary item. " +
					"Market price: " + itemMarketPrice + "."));
		} else if (ItemData.components.containsKey(itemMatData)) {
			Market.publicActions.MarketMessage(player, (itemName + " is a secondary item. It's components are:"));
			Object[] componentData = ItemData.components.get(itemMatData);
			String componentsString = "";
			int ii = 0;
			while ( ii < componentData.length) {
				String quantity = Double.toString((Double) componentData[ii]);
				String componentName = ItemData.name.get((MaterialData) componentData[ii+1]);
				if (ii > 0) {componentsString += ", ";}
				componentsString += quantity + " " + componentName;
				ii+=2;
			}
			Market.publicActions.MarketMessage(player, (componentsString));
			Market.publicActions.MarketMessage(player, ("Market price calculated as: " + Double.toString(Market.publicActions.MarketPrice(itemMatData))));
		}
		Market.publicActions.MarketMessage(player, ("Logged transactions: " + Integer.toString(ItemData.transactionLog.get(itemMatData))));
		if (ItemData.ssRatio.containsKey(itemMatData)) {
			String itemSSRatio = Double.toString(ItemData.ssRatio.get(itemMatData));
			Market.publicActions.MarketMessage(player, ("The server store offers this item with a buy/sell ratio of " + itemSSRatio));
		} else { Market.publicActions.MarketMessage(player, ("This item is not offered by the server store.")); }
	}
	
	private void AdminTransaction(Player player, MaterialData itemMatData, String[] args) {
		
		if (!(ItemData.name.containsKey(itemMatData))) {
			Market.publicActions.MarketMessage(player, ("Selected item not in database."));
			return;
		}
		
		Integer quantity = Integer.parseInt(args[1]);
		Double price = Double.parseDouble(args[2]);
		Market.publicActions.Transaction(itemMatData, quantity, price);
		
		String itemName = ItemData.name.get(itemMatData);
		Market.publicActions.MarketMessage(player, ("Completed transaction for " + Double.toString(quantity) + " " + itemName + " for " + Double.toString(price) + "."));

	}
	
}
