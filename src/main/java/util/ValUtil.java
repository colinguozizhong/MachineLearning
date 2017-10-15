package util;

public class ValUtil {
	
	public static double sign(double val) {
		if(val > 0)
			return 1;
		else if(val == 0)
			return 0;
		else
			return -1;
	}
	
}
