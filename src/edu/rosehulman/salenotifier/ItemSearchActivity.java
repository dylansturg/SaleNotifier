package edu.rosehulman.salenotifier;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import edu.rosehulman.salenotifier.models.BarcodeResult;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

import edu.rosehulman.salenotifier.R;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ItemSearchActivity extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private static Map<CharSequence, Double> DistanceUnitConversions = new HashMap<CharSequence, Double>();
	static {
		DistanceUnitConversions.put("Miles", 1.0);
		DistanceUnitConversions.put("Kilometers", 1.60934);
		DistanceUnitConversions.put("Pixels", Double.POSITIVE_INFINITY);
		DistanceUnitConversions.put("Blue Whales", 67.0560387316);
		DistanceUnitConversions.put("Lightyears",
				1.70111428 * Math.pow(10, -13));
	}

	public static final String KEY_DISTANCE_UNIT = "KEY_DISTANCE_UNIT";
	public static final int RESULT_BARCODE_SCAN = 0x80085;
	private static final int REQUEST_SEARCH = 0x5a1e;
	protected static final String KEY_SEARCH_FINISHED = "KEY_SEARCH_FINISHED";

	private Spinner mDistanceUnitPicker;
	private ArrayAdapter<CharSequence> mDistanceAdapter;
	private EditText mDistance;
	private EditText mName;
	private EditText mProductCode;
	private Button mScanButton;
	private Button mSearchButton;

	private int mCurrentDistanceUnit = 0;
	private String mBarcodeType;

	private GoogleApiClient mGoogleApiClient;
	private Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_search);

		if (savedInstanceState != null) {
			mCurrentDistanceUnit = savedInstanceState.getInt(KEY_DISTANCE_UNIT,
					0);
		}
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

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(KEY_DISTANCE_UNIT, mCurrentDistanceUnit);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_search:
			searchForItem();
			break;
		case R.id.item_search_scan:
			scanBarcode();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case RESULT_BARCODE_SCAN:
			BarcodeResult result = data
					.getParcelableExtra(BarcodeScannerActivity.KEY_BARCODE_RESULT);
			mProductCode.setText(result.getContent());
			break;
		case REQUEST_SEARCH:
			boolean searchFinished = data.getBooleanExtra(KEY_SEARCH_FINISHED, false);
			if(searchFinished){
				finish();
			}
			break;
		}
	}

	private void scanBarcode() {
		Intent launchScanner = new Intent(this, BarcodeScannerActivity.class);
		startActivityForResult(launchScanner, RESULT_BARCODE_SCAN);
	}

	private void searchForItem() {
		ItemQueryConstraints searchQuery = buildSearchItem();

		Intent launchSearch = new Intent(this, SearchResultsActivity.class);
		launchSearch.putExtra(SearchResultsActivity.KEY_SEARCH_ITEM,
				searchQuery);
		startActivityForResult(launchSearch, REQUEST_SEARCH);
	}

	private ItemQueryConstraints buildSearchItem() {
		ItemQueryConstraints query = new ItemQueryConstraints();
		query.setName(mName.getText().toString());
		query.setProductCode(mProductCode.getText().toString());
		query.setProductCodeType(mBarcodeType);
		query.setSearchRadius(parseSearchRadius());
		query.setSearchLocation(mCurrentLocation);
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
		double currentMiles = (1 / DistanceUnitConversions.get(currentUnit))
				* currentDistance;
		double distanceConverted = DistanceUnitConversions.get(mDistanceAdapter
				.getItem(unit)) * currentMiles;
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

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// Ignore
		Log.d(TrackedItemsActivity.LOG_TAG, "Google APIs failed to connect");
	}

	@Override
	public void onConnected(Bundle arg0) {
		mCurrentLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (mCurrentLocation != null) {
			Log.d(TrackedItemsActivity.LOG_TAG, "Received a lastLocation at: "
					+ mCurrentLocation.getLatitude() + " (lat), "
					+ mCurrentLocation.getLongitude() + " (long");
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// Ignore
		Log.d(TrackedItemsActivity.LOG_TAG, "Google APIs connection suspended");
	}
}
