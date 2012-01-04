package me.wooskie.market;
// Run at intervals, performing price normalization and storing data for market history logs.

import java.util.Calendar;

import org.bukkit.material.MaterialData;

public class MarketMaintenance {
	
	public static Long lastRecord = Calendar.getInstance().getTimeInMillis();
	public static Long recordInterval = 86400000L; //one day in milliseconds
	
	public void PriceStability() {
		
		Double woodenPlanksPrice = Market.publicActions.MarketPrice(new MaterialData(5));
		Double cobblePrice = Market.publicActions.MarketPrice(new MaterialData(4));
		Double sandPrice = Market.publicActions.MarketPrice(new MaterialData(12));
		
		Double baseIndex = (woodenPlanksPrice + cobblePrice + sandPrice)/3;
		Double targetBaseIndex = 0.25D;
		
		Double marketMultiplier = targetBaseIndex/baseIndex;
		
		for (MaterialData matData : ItemData.marketPrice.keySet()) {
			Double oldMarketPrice = ItemData.marketPrice.get(matData);
			Double newMarketPrice = oldMarketPrice*marketMultiplier;
			ItemData.marketPrice.put(matData, newMarketPrice);
		}
		
	}
	
	public void SaveAllData() {
		
		Market.fileIO.SaveMarketPrices();
		Market.fileIO.SaveServerStoreRatios();
		Market.fileIO.SaveTransactionLog();
		
	}

	public void RecordKeeping() {
		
		Long currentTime = Calendar.getInstance().getTimeInMillis();
		if ((currentTime - lastRecord) > recordInterval) { 
			
			lastRecord = currentTime;
			for (MaterialData matData : ItemData.marketPriceHistory.keySet()) {
				Double[] itemPriceHistory = ItemData.marketPriceHistory.get(matData);
				Double[] newItemPriceHistory = new Double[itemPriceHistory.length];
				newItemPriceHistory[0] = ItemData.marketPrice.get(matData);
				int ii = 1;
				while (ii<newItemPriceHistory.length) {
					newItemPriceHistory[ii] = itemPriceHistory[ii-1];
					ii++;
				}
				ItemData.marketPriceHistory.put(matData, newItemPriceHistory);
			}
			Market.fileIO.SaveMarketPriceHistory();
		}
	
	}
	
}
