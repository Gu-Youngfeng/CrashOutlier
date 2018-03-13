package cn.edu.whu.cstar.utils;

import java.util.Random;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

/***
 * <p>This class <b>DataSplit</b> is used to split the dataset into Training set and testing set
 * in each fold of cross-validation.
 * </p>
 *
 */
public class DataSplit {
	
	private static String[] dataPaths = {"jsieve-nominal.arff", "jxpath-nominal.arff", "spatial4j-nominal.arff", "time-nominal.arff"};

	public static void main(String[] args) {
		
		for(String path: dataPaths){
			generateTest(path, 10);
		}
	}
	
	/***
	 * <p>To get training set in each fold. The generated training sets are saved in <b>files</b> folder.</p>
	 * @param path dataset to be split
	 * @param fold cross-validation folds
	 */
	public static void generateTrain(String path, int fold){
		
		ARFFReader reader = new ARFFReader(path);
		Instances dataset = reader.getDataset();
		
		String name = dataset.relationName();
		
		dataset.randomize(new Random(0));
		dataset.stratify(fold);
		
		for(int i=0; i<fold; i++){
			Instances trains = dataset.trainCV(fold, i);
			try {
				DataSink.write("files/" + name + "-train-" + i + ".arff", trains);
			} catch (Exception e) {
				System.out.println("generate train arff error!");
				e.printStackTrace();
			}
		}
		System.out.println("Already generate " + fold + " training set!");
	}
	
	/***
	 * <p>To get testing set in each fold. The generated testing sets are saved in <b>files</b> folder.</p>
	 * @param path dataset to be split
	 * @param fold cross-validation folds
	 */
	public static void generateTest(String path, int fold){
		
		ARFFReader reader = new ARFFReader(path);
		Instances dataset = reader.getDataset();
		
		String name = dataset.relationName();
		
		dataset.randomize(new Random(0));
		dataset.stratify(fold);
		
		for(int i=0; i<fold; i++){
			Instances tests = dataset.testCV(fold, i);
			
			try {
				DataSink.write("files/" + name + "-test-" + i + ".arff", tests);
			} catch (Exception e) {
				System.out.println("generate test arff error!");
				e.printStackTrace();
			}
		}
		System.out.println("Already generate " + fold + " testing set!");
	}

}
