package NBM;

import java.util.List;

import util.ArrayUtil;

public class NaiveBayesian {
	
	double pAbusive;
	
	double[] p1Vect;
	
	double[] p0Vect;
	
	public void trainNB0(Integer[][] trainMatrix,Integer[] trainCategory){
		int numTrainDocs = trainMatrix.length;
		int numWords = trainMatrix[0].length;
		
		int hamMum = 0;
		for(int i=0;i<trainCategory.length;i++){
			if(trainCategory[i]==1){
				hamMum++;
			}
		}
		
		pAbusive = (hamMum*1.0)/numTrainDocs;
		Integer[] p0Num = new Integer[numWords]; 
		Integer[] p1Num = new Integer[numWords];
		for(int i=0;i<numWords;i++){
			p0Num[i] = 1;
			p1Num[i] = 1;
		}
		double p0Denom = 2.0; 
		double p1Denom = 2.0;
		
		for(int i=0;i<numTrainDocs;i++){
			if(trainCategory[i] == 1){
				p1Num = ArrayUtil.addArray(p1Num,trainMatrix[i]);
				p1Denom += ArrayUtil.sumArray(trainMatrix[i]);
			}else{
				p0Num = ArrayUtil.addArray(p0Num,trainMatrix[i]);
				p0Denom += ArrayUtil.sumArray(trainMatrix[i]);
			}
		}
		
		p1Vect = new double[numWords];
		p0Vect = new double[numWords];
		for(int i=0;i<numWords;i++){
			p1Vect[i] = Math.log((p1Num[i]*1.0)/p1Denom);
			p0Vect[i] = Math.log((p0Num[i]*1.0)/p0Denom);
		}
	}
	
	public int classifyNB(Integer[] vec2Classify, double[] p0Vec, double[] p1Vec, double pClass1){
		double p1 = 0.0;
		double p0 = 0.0;
		for(int i=0;i<vec2Classify.length;i++){
			p1 = p1 + vec2Classify[i] * p1Vec[i];
			p0 = p0 + vec2Classify[i] * p0Vec[i];
		}
		p1 = p1 + Math.log(pClass1);
		p0 = p0 + Math.log(1.0-pClass1);
		if(p1>p0){
			return 1;
		}else{
			return 0;
		}
	}
	
	
}
