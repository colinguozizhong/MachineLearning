package SVM;

import java.util.List;

public class OptStruct {
	double [][] X;
	//List<Integer> labelMat;
	int [] labelMat;
	double C;
	double tol;
	int m;
	double [][] alphas;
	double b;
	double [][] eCache;
	double [][] K;
 	
	public OptStruct(double [][] dataMatIn, List<Integer> classLabels, double C, double toler, Ktup kTup) throws Exception {
		this.X = dataMatIn;
		this.m = dataMatIn.length;
		//this.labelMat = classLabels;
		this.labelMat = new int[this.m];
		for(int i=0;i<classLabels.size();i++) {
			this.labelMat [i] = classLabels.get(i);
		}
		this.C = C;
		this.tol = toler;
		
		this.b = 0;
		this.alphas = new double[this.m][1];
		this.eCache = new double[this.m][2];
		this.K = new double[this.m][this.m];
		
		for(int i=0;i<this.m;i++) {
			this.alphas[i][0] = 0.0;
			
			this.eCache[i][0] = 0.0;
			this.eCache[i][1] = 0.0;
			
			for(int j=0;j<this.m;j++) {
				this.K[i][j] = 0.0;
			}
		}
		
		SVM s = new SVM();
		
		for(int i=0;i<m;i++) {
			double [][] t = s.kernelTrans(X, X[i], kTup);
			for(int j=0;j<X.length;j++) {
				K[j][i] = t[j][0];
			}
		}
		
		
	}
}
