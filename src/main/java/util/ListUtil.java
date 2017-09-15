package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListUtil {
	
	/**
	 * 返回一个HashMap ， key 不重复的数据，value 数据在原数组的索引
	 * @param dataArray 不包含重复数据的数组
	 * @return
	 */
	public static <T> Map<T,Integer> uniqueListMapIndex(List<T> list){
		
		Map<T,Integer> uniqueVals = new HashMap<T,Integer>();
		for(int i=0;i<list.size();i++){
			T key = list.get(i);
			uniqueVals.put(key, i);
		}		
		return uniqueVals;
	}
	
	/**
	 * 返回二维List唯一值的数据和出现次数 key：唯一值，value：出现次数
	 * @param dataList
	 * @return
	 */
	public static <T> Map<T,Integer> uniqueValsMap(List<List<T>> dataList){
		
		Map<T,Integer> uniqueVals = new HashMap<T,Integer>();
		for(List<T> list:dataList){
			for(T key:list){
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
	 * 返回字符型二维列表的唯一值数组
	 * @param dataArray
	 * @return
	 */
	public static <T> List<T> uniqueValsList(List<List<T>> dataList){
		Map<T,Integer> uniqueMap = uniqueValsMap(dataList);
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
}
