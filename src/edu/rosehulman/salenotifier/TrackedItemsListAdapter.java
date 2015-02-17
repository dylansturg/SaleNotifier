package edu.rosehulman.salenotifier;

import java.util.HashSet;
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

public class TrackedItemsListAdapter extends ArrayAdapter<Item> {

	private HashSet<Long> mActivatedIds = new HashSet<Long>();

	public TrackedItemsListAdapter(Context context, List<Item> objects) {
		super(context, R.layout.listview_tracked_item, objects);
	}

	public void setActivated(boolean isActivated, long id) {
		if (isActivated) {
			mActivatedIds.add(id);
		} else {
			mActivatedIds.remove(id);
		}
	}

	public void clearAllActivated() {
		mActivatedIds.clear();
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Item item = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.listview_tracked_item, parent, false);
		}

		convertView.setActivated(mActivatedIds.contains(item.getId()));

		TextView title = (TextView) convertView
				.findViewById(R.id.tracked_item_title);
		TextView subtitle = (TextView) convertView
				.findViewById(R.id.tracked_item_subtitle);
		final ImageView image = (ImageView) convertView
				.findViewById(R.id.tracked_item_image);

		title.setText(item.getDisplayName());
		// TODO Implement price display
		subtitle.setText(item.getProductCode());

		image.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						image.getViewTreeObserver().removeOnPreDrawListener(
								this);

						int width = image.getMeasuredWidth();
						int height = image.getMeasuredHeight();

						new DownloadImageTask(image,
								getContext().getCacheDir(), width, height)
								.execute(item.getImageUrl());

						return true;
					}
				});

		return convertView;
	}

}
