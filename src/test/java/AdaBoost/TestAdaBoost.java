package AdaBoost;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestAdaBoost {
	
	static double[][] trainingSet = null;
	//static List<Double> trainingLabels = null;
	static double[] trainingLabels = null;
	
	static double[][] testSet = null;
	//static List<Double> trainingLabels = null;
	static double[] testLabels = null;
	
	public static void loadDataSet(String filename) throws Exception {
		
		List<String[]> dataArray = new ArrayList<String[]>();
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line = "";
		while ((line = textBuffer.readLine()) != null) {
			String [] currLine = line.trim().split("\t"); 
			dataArray.add(currLine);
		}
		textBuffer.close();
		
		trainingSet = new double[dataArray.size()][21];
		//trainingLabels = new ArrayList<Double>();
		trainingLabels = new double[dataArray.size()];
		for(int i=0;i<dataArray.size();i++){
			for(int j=0;j<21;j++){
				trainingSet[i][j] = Double.valueOf(dataArray.get(i)[j].trim());
			}
			//trainingLabels.add(Double.valueOf(dataArray.get(i)[21].trim()));
			trainingLabels[i] = Double.valueOf(dataArray.get(i)[21].trim());
		}
	}
	
	public static void loadTestDataSet(String filename) throws Exception {
		
		List<String[]> dataArray = new ArrayList<String[]>();
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line = "";
		while ((line = textBuffer.readLine()) != null) {
			String [] currLine = line.trim().split("\t"); 
			dataArray.add(currLine);
		}
		textBuffer.close();
		
		testSet = new double[dataArray.size()][21];
		//trainingLabels = new ArrayList<Double>();
		testLabels = new double[dataArray.size()];
		for(int i=0;i<dataArray.size();i++){
			for(int j=0;j<21;j++){
				testSet[i][j] = Double.valueOf(dataArray.get(i)[j].trim());
			}
			//trainingLabels.add(Double.valueOf(dataArray.get(i)[21].trim()));
			testLabels[i] = Double.valueOf(dataArray.get(i)[21].trim());
		}
	}
	public static double adaTest() throws Exception {
		String filename = System.getProperty("user.dir")+"\\src\\test\\java\\AdaBoost\\horseColicTraining2.txt";
		loadDataSet(filename);
		AdaBoost adaBoost = new AdaBoost();
		List<Map<String,Object>> classifierArray = adaBoost.adaBoostTrainDS(trainingSet, trainingLabels, 40);
		
		int numTestVec = 67;
		
		String testfilename = System.getProperty("user.dir")+"\\src\\test\\java\\AdaBoost\\horseColicTest2.txt";
		loadTestDataSet(testfilename);
		
		double [] prediction10 = adaBoost.adaClassify(testSet, classifierArray);
		
		double [] errArr = new double[67];
				
		int errorCount = 0 ;
		for(int i =0 ;i< 67;i++) {
			if(prediction10[i] != testLabels[i]) {
				errArr[i] = 1;
				errorCount ++;
			}else {
				errArr[i] = 0;
			}
		}
		
		double errorRate = (errorCount*1.0)/numTestVec;
		System.out.println("the error rate of this test is: "+errorRate);
	    return errorRate;
	}
	
	public static void main(String[] args) throws Exception {
		
		adaTest();
		/*
		String filename = System.getProperty("user.dir")+"\\src\\test\\java\\AdaBoost\\horseColicTraining2.txt";
		loadDataSet(filename);	
		AdaBoost adaBoost = new AdaBoost();
		List<Map<String,Object>> classifierArray = adaBoost.adaBoostTrainDS(trainingSet, trainingLabels, 40);
		
		
		System.out.println("finished");
		*/
	}
}
