package RegressionForecasting;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class TestTest {
	public static void main(String[] args) {  
        double[][] a = {  
                {1,1,0},  
                {0,1,0},  
                {0,0,0}  
        };  
          
        //利用二维数组创建矩阵  
        Matrix matrix = new Matrix(a);  
          
        //打印矩阵，第一个参数是每一列的宽度，第二个参数是保留的小数点位数  
        matrix.print(3, 2);  
          
        //求矩阵的逆  
      //  matrix.inverse().print(3,2);  
          
        //求矩阵的转置  
        matrix.transpose().print(3,2);  
          
        //矩阵乘法  
        matrix.transpose().times(matrix).print(3,2);
        
        System.out.println(matrix.det());
        SingularValueDecomposition SVD = matrix.svd();
        System.out.println(SVD.cond());
        SVD.getSingularValues();
    }  

}
