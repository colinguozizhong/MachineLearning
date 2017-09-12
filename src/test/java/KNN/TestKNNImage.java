package KNN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestKNNImage {
	
	public static double[] img2vector(String fileName) throws Exception{
		double[] returnVector = new double[1024];
		for(int i=0;i<1024;i++){
			returnVector[i] = 1;
		}
		//System.out.println(fileName);
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String str = "";
		for(int i=0;i<32;i++){
			str = textBuffer.readLine();
			for(int j=0;j<32;j++){
				String c = str.substring(j, j+1);
				returnVector[32*i+j] = Double.valueOf(c);
			}
		}
		textBuffer.close();
		/*String str = "";
		List<String> list = new ArrayList<String>();
		while ((str = textBuffer.readLine()) != null) {
			list.add(str);
		}*/
		
		return returnVector;
	}
	
	public static void main(String[] args) throws Exception {
		KNN knn = new KNN();
		
		File d = new File(System.getProperty("user.dir")+"\\src\\test\\java\\KNN\\digits\\trainingDigits");
		String[] trainingFileList = d.list();
		
		int length = trainingFileList.length;
		double[][] trainingMat = new double[length][1024]; 
		
		double [] Labels = new double[length];
		
		for(int i=0;i<length;i++){
			String fileNameStr = trainingFileList[i];
			String fileName = fileNameStr.split("\\.")[0];
			String numClass = fileName.split("_")[0];
			Labels[i] = Double.valueOf(numClass);
			trainingMat[i] = img2vector(System.getProperty("user.dir")+"\\src\\test\\java\\KNN\\digits\\trainingDigits\\"+fileNameStr);
		}
		
		File dt = new File(System.getProperty("user.dir")+"\\src\\test\\java\\KNN\\digits\\testDigits");
		String[] testFileList = dt.list();
		int lengthTest = testFileList.length;
		for(int i=0;i<lengthTest;i++){
			String fileNameStr = testFileList[i];
			String fileName = fileNameStr.split("\\.")[0];
			String numClass = fileName.split("_")[0];
			double[] vectorUnderTest = img2vector(System.getProperty("user.dir")+"\\src\\test\\java\\KNN\\digits\\testDigits\\"+fileNameStr);
			double classifierResult = knn.classify0(vectorUnderTest,trainingMat,Labels,3);
			System.out.println(classifierResult+"  "+numClass);
		}
	}
}
