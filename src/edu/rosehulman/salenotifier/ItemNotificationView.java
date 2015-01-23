package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.NotificationPredicate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class ItemNotificationView extends FrameLayout {

	private Spinner mEventType;
	private EditText mThreshold;
	private long mId = -1;

	private ArrayAdapter<NotificationPredicate> mEventAdapter;

	public ItemNotificationView(Context context,
			List<NotificationPredicate> predicates) {
		this(context, predicates, null, null, -1);
	}

	public ItemNotificationView(Context context,
			List<NotificationPredicate> predicates, String selectedPredicate,
			String threshold, long settingId) {
		super(context);
		
		mId = settingId;
		View content = LayoutInflater.from(context).inflate(R.layout.item_notification_view, this, false);
		addView(content);
		
		setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		mEventType = (Spinner) content.findViewById(R.id.item_notification_view_spinner);
		mThreshold = (EditText) content.findViewById(R.id.item_notification_view_threshold);
		if(threshold != null){
			mThreshold.setText(threshold);
		}
		
		ImageButton remover = (ImageButton) content.findViewById(R.id.item_notification_view_remover);
		remover.setTag(settingId);

		mEventAdapter = new ArrayAdapter<NotificationPredicate>(context,
				android.R.layout.simple_spinner_dropdown_item, predicates);

		mEventAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mEventType.setAdapter(mEventAdapter);
		
		if(selectedPredicate != null){
			for (NotificationPredicate notificationPredicate : predicates) {
				if(notificationPredicate.getPredicate().equalsIgnoreCase(selectedPredicate)){
					int position = mEventAdapter.getPosition(notificationPredicate);
					mEventType.setSelection(position);
					break;
				}
			}
		}
		
	}
	
	public long getNotificationId(){
		return mId;
	}
	
	public String getThresholdValue(){
		return mThreshold.getText().toString();
	}
	
	public String getNotificationPredicate(){
		return ((NotificationPredicate) mEventType.getSelectedItem()).getPredicate();
	}

}
