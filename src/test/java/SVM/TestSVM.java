package SVM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestSVM {
	
	static double [][] dataArr;
	static List<Integer> labelArr;
	
	public static double[] img2vector(String fileName) throws Exception{
		double[] returnVector = new double[1024];
		for(int i=0;i<1024;i++){
			returnVector[i] = 1;
		}
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
		
		return returnVector;
	}
	
	public static void loadImages(String dirName) throws Exception{
		File d = new File(dirName);
		String[] trainingFileList = d.list();
		
		int m = trainingFileList.length;
		
		double [][] trainingMat = new double[m][1024]; 
		List<Integer> hwLabels = new ArrayList<Integer>();
		
		for(int i=0;i<m;i++){
			String fileNameStr = trainingFileList[i];
			String fileStr = fileNameStr.split("\\.")[0];
			int classNumStr = Integer.valueOf(fileStr.split("_")[0]);
			if(classNumStr == 9){
				hwLabels.add(-1);
			}else{
				hwLabels.add(1);
			}
			trainingMat[i] = img2vector(dirName+"\\"+fileNameStr);
		}
		
		dataArr = trainingMat;
		labelArr = hwLabels;
	}
	
	public static void main(String[] args) throws Exception {
		String trainingDir = System.getProperty("user.dir")+"\\src\\test\\java\\SVM\\trainingDigits";
		loadImages(trainingDir);
		
		for(int i=0;i<dataArr.length;i++){
			for(int j=0;j<dataArr[i].length;j++){
				System.out.print(dataArr[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
		for(int i=0;i<labelArr.size();i++){
			System.out.print(labelArr.get(i)+" ");
		}
		System.out.println();
	}
}
