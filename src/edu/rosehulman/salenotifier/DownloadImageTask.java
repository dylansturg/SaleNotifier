package edu.rosehulman.salenotifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {
	private ImageView mTargetView;

	public DownloadImageTask(ImageView target) {
		mTargetView = target;
	}

	@Override
	protected Bitmap doInBackground(URL... params) {
		if (params.length == 0) {
			cancel(true);
		}

		Bitmap result = null;

		try {
			URL imageUrl = params[0];
			if(imageUrl == null){
				cancel(true);
			}
			InputStream in = imageUrl.openStream();
			result = BitmapFactory.decodeStream(in);
			in.close();

		} catch (IOException e) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to load image into view", e);
		}
		return result;

	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (mTargetView != null && result != null) {
			mTargetView.setImageBitmap(result);
		} else {
			mTargetView.setImageResource(R.drawable.ic_action_error);
		}
	}
}
