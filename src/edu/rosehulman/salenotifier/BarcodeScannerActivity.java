package edu.rosehulman.salenotifier;

import edu.rosehulman.salenotifier.models.BarcodeResult;
import me.dm7.barcodescanner.zbar.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class BarcodeScannerActivity extends Activity implements
		ZBarScannerView.ResultHandler {

	public static final String KEY_BARCODE_RESULT = "KEY_BARCODE_RESULT";

	private ZBarScannerView mScannerView;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mScannerView = new ZBarScannerView(this);
		setContentView(mScannerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		mScannerView.setResultHandler(this);
		mScannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera();
	}

	@Override
	public void handleResult(Result rawResult) {
		Log.v(TrackedItemsActivity.LOG_TAG, rawResult.getContents()); 
		Log.v(TrackedItemsActivity.LOG_TAG, rawResult.getBarcodeFormat()
				.getName());

		Intent result = new Intent();
		BarcodeResult barcodeResult = new BarcodeResult(
				rawResult.getContents(), rawResult.getBarcodeFormat().getName());
		
		result.putExtra(KEY_BARCODE_RESULT, barcodeResult);
		setResult(RESULT_OK, result);
		finish();
	}
}
