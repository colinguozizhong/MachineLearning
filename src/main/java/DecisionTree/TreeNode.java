package DecisionTree;

import java.util.List;
import java.util.Map;

public class TreeNode {
	
	String labe;
	
	Map<String,TreeNode> child = null;
	
	List<TreeNode> childList = null;
	
	String result = null;
	
	TreeNode(){
		
	}
/*	
	TreeNode(String labe,Map<String,TreeNode> child){
		this.child = child;
		this.labe = labe;
	}
	*/
	TreeNode(String labe,List<TreeNode> childList, String result){
		this.childList = childList;
		this.labe = labe;
		this.result = result;
	}
}
