package Logistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Logistic {
	
	public double sigmoid(double inX){
		double ret = 0.0;
		ret = 1.0/(1+Math.pow(Math.E, -inX));
		return ret;
	}
	
	public double[] stocGradAscent1(double[][] dataMatrix, List<Double> classLabels, int numIter){
		
		Random random = new Random();
		
		int m = dataMatrix.length;
		int n = dataMatrix[0].length;
		
		double[] weights = new double[n];
		for(int wi=0;wi<n;wi++){
			weights[wi] = 1;
		}
		
		for(int j=0;j<numIter;j++){
			List<Integer> dataIndex = new ArrayList<Integer>();
			for(int di=0;di<m;di++){
				dataIndex.add(di);
			}
			
			for(int i=0;i<m;i++){
				double alpha = 4.0/(1.0+j+i)+0.0001;
				
				int randIndex = random.nextInt(dataIndex.size());
				//int index = dataIndex.get(randIndex);// 随机情况下感觉应该用index而不是书上的randIndex
				
				double z = 0.0;
				for(int wi=0;wi<weights.length;wi++){
					z += dataMatrix[randIndex][wi] * weights[wi];
					//z += dataMatrix[index][wi] * weights[wi];
				}
				
				double h = sigmoid(z);
				
				double error = classLabels.get(randIndex)-h;
				//double error = classLabels.get(index)-h;
				
				for(int wi=0;wi<weights.length;wi++){
					weights[wi] = weights[wi] + alpha * error * dataMatrix[randIndex][wi];
					//weights[wi] = weights[wi] + alpha * error * dataMatrix[index][wi];
				}
				
				dataIndex.remove(randIndex);
			}
		}
		return weights;
	}
	
	public double classifyVector(double[] inX,double[] weights){
		double z = 0.0;
		for(int wi=0;wi<weights.length;wi++){
			z += inX[wi] * weights[wi];
		}
		double prob = sigmoid(z);
		//System.out.println(z);
		if(prob > 0.5){
			return 1.0;
		}else{
			return 0.0;
		}
	}
}
