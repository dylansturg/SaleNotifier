package edu.rosehulman.salenotifier.db;

import java.util.List;

import edu.rosehulman.salenotifier.settings.Setting;

public class SettingsSQLiteAdapter {

	public SettingsSQLiteAdapter() {
	}
	
	public List<Setting<?>> getAllSettings(){
		SettingDataAdapter settingSource = new SettingDataAdapter();
		List<Setting<?>> settings = settingSource.getAll(null, null, null);
		return settings;
	}
	
	public List<Setting<?>> getSettingsForTarget(String target){
		SettingDataAdapter settingSource = new SettingDataAdapter();
		List<Setting<?>> settings = settingSource.getAll(SettingDataAdapter.DB_KEY_TARGET + " = " + target, null, null);
		return settings;
	}
	
	

}
