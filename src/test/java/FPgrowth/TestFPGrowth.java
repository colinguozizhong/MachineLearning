package FPgrowth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public static void loadDataSet() {
		Map<String,Integer> retDict = new HashMap<String,Integer>(); 
	}
	
	public static void main(String[] args) {
		System.out.println(FPGrowth.frozenset(textParse("This random girl is arguing the nba needs to raise the rims to make it more interesting... https://t.co/AGd1IrUc13")));
		
		// textParse("This random girl is arguing the nba needs to raise the rims to make it more interesting... https://t.co/AGd1IrUc13");
	}
}
