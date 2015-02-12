package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemCurrentActivity extends StorageActivity {

	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";

	private Item mItem;
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

	}

	private void displayBestItemPriceDetails() {
		TextView bestPrice = (TextView) findViewById(R.id.item_current_best_price);
		TextView bestSeller = (TextView) findViewById(R.id.item_current_best_seller);

		if (mCurrentPrices.size() > 0) {
			final ItemPrice bestItemPrice = mCurrentPrices.get(0);

			bestPrice.setText(getString(R.string.best_available_price_format,
					bestItemPrice.getPrice()));
			bestSeller.setText(getString(R.string.available_from_seller_format,
					bestItemPrice.getSellerName()));

			View bestPriceContainer = findViewById(R.id.item_current_best_price_container);
			bestPriceContainer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Uri link = Uri.parse(bestItemPrice.getBuyLocation());
						Intent openInBrowser = new Intent(Intent.ACTION_VIEW,
								link);
						startActivity(openInBrowser);
					} catch (Exception e) {
						Log.e(TrackedItemsActivity.LOG_TAG,
								"ItemCurrentActivity failed to launch browser with item buy location",
								e);
					}
				}
			});
		}
	}

	private void prepareCurrentPrices() {
		mCurrentPrices = mItem.getCurrentPrices();
		if (mCurrentPrices == null) {
			mCurrentPrices = new ArrayList<ItemPrice>();
		}

		// ItemPrice default compareTo does a lowest price based ordering
		Collections.sort(mCurrentPrices);

	}

	public void gotoBestSeller(View view) {
		Log.i(TrackedItemsActivity.LOG_TAG, "Executing gotoBestSeller");

	}

}
