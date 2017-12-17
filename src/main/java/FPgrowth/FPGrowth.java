package FPgrowth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.MD5Util;

public class FPGrowth {
	
	public static Map<String,List<Object>> transMap = new HashMap<String,List<Object>>();
	
	public static <T> String frozenset(List<T> dataList) {
		String md5;
		String orginVal = "";
		for(int i=0;i<dataList.size();i++){
			T key = dataList.get(i);
			orginVal = orginVal + key.toString() + "\r\n";
		}
		md5 = MD5Util.MD5(orginVal);
		transMap.put(md5, (List<Object>) dataList);
		return md5;
	}
	
	/**
	 * 更新节点 nodeLink 链表，确保节点链接指向树中该元素项的每一个实例
	 * 遍历nodeToTest的 nodeLink 链表，直到表尾部，然后再链接上 targetNode
	 * @param nodeToTest
	 * @param targetNode
	 */
	public void updateHeader(TreeNode nodeToTest, TreeNode targetNode) {
		while(nodeToTest.nodeLink != null) {
			nodeToTest = nodeToTest.nodeLink;
		}
		nodeToTest.nodeLink = targetNode;
	}
	
	public void updateTree() {
		
	}
	
	public void createTree(Map<String,Integer> dataSet,double minSup) {
		//Map<> headerTable;
		Map<String,Integer> headerTable = new HashMap<String,Integer>();
		for(String md5:dataSet.keySet()) {
			List<Object> trans = transMap.get(md5);
			for(Object itemObj:trans) {
				String item = (String) itemObj;
				int count = 0;
				if(headerTable.containsKey(item)) {
					count += headerTable.get(item);
				}
				count += dataSet.get(md5);
				headerTable.put(item, count);
			}
		}
		
		for(String k:headerTable.keySet()) {
			if(headerTable.get(k) < minSup) {
				headerTable.remove(k);
			}
		}
		
		Set<String> freqItemSet = headerTable.keySet();
		if(freqItemSet.size() == 0) {
			return;
		}
		
		
	}
	
}
