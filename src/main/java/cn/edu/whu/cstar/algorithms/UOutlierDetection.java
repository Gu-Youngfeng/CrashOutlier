package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;

import cn.edu.whu.cstar.utils.FilesSearcher;

/***
 * Unsupervised outlier detection includes <b>GaussBased, HilOut, DBSCAN, LOF, IsolationForest.</b> 
 *
 */
public class UOutlierDetection {

	public static List<double[]> results = new ArrayList<double[]>();	
	
	private static String[] RelName = {"jsieve", "jxpath", "spatial4j", "time"};
	
	private static String[] methodNames = {"--", "GaussBased", "HilOut", "DBSCAN", "LOF", "IsolationForest"};
	
	public static void main(String[] args) {
			
		for(String name: RelName){ // for each project
			System.out.println("* Dealing with <" + name + ">");
			for(int index =1; index <6; index++){
				System.out.printf("|%-15s", methodNames[index]);
				forProject(name, index);
			}
		}
		
		
	}
	
	public static void forProject(String name, int index){
		String[] dataPaths = FilesSearcher.search("files", name);
		for(String paths: dataPaths){ // for each fold test of project
			forMethods(paths, index);
		}
		double detectSum = 0;
		double fpSum = 0;
		for(double[] res: results){
			detectSum += res[0];
			fpSum += res[1];
		}
//		System.out.println("--------------------");
		System.out.printf("| %6.4f | %6.4f |\n", detectSum/10, fpSum/10);
		
		results.clear();
	}
	
	public static void forMethods(String paths, int flag){
		
		double detectRate = 0.0d;
		double fpRate = 0.0d;
		double[] res = new double[2];
		
		switch(flag){
		case 5:
			IsolationForests isf = new IsolationForests(paths);
			detectRate = isf.getDetectionRate();
			fpRate = isf.getFPRate();
			break;
		case 4:
			LOFs lof = new LOFs(paths);
			detectRate = lof.getDetectionRate();
			fpRate = lof.getFPRate();
			break;
		case 3:
			DBSCANs dbs = new DBSCANs(paths);
			detectRate = dbs.getDetectionRate();
			fpRate = dbs.getFPRate();
			break;
		case 2:
			HilOut hil = new HilOut(paths);
			detectRate = hil.getDetectionRate();
			fpRate = hil.getFPRate();
			break;
		case 1:
			GaussBased gb = new GaussBased(paths);
			detectRate = gb.getDetectionRate();
			fpRate = gb.getFPRate();
			break;
		default:
			System.out.println("METHODS CANNOT FOUND! <check the methods index.>");
			break;
		}
		res[0] = detectRate;
		res[1] = fpRate;
		results.add(res);
	}
	
	

}
