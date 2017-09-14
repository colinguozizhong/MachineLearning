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
	
	public TreeNode createTree(String[][] dataSet, String[] labels){
		
		String [] classList = new String[dataSet.length];
		
		int k = 0;// 记录classList[0]的出现次数
		for(int i=0;i<dataSet.length;i++){
			classList[i] = dataSet[i][dataSet[0].length-1];
			if(i == 0){
				k=1;
			}else if(classList[0].equals(classList[i])){
				k++;
			}
		}
		// 数据集的类别完全相同时则停止继续对数据集进行划分，并返回分类标签
		if(k == classList.length){
			// TreeNode n = new TreeNode(classList[0],null);
			TreeNode n = new TreeNode("",null,classList[0]);
			return n;
		}
		
		// 如果所有的特征值都已经遍历结束，且数据集的类别不完全相同
		if(dataSet[0].length == 1){
			Map<String,Integer> classCount = new HashMap<String,Integer>();
			for(String vote:classList){
				if(classCount.containsKey(vote)){
					classCount.put(vote, classCount.get(vote)+1);
				}else{
					classCount.put(vote, 1);
				}
			}
			// 对classCount排序
			// 通过ArrayList构造函数把map.entrySet()转换成list
	        List<Map.Entry<String,Integer>> maplist = new ArrayList<Map.Entry<String,Integer>>(classCount.entrySet());
	        // 通过比较器实现比较排序
	        Collections.sort(maplist, new Comparator<Map.Entry<String,Integer>>() {
	            public int compare(Map.Entry<String,Integer> mapping1, Map.Entry<String,Integer> mapping2) {
	                return mapping2.getValue().compareTo(mapping1.getValue());
	            }
	        });
	        // TreeNode n = new TreeNode(maplist.get(0).getKey(),null);
	        TreeNode n = new TreeNode("",null,maplist.get(0).getKey());
	        return n;
		}
		
		// 获取最好的划分特征,前面保证了dataSet至少有两列
		int bestFeat = chooseBestFeatureToSplit(dataSet);
		String bestFeatLabel = labels[bestFeat];
		
		// 以当前数据集中获取的最好的特征初始化树
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
		
		// 提取该特征对应的所有去重特征值
		Map<String,Integer> uniqueVals = new HashMap<String,Integer>();
		for(int i=0;i<dataSet.length;i++){
			String key = dataSet[i][bestFeat];
			if(uniqueVals.containsKey(key)){
				uniqueVals.put(key, uniqueVals.get(key)+1);
			}else{
				uniqueVals.put(key, 1);
			}
		}
		
		// 划分数据集，创建树的分支，将不同的特征值对应的数据集进行递归划分
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
