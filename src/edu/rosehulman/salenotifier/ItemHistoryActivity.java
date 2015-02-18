package edu.rosehulman.salenotifier;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.google.android.gms.internal.db;
import com.google.android.gms.internal.la;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

public class ItemHistoryActivity extends StorageActivity {

	
	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";
	LineChart mChart;
	Legend mLegend;
	
	private static int[] COLORS = new int[] {
		Color.RED,
		Color.BLUE,
		Color.GREEN,
		Color.MAGENTA,
		Color.CYAN,
		Color.YELLOW,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_history);
		
		long itemId = getIntent().getLongExtra(KEY_ITEM_ID, 0);
		if(itemId == 0)
			Log.e(TrackedItemsActivity.LOG_TAG, "Item id of 0 was passed to the activity");
		
		Item item = itemSource.getItemById(itemId);
		//List<ItemPrice> prices = item.getPrices();
		List<ItemPrice> prices = new ArrayList<ItemPrice>();
		prices.add(new ItemPrice(30.00d, "walmart.com", 2015, 1, 1));
		prices.add(new ItemPrice(30.00d, "walmart.com", 2015, 1, 2));
		prices.add(new ItemPrice(25.00d, "walmart.com", 2015, 1, 3));
		prices.add(new ItemPrice(25.00d, "walmart.com", 2015, 1, 4));
		
		prices.add(new ItemPrice(37.93d, "newegg.com", 2015, 1, 1));
		prices.add(new ItemPrice(35.00d, "newegg.com", 2015, 1, 2));
		prices.add(new ItemPrice(36.99d, "newegg.com", 2015, 1, 3));
		prices.add(new ItemPrice(36.99d, "newegg.com", 2015, 1, 4));

		
		// Create a mapping between our xAxis labels and their order (position)
		ArrayList<String> xAxis = getListOfDays(prices);
		HashMap<String, Integer> xAxisMapping = new HashMap<String, Integer>();
		for(int i = 0; i < xAxis.size(); i++)
			xAxisMapping.put(xAxis.get(i), i);
		
		// Create a DataSet for each seller
		int colorIndex = 0;
		HashMap<String, ArrayList<ItemPrice>> sellerMap = partitionBySeller(prices);
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		for(String seller : sellerMap.keySet()) {
			List<ItemPrice> fromSeller = sellerMap.get(seller);
			ArrayList<Entry> entries = new ArrayList<Entry>();
			HashMap<String, ArrayList<ItemPrice>> byDate = partitionByDay(fromSeller);
			for(String dateKey : byDate.keySet()) {
				ItemPrice lowest = byDate.get(dateKey).get(0);
				Log.d("SNL", "seller: " + seller + ", p: " + lowest.getPrice() + ", d: " + dateKey + ", dk: " + xAxisMapping.get(dateKey));
				entries.add(new Entry((float)lowest.getPrice(), xAxisMapping.get(dateKey)));
			}
			LineDataSet dataSet = new LineDataSet(entries, seller);
			dataSet.setCircleSize(4f);
			dataSet.setLineWidth(2f);
			dataSet.setCircleColor(COLORS[colorIndex % COLORS.length]);
			dataSet.setColor(COLORS[colorIndex++ % COLORS.length]);
			dataSet.setHighLightColor(Color.WHITE);
			dataSets.add(dataSet);
		}
	    
		// Create the labels for our xAxis
	    ArrayList<String> xVals = new ArrayList<String>();
	    for(int i = 0; i < xAxis.size(); i++)
	    	xVals.add(xAxis.get(i));
	    xVals.set(xVals.size()-1, ""); // last value is today
	    
	    LineData data = new LineData(xVals, dataSets);
	    
	    mChart = (LineChart) findViewById(R.id.chart);
	    for(int i = 0; i < data.getDataSetCount(); i++) {
	    	LineDataSet ds = data.getDataSetByIndex(i);
	    	for(int j = 0; j < ds.getEntryCount(); j++) {
	    		Entry e = ds.getEntryForXIndex(j);
	    		if(e != null)
	    			Log.d("SNL", "i=" + i + " j=" + j + " e.x=" + e.getXIndex() + " e.y=" + e.getVal());
	    	}
	    }
		mChart.setData(data);
		
		mChart.setDescription("");
		mChart.getLegend().setTextColor(Color.WHITE);
		mChart.getXLabels().setTextColor(Color.WHITE);
		mChart.getYLabels().setTextColor(Color.WHITE);
		
		mChart.getXLabels().setTextSize(14);
		mChart.getYLabels().setTextSize(14);
		mChart.getLegend().setTextSize(18);
		
		mChart.getLegend().setFormSize(10f);
		mChart.getLegend().setPosition(LegendPosition.BELOW_CHART_LEFT);
		
		mLegend = mChart.getLegend();
		mChart.setDrawLegend(false);
		
		Paint p = new Paint();
		p.setColor(Color.DKGRAY);
		mChart.setPaint(p, Chart.PAINT_GRID_BACKGROUND);
		mChart.setGridColor(Color.GREEN);
		mChart.setValueTextColor(Color.WHITE);
		mChart.setValueTextSize(12f);
		
		mChart.setStartAtZero(false);
		mChart.getXLabels().setPosition(XLabelPosition.BOTTOM);
		
	}
	
	private void showLegendDialog(ArrayList<LegendEntry> legend) {
		final ArrayList<LegendEntry> entries = legend;
		DialogFragment df = new DialogFragment() {
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Chart Legend");
				
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View dialogView = inflater.inflate(R.layout.legend_dialog, null);
				
				ListView list = ((ListView)dialogView.findViewById(R.id.list));
				ChartLegendAdapter adapter = new ChartLegendAdapter(ItemHistoryActivity.this, entries);
				list.setAdapter(adapter);
				
				builder.setView(dialogView);
				
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				return builder.create();
			}
		};
		df.show(getFragmentManager(), "legend");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.viewLegend) {
			ArrayList<LegendEntry> entries = new ArrayList<LegendEntry>();
			String[] labels = mChart.getLegend().getLegendLabels();
			for(int i = 0; i < labels.length; i++) {
				entries.add(new LegendEntry(COLORS[i % COLORS.length], labels[i]));
			}
			showLegendDialog(entries);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private HashMap<String, ArrayList<ItemPrice>> partitionBySeller(List<ItemPrice> prices) {
		HashMap<String, ArrayList<ItemPrice>> map = new HashMap<String, ArrayList<ItemPrice>>();
		for(ItemPrice ip : prices) {
			ArrayList<ItemPrice> list = new ArrayList<ItemPrice>();
			if(map.containsKey(ip.getSellerName()))
				list = map.get(ip.getSellerName());
			list.add(ip);
			map.put(ip.getSellerName(), list);
		}
		return map;
	}
	
	private ArrayList<String> getListOfDays(List<ItemPrice> prices) {
		Collections.sort(prices);
		ArrayList<String> days = new ArrayList<String>();
		for(ItemPrice ip : prices) {
			String dateString = dateToString(ip);
			if(!days.contains(dateString))
				days.add(dateString);
		}
		return days;
	}
	
	private String dateToString(ItemPrice ip) {
		return dateToString(ip.getDate().get(GregorianCalendar.MONTH), ip.getDate().get(GregorianCalendar.DAY_OF_MONTH));
	}
	
	private String dateToString(int month, int day) {
		String monthStr = new DateFormatSymbols().getMonths()[month - 1].substring(0, 3);
		return monthStr + " " + day;
	}
	
	private HashMap<String, ArrayList<ItemPrice>> partitionByDay(List<ItemPrice> prices) {
		HashMap<String, ArrayList<ItemPrice>> map = new HashMap<String, ArrayList<ItemPrice>>();
		for(ItemPrice ip : prices) {
			String dateKey = dateToString(ip);
			if(!map.containsKey(dateKey))
				map.put(dateKey, new ArrayList<ItemPrice>());
			ArrayList<ItemPrice> list = map.get(dateKey);
			list.add(ip);
		}
		for(String s : map.keySet())
			Collections.sort(map.get(s));
		return map;
	}
	
	private List<ItemPrice> getPricesForDay(List<ItemPrice> prices, ItemPrice itemPrice) {
		int month = itemPrice.getDate().get(GregorianCalendar.MONTH);
		int day = itemPrice.getDate().get(GregorianCalendar.DAY_OF_MONTH);
		List<ItemPrice> result = new ArrayList<ItemPrice>();
		for(ItemPrice ip : prices) {
			boolean dayMatch = ip.getDate().get(GregorianCalendar.DAY_OF_MONTH) == day;
			boolean monthMatch = ip.getDate().get(GregorianCalendar.MONTH) == month;
			if(dayMatch && monthMatch)
				result.add(ip);
		}
		return result;
	}
	
}
