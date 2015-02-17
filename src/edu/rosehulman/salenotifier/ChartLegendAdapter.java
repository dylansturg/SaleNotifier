package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ChartLegendAdapter extends ArrayAdapter<LegendEntry> {

	public ChartLegendAdapter(Context context, List<LegendEntry> objects) {
		super(context, R.layout.listitem_legend, objects);
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View resultView;
		if(convertView == null){
			resultView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_legend, parent, false);
		} else {
			resultView = convertView;
		}
		LegendEntry searchResult = getItem(position);
		((Button)resultView.findViewById(R.id.colorBtn)).setBackgroundColor(searchResult.color);
		((TextView)resultView.findViewById(R.id.legendLabel)).setText(searchResult.label);
		
		return resultView;
	}

}
