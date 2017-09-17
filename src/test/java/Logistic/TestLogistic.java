package Logistic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestLogistic {
	
	static double[][] trainingSet = null;
	static List<Double> trainingLabels = null;
	
	public static void loadDataSet(String filename) throws Exception{
		
		List<String[]> dataArray = new ArrayList<String[]>();
		
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line = "";
		while ((line = textBuffer.readLine()) != null) {
			String [] currLine = line.trim().split("\t"); 
			dataArray.add(currLine);
		}
		textBuffer.close();
		
		trainingSet = new double[dataArray.size()][21];
		trainingLabels = new ArrayList<Double>();
		for(int i=0;i<dataArray.size();i++){
			for(int j=0;j<21;j++){
				trainingSet[i][j] = Double.valueOf(dataArray.get(i)[j].trim());
			}
			trainingLabels.add(Double.valueOf(dataArray.get(i)[21].trim()));
		}
		
	}
	
	public static double colicTest() throws Exception{
		
		String filename = System.getProperty("user.dir")+"\\src\\test\\java\\Logistic\\horseColicTraining.txt";
		loadDataSet(filename);
		
		Logistic lg = new Logistic();
		double[] trainWeights = lg.stocGradAscent1(trainingSet, trainingLabels, 1000);
		
		int errorCount = 0;
		int numTestVec = 0;
		
		String testFile = System.getProperty("user.dir")+"\\src\\test\\java\\Logistic\\horseColicTest.txt";
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));
		String line = "";
		while ((line = textBuffer.readLine()) != null) {
			
			numTestVec++;
			
			String [] currLine = line.trim().split("\t");
			
			double[] lineArr = new double[currLine.length-1];
			for(int i=0;i<currLine.length-1;i++){
				lineArr[i] = Double.valueOf(currLine[i].trim());
			}
			//System.out.println(lg.classifyVector(lineArr, trainWeights));
			if(((int)lg.classifyVector(lineArr, trainWeights)) != (Integer.valueOf(currLine[currLine.length-1])).intValue()){
				errorCount ++;
			}
		}
		textBuffer.close();
		
		double errorRate = (errorCount*1.0)/numTestVec;
		System.out.println("the error rate of this test is: "+errorRate);
		return errorRate;
	}
	
	public static void multiTest() throws Exception{
		int numTests = 10;
		double errorSum = 0.0;
		for(int i=0;i<numTests;i++){
			errorSum += colicTest();
		}
		
		System.out.println("after "+numTests+" iterations the average error rate is: "+(errorSum/numTests));
	}
	
	public static void main(String[] args) throws Exception {
		
		multiTest();
		
		//colicTest();
	}
}
