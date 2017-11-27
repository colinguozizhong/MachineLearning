package Kmeans;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestKmeans {
	
	public static double[][] loadDataSet(String fileName) throws Exception {
		double [][] dataMat = null;
		
		List<String[]> dataArray = new ArrayList<String[]>();
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = "";
		while ((line = textBuffer.readLine()) != null) {
			String [] currLine = line.trim().split("\t"); 
			dataArray.add(currLine);
		}
		textBuffer.close();
		
		dataMat = new double[dataArray.size()][];
		for(int i=0;i<dataArray.size();i++) {
			String [] temp = dataArray.get(i);
			dataMat[i] = new double[2];
			dataMat[i][0] = Double.valueOf(temp[4]);
			dataMat[i][1] = Double.valueOf(temp[3]);
		}
		
		return dataMat;
	}
	
	public static void main(String[] args) throws Exception {
		String filename = System.getProperty("user.dir")+"\\src\\test\\java\\Kmeans\\places.txt";
		double[][] dataMat = loadDataSet(filename);
		Kmeans kmeans = new Kmeans();
		kmeans.biKmeans(dataMat, 5, "distSLC");
	}
}
