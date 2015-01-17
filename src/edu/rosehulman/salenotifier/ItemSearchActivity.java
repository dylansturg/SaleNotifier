package edu.rosehulman.salenotifier;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ItemSearchActivity extends Activity implements OnClickListener {

	private static Map<CharSequence, Double> DistanceUnitConversions = new HashMap<CharSequence, Double>();
	static {
		DistanceUnitConversions.put("Miles", 1.0);
		DistanceUnitConversions.put("Kilometers", 1.60934);
		DistanceUnitConversions.put("Pixels", Double.POSITIVE_INFINITY);
		DistanceUnitConversions.put("Blue Whales", 67.0560387316);
		DistanceUnitConversions.put("Lightyears",
				1.70111428 * Math.pow(10, -13));
	}

	private Spinner mDistanceUnitPicker;
	private ArrayAdapter<CharSequence> mDistanceAdapter;
	private EditText mDistance;
	private EditText mName;
	private EditText mProductCode;
	private Button mScanButton;
	private Button mSearchButton;

	private int mCurrentDistanceUnit = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_search);

		mDistance = (EditText) findViewById(R.id.item_search_distance);

		mDistanceUnitPicker = (Spinner) findViewById(R.id.item_search_distance_unit);
		mDistanceAdapter = ArrayAdapter.createFromResource(this,
				R.array.search_distance_units,
				android.R.layout.simple_spinner_item);
		mDistanceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDistanceUnitPicker.setAdapter(mDistanceAdapter);
		mDistanceUnitPicker
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (!updateDistanceFieldToUnit(position)) {
							mDistanceUnitPicker
									.setSelection(mCurrentDistanceUnit);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		mName = (EditText) findViewById(R.id.item_search_name);
		mProductCode = (EditText) findViewById(R.id.item_search_upc);
		mScanButton = (Button) findViewById(R.id.item_search_scan);
		mScanButton.setOnClickListener(this);
		mSearchButton = (Button) findViewById(R.id.item_search);
		mSearchButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_search:
			searchForItem();
			break;
		case R.id.item_search_scan:
			Toast.makeText(this, "Scanning your barcode!", Toast.LENGTH_LONG)
					.show();
			break;
		}
	}
	
	private void searchForItem(){
		ItemQueryConstraints searchQuery = buildSearchItem();
		
		Intent launchSearch = new Intent(this, SearchResultsActivity.class);
		launchSearch.putExtra(SearchResultsActivity.KEY_SEARCH_ITEM, searchQuery);
		startActivity(launchSearch);
		finish();
	}

	private ItemQueryConstraints buildSearchItem() {
		ItemQueryConstraints query = new ItemQueryConstraints();
		query.setName(mName.getText().toString());
		query.setProductCode(mProductCode.getText().toString());
		query.setSearchRadius(parseSearchRadius());
		return query;
	}

	private double parseSearchRadius() {
		return parseSearchRadius(0);
	}

	private double parseSearchRadius(int unit) {
		CharSequence currentUnit = mDistanceAdapter
				.getItem(mCurrentDistanceUnit);
		double currentDistance = Double.parseDouble(mDistance.getText()
				.toString());
		double currentMiles = (1 / DistanceUnitConversions.get(currentUnit)) * currentDistance;
		double distanceConverted = DistanceUnitConversions.get(mDistanceAdapter.getItem(unit)) * currentMiles;
		return distanceConverted;
	}

	private boolean updateDistanceFieldToUnit(int unitPosition) {
		double distanceConverted = parseSearchRadius(unitPosition);
		if (Double.isNaN(distanceConverted)
				|| Double.isInfinite(distanceConverted)) {
			CharSequence unit = mDistanceAdapter.getItem(unitPosition);
			Toast.makeText(
					this,
					getString(R.string.bad_distance_unit_message,
							unit.toString()), Toast.LENGTH_LONG).show();
			return false;
		}
		mDistance.setText("" + distanceConverted);
		mCurrentDistanceUnit = unitPosition;
		return true;
	}
}
