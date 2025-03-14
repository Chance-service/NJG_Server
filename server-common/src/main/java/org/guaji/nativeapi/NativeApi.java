package org.guaji.nativeapi;

public class NativeApi {
	public static native boolean initHawk();
	
	public static native boolean checkHawk();
	
	public static native boolean tickHawk();
	
	public static native boolean protocol(int type, int size, int reserve, int crc);
	
}
