package NBM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestNaiveBayesianSpam {
	
	public static void loadEmailFile(String filename) throws Exception{
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String str = "";
		StringBuilder txt = new StringBuilder();
		while ((str = textBuffer.readLine()) != null) {
			txt.append(str);
		}
		System.out.println(txt);
	}
	
	public static void main(String[] args) throws Exception {
		loadEmailFile(System.getProperty("user.dir")+"\\src\\test\\java\\DecisionTree\\lenses.txt");
	}
}
