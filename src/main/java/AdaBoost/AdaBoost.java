package AdaBoost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ValUtil;

public class AdaBoost {
	
	public List<String> inequalList = Arrays.asList("lt","gt");
	
	public double[] stumpClassify(double[][] dataMatrix,int dimen,double threshVal,String threshIneq) {
		double [] retArray = new double[dataMatrix.length];
		for(int i=0;i<retArray.length;i++) {
			retArray[i] = 1;
		}
		
		if("lt".equals(threshIneq)) {
			for(int i=0;i<retArray.length;i++) {
				if(dataMatrix[i][dimen] <= threshVal) {
					retArray[i] = -1;
				}
			}
		}else {
			for(int i=0;i<retArray.length;i++) {
				if(dataMatrix[i][dimen] > threshVal) {
					retArray[i] = -1;
				}
			}
		}
		return retArray;
	}
	
	public Map<String,Object> buildStump(double [][] dataArr,double [] classLabels, double [] D) {
		
		Map<String,Object> retMap = new HashMap<String, Object>();
		
		double [][] dataMatrix = dataArr;
		double [] labelMat = classLabels;
		int m = dataMatrix.length;
		int n = dataMatrix[0].length;
		
		double numSteps = 10.0;
		Map<String,Object> bestStump = new HashMap<String,Object>();
		double [] bestClasEst = new double[m];
		
		double minError = Double.MAX_VALUE;
		
		for(int i=0;i<n;i++) {
			double rangeMin = Double.MAX_VALUE;
			double rangeMax = -Double.MAX_VALUE;// min
			for(int j=0;j<m;j++) {
				if(rangeMin > dataMatrix[j][i]) {
					rangeMin = dataMatrix[j][i];
				}
				if(rangeMax < dataMatrix[j][i]) {
					rangeMax = dataMatrix[j][i];
				}
			}
			
			double stepSize = (rangeMax - rangeMin)/numSteps;
			
			for(int j=-1;j<((int)numSteps + 1);j++) {
				
				for(String inequal:inequalList) {
					double threshVal = (rangeMin + (j*1.0)*stepSize);
					
					double[] predictedVals = stumpClassify(dataMatrix, i, threshVal, inequal);
					
					int [] errArr = new int[m];
					for(int k = 0; k < m; k++) {
						if(predictedVals[k] == labelMat[k]) {
							errArr[k] = 0;
						}else {
							errArr[k] = 1;
						}
					}
					
					double weightedError = 0;
					for(int k = 0; k < m; k++) {
						weightedError += D[k] * errArr[k];
					}
					
					if(weightedError < minError) {
						minError = weightedError;
						bestClasEst = predictedVals.clone();
						bestStump.put("dim", i);
						bestStump.put("thresh", threshVal);
						bestStump.put("ineq", inequal);
					}
				}
			}
		}
		
		retMap.put("bestStump", bestStump);
		retMap.put("minError", minError);
		retMap.put("bestClasEst", bestClasEst);
		
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> adaBoostTrainDS(double [][] dataArr,double [] classLabels, int numIt) {
		
		List<Map<String,Object>> weakClassArr = new ArrayList<Map<String,Object>>();
		int m = dataArr.length;
		double [] D = new double[m];
		for(int i=0;i<m;i++) {
			D[i] = 1.0/m;
		}
		double [] aggClassEst = new double[m];
		
		for(int i=1;i<numIt;i++) {
			Map<String,Object> stumpObject = buildStump(dataArr, classLabels, D);
			Map<String,Object> bestStump = (Map<String, Object>) stumpObject.get("bestStump");
			double error = (Double) stumpObject.get("minError");
			double [] classEst = (double[]) stumpObject.get("bestClasEst");
			
			double alpha = 0.5*Math.log((1.0 - error)/Math.max(error, Double.MIN_VALUE));
			bestStump.put("alpha", alpha);
			
			weakClassArr.add(bestStump);
			
			double[] expon = new double[m];
			double dSum = 0;
			for(int j=0;j<m;j++) {
				expon[j] = -1 * alpha * classLabels[j] * classEst[j];
				D[j] = D[j] * (Math.pow(Math.E, expon[j]));
				dSum += D[j];
			}
			
			for(int j=0;j<m;j++) {
				D[j] = (1.0*D[j])/dSum;
			}
			
			for(int j=0;j<m;j++) {
				aggClassEst[j] += alpha * classEst[j];
			}
			
			int [] aggErrors = new int[m];
			int aggErrorsSum = 0;
			for(int j=0;j<m;j++) {
				if(ValUtil.sign(aggClassEst[j]) != classLabels[j]) {
					aggErrors[j] = 1;
				}else {
					aggErrors[j] = 0;
				}
				aggErrorsSum += aggErrors[j]; 
			}
			double errorRate = (1.0*aggErrorsSum)/m;
			System.out.println("total error："+errorRate);
			
			if(errorRate == 0.0)
				break;
		}
		
		return weakClassArr;
	}
	
	public double[] adaClassify(double [][] datToClass, List<Map<String,Object>> classifierArr) {
		double [][] dataMatrix = datToClass;
		int m = dataMatrix.length;
		
		double [] aggClassEst = new double[m];
		
		for (int i=0;i<classifierArr.size();i++) {
			double[] classEst = stumpClassify(dataMatrix,
					(Integer)classifierArr.get(i).get("dim"), 
					(Double)classifierArr.get(i).get("thresh"), 
					(String)classifierArr.get(i).get("ineq"));
			
			for(int j=0;j<m;j++) {
				aggClassEst[j] += ((Double)classifierArr.get(i).get("alpha")) * classEst[j];
			}
		}
		
		double[] ret = new double[m];
		for(int j=0;j<m;j++) {
			ret[j] = ValUtil.sign(aggClassEst[j]);
		}
		return ret;
	}
}
