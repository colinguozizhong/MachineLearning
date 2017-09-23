package SVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.ArrayUtil;

public class SVM {
	
	int selectJrand(int i, int m) {
		int j = i;
		
		Random random = new Random();
//		while(j == i || j == 0) {
//			j = random.nextInt(m);
//		}
		
		if(i==1 && m==2) {
			return -1;
		}else if(i==0 || i==m) {
			j = random.nextInt(m-1)+1;
		}else if(i == 1){// m == 3 , [0,3-1)=[0,2)
			j = random.nextInt(m-i-1)+i+1;
		}else { //i=3 m=7  [0,2)+1=[1,3)  [0,7-3-1)+3+1=[0,3)+4=[4,7)
			int j1 = random.nextInt(i-1)+1;
			int j2 = random.nextInt(m-i-1)+i+1;
			double f = random.nextDouble();
			if(f>(i-1)/(m-1)) {
				j = j1;
			}else {
				j = j2;
			}
		}
		
		return j;
	}
	
	double clipAlpha(double aj,double H,double L){
		if(aj > H) {
			aj = H;
		}
		if(L > aj) {
			aj = L;
		}
		return aj;
	}
	
	public double calcEk(OptStruct oS, int k) {
		// fXk = float(multiply(oS.alphas,oS.labelMat).T*oS.K[:,k] + oS.b)
		// multiply(oS.alphas,oS.labelMat)
		double[][] temp = new double[oS.alphas.length][1];
		for(int i=0;i<oS.alphas.length;i++) {
			temp[i][0] = oS.alphas[i][0] * oS.labeMat.get(i);
		}
		double[][] tempT = ArrayUtil.transpositionArray(temp);
		
		// multiply(oS.alphas,oS.labelMat).T*oS.K[:,k] + oS.b
		double tempMulit = 0.0;
		for(int mi=0;mi<tempT[0].length;mi++) {
			tempMulit += tempT[0][mi] * oS.K[mi][k];
		}
		// 这个和上面效果一样一样的
//		for(int mi=0;mi<temp.length;mi++) {
//			tempMulit += temp[mi][0] * oS.K[mi][k];
//		}
		double fXk = tempMulit + (double)oS.b;
		
		double Ek = fXk - (double)oS.labeMat.get(k);
		return Ek;
	}
	
	public Map<String,Object> selectJ(int i, OptStruct oS, double Ei) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		int maxK = -1;
		double maxDeltaE = 0;
		double Ej = 0;
		
		oS.eCache[i][0] = 1;
		oS.eCache[i][1] = Ei;
		
		//validEcacheList = nonzero(oS.eCache[:,0].A)[0], .A：matrix->Array
		// http://blog.csdn.net/roler_/article/details/42395393
		// nonzero(oS.eCache[:,0].A)[0]：将oS.eCache[:,0]转成数组后，获取其非零元素所在的位置，且返回一个维度的位置,这里即为行号
		List<Integer> validEcacheList = new ArrayList<Integer>();
		for(int mi=0;mi<oS.eCache.length;mi++) {
			if(oS.eCache[mi][0]!=0.0) {
				validEcacheList.add(mi);
			}
		}
		
		if(validEcacheList.size()>1) {
			for(Integer k:validEcacheList) {
				if(k == i) {
					continue;
				}else {
					double Ek = calcEk(oS, k);
					double deltaE = Math.abs(Ei - Ek);
					if(deltaE > maxDeltaE) {
						maxK = k;
						maxDeltaE = deltaE;
						Ej = Ek;
					}
				}
			}
			retMap.put("maxK", maxK);
			retMap.put("Ej", Ej);
			return retMap;
		}else {
			int j = selectJrand(i, oS.m);
			Ej = calcEk(oS, j);
			retMap.put("maxK", j);
			retMap.put("Ej", Ej);
			return retMap;
		}
	}
	
	public void updateEk(OptStruct oS, int k) {
		double Ek = calcEk(oS, k);
		oS.eCache[k][0] = 1;
		oS.eCache[k][1] = Ek;
	}
	

	public double [][] kernelTrans(double[][] X, double[] A,Ktup ktup) throws Exception {
		// A是(xi1,xi2,xi3……,xim)横向量
		int m,n;
		m = n = X.length;
		double[][] K = new double[m][1]; // 基础类型初始化为0.0
		
		if("lin".equals(ktup.ktupType)) {
			// K = X * A.T    (A.T：A的转置)
			for(int mi=0;mi<m;mi++) {
				for(int ni=0;ni<n;ni++) {
					K[mi][1] = K[mi][1] + (X[mi][ni] * A[ni]);
				}
			}
//			
//			for(int mi=0;mi<m;mi++) {
//				for(int ni=0;ni<n;ni++) {
//					for(int ki=0;ki<k;ki++) {
//						T[mi][ni] = T[mi][ni] + M[mi][ki]*N[ki][ni];
//					}
//				}
//			}
		}else if("rbf".equals(ktup.ktupType)) {
			for(int j=0;j<m;j++) {
				double[] deltaRow = new double[n];
				for(int ni=0;ni<n;ni++) {
					deltaRow[ni] = X[j][ni] - A[ni];
				}
				// K[j] = deltaRow*deltaRow.T
				for(int ni=0;ni<n;ni++) {
					K[j][1] = K[j][1] + deltaRow[ni]*deltaRow[ni];
				}
			}
			// K = exp(K/(-1*kTup[1]**2))
			for(int mi=0;mi<m;mi++) {
				K[mi][1] = Math.pow(Math.E, K[mi][1]/(-1.0*Math.pow(ktup.σ, 2)));
			}
		}else {
			throw new Exception("'Houston We Have a Problem -- \\\r\n" + 
					" That Kernel is not recognized'");
		}
		
		return K;
	}
	
	public static void main(String[] args) {
		SVM svm = new SVM();
		System.out.println(svm.selectJrand(1,3));

	}
	
}
