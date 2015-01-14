package edu.rosehulman.salenotifier;

import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TrackedItemsActivity extends Activity {

	public static final String LOG_TAG = "SNL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracked_items);

		initPersistentStorage();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracked_items, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_search:
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void launchSearch(){
		DialogFragment tempSearch = new DialogFragment(){
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				return super.onCreateDialog(savedInstanceState);
			}
		};
		tempSearch.show(getFragmentManager(), "temp_search_dialog");
	}

	private void initPersistentStorage() {
		SaleNotifierSQLHelper.init(this);
	}
}
