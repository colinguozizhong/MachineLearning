package FPgrowth;

import java.util.List;

import util.MD5Util;

public class FPGrowth {
	
	public static <T> String frozenset(List<T> dataList) {
		String md5;
		String orginVal = "";
		for(int i=0;i<dataList.size();i++){
			T key = dataList.get(i);
			orginVal = orginVal + key.toString() + "\r\n";
		}	
		return MD5Util.MD5(orginVal);
	}
	
}
