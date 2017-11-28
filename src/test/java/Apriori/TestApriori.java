package Apriori;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class TestApriori {
	
	public static Map<String,Object> loadDataSet() throws Exception{
		String voteFile = System.getProperty("user.dir")+"\\src\\test\\java\\Apriori\\votetransdata.txt";
		String meaningFile = System.getProperty("user.dir")+"\\src\\test\\java\\Apriori\\itemMeaning.txt";
		
		BufferedReader vfile = new BufferedReader(new InputStreamReader(new FileInputStream(voteFile)));
		BufferedReader mfile = new BufferedReader(new InputStreamReader(new FileInputStream(meaningFile)));
		
		Map<String,List<String>> transDict = new HashMap<String,List<String>>();
		String line = "";
		while ((line = vfile.readLine()) != null) {
			String [] infolist = line.trim().split("\t"); 
			List<String> itemlist = new ArrayList<String>();
			for(int i=1;i<infolist.length;i++){
				itemlist.add(infolist[i].trim());
			}
			// 按照从小到大排序
			Collections.sort(itemlist,new Comparator<String>(){
				public int compare(String o1, String o2) {
					if(o1.length() < o2.length()){
						return -1;
					}else if(o1.length() > o2.length()){
						return 1;
					}else{
						return o1.compareTo(o2);
					}
				}
			});
			transDict.put(infolist[0].trim(), itemlist);
		}
		vfile.close();
		
		List<String> itemMeaningList = new ArrayList<String>();
		while ((line = mfile.readLine()) != null) {
			itemMeaningList.add(line);
		}
		mfile.close();
		
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("transDict", transDict);
		ret.put("itemMeaning", itemMeaningList);
		
		return ret;
	}
	
	public static void main(String[] args) throws Exception {
		Map<String,Object> data = loadDataSet();
		Map<String,List<String>> transDict = (Map<String, List<String>>) data.get("transDict");
		List<String> itemMeaning = (List<String>) data.get("itemMeaning");
		
		List<List<String>> dataSet = new ArrayList<List<String>>();
		
		Iterator<Map.Entry<String,List<String>>> entries = transDict.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String,List<String>> entry = entries.next();
		    dataSet.add(entry.getValue());
		}  
		
		Apriori apriori = new Apriori();
		Map<String,Object> aprioriRet = apriori.apriori(dataSet,0.5);
		List<List<LinkedHashSet<String>>> L = (List<List<LinkedHashSet<String>>>) aprioriRet.get("L");
		Map<String,Double> suppData = (Map<String, Double>) aprioriRet.get("supportData");
		
		List<Object[]> rules = apriori.generateRules(L, suppData, 0.7);
		
		System.out.println("rules finish");
	}
}
