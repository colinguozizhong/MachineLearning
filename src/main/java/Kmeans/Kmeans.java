package Kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.ArrayUtil;

public class Kmeans {
	
	public double[][] randCent(double[][] dataSet,int k) {
		int n = dataSet[0].length;
		int m = dataSet.length;
		double [][] centroids = new double[k][n];
		
		for(int j=0;j<n;j++) {
			double minJ = Double.MAX_VALUE;
			double maxJ = -Double.MAX_VALUE;
			for(int i=0;i<m;i++) {
				if(minJ > dataSet[i][j]) {
					minJ = dataSet[i][j];
				}
				if(maxJ < dataSet[i][j]) {
					maxJ = dataSet[i][j];
				}
			}
			
			double rangeJ = maxJ - minJ;
			
			// 生成k个随机数random.rand(k,1)
			double [] randArr = new double[k];
			Random r = new Random();
			for(int i=0;i<k;i++) {
				centroids[i][j] = minJ + rangeJ * Math.random();
			}
		}
		
		return centroids;
	}
	
	public double distSLC(double[] vecA, double[] vecB) {
		
		double a = Math.sin(vecA[1]*Math.PI/180) * Math.sin(vecB[1]*Math.PI/180);
		double b = Math.cos(vecA[1]*Math.PI/180) * Math.cos(vecB[1]*Math.PI/180) * Math.cos(Math.PI*(vecB[0] - vecA[0])/180);
		
		double ret = Math.acos(a+b)*6371.0d; // 地球半径6371km
		
		return ret;
	}
	
	public List<double [][]> kMeans(double[][] dataSet,int k,String distMeas,String createCent){
		int m = dataSet.length;
		
		List<double [][]> ret = new ArrayList<double [][]>();
		
		double [][] clusterAssment = new double[m][2];
		
		double [][] centroids = null;
		if("randCent".equals(createCent)){
			centroids = randCent(dataSet,k);
		}
		
		boolean clusterChanged = true;
		
		while(clusterChanged){
			clusterChanged = false;
			
			for(int i=0;i<m;i++){
				double minDist = Double.MAX_VALUE;
				int minIndex = -1;
				for(int j=0;j<k;j++){
					double distJI = 0;
					if("distSLC".equals(distMeas)){
						distJI = distSLC(centroids[j],dataSet[i]);
					}
					if(distJI<minDist){
						minDist = distJI;
						minIndex = j;
					}
				}
				if(clusterAssment[i][0]!=minIndex){
					clusterChanged = true;
				}
				clusterAssment[i][0] = minIndex;
				clusterAssment[i][1] = Math.pow(minDist,2);
			}
			
			for(int cent=0;cent<k;cent++){
				double[][] ptsInClust = null;
				List<double[]> ptsInClustList = new ArrayList<double[]>();
				for(int j=0;j<clusterAssment.length;j++) {
					if(clusterAssment[j][0] == cent) {
						ptsInClustList.add(dataSet[j]);
					}
				}
				ptsInClust = new double[ptsInClustList.size()][];
				for(int j=0;j<ptsInClust.length;j++) {
					ptsInClust[j] = ptsInClustList.get(j).clone();
				}
				
				centroids[cent] = ArrayUtil.mean(ptsInClust, 0);
				
			}
			
		}
		ret.add(centroids);
		ret.add(clusterAssment);
		return ret;
	}
	
	public void biKmeans(double[][] dataSet,int k,String distMeas) {
		int m = dataSet.length;
		int n = dataSet[0].length;
		double [][] clusterAssment = new double[m][2];
		
		double [] centroid0 = new double[n];
		centroid0 = ArrayUtil.mean(dataSet, 0);
		
		List<double []> centList = new ArrayList<double []>();
		
		centList.add(centroid0);
		
		for(int j=0;j<m;j++) {
			if("distSLC".equals(distMeas)) {
				clusterAssment[j][1] = Math.pow(distSLC(centroid0,dataSet[j]),2);
			}
		}
		
		while(centList.size() < k) {
			double lowestSSE = Double.MAX_VALUE;
			
			int bestCentToSplit = -999;
			double [][] bestNewCents = null;
			double [][] bestClustAss = null;
			
			for(int i=0;i<centList.size();i++) {
				double [][] ptsInCurrCluster = null;
				List<double[]> ptsInCurrClusterList = new ArrayList<double[]>();
				for(int ii = 0;ii<clusterAssment.length;ii++) {
					if(clusterAssment[ii][0] == i) {
						ptsInCurrClusterList.add(dataSet[ii]);
					}
				}
				ptsInCurrCluster = new double[ptsInCurrClusterList.size()][];
				for(int ii=0;ii<ptsInCurrClusterList.size();ii++) {
					ptsInCurrCluster[ii] = ptsInCurrClusterList.get(ii);
				}
				
				List<double[][]> retKmens = kMeans(ptsInCurrCluster,2,"distSLC","randCent");
				double [][] centroidMat = retKmens.get(0);
				double [][] splitClustAss = retKmens.get(1);
				
				double sseSplit = 0;
				for(int ii=0;ii<splitClustAss.length;ii++) {
					sseSplit+=splitClustAss[ii][1];
				}
				
				double sseNotSplit = 0;
				for(int ii = 0;ii<clusterAssment.length;ii++) {
					if(clusterAssment[ii][0] != i) {
						sseNotSplit+=clusterAssment[ii][1];
					}
				}
				System.out.println("sseSplit, and notSplit: "+sseSplit+" "+sseNotSplit);
				
				
				if(sseSplit + sseNotSplit < lowestSSE) {
					bestCentToSplit = i;
					bestNewCents = centroidMat;
					bestClustAss = splitClustAss.clone();
					
					lowestSSE = sseSplit + sseNotSplit;
				}
			}
			
			for(int ii=0;ii<bestClustAss.length;ii++) {
				if(bestClustAss[ii][0] == 1) {
					bestClustAss[ii][0] = centList.size();
				}else if(bestClustAss[ii][0] == 0) {
					bestClustAss[ii][0] = bestCentToSplit;
				}
			}
			System.out.println("the bestCentToSplit is: "+bestCentToSplit);
			System.out.println("the len of bestClustAss is: "+bestClustAss.length);
			
			centList.set(bestCentToSplit,bestNewCents[0]);
			centList.add(bestNewCents[1]);
			
			for(int ii=0;ii<clusterAssment.length;ii++) {
				if(clusterAssment[ii][0] == bestCentToSplit) {
					clusterAssment[ii] = bestClustAss[ii];
				}
			}
			
			System.out.println("eeee");
			
		}
		
		
	}
	
}
