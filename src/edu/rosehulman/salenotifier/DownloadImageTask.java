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
			if (imageUrl == null) {
				cancel(true);
			}
			InputStream in;

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			in = imageUrl.openStream();
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int reqHeight = 50;
			int reqWidth = 50;
			
			int unscaledWidth = o.outWidth;
			int unscaledHeight = o.outHeight;
			int inSampleSize = 1;
		    if (unscaledHeight > reqHeight || unscaledWidth > reqWidth) {

		        final int halfHeight = unscaledHeight / 2;
		        final int halfWidth = unscaledWidth / 2;

		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight
		                && (halfWidth / inSampleSize) > reqWidth) {
		            inSampleSize *= 2;
		        }
		    }

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = inSampleSize;
			in = imageUrl.openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, o2);
			in.close();
			return bitmap;

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
