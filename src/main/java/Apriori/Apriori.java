package Apriori;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Apriori {
	
	public Map<String,Object> apriori(List<List<String>> dataSet, double minSupport){
		List<LinkedHashSet<String>> C1 = createC1(dataSet);
		
		List<LinkedHashSet<String>> D = new ArrayList<LinkedHashSet<String>>();
		for(List<String> dataSetItem:dataSet){
			// 按照从小到大排序
			Collections.sort(dataSetItem,new Comparator<String>(){
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
			LinkedHashSet<String> itemSet = new LinkedHashSet<String>();
			for(String str : dataSetItem){
				itemSet.add(str);
			}
			D.add(itemSet);
		}
		
		Map<String,Object> scanDRet = scanD(D, C1, minSupport);
		List<LinkedHashSet<String>> L1 = (List<LinkedHashSet<String>>) scanDRet.get("retList"); 
		Map<String,Double> supportData = (Map<String, Double>) scanDRet.get("supportData");
		
		List<List<LinkedHashSet<String>>> L = new ArrayList<List<LinkedHashSet<String>>>();
		L.add(L1);
		int k = 2;
		
		while(k-2<L.size() && L.get(k-2).size()>0){
			List<LinkedHashSet<String>> Ck = aprioriGen(L.get(k-2),k);
			
			Map<String,Object> scanDKRet = scanD(D, Ck, minSupport);
			List<LinkedHashSet<String>> Lk = (List<LinkedHashSet<String>>) scanDKRet.get("retList"); 
			Map<String,Double> supK = (Map<String, Double>) scanDKRet.get("supportData");
			
			Iterator<Map.Entry<String, Double>> entries = supK.entrySet().iterator();  
			  
			while (entries.hasNext()) {
			    Map.Entry<String, Double> entry = entries.next();  
			    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
			    
			    supportData.put(entry.getKey(), entry.getValue());
			} 

			L.add(Lk);
			k+=1;
			
			
		}
		
		System.out.println("apriori finish");
		
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("L", L);
		ret.put("supportData",supportData);
		return ret;
		
	}
	
	public List<LinkedHashSet<String>> createC1(List<List<String>> dataSet){
		List<String> C1 = new ArrayList<String>();
		
		for(List<String> transaction : dataSet){
			for(String item:transaction){
				if(!C1.contains(item)){
					C1.add(item);// 由于contains使用equal，所以可以在String时候判断相等
				}
			}
		}
		
		Collections.sort(C1,new Comparator<String>(){
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
		
		List<LinkedHashSet<String>> ret = new ArrayList<LinkedHashSet<String>>();
		for(String s:C1){
			LinkedHashSet<String> retItem = new LinkedHashSet<String>();
			retItem.add(s);
			ret.add(retItem);
		}
		
		System.out.println("C1 Finish");
		
		return ret;
	}
	
	public Map<String,Object> scanD(List<LinkedHashSet<String>> D,List<LinkedHashSet<String>> Ck,double minSupport){
		Map<String,Integer> ssCnt = new HashMap<String,Integer>();
		Map<String,LinkedHashSet<String>> keys = new HashMap<String,LinkedHashSet<String>>();
		for(LinkedHashSet<String> tid:D){
			for(LinkedHashSet<String> can:Ck){
				if(issubset(can,tid)){
					String keyStr = "";
					
					Iterator<String> linkedSetStringIt = can.iterator();  
			        while(linkedSetStringIt.hasNext()) {
			        	keyStr+=linkedSetStringIt.next() + "\t";  
			        }
			        if(ssCnt.containsKey(keyStr)){
			        	ssCnt.put(keyStr, ssCnt.get(keyStr)+1);
			        }else{
			        	ssCnt.put(keyStr, 1);
			        	keys.put(keyStr, can);
			        }
				}
			}
		}
		
		double numItems = D.size();
		List<LinkedHashSet<String>> retList = new ArrayList<LinkedHashSet<String>>();
		Map<String,Double> supportData = new HashMap<String,Double>();
		
		Iterator<Map.Entry<String, Integer>> entries = ssCnt.entrySet().iterator();  
		while (entries.hasNext()) {  
		    Map.Entry<String, Integer> entry = entries.next();  
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
		    double support = entry.getValue().doubleValue()/numItems;
		    
		    if(support > minSupport){
		    	retList.add(keys.get(entry.getKey()));
		    }
		    
		    supportData.put(entry.getKey(), support);
		} 
		
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("retList", retList);
		ret.put("supportData", supportData);
		
		System.out.println("scanD finish");
		return ret;
	}
	
	public List<LinkedHashSet<String>> aprioriGen(List<LinkedHashSet<String>> Lk,int k){
		List<LinkedHashSet<String>> retList = new ArrayList<LinkedHashSet<String>>();
		
		int lenLk = Lk.size();
		for(int i=0;i<lenLk;i++){
			for(int j=i+1;j<lenLk;j++){
				//关于上面程序 函数 aprioriGen 中的 k−2的说明：当利用 {0}, {1}, {2} 这些只含有一个元素的候选项集构建含有 2 个元素的候选项集时，就是两两合并得到 {0,1}, {0,2}, {1,2}; 如果进一步用包含连个元素的候选项集来构建包含 3 个元素的候选项集，同样两两合并，就会得到 {0,1,2},{0,1,2},{0,1,2}. 就是说会出现重复的项集，接下来就需要扫描三元素项集得到非重复结果，显然增加了计算时间。现在，如果比较 {0,1}, {0,2}, {1,2} 的第 0 个元素并只对第 0 个元素相同的集合求并，就会得到 {0,1,2}, 而且只有一次操作，这样就不需要遍历列表来寻找非重复值。
				List<String> L1 = new ArrayList<String>();
				LinkedHashSet<String> L1Set = Lk.get(i);
				Iterator<String> linkedSetStringIt1 = L1Set.iterator();
				int L1index = 0;
		        while(linkedSetStringIt1.hasNext() && L1index<k-2) {
		        	L1.add(linkedSetStringIt1.next());  
		        }
				
				List<String> L2 = new ArrayList<String>();
				LinkedHashSet<String> L2Set = Lk.get(j);
				Iterator<String> linkedSetStringIt2 = L2Set.iterator();
				int L2index = 0;
		        while(linkedSetStringIt2.hasNext() && L2index<k-2) {
		        	L2.add(linkedSetStringIt2.next());  
		        }
		        
		        // 按照从小到大排序
				Collections.sort(L1,new Comparator<String>(){
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
				
				// 按照从小到大排序
				Collections.sort(L2,new Comparator<String>(){
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
				
				boolean same = true;
				for(int index=0;index<k-2;index++){
					if(!L1.get(index).equals(L2.get(index))){
						same = false;
						break;
					}
				}
				if(same){
					LinkedHashSet<String> temp = new LinkedHashSet<String>();
					while(linkedSetStringIt1.hasNext() ) {
						temp.add(linkedSetStringIt1.next());  
			        }
					while(linkedSetStringIt2.hasNext() ) {
						temp.add(linkedSetStringIt2.next());  
			        }
					retList.add(temp);
				}
			}
		}
		
		return retList;
	}
	
	public List<Object[]> generateRules(List<List<LinkedHashSet<String>>> L,Map<String,Double> supportData,double minConf){
		
		List<Object[]> bigRuleList = new ArrayList<Object[]>(); 
		
		for(int i=1;i<L.size();i++){
			for(LinkedHashSet<String> freqSet:L.get(i)){
				List<LinkedHashSet<String>> H1 = new ArrayList<LinkedHashSet<String>>();
				Iterator<String> linkedSetStringIt = freqSet.iterator();
		        while(linkedSetStringIt.hasNext()) {
		        	String keyStr = linkedSetStringIt.next(); 
		        	LinkedHashSet<String> temp = new LinkedHashSet<String>();
		        	temp.add(keyStr);
		        	H1.add(temp);
		        }
		        if(i>1){
		        	rulesFromConseq(freqSet,H1,supportData,bigRuleList,minConf);
		        }else{
		        	calcConf(freqSet,H1,supportData,bigRuleList,minConf);
		        }
			}
		}
		
		return bigRuleList;
	}
	
	public List<LinkedHashSet<String>> calcConf(LinkedHashSet<String> freqSet,List<LinkedHashSet<String>> H,
			Map<String,Double> supportData,List<Object[]> brl,double minConf){
		List<LinkedHashSet<String>> prunedH = new ArrayList<LinkedHashSet<String>>();
		
		for(LinkedHashSet<String> conseq:H){
			LinkedHashSet<String> freqSetLeft = new LinkedHashSet<String>();
			
			Iterator<String> linkedSetStringIt = freqSet.iterator();
	        while(linkedSetStringIt.hasNext()) {
	        	String keys = linkedSetStringIt.next();
	        	if(!conseq.contains(keys)){
	        		freqSetLeft.add(keys);
	        	}
	        }
	        
	        List<String> freqSetList = new ArrayList<String>();
			Iterator<String> linkedSetStringIt1 = freqSet.iterator();
	        while(linkedSetStringIt1.hasNext()) {
	        	freqSetList.add(linkedSetStringIt1.next());  
	        }
			
			List<String> freqSet_conseqList = new ArrayList<String>();
			Iterator<String> linkedSetStringIt2 = freqSetLeft.iterator();
	        while(linkedSetStringIt2.hasNext()) {
	        	freqSet_conseqList.add(linkedSetStringIt2.next());  
	        }
	        
	        List<String> conseqList = new ArrayList<String>();
			Iterator<String> linkedSetStringIt3 = conseq.iterator();
	        while(linkedSetStringIt3.hasNext()) {
	        	conseqList.add(linkedSetStringIt3.next());  
	        }
	        
	        // 按照从小到大排序
			Collections.sort(freqSetList,new Comparator<String>(){
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
			String freqSetStr = "";
			for(String t:freqSetList){
				freqSetStr+=t+"\t";
			}
			
			// 按照从小到大排序
			Collections.sort(freqSet_conseqList,new Comparator<String>(){
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
			
			String freqSet_conseqStr = "";
			for(String t:freqSet_conseqList){
				freqSet_conseqStr+=t+"\t";
			}
			
			// 按照从小到大排序
			Collections.sort(conseqList,new Comparator<String>(){
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
			
			String conseqStr = "";
			for(String t:conseqList){
				conseqStr+=t+"\t";
			}
			
			double conf = supportData.get(freqSetStr)/supportData.get(freqSet_conseqStr);
			if(conf>=minConf){
				System.out.println(freqSet_conseqStr.trim()+" --> "+conseqStr.trim()+" conf: "+conf);
				Object[] tempObj = new Object[3];
				tempObj[0] = freqSet_conseqStr;
				tempObj[1] = conseqStr;
				tempObj[2] = conf;
				brl.add(tempObj);
				
				prunedH.add(conseq);
			}
		}
		return prunedH;
	}
	
	public void rulesFromConseq(LinkedHashSet<String> freqSet,List<LinkedHashSet<String>> H,
			Map<String,Double> supportData,List<Object[]> brl,double minConf){
		int m = H.get(0).size();
		if(freqSet.size()>(m+1)){
			List<LinkedHashSet<String>> Hmp1 = aprioriGen(H,m+1);
			Hmp1 = calcConf(freqSet,Hmp1,supportData,brl,minConf);
			
			if(Hmp1.size()>1){
				rulesFromConseq(freqSet,Hmp1,supportData,brl,minConf);
			}
		}
	}
	
	
	public boolean issubset(LinkedHashSet<String> sub,LinkedHashSet<String> all){
		boolean retFlag = true;
		Iterator<String> linkedSetStringIt = sub.iterator();  
        while(linkedSetStringIt.hasNext()) {
        	if(!all.contains(linkedSetStringIt.next())){
        		retFlag = false;
        	}
        } 
		return retFlag;
	}
}
