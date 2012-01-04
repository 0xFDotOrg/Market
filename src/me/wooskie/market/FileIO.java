package me.wooskie.market;
// Saves and loads market data to and from text files


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.bukkit.material.MaterialData;

public class FileIO {
	
	static private Market plugin;
	static private File marketPricesFile;
	static private File ssRatiosFile;
	static private File transactionLogFile;
	static private File marketPriceHistoryFile;
	static private File dataFolder;
	
	public FileIO(Market parent) {
		plugin = parent;
		dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
		marketPricesFile = new File(dataFolder, "marketPrices.txt");
		ssRatiosFile = new File(dataFolder, "ssRatios.txt");
		transactionLogFile = new File(dataFolder, "transactionLog.txt");
		marketPriceHistoryFile = new File(dataFolder, "marketPricesHistory.txt");
	}

	public void LoadMarketPrices() {
		
		String inputLine;
		
		if (marketPricesFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(marketPricesFile)));
				br.readLine();
				while ((inputLine = br.readLine()) != null) {
					String splits[] = inputLine.split(",",3);
					MaterialData itemMatData = new MaterialData(Integer.parseInt(splits[0]), Byte.parseByte(splits[1]));
					double itemMarketPrice = Double.parseDouble(splits[2]);
					ItemData.marketPrice.put(itemMatData, itemMarketPrice);
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void LoadMarketPriceHistory() {
		
		String inputLine;
		
		if (marketPriceHistoryFile.exists()) {
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(marketPriceHistoryFile)));
				inputLine = br.readLine();
				MarketMaintenance.lastRecord = Long.parseLong(inputLine);
				br.readLine();
				while ((inputLine = br.readLine()) != null) {
					String splits[] = inputLine.split(",");
					MaterialData itemMatData = new MaterialData(Integer.parseInt(splits[0]), Byte.parseByte(splits[1]));
					Double[] itemMarketPriceHistory = new Double[splits.length-2];
					int ii = 2;
					while (ii < splits.length) {
						itemMarketPriceHistory[ii-2] = Double.parseDouble(splits[ii]);
					}
					ItemData.marketPriceHistory.put(itemMatData, itemMarketPriceHistory);
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void LoadSSRatios() {
		
		String inputLine;
		
		if (ssRatiosFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ssRatiosFile)));
				br.readLine();
				while ((inputLine = br.readLine()) != null) {
					String splits[] = inputLine.split(",",3);
					MaterialData itemMatData = new MaterialData(Integer.parseInt(splits[0]), Byte.parseByte(splits[1]));
					double ssRatioFile = Double.parseDouble(splits[2]);
					ItemData.ssRatio.put(itemMatData, ssRatioFile);
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void LoadTransactionLog() {
		
		String inputLine;
		
		if (transactionLogFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(transactionLogFile)));
				br.readLine();
				while ((inputLine = br.readLine()) != null) {
					String splits[] = inputLine.split(",",3);
					MaterialData itemMatData = new MaterialData(Integer.parseInt(splits[0]), Byte.parseByte(splits[1]));
					int loggedTransactions = Integer.parseInt(splits[2]);
					ItemData.transactionLog.put(itemMatData, loggedTransactions);
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void SaveMarketPrices() {
		
		HashMap<MaterialData, Double> marketPrice = ItemData.marketPrice;
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(marketPricesFile)));
			bw.write("Format: [itemID], [itemDamage], [itemMarketPrice]");
			bw.newLine();
			for (MaterialData itemMatData : marketPrice.keySet()) {
				String itemID = Integer.toString(itemMatData.getItemTypeId());
				String itemDamage = Byte.toString(itemMatData.getData());
				String itemMarketPrice = Double.toString(marketPrice.get(itemMatData));
				bw.write(itemID + "," + itemDamage + "," + itemMarketPrice);
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void SaveMarketPriceHistory() {
		
		HashMap<MaterialData, Double[]> marketPriceHistory = ItemData.marketPriceHistory;
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(marketPriceHistoryFile)));
			bw.write(MarketMaintenance.lastRecord.toString());
			bw.newLine();
			bw.write("Format: [itemID], [itemDamage], [itemMarketPrice1], [itemMarketPrice2], ...");
			bw.newLine();
			for (MaterialData itemMatData : marketPriceHistory.keySet()) {
				String itemID = Integer.toString(itemMatData.getItemTypeId());
				String itemDamage = Byte.toString(itemMatData.getData());
				String itemMarketPriceHistory = "";
				for (Double price : marketPriceHistory.get(itemMatData)) {
					itemMarketPriceHistory += Double.toString(price) + ", ";
				}
				itemMarketPriceHistory = itemMarketPriceHistory.substring(0, itemMarketPriceHistory.length()-2);
				bw.write(itemID + "," + itemDamage + "," + itemMarketPriceHistory);
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void SaveServerStoreRatios() {
		
		HashMap<MaterialData, Double> ssRatio = ItemData.ssRatio;
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ssRatiosFile)));
			bw.write("Format: [itemID], [itemDamage], [itemServerStoreRatio]");
			bw.newLine();
			for (MaterialData itemMatData : ssRatio.keySet()) {
				String itemID = Integer.toString(itemMatData.getItemTypeId());
				String itemDamage = Byte.toString(itemMatData.getData());
				String itemSSRatio = Double.toString(ssRatio.get(itemMatData));
				bw.write(itemID + "," + itemDamage + "," + itemSSRatio);
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void SaveTransactionLog() {
		
		HashMap<MaterialData, Integer> transactionLog = ItemData.transactionLog;
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(transactionLogFile)));
			bw.write("Format: [itemID], [itemDamage], [itemLoggedTransactions]");
			bw.newLine();
			for (MaterialData itemMatData : transactionLog.keySet()) {
				String itemID = Integer.toString(itemMatData.getItemTypeId());
				String itemDamage = Byte.toString(itemMatData.getData());
				String loggedTransactions = Integer.toString(transactionLog.get(itemMatData));
				bw.write(itemID + "," + itemDamage + "," + loggedTransactions);
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
}