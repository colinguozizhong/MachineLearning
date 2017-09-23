package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ArrayUtil{
	
	/**
	 * 想通过泛型获取唯一数据，但是泛型貌似不支持数组
	 * @param dataArray
	 * @return
	 */
	public static <T> T[] uniqueValsT(T[] dataArray){
		
		Map<T,Integer> uniqueVals = new HashMap<T,Integer>();
		for(int i=0;i<dataArray.length;i++){
			T key = dataArray[i];
			if(uniqueVals.containsKey(key)){
				uniqueVals.put(key, uniqueVals.get(key)+1);
			}else{
				uniqueVals.put(key, 1);
			}
		}
		T[] returnArray = (T[])new Object[uniqueVals.size()];
		int i = 0;
		Iterator<Map.Entry<T, Integer>> it = uniqueVals.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<T, Integer> entry = it.next();
			T key = entry.getKey();
			returnArray[i] = key;
			i++;
		}
		return returnArray;
	}
	
	/**
	 * 返回唯一值的数据和出现次数 key：唯一值，value：出现次数
	 * @param dataArray
	 * @return
	 */
	public static <T> Map<T,Integer> uniqueValsMap(T[] dataArray){
		
		Map<T,Integer> uniqueVals = new HashMap<T,Integer>();
		for(int i=0;i<dataArray.length;i++){
			T key = dataArray[i];
			if(uniqueVals.containsKey(key)){
				uniqueVals.put(key, uniqueVals.get(key)+1);
			}else{
				uniqueVals.put(key, 1);
			}
		}
		return uniqueVals;
	}
	
	/**
	 * 返回一个HashMap ， key 不重复的数据，value 数据在原数组的索引
	 * @param dataArray 不包含重复数据的数组
	 * @return
	 */
	public static <T> Map<T,Integer> uniqueArrayMapIndex(T[] dataArray){
		Map<T,Integer> uniqueVals = new HashMap<T,Integer>();
		for(int i=0;i<dataArray.length;i++){
			T key = dataArray[i];
			uniqueVals.put(key, i);
		}
		return uniqueVals;
	}
	
	/**
	 * 返回字符型数组的唯一值数组
	 * @param dataArray
	 * @return
	 */
	public static String[] uniqueVals(String[] dataArray){
		Map<String,Integer> uniqueMap = uniqueValsMap(dataArray);
		String[] returnArray = new String[uniqueMap.size()];
		int i = 0;
		Iterator<Map.Entry<String, Integer>> it = uniqueMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String key = entry.getKey();
			returnArray[i] = key;
			i++;
		}
		return returnArray;
	}
	
	/**
	 * 返回二维数组唯一值的数据和出现次数 key：唯一值，value：出现次数
	 * @param dataArray
	 * @return
	 */
	public static <T> Map<T,Integer> uniqueValsMap(T[][] dataArray){
		
		Map<T,Integer> uniqueVals = new HashMap<T,Integer>();
		for(int i=0;i<dataArray.length;i++){
			for(int j=0;j<dataArray[i].length;j++){
				T key = dataArray[i][j];
				if(uniqueVals.containsKey(key)){
					uniqueVals.put(key, uniqueVals.get(key)+1);
				}else{
					uniqueVals.put(key, 1);
				}
			}
			
		}
		return uniqueVals;
	}
	
	/**
	 * 返回二维数组字符型数组的唯一值数组
	 * @param dataArray
	 * @return
	 */
	public static String[] uniqueVals(String[][] dataArray){
		Map<String,Integer> uniqueMap = uniqueValsMap(dataArray);
		String[] returnArray = new String[uniqueMap.size()];
		int i = 0;
		Iterator<Map.Entry<String, Integer>> it = uniqueMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String key = entry.getKey();
			returnArray[i] = key;
			i++;
		}
		return returnArray;
	}
	
	public static <T> List<T> uniqueValsList(T[][] dataArray){
		Map<T,Integer> uniqueMap = uniqueValsMap(dataArray);
		List<T> returnList = new ArrayList<T>();
		int i = 0;
		Iterator<Map.Entry<T, Integer>> it = uniqueMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<T, Integer> entry = it.next();
			T key = entry.getKey();
			returnList.add(key);
		}
		return returnList;
	}
	
	public static Integer sumArray(Integer[] dataArray){
		int sum = 0;
		for(int i=0;i<dataArray.length;i++){
			sum = sum + dataArray[i];
		}
		return sum;
	}
	
	public static Integer[] addArray(Integer[] dataArray1, Integer[] dataArray2){
		
		Integer[] array = null;
		if(dataArray1.length >= dataArray2.length){
			array = new Integer[dataArray1.length];
			for(int i=0;i<dataArray1.length;i++){
				if(i<dataArray2.length){
					array[i] = dataArray1[i] + dataArray2[i];
				}else{
					array[i] = 0;
				}
			}
		}else{
			array = new Integer[dataArray2.length];
			for(int i=0;i<dataArray2.length;i++){
				if(i<dataArray1.length){
					array[i] = dataArray1[i] + dataArray2[i];
				}else{
					array[i] = 0;
				}
			}
		}
		
		return array;
	}
	
	public static double[][] transpositionArray(double[][] orginArray){
		int m = orginArray.length;
		int n = orginArray[0].length;
		
		double[][] tArray = new double[n][m];
		
		for(int i=0;i<n;i++) {
			for(int j=0;j<m;j++) {
				tArray[i][j] = orginArray[j][i];
			}
		}
		return tArray;
	}
	
}
