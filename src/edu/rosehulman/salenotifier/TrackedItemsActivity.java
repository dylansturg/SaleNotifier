package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TrackedItemsActivity extends Activity {

	public static final String LOG_TAG = "SNL";
	
	private IItemSourceAdapter itemSource;
	
	private ListView listView;
	private ListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracked_items);

		initPersistentStorage();
		itemSource = new SQLiteAdapter();
		
		listView = (ListView)findViewById(R.id.tracked_items_list);
		List<Item> items = itemSource.getAllItems();
		listAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(listAdapter);
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
			launchSearch();
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
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				View v = getLayoutInflater().inflate(R.layout.dialog_create_item, null, false);
				builder.setView(v);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog diag = (AlertDialog)dialog;
						String name = ((EditText)diag.findViewById(R.id.dialog_create_item_name)).getText().toString();
						String productCode = ((EditText)diag.findViewById(R.id.dialog_create_item_product_code)).getText().toString();
						String image = ((EditText)diag.findViewById(R.id.dialog_create_item_image)).getText().toString();
						
						Item item = new Item();
						item.setDisplayName(name);
						item.setProductCode(productCode);
						item.setImageUrl(image);
						
						itemSource.saveItem(item);
						
						updateItemList();
					}
				});
				
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				});
				
				return builder.create();
			}
		};
		tempSearch.show(getFragmentManager(), "temp_search_dialog");
	}
	
	private void updateItemList(){
		List<Item> items = itemSource.getAllItems();
		listAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(listAdapter);
	}

	private void initPersistentStorage() {
		SaleNotifierSQLHelper.init(this);
	}
}
