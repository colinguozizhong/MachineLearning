package FPgrowth;

import java.util.HashMap;
import java.util.Map;

public class TreeNode {

	public String name;
	
	public int count;
	
	public TreeNode nodeLink;
	
	public TreeNode parent;
	
	public Map<String,TreeNode> children;
	
	public TreeNode(String nameValue,int numOccur,TreeNode parentNode) {
		this.name = nameValue;
		this.count = numOccur;
		this.nodeLink = null;
		this.parent = parentNode;
		this.children = new HashMap<String,TreeNode>();
	}
	
	public void inc(int numOccur) {
		this.count += numOccur;
	}
}
