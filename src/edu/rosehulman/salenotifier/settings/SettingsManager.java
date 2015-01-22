package edu.rosehulman.salenotifier.settings;

import java.util.List;

import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.SettingsSQLiteAdapter;
import edu.rosehulman.salenotifier.models.Item;

public class SettingsManager {
	public static final String KEY_APP_SETTINGS = "app";
	
	private SettingsSQLiteAdapter sqlSource;
	
	private static SettingsManager instance;
	
	public static SettingsManager getManager(){
		if(instance == null){
			instance = new SettingsManager();
		}
		return instance;
	}
	
	public SettingsManager(){
		sqlSource = new SettingsSQLiteAdapter();
	}
	
	public Enumerable<Setting<?>> getSettingsForTarget(String target){
		return sqlSource.getSettingsForTarget(target);
	}
	
	public Enumerable<Setting<?>> getAppSettings(){
		return sqlSource.getSettingsForTarget(KEY_APP_SETTINGS);
	}
	
	public Enumerable<Setting<?>> getItemSettings(Item item){
		if(item == null){
			throw new IllegalArgumentException("Cannot get settings for null item.");
		}
		return sqlSource.getSettingsForTarget("" + item.getId());
	}
	
	public void saveSettings(List<Setting<?>> settings){
		if(settings == null){
			throw new IllegalArgumentException("Cannot save settings for null collection.");
		}
		sqlSource.saveAll(settings);
	}
	
	public void saveSetting(Setting<?> setting){
		if(setting == null){
			throw new IllegalArgumentException("Cannot save settings for null item.");
		}
		sqlSource.saveSetting(setting);
	}
	
	public void deleteSetting(Setting<?> setting){
		if(setting == null){
			throw new IllegalArgumentException("Cannot delete settings for null item.");
		}
		sqlSource.deleteSetting(setting);
	}
	
	public void deleteAll(List<Setting<?>> settings){
		if(settings == null){
			throw new IllegalArgumentException("Cannot delete settings for null collection.");
		}
		sqlSource.deleteAll(settings);
	}
	
}
