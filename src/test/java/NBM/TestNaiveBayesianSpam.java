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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.ArrayUtil;
import util.ListUtil;

public class TestNaiveBayesianSpam {
	
	/**
	 * 读取邮件数据
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static String[] loadEmailFileArray(String filename) throws Exception{
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String str = "";
		StringBuilder txt = new StringBuilder();
		while ((str = textBuffer.readLine()) != null) {
			txt.append(str);
			txt.append(System.lineSeparator());
		}
		textBuffer.close();
		
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
		}
		return matcherStrs;
	}
	
	public static List<String> loadEmailFile(String filename) throws Exception{
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String str = "";
		StringBuilder txt = new StringBuilder();
		while ((str = textBuffer.readLine()) != null) {
			txt.append(str);
			txt.append(System.lineSeparator());
		}
		textBuffer.close();
		
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
		return matcherList;
	}
	
	public static String[] createVocabListArray(String [][] docList){
		String [] vocabArray = null;
		vocabArray = ArrayUtil.uniqueVals(docList);
		return vocabArray;
	}
	
	public static int[] bagOfWords2VecMNArray(String [] vocabList, String [] inputSet){
		int[] returnVec = new int[vocabList.length];// 初始为0
		Map<String,Integer> uniqueVocabVals = ArrayUtil.uniqueArrayMapIndex(vocabList);
		for(String word:inputSet){
			if(uniqueVocabVals.containsKey(word)){
				returnVec[uniqueVocabVals.get(word)]++;
			}
		}
		return returnVec;
	}
	
	
	public static List<String> createVocabList(List<List<String>> docList){
		List<String> vocabList = null;
		vocabList = ListUtil.uniqueValsList(docList);
		return vocabList;
	}
	
	public static List<Integer> bagOfWords2VecMN(List<String> vocabList, List<String> inputSet){
		List<Integer> returnVec = new ArrayList<Integer>();
		for(int i=0;i<vocabList.size();i++){
			returnVec.add(0);
		}
		Map<String,Integer> uniqueVocabVals = ListUtil.uniqueListMapIndex(vocabList);
		for(String word:inputSet){
			if(uniqueVocabVals.containsKey(word)){
				Integer o = returnVec.get(uniqueVocabVals.get(word));
				if(o == null){
					o = 0;
				}
				o++;
				returnVec.remove(uniqueVocabVals.get(word).intValue());// 由于uniqueVocabVals.get(word)返回是Integer，如果没有.intValue()，List是按照value去找
				returnVec.add(uniqueVocabVals.get(word), o);
			}
		}
		return returnVec;
	}
	
	public static void spamTest() throws Exception{
		
		List<String> fullText = new ArrayList<String>();
		List<List<String>> docList = new ArrayList<List<String>>();
		List<Integer> classList = new ArrayList<Integer>();
		
		for(int i=1;i<26;i++){
			String spamfilename = System.getProperty("user.dir")+"\\src\\test\\java\\NBM\\email\\spam\\"+i+".txt";
			List<String> spamWordList = loadEmailFile(spamfilename);
			docList.add(spamWordList);
			fullText.addAll(spamWordList);
			classList.add(1);
			
			String hamfilename = System.getProperty("user.dir")+"\\src\\test\\java\\NBM\\email\\ham\\"+i+".txt";
			List<String> hamWordList = loadEmailFile(hamfilename);
			docList.add(hamWordList);
			fullText.addAll(hamWordList);
			classList.add(0);
		}
		
		List<String> vocabList = createVocabList(docList);
		
		List<Integer> trainingSet = new ArrayList<Integer>();
		for(int i=0;i<50;i++){
			trainingSet.add(i);
		}
		List<Integer> testSet = new ArrayList<Integer>();
		Random random=new Random();
		for(int i=0;i<10;i++){
			int randIndex = random.nextInt(trainingSet.size());
			testSet.add(trainingSet.get(randIndex));
			trainingSet.remove(randIndex);
		}
		
		List<List<Integer>> trainMat = new ArrayList<List<Integer>>();
		List<Integer> trainClasses = new ArrayList<Integer>();
		for(Integer docIndex:trainingSet){
			trainMat.add(bagOfWords2VecMN(vocabList, docList.get(docIndex)));
			trainClasses.add(classList.get(docIndex));
		}
		
		Integer[][] trainMatrix = new Integer[trainMat.size()][];
		for(int i=0;i<trainMat.size();i++){
			Integer[] trainMatrixSub = new Integer[trainMat.get(i).size()];
			for(int j=0;j<trainMat.get(i).size();j++){
				trainMatrixSub[j] = trainMat.get(i).get(j);
			}
			trainMatrix[i] = trainMatrixSub;
		}
		Integer[] trainCategory = new Integer[trainClasses.size()];
		for(int i=0;i<trainClasses.size();i++){
			trainCategory[i] = trainClasses.get(i);
		}
		
		NaiveBayesian nb = new NaiveBayesian();
		nb.trainNB0(trainMatrix, trainCategory);
		double pSpam = nb.pAbusive;
		double[] p1V = nb.p1Vect;
		double[] p0V = nb.p0Vect;
		
		int errorCount = 0;
		for(Integer docIndex :testSet){
			List<Integer> wordVector = bagOfWords2VecMN(vocabList,docList.get(docIndex));
			Integer[] wordVectorArray = new Integer[wordVector.size()];
			for(int i=0;i<wordVector.size();i++){
				wordVectorArray[i] = wordVector.get(i);
			}
			
			if(nb.classifyNB(wordVectorArray,p0V,p1V,pSpam) != classList.get(docIndex).intValue()){
				errorCount += 1;
				System.out.print("classification error:");
				for(String word:docList.get(docIndex)){
					System.out.print(" "+word);
				}
				System.out.println();
			}
		}
		
		
		System.out.println("the error rate is: "+((errorCount*1.0)/testSet.size()));
		
		
	}
	
	public static void main(String[] args) throws Exception {
		
		//System.out.println(Math.log(6.0/603));;
		
		//loadEmailFile(System.getProperty("user.dir")+"\\src\\test\\java\\NBM\\email\\spam\\12.txt");
		//String [][] t = {{"1","1","3","1","2","1","3","2"},{"5","7","8"}};
		//createVocabList(t);
		spamTest();
		
	}
}
