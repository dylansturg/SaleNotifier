package edu.rosehulman.salenotifier;

import me.dm7.barcodescanner.zbar.*;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class BarcodeScannerActivity extends Activity implements
		ZBarScannerView.ResultHandler {

	private ZBarScannerView mScannerView;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mScannerView = new ZBarScannerView(this); // Programmatically initialize
													// the scanner view
		setContentView(mScannerView); // Set the scanner view as the content
										// view
	}

	@Override
	public void onResume() {
		super.onResume();
		mScannerView.setResultHandler(this); // Register ourselves as a handler
												// for scan results.
		mScannerView.startCamera(); // Start camera on resume
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera(); // Stop camera on pause
	}

	@Override
	public void handleResult(Result rawResult) {
		// Do something with the result here
		Log.v(TrackedItemsActivity.LOG_TAG, rawResult.getContents()); // Prints scan results
		Log.v(TrackedItemsActivity.LOG_TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan
															// format (qrcode,
															// pdf417 etc.)
	}
}
