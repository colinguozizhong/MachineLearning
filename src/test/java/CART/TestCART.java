package CART;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestCART {
	
	public static double[][] loadDataSet(String fileName) throws Exception {
		
		List<String[]> dataArray = new ArrayList<String[]>();
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = "";
		while ((line = textBuffer.readLine()) != null) {
			String [] currLine = line.trim().split("\t"); 
			dataArray.add(currLine);
		}
		textBuffer.close();
		
		double [][] dataMat = new double[dataArray.size()][];
		for(int i=0;i<dataArray.size();i++) {
			String [] temp = dataArray.get(i);
			dataMat[i] = new double[temp.length];
			for(int j=0;j<temp.length;j++) {
				dataMat[i][j] = Double.valueOf(temp[j]).doubleValue();
			}
		}
		return dataMat;
	}
	
	public static void main(String[] args) throws Exception {
		String filename = System.getProperty("user.dir")+"\\src\\test\\java\\CART\\bikeSpeedVsIq_train.txt";
		double[][] dataMat = loadDataSet(filename);
		CART cart = new CART();
		cart.linearSolve(dataMat);
		
		System.out.println(cart.modelErr(dataMat));
		System.out.println(cart.regErr(dataMat));
		
		String trainFile = System.getProperty("user.dir")+"\\src\\test\\java\\CART\\bikeSpeedVsIq_train.txt";
		String testFile = System.getProperty("user.dir")+"\\src\\test\\java\\CART\\bikeSpeedVsIq_test.txt";
		
		double [][] trainMat = loadDataSet(trainFile);
		double [][] testMat = loadDataSet(testFile);
		double[] ops = new double[2];
		ops[0] = 1;
		ops[1] = 20;
		double [][] testMat0 = new double[testMat.length][1];
		double [] testMat1 = new double[testMat.length];
		for(int i=0;i<testMat.length;i++) {
			testMat0[i][0] = testMat[i][0];
			testMat1[i] = testMat[i][1];
		}
		
		// 创建回归树并计算R^2值
		Map<String,Object> myTree = cart.createTree(trainMat, "regLeaf", "regErr", ops);
		double[][] yHat = cart.createForeCast(myTree,testMat0,"regTreeEval");
		double[] yHatt = new double[yHat.length];
		for(int i=0;i<yHat.length;i++) {
			yHatt[i] = yHat[i][0];
		}
		double regTreeR2 = corrcoef(yHatt,testMat1);
		System.out.println("regTreeR^2 = "+regTreeR2);
		
		// 创建模型树并计算R^2值
		Map<String,Object> myTree2 = cart.createTree(trainMat, "modelLeaf", "modelErr", ops);
		double[][] yHat2 = cart.createForeCast(myTree2, testMat0, "modelTreeEval");
		double[] yHatt2 = new double[yHat2.length];
		for(int i=0;i<yHat2.length;i++) {
			yHatt2[i] = yHat2[i][0];
		}
		double modelTreeR2 = corrcoef(yHatt2, testMat1);
		System.out.println("modelTreeR^2 = "+modelTreeR2);
		
		
		// 创建线性模型并计算R^2
		Map<String,double[][]> data = cart.linearSolve(trainMat);
		double[][] ws = data.get("ws");
		double[][] X = data.get("X");
		double[][] Y = data.get("Y");
		double[] yHatt3 = new double[testMat.length];
		for(int i=0;i<testMat.length;i++){
			yHatt3[i] = testMat[i][0] * ws[1][0] + ws[0][0];
		}
		double linearTreeR2 = corrcoef(yHatt3, testMat1);
		System.out.println("linearTreeR^2 = "+linearTreeR2);
		
	}
	
	public static double mean(double [] data) {
		double sum = 0;
		for(int i=0;i<data.length;i++) {
			sum += data[i];
		}
		return sum/data.length;
	}
	
	public static double std(double[] data) {
		double dataMean = mean(data);
		double sum = 0;
		for(int i=0;i<data.length;i++) {
			sum += Math.pow((data[i]-dataMean), 2);
		}
		double var = sum/data.length;
		double std = Math.pow(var, 0.5);
		return std;
	}
	
	public static double corrcoef(double [] vb,double [] vc) {
		// mean(multiply((vc-mean(vc)),(vb-mean(vb))))/(std(vb)*std(vc))
		double covSum = 0;
		double vbMean = mean(vb);
		double vcMean = mean(vc);
		for(int i=0;i<vb.length;i++) {
			covSum += ((vb[i]-vbMean)*(vc[i]-vcMean));
		}
		double cov = covSum/vb.length;
		double corrcoef = cov/(std(vb)*std(vc));
		return corrcoef;
	}
	
}
