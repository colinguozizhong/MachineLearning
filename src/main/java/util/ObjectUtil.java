package util;

public class ObjectUtil {
	public static String[] ObjectArrayToStringArray(Object[] oArray){
		String[] sArray = new String[oArray.length];
		for(int i=0;i<oArray.length;i++){
			sArray[i] = (String) oArray[i];
		}
		return sArray;
	}
}
