package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;

import edu.rosehulman.salenotifier.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultsAdapter extends ArrayAdapter<Item> {

	public SearchResultsAdapter(Context context, List<Item> objects) {
		super(context, R.layout.listview_search_result, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View resultView;
		if (convertView == null) {
			resultView = LayoutInflater.from(getContext()).inflate(
					R.layout.listview_search_result, parent, false);
		} else {
			resultView = convertView;
		}

		final Item searchResult = getItem(position);

		((TextView) resultView.findViewById(R.id.search_result_title))
				.setText(searchResult.getDisplayName());
		((TextView) resultView.findViewById(R.id.search_result_subtitle))
				.setText(searchResult.getLowestPriceAsString());

		final ImageView image = (ImageView) resultView
				.findViewById(R.id.search_result_image);
		if (searchResult.getImageUrl() != null) {

			image.getViewTreeObserver().addOnPreDrawListener(
					new OnPreDrawListener() {
						@Override
						public boolean onPreDraw() {
							image.getViewTreeObserver()
									.removeOnPreDrawListener(this);
							int width = image.getMeasuredWidth();
							int height = image.getMeasuredHeight();
							new DownloadImageTask(image, getContext()
									.getCacheDir(), width, height)
									.execute(searchResult.getImageUrl());
							return false;
						}
					});
		} else {
			image.setImageResource(R.drawable.ic_action_error);
		}
		return resultView;
	}

}
