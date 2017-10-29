package RegressionForecasting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.ArrayUtil;

public class TestRegressionForecasting {
	
	public static void scrapePage(List<double[]> retX, List<Double> retY, String inFile, int yr, int numPce, double origPrc) throws Exception {
		
		if(retX == null) {
			retX = new ArrayList<double[]>();
		}
		
		if(retY == null) {
			retY = new ArrayList<Double>();
		}
		
		File input = new File(inFile);
		Document soup = Jsoup.parse(input, "UTF-8");
		
		int i=1;
		
		// 根据HTML页面结构进行解析
		Elements currentRow = soup.select("table[r='"+i+"']");
		while(currentRow.size()>0) {
			String title = currentRow.get(0).select("a").get(1).text();
			String lwrTitle = title.toLowerCase();
			
			// 查找是否有全新标签
			int newFlag = 0;
			if(lwrTitle.indexOf("new")>-1 || lwrTitle.indexOf("nisb")>-1) {
				newFlag = 1;
			}else {
				newFlag = 0;
			}
			
			// 查找是否已经标志出售，我们只收集已出售的数据
			Elements soldUnicde = currentRow.get(0).select("td").get(3).select("span");
			if(soldUnicde.size() == 0) {
				System.out.println("item #"+i+" did not sell");
			}else {
				// 解析页面获取当前价格
				Element soldPrice = currentRow.get(0).select("td").get(4);
				String priceStr = soldPrice.text();
				priceStr = priceStr.replace("$",""); //strips out $
				priceStr = priceStr.replace(",",""); //strips out ,
				if(soldPrice.childNodeSize()>1) {
					// 有Free shipping的元素
					priceStr = priceStr.replace("Free shipping", "");
				}
				double sellingPrice = Double.valueOf(priceStr);
				
				// 去掉不完整的套装价格
				if(sellingPrice > origPrc * 0.5) {
					System.out.println(yr+"\t"+numPce+"\t"+newFlag+"\t"+origPrc+"\t"+sellingPrice);
					double[] temp = new double[] {(double)yr, (double)numPce, (double)newFlag, origPrc};
					retX.add(temp);
					retY.add(sellingPrice);
				}
			}
			i++;
			currentRow = soup.select("table[r='"+i+"']");
		}
		
	}
	
	public static void setDataCollect(List<double[]> retX, List<Double> retY) throws Exception {
		scrapePage(retX,retY, System.getProperty("user.dir")+"\\src\\test\\java\\RegressionForecasting\\setHtml\\lego8288.html", 2006, 800, 49.99);
		scrapePage(retX,retY, System.getProperty("user.dir")+"\\src\\test\\java\\RegressionForecasting\\setHtml\\lego10030.html", 2002, 3096, 269.99);
		scrapePage(retX,retY, System.getProperty("user.dir")+"\\src\\test\\java\\RegressionForecasting\\setHtml\\lego10179.html", 2007, 5195, 499.99);
		scrapePage(retX,retY, System.getProperty("user.dir")+"\\src\\test\\java\\RegressionForecasting\\setHtml\\lego10181.html", 2007, 3428, 199.99);
		scrapePage(retX,retY, System.getProperty("user.dir")+"\\src\\test\\java\\RegressionForecasting\\setHtml\\lego10189.html", 2008, 5922, 299.99);
		scrapePage(retX,retY, System.getProperty("user.dir")+"\\src\\test\\java\\RegressionForecasting\\setHtml\\lego10196.html", 2009, 3263, 249.99);
		
		System.out.println("setDataCollect Finished");
	}
	
	public static void crossValidation(List<double[]> xArr, List<Double> yArr, int numVal) {
		int m = yArr.size();
		int [] indexList = new int[m];
		
		double [][] errorMat = new double[numVal][30];
		double [][] wMat = null;
		Random random = new Random();
		for(int i=0;i<numVal;i++) {
			int n = (int) (m*0.9);
			double [][] trainX = new double[n][];
			double [] trainY = new double[n];
			double [][] testX = new double[m-n][];
			double [] testY = new double[m-n];
			
			List<Integer> indexArr = new ArrayList<Integer>();
			for(int j=0;j<m;j++) {
				indexArr.add(j);
			}
			for(int j=0;j<m;j++) {
				int indexIndex = random.nextInt(m-j);
				indexList[j] = indexArr.get(indexIndex);
				indexArr.remove(indexIndex);
			}
			
			for(int j=0;j<m;j++) {
				if(j<n) {
					trainX[j] = xArr.get(indexList[j]).clone();
					trainY[j] = yArr.get(indexList[j]).doubleValue();
				}else {
					testX[j-n] = xArr.get(indexList[j]).clone();
					testY[j-n] = yArr.get(indexList[j]).doubleValue();
				}
			}
			
			RegressionForecasting r = new RegressionForecasting();
			
			wMat = r.ridgeTest(trainX, trainY);
			
			for(int k=0;k<30;k++) {
				double [][] matTestX = testX;
				double [][] matTrainX = trainX;
				
				double [] meanTrain = ArrayUtil.mean0(matTrainX);
				double [] varTrain = ArrayUtil.var0(matTrainX);

				for(int j=0;j<matTestX.length;j++) {
					for(int jj=0;jj<matTestX[j].length;jj++) {
						matTestX[j][jj] = (matTestX[j][jj] - meanTrain[jj])/varTrain[jj];
					}
				}
				
				double[] yEst = new double[matTestX.length];
				for(int j=0;j<yEst.length;j++) {
					double matTestXjSum = 0;
					for(int jj=0;jj<matTestX[j].length;jj++) {
						matTestXjSum += matTestX[j][jj]*wMat[k][jj]; 
					}
					yEst[j] = matTestXjSum + ArrayUtil.mean(trainY);
				}
				
				double errorMati = 0;
				for(int j=0;j<yEst.length;j++) {
					errorMati+= Math.pow((yEst[j] - testY[j]),2);
				}
				errorMat[i][k] = errorMati;
			}
			
			
		}
		
		double [] meanErrors = ArrayUtil.mean0(errorMat);
		double minMean = Double.MAX_VALUE;
		for(int i=0;i<meanErrors.length;i++) {
			if(minMean>meanErrors[i]) {
				minMean = meanErrors[i];
			}
		}
		
		List<double[]> bestWeights = new ArrayList<double[]>();
		for(int i=0;i<meanErrors.length;i++) {
			if(meanErrors[i] == minMean) {
				bestWeights.add(wMat[i]);
			}
		}
		
		double [][] xMat = new double[xArr.size()][];
		for(int i=0;i<xArr.size();i++) {
			xMat[i] = xArr.get(i);
		}
		double [] yMat = new double [yArr.size()];
		for(int i=0;i<yArr.size();i++) {
			yMat[i] = yArr.get(i).doubleValue();
		} 
		double [] meanX = ArrayUtil.mean0(xMat);
		double [] varX = ArrayUtil.var0(xMat);
		double [][] unReg = new double[bestWeights.size()][]; 
		for(int i=0;i<bestWeights.size();i++) {
			double [] temp = bestWeights.get(i);
			for(int j=0;j<temp.length;j++) {
				temp[j] = temp[j]/varX[j]; 
			}
			unReg[i] = temp;
		}
		
		System.out.println("the best model from Ridge Regression is:");
		System.out.print("[");
		for(int i=0;i<unReg.length;i++) {
			System.out.print("[");
			for(int j=0;j<unReg[i].length;j++) {
				if(j==0) {
					System.out.print(unReg[i][j]);
				}else {
					System.out.print(","+unReg[i][j]);
				}
				
			}
			System.out.print("]");
		}
		System.out.println("]");
	    //print "the best model from Ridge Regression is:\n",unReg
	    //print "with constant term: ",-1*sum(multiply(meanX,unReg)) + mean(yMat)
		
		double sum = 0;
		for(int i=0;i<unReg.length;i++) {
			for(int j=0;j<unReg[i].length;j++) {
				sum += meanX[j]*unReg[i][j];
			}
		}
		double term = -1*sum + ArrayUtil.mean(yMat);
		System.out.println("with constant term: "+term);
	}
	
	public static void main(String[] args) throws Exception {
		File input = new File("C:/Users/zizhong/Desktop/lego8288.html");
		Document doc = Jsoup.parse(input, "UTF-8");
		
		int i=1;
		Elements currentRow = doc.select("table[r='1']");
		System.out.println(currentRow.size());
		System.out.println(currentRow.get(0).select("a").get(1).text());
		/*
		String filename = System.getProperty("user.dir")+"\\src\\test\\java\\RegressionForecasting\\setHtml\\lego8288.html";
		scrapePage(null,null, filename, 2006, 800, 49.99);
		*/
		
		List<double[]> retX = new ArrayList<double[]>();
		List<Double> retY = new ArrayList<Double>();
		
		setDataCollect(retX,retY);
		crossValidation(retX,retY,10);
	}
}
