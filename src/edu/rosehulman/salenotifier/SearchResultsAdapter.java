package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		if(convertView == null){
			resultView = LayoutInflater.from(getContext()).inflate(R.layout.listview_search_result, parent, false);
		} else {
			resultView = convertView;
		}
		
		Item searchResult = getItem(position);
		
		((TextView)resultView.findViewById(R.id.search_result_title)).setText(searchResult.getDisplayName());
		((TextView)resultView.findViewById(R.id.search_result_subtitle)).setText(searchResult.getLowestPriceAsString());
		
		ImageView image = (ImageView)resultView.findViewById(R.id.search_result_image);
		new DownloadImageTask(image, getContext().getCacheDir()).execute(searchResult.getImageUrl());
		
		return resultView;
	}

}
