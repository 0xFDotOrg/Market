package me.wooskie.market;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class PublicActions {
	
	ChatColor messageColor = ChatColor.GOLD;
	ChatColor itemColor = ChatColor.GREEN;

	// Returns market price for itemMatData. Returns -1 if unable to calculate price.
	public double MarketPrice(MaterialData itemMatData) {
		
		Double marketPrice = (double) 0;
		
		if (!(ItemData.name.containsKey(itemMatData))) {return -1;}

		if (ItemData.marketPrice.containsKey(itemMatData)) {
			marketPrice = (Double) ItemData.marketPrice.get(itemMatData);
			return marketPrice;			
		} else if (ItemData.components.containsKey(itemMatData)) {
			Object[] componentData = ItemData.components.get(itemMatData);
			int ii = 0;
			while (ii < componentData.length) {
				double componentQuantity = (Double) componentData[ii];
				double componentMarketPrice = MarketPrice((MaterialData) componentData[ii+1]);
				if (componentMarketPrice == -1) {return -1;}
				marketPrice += (componentQuantity*componentMarketPrice);
				ii+=2;
			}
			return marketPrice;
		} else { return -1; }
	}

	// Logs transaction, sends transaction data to UpdateMarketPrices
	public void Transaction(MaterialData itemMatData, int quantity, double price) {
		
		int logged = ItemData.transactionLog.get(itemMatData);
		ItemData.transactionLog.put(itemMatData, logged+quantity);
		UpdateMarketPrices(itemMatData, (double) quantity, price);
		Market.fileIO.SaveTransactionLog();
	}
	
	// Updates market price for item in transaction. "Secondary" items are broken down into component parts, only "primary" items have a market price.
	public void UpdateMarketPrices(MaterialData itemMatData, double quantity, double price) {
		
		if (ItemData.marketPrice.containsKey(itemMatData)) {
			double oldMarketPrice = ItemData.marketPrice.get(itemMatData);
			double stability = 1000;
			double newMarketPrice = (price/quantity - oldMarketPrice)*price/(10*price+stability) + oldMarketPrice;
			ItemData.marketPrice.put(itemMatData, newMarketPrice);
			Market.fileIO.SaveMarketPrices();
			UpdateServerStorePrice(itemMatData);
			
		} else if (ItemData.components.containsKey(itemMatData)) {
			Object[] componentData = ItemData.components.get(itemMatData);
			double itemMarketPrice = MarketPrice(itemMatData);
			int ii = 0;
			while (ii < componentData.length) {
				double componentQuantity = (Double) componentData[ii];
				MaterialData componentMatData = (MaterialData) componentData[ii+1];
				double componentMarketPrice = MarketPrice(componentMatData);
				UpdateMarketPrices(componentMatData, quantity*componentQuantity, price*componentQuantity*componentMarketPrice/itemMarketPrice);
				ii+=2;
			}
		} else { 
			Market.log.info("Market Plugin attempted to process transaction, but item data not present.");
		}
		
	}
	
	// Calculates server store prices based on market price and markup.
	public void UpdateServerStorePrice(MaterialData itemMatData) {
		
		if (!(ItemData.ssRatio.containsKey(itemMatData))) {return;}
		
		double marketPrice = Market.publicActions.MarketPrice(itemMatData);
		if (marketPrice == -1) {
			Market.log.info("No data for item passed to ServerStore.");
			ItemData.ssSellPrice.put(itemMatData, (double) 0);
			ItemData.ssBuyPrice.put(itemMatData, (double) 0);
			ItemData.ssBundleSize.put(itemMatData, 0);
		} else {
			int bundleSize;
			double ratio = ItemData.ssRatio.get(itemMatData);
			double markup = (ratio - 1)/(ratio + 1);
			if (marketPrice < 200/itemMatData.getItemType().getMaxStackSize()) {
				bundleSize = itemMatData.getItemType().getMaxStackSize();
			} else {
				bundleSize = 1;
			}
			ItemData.ssBundleSize.put(itemMatData, bundleSize);
			ItemData.ssSellPrice.put(itemMatData, MarketRound(marketPrice*bundleSize*(1+markup))); 
			ItemData.ssBuyPrice.put(itemMatData, MarketRound(marketPrice*bundleSize*(1-markup)));
		}
	}
	
	// Rounds a price to a "nice-looking" number.
	public double MarketRound(double price) {
		if (price > 150) {
			price = (double) (Math.round((float) price/20))*20; // Reduces precision to 5 for numbers over 150;
		} else if (price > 30) {
			price = (double) (Math.round((float) price)); // Reduces precision to 1 for numbers over 30;
		} else if (price > 1.5) {
			price = (double) (Math.round((float) price*20))/20; // Reduces precision to 0.05 for numbers over 1.5;
		} else {
			price = (double) (Math.round((float) price*100))/100; // Reduces precision to 0.01 for all numbers.
		}
		return price;
	}

	// Handles the "/price" command, returning server store prices (or perhaps suggested retail value?)
	public void PriceQuery(Player player, MaterialData itemMatData, String[] args) {
		
		if (args.length>0) {
			String queryName = args[0];
			int ii = 1;
			while (ii < args.length) {
				queryName += " " + args[ii];
			}
			Boolean match = false;
			String possibleMatches = "";
			String matchName;
			for (MaterialData matData : ItemData.name.keySet()) {
				matchName = ItemData.name.get(matData);
				if (matchName.equalsIgnoreCase(queryName)) {
					itemMatData = matData;
					match = true;
					break;
				}
				for (String arg : args) {
					if (matchName.toLowerCase().contains(arg.toLowerCase())) {
						possibleMatches += matchName + ", ";
					}
				}
			}
			if (!match) {
				possibleMatches = possibleMatches.substring(0, possibleMatches.length()-2);
				MarketMessage(player, ("No exact matches found for: '" + queryName + "'. Possible matches include:"));
				MarketMessage(player, (possibleMatches));
				return;
			}
		}
		
		UpdateServerStorePrice(itemMatData);
		
		if (!(ItemData.ssBundleSize.containsKey(itemMatData))) {
			MarketMessage(player, ("Item not sold at Server Store."));
			return;
		}
		if (ItemData.ssBundleSize.get(itemMatData).equals(0)) {
			MarketMessage(player, ("Item price error."));
			return;
		}
		
		String itemName = ItemData.name.get(itemMatData);
		String bundleSize = Integer.toString(ItemData.ssBundleSize.get(itemMatData));
		String sellPrice = Double.toString(ItemData.ssSellPrice.get(itemMatData));
		if (ItemData.ssBuyPrice.get(itemMatData) > 30) {
			sellPrice = sellPrice.replace(".0", "");
		} else {
			if (sellPrice.charAt(sellPrice.length()-2)==(".".charAt(0)))
			sellPrice = sellPrice.concat("0");
		}
		String buyPrice = Double.toString(ItemData.ssBuyPrice.get(itemMatData));
		if (ItemData.ssBuyPrice.get(itemMatData) > 30) {
			buyPrice = buyPrice.replace(".0", "");
		} else {
			if (buyPrice.charAt(buyPrice.length()-2)==(".".charAt(0)))
			buyPrice = buyPrice.concat("0");
		}
		
		if (ItemData.ssBundleSize.get(itemMatData).equals(1)) {
			MarketMessage(player, ("The Server Store sells " + itemName + " for $" + sellPrice + " each."));
			MarketMessage(player, ("The Server Store buys " + itemName + " for $" + buyPrice + " each."));
		} else {
			MarketMessage(player, ("The Server Store sells a stack of " + bundleSize + " " + itemName + 
					" for $" + sellPrice + "."));
			MarketMessage(player, ("The Server Store buys a stack of " + bundleSize + " " + itemName + 
					" for $" + buyPrice + "."));
		}
		
	}
	
	// Used instead of player.sendMessage. Fixes color.
	public void MarketMessage(Player player, String message) {
		for (String itemName : ItemData.name.values()) {
			if (message.contains(itemName)) {
				message = message.replaceAll(itemName, itemColor + itemName + messageColor);
			}
		}
		player.sendMessage(messageColor + message);
	}

}