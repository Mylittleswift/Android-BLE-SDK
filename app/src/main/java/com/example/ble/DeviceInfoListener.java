package com.example.ble;

public interface DeviceInfoListener {
	public void setNameCallBack();

	public void setFactoryModeCallBack();

	public void setMcuModeCallBack();

	public void getName(byte[] value);

	public void getVerSion(byte[] value);

	public void getAddress(byte[] value);

	public void getBattery(byte[] value);

}
