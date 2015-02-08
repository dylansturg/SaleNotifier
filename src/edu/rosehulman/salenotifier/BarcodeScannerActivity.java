package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Symbol;
import edu.rosehulman.salenotifier.models.BarcodeResult;
import me.dm7.barcodescanner.zbar.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BarcodeScannerActivity extends Activity implements
		ZBarScannerView.ResultHandler {
	public static final String KEY_BARCODE_RESULT = "KEY_BARCODE_RESULT";

	protected static List<String> AVAILABLE_CODE_TYPES = new ArrayList<String>();
	static {
		AVAILABLE_CODE_TYPES.add("UPC");
		AVAILABLE_CODE_TYPES.add("ISBN");
		AVAILABLE_CODE_TYPES.add("EAN");
	}

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
		String code = rawResult.getContents();
		BarcodeFormat type = rawResult.getBarcodeFormat();
		String resultType = stringifyBarcodeFormat(type);

		BarcodeResult barcodeResult = new BarcodeResult(code, resultType);

		result.putExtra(KEY_BARCODE_RESULT, barcodeResult);
		setResult(RESULT_OK, result);
		finish();
	}

	private static String stringifyBarcodeFormat(BarcodeFormat format) {
		switch (format.getId()) {
		case Symbol.ISBN10:
		case Symbol.ISBN13:
			return "ISBN";

		case Symbol.UPCA:
		case Symbol.UPCE:
			return "UPC";

		case Symbol.EAN8:
		case Symbol.EAN13:
			return "EAN";

		default:
			return null;
		}
	}
}
