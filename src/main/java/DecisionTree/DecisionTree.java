package DecisionTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
}
