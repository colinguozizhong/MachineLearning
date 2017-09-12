package KNN;

public class TestKNN {
	public static void main(String[] args) throws Exception {
		
		double hoRatio = 0.10;
		
		KNN knn = new KNN();
		knn.file2matrix(System.getProperty("user.dir")+"\\src\\test\\java\\KNN\\datingTestSet2.txt");
		knn.autoNorm();
		
		int m = knn.normDataSet.length;
		int numTestVecs = (int) (m*hoRatio);
		
		int errorCount = 0;
		double [][] normMat = new double[m-numTestVecs][knn.normDataSet[0].length];
		double [] datingLabels = new double[m-numTestVecs];
		System.out.println(m-numTestVecs);
		for(int i=0;i<(m-numTestVecs);i++){
			normMat[i] = knn.normDataSet[i+numTestVecs];
			datingLabels[i] = knn.classLabelVector[i+numTestVecs];
		}
		
/*		for(int i=0;i<numTestVecs;i++){
			for(int j=0;j<knn.normDataSet[i].length;j++){
				System.out.print(knn.normDataSet[i][j]+"   ");
			}
			System.out.println();
		}*/
		
/*		for(int i=0;i<datingLabels.length;i++){
			System.out.println(datingLabels[i]);
		}*/
		
		for(int i=0;i<numTestVecs;i++){
			double classifierResult = knn.classify0(knn.normDataSet[i],normMat,datingLabels,3); 
			System.out.println("the classifier came back with: "+classifierResult+", the real answer is:"+knn.classLabelVector[i]);
			if(classifierResult!=knn.classLabelVector[i]){
				errorCount++;
			}
		}
		System.out.println("the total error rate is: "+(1.0*errorCount/numTestVecs));
		
		//knn.print();
	}
}
