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
	
	public static void testDigits(Ktup kTup) throws Exception {
		loadImages(System.getProperty("user.dir")+"\\src\\test\\java\\SVM\\trainingDigits");
		SVM svm = new SVM();
		OptStruct oS = svm.smoP(dataArr, labelArr, 200, 0.0001, 10000, kTup);
		double b = oS.b;
		double [][] alphas = oS.alphas;
		
		// labelMat = mat(labelArr).transpose() 横向量改为竖向量
		double [][] datMat = dataArr;
		int [] labelMat = new int[labelArr.size()];
		for(int tempI=0;tempI<labelArr.size();tempI++) {
			labelMat[tempI] = labelArr.get(tempI);
		}
		
		// http://www.cnblogs.com/1zhk/articles/4782812.html
		List<Integer> svInd = new ArrayList<Integer>();
		for(int i=0;i<alphas.length;i++) {
			if(alphas[i][0] > 0 ) {
				svInd.add(i);
			}
		}
		double[][] sVs = new double[svInd.size()][];
		double[] labelSV = new double[svInd.size()];
		
		for(int i=0;i<svInd.size();i++) {
			sVs[i] = datMat[svInd.get(i)];
			labelSV[i] = labelMat[svInd.get(i)];
		}
		System.out.println("there are "+sVs.length+" Support Vectors");
		
		int m = datMat.length;
		int n = datMat[0].length;
		int errorCount = 0;
		
		double [] alphasSvId = new double[svInd.size()];
		
		for(int i=0;i<m;i++) {
			double[][] kernelEval = svm.kernelTrans(sVs, datMat[i], kTup);
			double[][] kernelEvalT = new double[1][kernelEval.length];
			for(int mi=0;mi<kernelEval.length;mi++) {
				kernelEvalT[0][mi] = kernelEval[mi][0];
			}
			double [] multiplyTemp = new double[labelSV.length];
			double predict = b;
			for(int mi=0;mi<kernelEval.length;mi++) {
				predict += (kernelEval[0][mi] * (labelSV[mi]*alphasSvId[mi])); 
			}
			
			if((predict==0 && labelArr.get(i) == 0 )|| predict*labelArr.get(i)>0) {
				errorCount += 1;
			}
		}
		
		System.out.println("the training error rate is:"+ (1.0*errorCount)/m);
		
		
		loadImages(System.getProperty("user.dir")+"\\src\\test\\java\\SVM\\testDigits");
		

		// labelMat = mat(labelArr).transpose() 横向量改为竖向量
		datMat = dataArr;
		labelMat = new int[labelArr.size()];
		for(int tempI=0;tempI<labelArr.size();tempI++) {
			labelMat[tempI] = labelArr.get(tempI);
		}
		m = datMat.length;
		n = datMat[0].length;
		errorCount = 0;
		
		for(int i=0;i<m;i++) {
			double[][] kernelEval = svm.kernelTrans(sVs, datMat[i], kTup);
			double[][] kernelEvalT = new double[1][kernelEval.length];
			for(int mi=0;mi<kernelEval.length;mi++) {
				kernelEvalT[0][mi] = kernelEval[mi][0];
			}
			double [] multiplyTemp = new double[labelSV.length];
			double predict = b;
			for(int mi=0;mi<kernelEval.length;mi++) {
				predict += (kernelEval[0][mi] * (labelSV[mi]*alphasSvId[mi])); 
			}
			
			if((predict==0 && labelArr.get(i) == 0 )|| predict*labelArr.get(i)>0) {
				errorCount += 1;
			}
		}
		
		System.out.println("the test error rate is:"+ (1.0*errorCount)/m);
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
