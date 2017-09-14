package DecisionTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DecisionTree {
	
	/**
	 * ������ũ��
	 * @param dataSet
	 * @return
	 */
	public double calcShannonEnt(String[][] dataSet){
		
		// ������ݼ��Ĵ�С
		int numEntries = dataSet.length;
		
		Map<String,Integer> labelCounts = new HashMap<String,Integer>();
		
		// �������ݼ�
		for(int i=0;i<numEntries;i++){
			String [] featVec = dataSet[i];
			if(featVec!=null && featVec.length>0){
				// ��ȡ�����е����ͣ�������Ϊҽ���Ƽ��������۾�����
				String currentLabel = featVec[featVec.length-1].trim();
				// ����������۾������ݼ��г��ֵĴ���
				if(labelCounts.containsKey(currentLabel)){
					labelCounts.put(currentLabel, (Integer)labelCounts.get(currentLabel)+1);
				}else{
					labelCounts.put(currentLabel, 1);
				}
			}
		}
		
		// ��ʼ����ũ��
		double shannonEnt = 0.0;
		
		// ���������۾��������ֵ������ũ��
		Iterator<Map.Entry<String, Integer>> it = labelCounts.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
			// ����P35�ļ�����ũ�صĹ�ʽ
			double prob = entry.getValue()*1.0/numEntries;
			shannonEnt -= prob*(Math.log(prob)/Math.log(2));// log base 2
			System.out.println(shannonEnt);
		}
		
		return shannonEnt;
	}
	
	/**
	 * �������ݼ�
	 * @param dataSet
	 * @param axis
	 * @param value
	 * @return
	 */
	public String[][] splitDataSet(String[][] dataSet, int axis, String value){
		String [][] retDataSet = new String[0][0];
		if(dataSet.length<1){
			return retDataSet;
		}
		
		List<String[]> retDataList = new ArrayList<String[]>();
		// �������ݼ�
		for(int i=0;i<dataSet.length;i++){
			String [] featVec = dataSet[i];
			// �жϵ�ǰ���ֵ�������Ӧ��ֵ�Ƿ�����Ҫ���ص�ֵ
			if(value.equals(featVec[axis])){
				// ����ǰ����������б�featVec�����ع����
				String [] reducedFeatVec = new String[featVec.length - 1];
				//http://blog.csdn.net/u012506661/article/details/52757069
				System.arraycopy(featVec,0,reducedFeatVec,0,axis);
				System.arraycopy(featVec,axis+1,reducedFeatVec,axis,featVec.length - axis - 1);
				retDataList.add(reducedFeatVec);
			}
		}
		retDataSet = new String[retDataList.size()][dataSet[0].length];
		for(int i=0;i<retDataList.size();i++){
			retDataSet[i] = retDataList.get(i);
		}
		return retDataSet;
	}
	
	public int chooseBestFeatureToSplit(String[][] dataSet){
		
		// �������ֵ��������������ݼ������һ���Ƿ��಻������
		int numFeatures = dataSet[0].length-1;
		
		// �������ݼ�����ũ��
		double baseEntropy = calcShannonEnt(dataSet);
		
		// ��ʼ��
		double bestInfoGain = 0.0;
		int bestFeature = -1;
		
		// ������������
		for(int i=0;i<numFeatures;i++){
			Map<String,Integer> uniqueVals = new HashMap<String,Integer>();
			// ����ȡi�е�ȥ�ص�����ֵ
			for(int j=0;j<dataSet.length;j++){
				String value = dataSet[j][i];
				
				// Ҳ����ʹ��List.contains,���ݶ��ʱ��Ч��ûHashMap��
				if(!uniqueVals.containsKey(value)){
					uniqueVals.put(value, 1);
				}
			}
			
			double newEntropy = 0.0;
			
			// ѭ��ʹ�ø�����ֵ��Ϊ�����������ݼ����ֳ���
			Iterator<Map.Entry<String, Integer>> it = uniqueVals.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = it.next();
				String value = entry.getKey();
				String [][] subDataSet = splitDataSet(dataSet,i,value);
				
				// �Ի��ֺ�����ݼ�������ũ�أ���������Ͳ���
				double prob = (subDataSet.length*1.0)/dataSet.length;
				newEntropy += prob*calcShannonEnt(subDataSet);
			}
			
			// ������Ϣ����
			double infoGain = baseEntropy - newEntropy;
			
			// ��ȡ��õ���Ϣ����
			if(infoGain > bestInfoGain){
				bestInfoGain = infoGain;
				bestFeature = i;
			}
		}
		return bestFeature;
	}
	
	public TreeNode createTree(String[][] dataSet, String[] labels){
		
		String [] classList = new String[dataSet.length];
		
		int k = 0;// ��¼classList[0]�ĳ��ִ���
		for(int i=0;i<dataSet.length;i++){
			classList[i] = dataSet[i][dataSet[0].length-1];
			if(i == 0){
				k=1;
			}else if(classList[0].equals(classList[i])){
				k++;
			}
		}
		// ���ݼ��������ȫ��ͬʱ��ֹͣ���������ݼ����л��֣������ط����ǩ
		if(k == classList.length){
			// TreeNode n = new TreeNode(classList[0],null);
			TreeNode n = new TreeNode("",null,classList[0]);
			return n;
		}
		
		// ������е�����ֵ���Ѿ����������������ݼ��������ȫ��ͬ
		if(dataSet[0].length == 1){
			Map<String,Integer> classCount = new HashMap<String,Integer>();
			for(String vote:classList){
				if(classCount.containsKey(vote)){
					classCount.put(vote, classCount.get(vote)+1);
				}else{
					classCount.put(vote, 1);
				}
			}
			// ��classCount����
			// ͨ��ArrayList���캯����map.entrySet()ת����list
	        List<Map.Entry<String,Integer>> maplist = new ArrayList<Map.Entry<String,Integer>>(classCount.entrySet());
	        // ͨ���Ƚ���ʵ�ֱȽ�����
	        Collections.sort(maplist, new Comparator<Map.Entry<String,Integer>>() {
	            public int compare(Map.Entry<String,Integer> mapping1, Map.Entry<String,Integer> mapping2) {
	                return mapping2.getValue().compareTo(mapping1.getValue());
	            }
	        });
	        // TreeNode n = new TreeNode(maplist.get(0).getKey(),null);
	        TreeNode n = new TreeNode("",null,maplist.get(0).getKey());
	        return n;
		}
		
		// ��ȡ��õĻ�������,ǰ�汣֤��dataSet����������
		int bestFeat = chooseBestFeatureToSplit(dataSet);
		String bestFeatLabel = labels[bestFeat];
		
		// �Ե�ǰ���ݼ��л�ȡ����õ�������ʼ����
		// TreeNode myTree = new TreeNode(bestFeatLabel,null);
		TreeNode myTree = new TreeNode(bestFeatLabel,null,null);
		String [] subLabels = new String[labels.length - 1];
		for(int i=0;i<subLabels.length;i++){
			if(i<bestFeat){
				subLabels[i] = labels[i];
			}else{
				subLabels[i] = labels[i+1];
			}
		}
		
		// ��ȡ��������Ӧ������ȥ������ֵ
		Map<String,Integer> uniqueVals = new HashMap<String,Integer>();
		for(int i=0;i<dataSet.length;i++){
			String key = dataSet[i][bestFeat];
			if(uniqueVals.containsKey(key)){
				uniqueVals.put(key, uniqueVals.get(key)+1);
			}else{
				uniqueVals.put(key, 1);
			}
		}
		
		// �������ݼ����������ķ�֧������ͬ������ֵ��Ӧ�����ݼ����еݹ黮��
		Iterator<Map.Entry<String, Integer>> it = uniqueVals.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String value = entry.getKey();
			
			TreeNode o = createTree(splitDataSet(dataSet,bestFeat,value),subLabels);
			
			if(myTree.childList == null){
				myTree.childList = new ArrayList<TreeNode>();
			}
			o.labe = value;
			myTree.childList.add(o);
			
/*			if(myTree.child == null){
				myTree.child = new HashMap<String,TreeNode>();
			}
			myTree.child.put(value, o);
			*/
		}
		
		return myTree;
		
	}
}
