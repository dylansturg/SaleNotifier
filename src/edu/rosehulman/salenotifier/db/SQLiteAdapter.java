package edu.rosehulman.salenotifier.db;

import java.util.List;

import edu.rosehulman.salenotifier.IItemSourceAdapter;
import edu.rosehulman.salenotifier.Item;

public class SQLiteAdapter implements IItemSourceAdapter {
	
	private static SQLiteAdapter instance = new SQLiteAdapter();
	public static synchronized SQLiteAdapter getInstance(){
		return instance;
	}

	@Override
	public Item getItem(String productCode) {
		ItemDataAdapter itemDb = new ItemDataAdapter();
		List<Item> results = itemDb.getAll(ItemDataAdapter.DB_KEY_PRODUCT_CODE + "=" + productCode, null, null);
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}

	@Override
	public void saveItem(Item item) {
		ItemDataAdapter itemDb = new ItemDataAdapter();
		itemDb.update(item);
	}

	@Override
	public List<Item> getAllItems() {
		ItemDataAdapter itemDb = new ItemDataAdapter();
		return itemDb.getAll(null, null, null);
	}

}
