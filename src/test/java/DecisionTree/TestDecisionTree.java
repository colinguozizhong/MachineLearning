package DecisionTree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestDecisionTree {
	
	static String[] lensesLabels = null;
	static String[][] lenses = null;
	
	/**
	 * 加载数据
	 * @param filename
	 * @throws Exception
	 */
	public static void loadData(String filename) throws Exception{
		
		// 打开数据文件
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String str = "";
		List<String> list = new ArrayList<String>();
		while ((str = textBuffer.readLine()) != null) {
			list.add(str);
			System.out.println(str);
		}
		
		// 读取生成列表后存入lenses列表
		lenses = new String[list.size()][];
		for(int i=0;i<list.size();i++){
			String line = list.get(i);
			String [] dStr = line.trim().split("\t");
			lenses[i] = dStr;
		}
		
		// 构建标签列表
		lensesLabels = new String[]{"age", "prescript", "astigmatic", "tearRate"};
	}
	
	public static void main(String[] args) throws Exception {
		loadData(System.getProperty("user.dir")+"\\src\\test\\java\\DecisionTree\\lenses.txt");
		
		DecisionTree d = new DecisionTree();
		//System.out.println(d.calcShannonEnt(lenses));
		//System.out.println(d.chooseBestFeatureToSplit(lenses));
		TreeNode root = d.createTree(lenses, lensesLabels);
		System.out.println(root);
	}
}
