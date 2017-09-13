package DecisionTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DecisionTree {
	
	/**
	 * 计算香农熵
	 * @param dataSet
	 * @return
	 */
	public double calcShannonEnt(String[][] dataSet){
		
		// 获得数据集的大小
		int numEntries = dataSet.length;
		
		Map<String,Integer> labelCounts = new HashMap<String,Integer>();
		
		// 遍历数据集
		for(int i=0;i<numEntries;i++){
			String [] featVec = dataSet[i];
			if(featVec!=null && featVec.length>0){
				// 提取数据中的类型，本例中为医生推荐的隐形眼镜类型
				String currentLabel = featVec[featVec.length-1].trim();
				// 计算该类型眼镜在数据集中出现的次数
				if(labelCounts.containsKey(currentLabel)){
					labelCounts.put(currentLabel, (Integer)labelCounts.get(currentLabel)+1);
				}else{
					labelCounts.put(currentLabel, 1);
				}
			}
		}
		
		// 初始化香农熵
		double shannonEnt = 0.0;
		
		// 遍历隐形眼镜的类型字典计算香农熵
		Iterator<Map.Entry<String, Integer>> it = labelCounts.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
			// 见书P35的计算香农熵的公式
			double prob = entry.getValue()*1.0/numEntries;
			shannonEnt -= prob*(Math.log(prob)/Math.log(2));// log base 2
			System.out.println(shannonEnt);
		}
		
		return shannonEnt;
	}
	
	/**
	 * 划分数据集
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
		// 遍历数据集
		for(int i=0;i<dataSet.length;i++){
			String [] featVec = dataSet[i];
			// 判断当前划分的特征对应的值是否是需要返回的值
			if(value.equals(featVec[axis])){
				// 将当前处理的数据列表featVec进行重构组合
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
		
		// 用来划分的特征数量，数据集中最后一列是分类不是特征
		int numFeatures = dataSet[0].length-1;
		
		// 计算数据集的香农熵
		double baseEntropy = calcShannonEnt(dataSet);
		
		// 初始化
		double bestInfoGain = 0.0;
		int bestFeature = -1;
		
		// 遍历所有特征
		for(int i=0;i<numFeatures;i++){
			Map<String,Integer> uniqueVals = new HashMap<String,Integer>();
			// 遍历取i列的去重的特征值
			for(int j=0;j<dataSet.length;j++){
				String value = dataSet[j][i];
				
				// 也可以使用List.contains,数据多的时候效率没HashMap高
				if(!uniqueVals.containsKey(value)){
					uniqueVals.put(value, 1);
				}
			}
			
			double newEntropy = 0.0;
			
			// 循环使用该特征值作为参数进行数据集划分尝试
			Iterator<Map.Entry<String, Integer>> it = uniqueVals.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = it.next();
				String value = entry.getKey();
				String [][] subDataSet = splitDataSet(dataSet,i,value);
				
				// 对划分后的数据集计算香农熵，并进行求和操作
				double prob = (subDataSet.length*1.0)/dataSet.length;
				newEntropy += prob*calcShannonEnt(subDataSet);
			}
			
			// 计算信息增益
			double infoGain = baseEntropy - newEntropy;
			
			// 获取最好的信息增益
			if(infoGain > bestInfoGain){
				bestInfoGain = infoGain;
				bestFeature = i;
			}
		}
		return bestFeature;
	}
}
