package CART;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import Jama.Matrix;

public class CART {
	
	public Map<String,double[][]> linearSolve(double [][] dataSet) {
		int m = dataSet.length;
		int n = dataSet[0].length;
		
		double [][] X = new double[m][n];
		double [][] Y = new double[m][1];
		
		for(int i=0;i<m;i++) {
			for(int j=0;j<n;j++) {
				X[i][j] = 1;
			}
			Y[i][0] = 1;
		}
		for(int i=0;i<m;i++) {
			for(int j=1;j<n;j++) {
				X[i][j] = dataSet[i][j-1];
			}
			Y[i][0] = dataSet[i][n-1];
		}
		/*
		double[][] XT = new double[n][m];
		for(int i=0;i<n;i++) {
			for(int j=0;j<m;j++) {
				XT[i][j] = XT[j][i];
			}
		}
		
		double[][] xTx = new double[n][n];
		for(int i=0;i<n;i++) {
			for(int k=0;k<n;k++) {
				double temp = 0;
				for(int j=0;j<m;j++) {
					temp += XT[i][j]*X[j][k];
				}
				xTx[i][k] = temp;
			}
		}
		
		Matrix denomMat = new Matrix(xTx);
		if(denomMat.det() == 0) {
			System.out.println("This matrix is singular, cannot do inverse,\\n\\\r\n" + 
					"        try increasing the second value of ops");
		}
		*/
		
		
		Matrix xMat = new Matrix(X);
		Matrix yMat = new Matrix(Y);
		Matrix xMatT = xMat.transpose();
		Matrix xTxMat = xMatT.times(xMat);
		if(xTxMat.det() == 0) {
			System.out.println("This matrix is singular, cannot do inverse,\\n\\\r\n" + 
					"        try increasing the second value of ops");
		}
		Matrix wsMat = xTxMat.inverse().times((xMatT.times(yMat)));// 一维
		double [][] ws = wsMat.getArrayCopy();
		
		Map<String,double[][]> ret = new HashMap<String,double[][]>();
		ret.put("ws", ws);
		ret.put("X", X);
		ret.put("Y", Y);
		return ret;
	}
	
	public Map<String,double[][]> binSplitDataSet(double[][] dataSet, int feature, double value) {
		List<double[]> mat0List = new ArrayList<double[]>();
		List<double[]> mat1List = new ArrayList<double[]>();
		for(int i=0;i<dataSet.length;i++) {
			if(dataSet[i][feature] > value) {
				mat0List.add(dataSet[i]);
			}else {
				mat1List.add(dataSet[i]);
			}
		}
		double[][] mat0 = new double[mat0List.size()][];
		for(int i=0;i<mat0List.size();i++) {
			mat0[i] = mat0List.get(i);
		}
		
		double[][] mat1 = new double[mat1List.size()][];
		for(int i=0;i<mat1List.size();i++) {
			mat1[i] = mat1List.get(i);
		}
		
		Map<String,double[][]> ret = new HashMap<String,double[][]>();
		ret.put("mat0", mat0);
		ret.put("mat1", mat1);
		
		return ret;
	}
	
	//# 回归树叶节点生成函数
	public double regLeaf(double [][] dataSet) {
		double sum = 0;
		
		for(int i=0;i<dataSet.length;i++) {
			sum += dataSet[i][dataSet[i].length-1];
		}
		
		return sum/dataSet.length;
	}
	
	public double regErr(double[][] dataSet) {
		// 目标变量的均值
		double sum = 0;
		for(int i=0;i<dataSet.length;i++) {
			sum += dataSet[i][dataSet[i].length-1];
		}
		double avg = sum/dataSet.length;
		
		sum = 0;
		for(int i=0;i<dataSet.length;i++) {
			sum += Math.pow(dataSet[i][dataSet[i].length-1] - avg,2);
		}
		double sumVar = sum/dataSet.length;
		
		return sumVar * dataSet.length;
	}
	
	public double[][] modelLeaf(double[][] dataSet) {
		Map<String,double[][]> data = linearSolve(dataSet);
		double[][] ws = data.get("ws");
		double[][] X = data.get("X");
		double[][] Y = data.get("Y");
		
		return ws;
	}
	
	public double modelErr(double[][] dataSet) {
		Map<String,double[][]> data = linearSolve(dataSet);
		double[][] ws = data.get("ws");
		double[][] X = data.get("X");
		double[][] Y = data.get("Y");
		
		Matrix XMat = new Matrix(X);
		Matrix wsMat = new Matrix(ws);
		Matrix yHatMat = XMat.times(wsMat);
		double [][] yHat = yHatMat.getArrayCopy();
		
		double sum = 0;
		for(int i=0;i<Y.length;i++) {
			sum += Math.pow((Y[i][0] - yHat[i][0]), 2);
		}
		return sum;
		
	}
	

	public Map<String,Object> chooseBestSplit(double[][] dataSet,String leafType,String errType,double[] ops) {
		double tolS = ops[0];
		int tolN = (int)ops[1];
		
		Map<String,Object> ret = new HashMap<String,Object>();
		
		if(dataSet.length == 1) {
			// return Node,leafType(dataSet)
			ret.put("feat", null);
			if("regLeaf".equals(leafType)) { 
				ret.put("val", regLeaf(dataSet)); 
			}else if("modelLeaf".equals(leafType)) {
				ret.put("val", modelLeaf(dataSet));
			}
			return ret;
		}
		
		int m = dataSet.length;
		int n = dataSet[0].length;
		
		double S = 0;
		if("regErr".equals(errType)) {
			S = regErr(dataSet);
		}else if("modelErr".equals(errType)) {
			S = modelErr(dataSet);
		}
		double bestS = Double.MAX_VALUE;
		int bestIndex = 0;
		double bestValue = 0;
		
		for(int featIndex = 0;featIndex<n-1;featIndex++) {
			Set<Double> treeSet=new TreeSet<Double>();
			for(int rowIndex = 0;rowIndex < dataSet.length;rowIndex++) {
				treeSet.add(dataSet[rowIndex][featIndex]);
			}
			for(Double setval : treeSet) {
				double splitVal = setval.doubleValue();
				Map<String,double[][]> mat = binSplitDataSet(dataSet,featIndex,splitVal);
				double[][] mat0 = mat.get("mat0");
				double[][] mat1 = mat.get("mat1");
				if(mat0.length < tolN || mat1.length < tolN)
					continue;
				
				double newS = Double.MAX_VALUE;
				if("regErr".equals(errType)) {
					newS = regErr(mat0) + regErr(mat1);
				}else if("modelErr".equals(errType)) {
					newS = modelErr(mat0) + modelErr(mat1);
				}
				
				if(newS < bestS) {
					bestIndex = featIndex;
					bestValue = splitVal;
					bestS = newS;
				}
			}
		}
		
		if((S - bestS) < tolS) {
			// return Node,leafType(dataSet);
			ret.put("feat", null);
			if("regLeaf".equals(leafType)) { 
				ret.put("val", regLeaf(dataSet)); 
			}else if("modelLeaf".equals(leafType)) {
				ret.put("val", modelLeaf(dataSet));
			}
			return ret;
		}
		Map<String,double[][]> mat = binSplitDataSet(dataSet,bestIndex,bestValue);
		double[][] mat0 = mat.get("mat0");
		double[][] mat1 = mat.get("mat1");
		if(mat0.length < tolN || mat1.length < tolN) {
			// return Node,leafType(dataSet);
			ret.put("feat", null);
			if("regLeaf".equals(leafType)) { 
				ret.put("val", regLeaf(dataSet)); 
			}else if("modelLeaf".equals(leafType)) {
				ret.put("val", modelLeaf(dataSet));
			}
			return ret;
		}
		
		ret.put("feat", bestIndex);
		ret.put("val", bestValue); 
		return ret;
		
	}
	
	public Map<String,Object> createTree(double[][] dataSet,String leafType,String errType,double[] ops){
		
		Map<String,Object> retTree = new HashMap<String,Object>();
		
		Map<String,Object> ret = new HashMap<String,Object>();
		ret = chooseBestSplit(dataSet,leafType,errType,ops);
		Integer feat = (Integer) ret.get("feat");
		if(feat == null){
			if("regLeaf".equals(leafType)) { 
				double val = (Double) ret.get("val");
				retTree.put("spInd", null);
				retTree.put("spVal", val);
				retTree.put("left", null);
				retTree.put("right", null);
				return retTree;
			}else if("modelLeaf".equals(leafType)) {
				double[][] val = (double[][]) ret.get("val");
				retTree.put("spInd", null);
				retTree.put("spVal", val);
				retTree.put("left", null);
				retTree.put("right", null);
				return retTree;
			}
		}
		
		retTree.put("spInd", feat.intValue());
		double val = (Double) ret.get("val");
		retTree.put("spVal", val);
		Map<String,double[][]> mat = binSplitDataSet(dataSet,feat.intValue(),val);
		double[][] lSet = mat.get("mat0");
		double[][] rSet = mat.get("mat1");
		
		retTree.put("left", createTree(lSet, leafType, errType, ops));
		retTree.put("right", createTree(rSet, leafType, errType, ops));
		return retTree;
	}
	
	public double regTreeEval(double model,double[][] inDat){
		return model;
	}
	
	public double modelTreeEval(double[][] model,double[][] inDat){
		int n = inDat[0].length;
		double [][] X = new double[inDat.length][n+1];
		for(int i=0;i<inDat.length;i++){
			X[i][0] = 1;
			for(int j=1;j<n+1;j++){
				X[i][j] = inDat[i][j-1];
			}
		}
		Matrix XMat = new Matrix(X);
		Matrix modelMat = new Matrix(model);
		Matrix ret = XMat.times(modelMat);
		return ret.getArrayCopy()[0][0];
	}
	
	public boolean isTree(Map<String,Object> obj){
		if(obj.get("spInd") == null){
			return false;
		}else{
			return true;
		}
	}
	
	public double treeForeCast(Map<String,Object> tree,double [][] inData,String modelEval){
		if(!isTree(tree)){
			if("regTreeEval".equals(modelEval)){
				double spVal = ((Double) tree.get("spVal")).doubleValue();
				return regTreeEval(spVal,inData);
			}else if("modelTreeEval".equals(modelEval)){
				double[][] spVal = (double[][]) tree.get("spVal");
				return modelTreeEval(spVal,inData);
			}
		}
		
		if(inData[0][(Integer)tree.get("spInd")]>(Double)tree.get("spVal")){
			if(isTree((Map<String,Object>)tree.get("left"))){
				return treeForeCast((Map<String,Object>)tree.get("left"),inData,modelEval);
			}else{
				if("regTreeEval".equals(modelEval)){
					double spVal = ((Double) ((Map<String,Object>)tree.get("left")).get("spVal")).doubleValue();
					return regTreeEval(spVal,inData);
				}else if("modelTreeEval".equals(modelEval)){
					double[][] spVal = (double[][]) ((Map<String,Object>)tree.get("left")).get("spVal");
					return modelTreeEval(spVal,inData);
				}
			}
		}else{
			if(isTree((Map<String,Object>)tree.get("right"))){
				return treeForeCast((Map<String,Object>)tree.get("right"),inData,modelEval);
			}else{
				if("regTreeEval".equals(modelEval)){
					double spVal = ((Double) ((Map<String,Object>)tree.get("right")).get("spVal")).doubleValue();
					return regTreeEval(spVal,inData);
				}else if("modelTreeEval".equals(modelEval)){
					double[][] spVal = (double[][]) ((Map<String,Object>)tree.get("right")).get("spVal");
					return modelTreeEval(spVal,inData);
				}
			}
		}
		return 0;
	}
	
	public double [][] createForeCast(Map<String,Object> tree,double [][] testData,String modelEval){
		int m = testData.length;
		double [][] yHat = new double[m][1];
		for(int i=0;i<m;i++){
			double [][] tmp = new double[1][testData[i].length];
			tmp[0] = testData[i];
			yHat[i][0] = treeForeCast(tree,tmp,modelEval);
		}
		return yHat;
	}
}
