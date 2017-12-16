package FPgrowth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.FileUtil;

public class TestFPGrowth {

	public static List<String> textParse(String bigString) {
		String urlRemoved = bigString.replaceAll("(http[s]?:[/][/]|www.)([a-z]|[A-Z]|[0-9]|[/.]|[~])*", "");
		String[] listOfTokens = urlRemoved.split("\\W");
		List<String> tokList = new ArrayList<String>();
		for(int i=0;i<listOfTokens.length;i++) {
			if(listOfTokens[i].length() > 2) {
				tokList.add(listOfTokens[i]);
			}
		}
		return tokList;
	}
	
	public static Map<String,Integer> loadDataSet() {
		Map<String,Integer> retDict = new HashMap<String,Integer>();
		String dataDir = System.getProperty("user.dir")+"\\src\\test\\java\\FPgrowth\\twitterdata\\";
		File dir = new File(dataDir);
		File[] files = dir.listFiles();
		for(File f:files) {
			retDict.put(FPGrowth.frozenset(textParse(FileUtil.readFile(f))),1);
		}
		return retDict;
	}
	
	public static void main(String[] args) {
		
		Map<String,Integer> retDict = loadDataSet();
		System.out.println(FPGrowth.frozenset(textParse("This random girl is arguing the nba needs to raise the rims to make it more interesting... https://t.co/AGd1IrUc13")));
		
		// textParse("This random girl is arguing the nba needs to raise the rims to make it more interesting... https://t.co/AGd1IrUc13");
	}
}
