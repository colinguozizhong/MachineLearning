package NBM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestNaiveBayesianSpam {
	
	public static void loadEmailFile(String filename) throws Exception{
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String str = "";
		StringBuilder txt = new StringBuilder();
		String txtStr = null;
		while ((str = textBuffer.readLine()) != null) {
			txt.append(str);
			txt.append(System.lineSeparator());
		}
		System.out.println(txt);
		txtStr = txt.toString();
		
		Pattern p = Pattern.compile("\\w*");
		Matcher matcher = p.matcher(txt);
		String regexStr = "";
		List<String> matcherList = new ArrayList<String>();
		while(matcher.find()){
			regexStr = matcher.group();
			if(!"".equals(regexStr) && regexStr.length() > 2){
				matcherList.add(regexStr.toLowerCase());
			}
		}
		String [] matcherStrs = new String[matcherList.size()];
		for(int i = 0;i<matcherList.size();i++){
			matcherStrs[i] = matcherList.get(i);
			System.out.println(matcherList.get(i));
		}
	}
	
	public static void main(String[] args) throws Exception {
		loadEmailFile(System.getProperty("user.dir")+"\\src\\test\\java\\NBM\\email\\spam\\12.txt");
	}
}
