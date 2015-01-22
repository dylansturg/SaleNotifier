package edu.rosehulman.salenotifier;

import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import android.app.Activity;
import android.os.Bundle;

public class StorageActivity extends Activity {
	
	protected SQLiteAdapter itemSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initPersistentStorage();
		itemSource = new SQLiteAdapter();
	}
	
	private void initPersistentStorage() {
		if (!SaleNotifierSQLHelper.isInit()) {
			SaleNotifierSQLHelper.init(this);
		}
	}

}
