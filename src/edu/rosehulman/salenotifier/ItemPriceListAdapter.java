package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.ItemPrice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemPriceListAdapter extends ArrayAdapter<ItemPrice> {

	public ItemPriceListAdapter(Context context, List<ItemPrice> objects) {
		super(context, R.layout.listview_itemprice, R.id.itemprice_price,
				objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		if (view == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.listview_itemprice, parent, false);
		}

		ItemPrice item = getItem(position);

		TextView price = (TextView) view.findViewById(R.id.itemprice_price);
		TextView seller = (TextView) view.findViewById(R.id.itemprice_seller);

		price.setText(getContext().getString(R.string.price_format,
				item.getPrice()));
		seller.setText(getContext().getString(R.string.itemprice_seller_format,
				item.getSellerName()));

		try {
			final Uri uri = Uri.parse(item.getBuyLocation());
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
					getContext().startActivity(launchBrowser);
				}
			});
		} catch (Exception e) {
			Log.e(TrackedItemsActivity.LOG_TAG,
					"Failed to set onclick for a ItemPrice view on ItemPriceListAdapter",
					e);
		}

		return view;
	}
}
