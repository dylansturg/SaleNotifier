package edu.rosehulman.salenotifier;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {
	private static long DownloadId = 0;
	private ImageView mTargetView;
	private File mCacheDir;

	public DownloadImageTask(ImageView target, File cacheDir) {
		mTargetView = target;
		mCacheDir = cacheDir;
	}
	
	private int getImageScale(int unscaledWidth, int unscaledHeight, int reqWidth, int reqHeight){
		int inSampleSize = 1;
		if (unscaledHeight > reqHeight || unscaledWidth > reqWidth) {

			final int halfHeight = unscaledHeight / 2;
			final int halfWidth = unscaledWidth / 2;

			// Calculate the largest inSampleSize value that is a power of 2
			// and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}
	
	private void prepStorageDir(){
		File imagesDir = new File(mCacheDir + "/images/");
		imagesDir.mkdir();
	}

	@Override
	protected Bitmap doInBackground(URL... params) {
		if (params.length == 0) {
			cancel(true);
		}

		Bitmap result = null;
		File tempImage = null;

		try {
			URL imageUrl = params[0];
			if (imageUrl == null) {
				return null;
			}

			prepStorageDir();
			tempImage = new File(mCacheDir.getPath() + "/images/"
					+ DownloadId++);
			tempImage.createNewFile();
			BufferedOutputStream bImageWriter = new BufferedOutputStream(new FileOutputStream(tempImage));

			InputStream imageInStream = imageUrl.openStream();
			final int bufferSize = 1024;
			byte[] imageBuffer = new byte[bufferSize];
			int readCount = 0;
			while((readCount = imageInStream.read(imageBuffer, 0, bufferSize)) > 0){
				bImageWriter.write(imageBuffer, 0, readCount);
			}
			imageInStream.close();
			bImageWriter.close();
			

			InputStream in;
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			in = new FileInputStream(tempImage);
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int reqHeight = 50;
			int reqWidth = 50;

			int unscaledWidth = o.outWidth;
			int unscaledHeight = o.outHeight;
			int inSampleSize = getImageScale(unscaledWidth, unscaledHeight, reqWidth, reqHeight);
			
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = inSampleSize;
			in = new FileInputStream(tempImage);
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, o2);
			in.close();
			
			return bitmap;

		} catch (Exception e) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to load image into view", e);
		} finally {
			if(tempImage != null){
				boolean didDelete = tempImage.delete();
				Log.d(TrackedItemsActivity.LOG_TAG, "Cache file removed? " + didDelete);
			}
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
