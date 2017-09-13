package KNN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNN {	
	double [][] normDataSet;
	
	double [][] returnMat;
	
	double [] classLabelVector;
	
	double [][] returnMatMinMax;
	double [] ranges;
	
	public void print(){
		System.out.print("returnMat:\n[");
		for(int i = 0;i<returnMat.length;i++){
			System.out.print("[");
			
			for(int j=0;j<returnMat[i].length;j++){
				if(j==0){
					System.out.print("");
				}else{
					System.out.print(",");
				}
				System.out.print(returnMat[i][j]);
			}
			
			System.out.println("]");
		}
		System.out.println("]\n");
		
		
		System.out.print("classLabelVector:\n[");
		for(int j=0;j<classLabelVector.length;j++){
			if(j==0){
				System.out.print("");
			}else{
				System.out.print(",");
			}
			System.out.print(classLabelVector[j]);
		}
		System.out.println("]\n");
		
		
		System.out.print("normDataSet:\n[");
		for(int i = 0;i<normDataSet.length;i++){
			System.out.print("[");
			
			for(int j=0;j<normDataSet[i].length;j++){
				if(j==0){
					System.out.print("");
				}else{
					System.out.print(",");
				}
				System.out.print(normDataSet[i][j]);
			}
			
			System.out.println("]");
		}
		System.out.println("]\n");
	}
	
	@Override
	public String toString() {
		return "KNN [returnMat=" + Arrays.toString(returnMat)
				+ ", classLabelVector=" + Arrays.toString(classLabelVector)
				+ "]";
	}

	public void file2matrix(String filename) throws Exception{
		// 打开数据文件，读取每行内容
		BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String str = "";
		List<String> list = new ArrayList<String>();
		while ((str = textBuffer.readLine()) != null) {
			list.add(str);
		}
		
		returnMat = new double[list.size()][3];
		classLabelVector = new double[list.size()];
		
		for(int i=0;i<list.size();i++){
			String line = list.get(i);
			String [] dStr = line.trim().split("\t");
			returnMat[i][0] = Double.valueOf(dStr[0].trim()).doubleValue();
			returnMat[i][1] = Double.valueOf(dStr[1].trim()).doubleValue();
			returnMat[i][2] = Double.valueOf(dStr[2].trim()).doubleValue();
			classLabelVector[i] = Double.valueOf(dStr[3].trim()).doubleValue();
		}
		
		
		
/*		while ((str = textBuffer.readLine()) != null) {
			String [] dStr = str.trim().split("\t");
			double [] d = new double[dStr.length];
			for(int i=0;i<dStr.length;i++){
				d[i] = Double.valueOf(dStr[i]);
				System.out.print(d[i]);
				System.out.print("   ");
			}
			System.out.println();
		}*/
	
	}
	
	
	public void autoNorm(){
		
		returnMatMinMax = new double[returnMat[0].length][2];
		for(int i=0;i<returnMatMinMax.length;i++){
			returnMatMinMax[i][0] = 1.7976931348623157E308;
			returnMatMinMax[i][1] = 4.9E-324;
		}
		
		for(int i=0;i<returnMat.length;i++){
			for(int j=0;j<returnMat[i].length;j++){
				if(returnMat[i][j] < returnMatMinMax[j][0]){
					returnMatMinMax[j][0] = returnMat[i][j];
				}
				if(returnMat[i][j] > returnMatMinMax[j][1]){
					returnMatMinMax[j][1] = returnMat[i][j];
				}
			}
		}
		
		ranges = new double[returnMatMinMax.length];
		for(int i=0;i<returnMatMinMax.length;i++){
			ranges[i] = returnMatMinMax[i][1] - returnMatMinMax[i][0];
		}
		
		normDataSet = new double[returnMat.length][3];
		// (旧-小)/(大-小)
		for(int i=0;i<returnMat.length;i++){
			for(int j=0;j<returnMat[i].length;j++){
				normDataSet[i][j] = (returnMat[i][j] - returnMatMinMax[j][0])/ranges[j];
			}
		}
	}
	
	
	public double classify0(double[] intX,double[][] dataSet,double[] labels,int k){
		int dataSetSize = dataSet.length;
		
		double[][] diffMat = new double[dataSet.length][intX.length];
		double[][] sqDiffMat = new double[dataSet.length][intX.length];
		double[] sqDistances = new double[dataSet.length];
		double[] distances = new double[dataSet.length];
		
		List<Map<String,Object>> sortList = new ArrayList<Map<String,Object>>();
		
		for(int i=0;i<dataSet.length;i++){
			// (A1-B1)*(A1-B1)+(A2-B2)*(A2-B2)+(A3-B3)*(A3-B3) 再开平方
			double sum = 0;
			for(int j=0;j<intX.length;j++){
				diffMat[i][j] = intX[j] - dataSet[i][j];
				sqDiffMat[i][j] = Math.pow(diffMat[i][j], 2);
				sum = sum + Math.pow(intX[j] - dataSet[i][j], 2); 
			}
			
			for(int j=0;j<intX.length;j++){
				if(j==0){
					sqDistances[i] = sqDiffMat[i][j];
				}else{
					sqDistances[i] = sqDistances[i] + sqDiffMat[i][j];
				}
			}
			// distances[i] = Math.pow(sqDistances[i],0.5);
			distances[i] = Math.pow(sum,0.5); 
			
			Map<String,Object> o = new HashMap<String,Object>();
			o.put("index", i);
			o.put("value", distances[i]);
			sortList.add(o);
		}
		
		// 
		Collections.sort(sortList, new Comparator<Map<String,Object>>(){

			public int compare(Map<String,Object> arg0, Map<String,Object> arg1) {
			//	return ((Double)arg0.get("value")).compareTo((Double)arg1.get("value"));
				if((Double)arg0.get("value") > (Double)arg1.get("value"))
					return 1;
				else if((Double)arg0.get("value") < (Double)arg1.get("value"))
					return -1;
				else 
					return 0;
			}
			
		});
		
		Map<Double,Integer> classCount = new HashMap<Double,Integer>();
		for(int i=0;i<k;i++){
			HashMap<String,Object> o = (HashMap<String, Object>) sortList.get(i);
			
			double voteIlabel = labels[(Integer)o.get("index")];
			if(classCount.containsKey(voteIlabel)){
				classCount.put(voteIlabel, classCount.get(voteIlabel)+1);
			}else{
				classCount.put(voteIlabel, 1);
			}
		}
		
		// 对classCount排序
		// 通过ArrayList构造函数把map.entrySet()转换成list
        List<Map.Entry<Double,Integer>> maplist = new ArrayList<Map.Entry<Double,Integer>>(classCount.entrySet());
        // 通过比较器实现比较排序
        Collections.sort(maplist, new Comparator<Map.Entry<Double,Integer>>() {
            public int compare(Map.Entry<Double,Integer> mapping1, Map.Entry<Double,Integer> mapping2) {
                return mapping2.getValue().compareTo(mapping1.getValue());
            }
        });
 
        return maplist.get(0).getKey();
	}
	
}

