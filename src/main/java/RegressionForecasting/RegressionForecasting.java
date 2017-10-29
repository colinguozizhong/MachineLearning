package RegressionForecasting;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class RegressionForecasting {
	
	public double[] ridgeRegres(double[][] xMat,double[] yMat,double lam) {
		int m = xMat.length;
		int n = xMat[0].length;
		double[][] xMatT = new double[n][m];
		for(int i=0;i<n;i++) {
			for(int j=0;j<m;j++) {
				xMatT[i][j] = xMat[j][i];
			}
		}
		
		double[][] xTx = new double[n][n];
		for(int i=0;i<n;i++) {
			for(int k=0;k<n;k++) {
				double temp = 0;
				for(int j=0;j<m;j++) {
					temp += xMatT[i][j]*xMat[j][k];
				}
				xTx[i][k] = temp;
			}
		}
		
		double[][] denom = new double[n][n];
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				if(i == j) {
					denom[i][j] = 1*lam;
				}else {
					denom[i][j] = 0*lam;
				}
				
				denom[i][j] += xTx[i][j];
				
			}
		}
		
		// http://www.cnblogs.com/ayan/archive/2012/04/17/2453471.html
		// http://introcs.cs.princeton.edu/java/95linear/SVD.java.html
		Matrix denomMat = new Matrix(denom);
		if(denomMat.det() == 0) {
			System.out.println("This matrix is singular, cannot do inverse");
		}
		
		Matrix denomMatI = denomMat.inverse();
		double [][] denomI = denomMatI.getArray();
		
		// xMat.T*yMat
		double [] tempArr = new double[n];
		for(int i=0;i<n;i++) {
			for(int k=0;k<1;k++) {
				double temp = 0;
				for(int j=0;j<m;j++) {
					temp += xMatT[i][j]*yMat[j];
				}
				tempArr[i] = temp;
			}
		}
		
		double [] ws = new double[n];
		for(int i=0;i<n;i++) {
			for(int k=0;k<1;k++) {
				double temp = 0;
				for(int j=0;j<n;j++) {
					temp += denomI[i][j]*tempArr[j];
				}
				ws[i] = temp;
			}
		}
		
		return ws;
	}
	
	public double[][] ridgeTest(double [][] xArr,double [] yArr) {
		double [][] xMat = xArr;
		double [] yMat = yArr;
		
		double ySum = 0;
		for(int i=0;i<yMat.length;i++) {
			ySum += yMat[i];
		}
		double yMean = ySum/yMat.length;
		
		double [] xMeans = new double[xMat[0].length];
		for(int j=0;j<xMat[0].length;j++) {
			double xSum = 0;
			for(int i=0;i<xMat.length;i++) {
				xSum += xMat[i][j];
			}
			xMeans[j] = xSum/xMat.length;
		}
		
		double [] xVar = new double[xMat[0].length];
		for(int j=0;j<xMat[0].length;j++) {
			//mean(abs(x - x.mean())**2);
			double sumTemp = 0;
			for(int i=0;i<xMat.length;i++) {
				double temp = Math.pow(Math.abs(xMat[i][j] - xMeans[j]),2);
				sumTemp += temp;
			}
			xVar[j] = sumTemp/xMat.length;
			 
		}
		
		for(int i=0;i<xMat.length;i++) {
			for(int j=0;j<xMat[i].length;j++) {
				xMat[i][j] = (xMat[i][j] - xMeans[j])/xVar[j]; 
			}
		}
		
		int numTestPts = 30;
		double [][] wMat = new double[numTestPts][xMat[0].length];
		
		for(int i=0;i<numTestPts;i++) {
			double [] ws = ridgeRegres(xMat, yMat, Math.pow(Math.E, i-10));
			wMat[i] = ws;
		}
		
		return wMat;
	}
}
