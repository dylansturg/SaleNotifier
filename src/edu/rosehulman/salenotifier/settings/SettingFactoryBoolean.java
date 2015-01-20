package edu.rosehulman.salenotifier.settings;

public class SettingFactoryBoolean extends SettingFactory {

	public SettingFactoryBoolean(){
		
	}
	
	/**
	 * Examine the first byte of the array, 1 -> true, 0 -> false.  No value yields false.
	 */
	@Override
	protected Setting<?> buildSettingForValue(byte[] value) {
		Setting<Boolean> result = new Setting<Boolean>();
		if(value != null && value.length > 0){
			result.setValue(value[0] == 1);
		} else {
			result.setValue(false);
		}
		return result;
	}

	@Override
	protected byte[] createBlobForSetting(Object value) {
		byte[] result = new byte[1];
		try{
			Boolean realVal = (Boolean)value;
			result[0] = (byte) (realVal ? 1 : 0);
		} catch(ClassCastException exp){
			result[0] = (byte) 0;
		}
		return result;
	}

}
