package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ItemDatabaseContentActivity extends StorageActivity {
	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";

	private long mItemId;
	private Item mItem;

	private ArrayAdapter<ItemPrice> mListAdapter;
	private ListView mPricesList;
	private List<ItemPrice> mPrices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_database_content);

		Intent args = getIntent();
		if (!args.hasExtra(KEY_ITEM_ID)) {
			throw new IllegalStateException(
					"Attempt to create ItemDatabaseContentActivity without "
							+ KEY_ITEM_ID);
		}

		mItemId = args.getLongExtra(KEY_ITEM_ID, -1);
		mItem = itemSource.getItemById(mItemId);

		TextView title = (TextView) findViewById(R.id.item_database_name);
		title.setText(mItem.getDisplayName());

		TextView idView = (TextView) findViewById(R.id.item_database_id);
		idView.setText(Long.toString(mItemId));

		TextView productCode = (TextView) findViewById(R.id.item_database_product_code);
		productCode.setText(mItem.getProductCode());

		TextView image = (TextView) findViewById(R.id.item_database_image);
		image.setText(mItem.getImageUrl() != null ? mItem.getImageUrl()
				.toExternalForm() : "none");

		mPricesList = (ListView) findViewById(R.id.item_database_prices_list);

		mPrices = mItem.getPrices();
		if (mPrices != null) {
			mListAdapter = new ArrayAdapter<ItemPrice>(this,
					android.R.layout.simple_list_item_1, mPrices);
			mPricesList.setAdapter(mListAdapter);
		}

	}
}
