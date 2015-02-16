package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ItemCurrentActivity extends StorageActivity {

	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";
	public static final String KEY_ITEM = "KEY_ITEM";

	private Item mItem;
	private ItemPrice mBestPrice;
	private List<ItemPrice> mCurrentPrices;

	private ListView mPricesListView;
	private ListAdapter mPricesListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_current);

		Intent launchParams = getIntent();

		if (launchParams.hasExtra(KEY_ITEM_ID)) {
			long id = launchParams.getLongExtra(KEY_ITEM_ID, -1);
			mItem = itemSource.getItemById(id);
		} else if (launchParams.hasExtra(KEY_ITEM)) {
			mItem = launchParams.getParcelableExtra(KEY_ITEM);
		}

		if (mItem == null) {
			throw new IllegalStateException(
					"ItemCurrentActivity requires a valid Item to display, please include one in the Intent");
		}
		// Don't forget this one
		prepareCurrentPrices();

		TextView title = (TextView) findViewById(R.id.item_current_title);
		title.setText(mItem.getDisplayName());
		setTitle(mItem.getDisplayName());
		displayBestItemPriceDetails();

		mPricesListView = (ListView) findViewById(R.id.item_current_list);
		mPricesListAdapter = new ItemPriceListAdapter(this, mCurrentPrices);
		mPricesListView.setAdapter(mPricesListAdapter);

	}

	private void displayBestItemPriceDetails() {
		TextView bestPrice = (TextView) findViewById(R.id.item_current_best_price);
		TextView bestSeller = (TextView) findViewById(R.id.item_current_best_seller);

		if (mBestPrice != null) {

			bestPrice.setText(getString(R.string.best_available_price_format,
					mBestPrice.getPrice()));
			bestSeller.setText(getString(R.string.available_from_seller_format,
					mBestPrice.getSellerName()));

			View bestPriceContainer = findViewById(R.id.item_current_best_price_container);

			try {
				final Uri sellerLocation = Uri.parse(mBestPrice
						.getBuyLocation());
				bestPriceContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						Intent openInBrowser = new Intent(Intent.ACTION_VIEW,
								sellerLocation);
						startActivity(openInBrowser);
					}
				});

			} catch (Exception e) {
				Log.e(TrackedItemsActivity.LOG_TAG,
						"ItemCurrentActivity failed to launch browser with item buy location",
						e);
			}
		}
	}

	private void prepareCurrentPrices() {
		mCurrentPrices = mItem.getCurrentPrices();
		if (mCurrentPrices == null) {
			mCurrentPrices = new ArrayList<ItemPrice>();
		}

		// ItemPrice default compareTo does a lowest price based ordering
		Collections.sort(mCurrentPrices);

		if (mCurrentPrices.size() > 0) {
			mBestPrice = mCurrentPrices.get(0);
			mCurrentPrices.remove(mBestPrice);
		}
	}

	public void gotoBestSeller(View view) {
		Log.i(TrackedItemsActivity.LOG_TAG, "Executing gotoBestSeller");

	}

}
