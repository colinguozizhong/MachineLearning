package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ArrayUtil{
	
	/**
	 * ��ͨ�����ͻ�ȡΨһ���ݣ����Ƿ���ò�Ʋ�֧������
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
	 * ����Ψһֵ�����ݺͳ��ִ��� key��Ψһֵ��value�����ִ���
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
	 * ����һ��HashMap �� key ���ظ������ݣ�value ������ԭ���������
	 * @param dataArray �������ظ����ݵ�����
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
	 * �����ַ��������Ψһֵ����
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
	 * ���ض�ά����Ψһֵ�����ݺͳ��ִ��� key��Ψһֵ��value�����ִ���
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
	 * ���ض�ά�����ַ��������Ψһֵ����
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
	
	public static double[] mean0(double[][] data) {
		double [] means = new double[data[0].length];
		for(int j=0;j<data[0].length;j++) {
			double xSum = 0;
			for(int i=0;i<data.length;i++) {
				xSum += data[i][j];
			}
			means[j] = xSum/data.length;
		}
		return means;
	}
	
	public static double mean(double[] data) {
		double mean = 0;
		double sum = 0;
		for(int j=0;j<data.length;j++) {
			sum += data[j];
		}
		return sum/data.length;
	}
	
	public static double[] var0(double[][] data) {
		double [] mean0 = mean0(data);
		double [] var0 = new double[data[0].length];
		for(int j=0;j<data[0].length;j++) {
			//mean(abs(x - x.mean())**2);
			double sumTemp = 0;
			for(int i=0;i<data.length;i++) {
				double temp = Math.pow(Math.abs(data[i][j] - mean0[j]),2);
				sumTemp += temp;
			}
			var0[j] = sumTemp/data.length;
		}
		return var0;
	}
}
