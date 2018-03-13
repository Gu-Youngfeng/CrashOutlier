package cn.edu.whu.cstar.algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.edu.whu.cstar.utils.ARFFReader;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.MetaCost;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;

/***
 * Supervised outlier detection includes <b>Cost-sensitive Learning, Resampling, and One-class SVM</b> 
 *
 */
public class SOutlierDetection {

	private static List<double[]> results = new ArrayList<double[]>();
	
	private static String[] dataPaths = {"jsieve-nominal.arff", "jxpath-nominal.arff", "spatial4j-nominal.arff", "time-nominal.arff"};

	public static void main(String[] args) throws Exception {
		
		for(String paths: dataPaths){			
			System.out.println("* Dealing with <" + paths + ">");
			calculate(paths);
			System.out.println("");

		}
	}
	
	public static void calculate(String path) throws Exception{
		ARFFReader reader = new ARFFReader(path);
		Instances dataset = reader.getDataset();
		
		MetaCost mc = new MetaCost();
		FileReader fr1 = new FileReader(new File("src/main/resources/costm"));
		mc.setCostMatrix(new CostMatrix(fr1));
		mc.setClassifier(new RandomForest());
		
		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		FileReader fr2 = new FileReader(new File("src/main/resources/costm"));
		csc.setCostMatrix(new CostMatrix(fr2));
		csc.setClassifier(new RandomForest());
//		csc.setClassifier(new J48());
		
		RandomForest rf = new RandomForest();
		System.out.printf("|%-25s|", "Resampling");
		crossValidationResample(dataset, rf, 10);
		
		System.out.printf("|%-25s|", "SMOTE");
		crossValidationSMOTE(dataset, rf, 10);
		
		Classifier[] cls = {mc, csc};
		for(int i=0; i<cls.length; i++){
//		for(int i=1; i<2; i++){
			System.out.printf("|%-25s|", cls[i].getClass().getSimpleName());
			crossValidation(dataset, cls[i], 10);
		}
		

	}
	
	public static void crossValidation(Instances dataset, Classifier classifier, int fold){
		
		dataset.randomize(new Random(0));
		dataset.stratify(fold);
		
		for(int i=0; i<fold; i++){
			Instances trains = dataset.trainCV(fold, i);
			Instances tests = dataset.testCV(fold, i);
				
			try {		
				classifier.buildClassifier(trains);
				Evaluation eval = new Evaluation(trains);
				eval.evaluateModel(classifier, tests);
//				System.out.println(eval.toMatrixString());
				double tp = eval.numTruePositives(0);
				double fn = eval.numFalseNegatives(0);
				double fp = eval.numFalsePositives(0);
				double tn = eval.numTrueNegatives(0);
				
//				System.out.println(tp + "," + fn +"," + fp + "," +tn);
				double detectRatio = tp*1.0/(tp+fn)*1.0;
				double fpRatio = fp*1.0/(fp+tn)*1.0;
				
				double[] res = {detectRatio, fpRatio};
				results.add(res); // add results into results List
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		double dr = 0.0d;
		double fr = 0.0d;
		
		for(double[] res: results){
//			System.out.println("detect ratio: " + res[0] + ", fp ratio: " + res[1]);
			dr += res[0];
			fr += res[1];
		}
		System.out.printf("%6.4f | %6.4f |\n", dr/10, fr/10);
		results.clear();
	}

	public static void crossValidationResample(Instances dataset, Classifier classifier, int fold){
		
		dataset.randomize(new Random(0));
		dataset.stratify(fold);
		
		for(int i=0; i<fold; i++){
			Instances trains = dataset.trainCV(fold, i);
			Instances tests = dataset.testCV(fold, i);
					
			try {
				Resample resample = new Resample();
				resample.setInputFormat(trains);
				trains = Filter.useFilter(trains, resample);
				
				classifier.buildClassifier(trains);
				Evaluation eval = new Evaluation(trains);
				eval.evaluateModel(classifier, tests);
//				System.out.println(eval.toMatrixString());
				double tp = eval.numTruePositives(0);
				double fn = eval.numFalseNegatives(0);
				double fp = eval.numFalsePositives(0);
				double tn = eval.numTrueNegatives(0);
				
//				System.out.println(tp + "," + fn +"," + fp + "," +tn);
				double detectRatio = tp*1.0/(tp+fn)*1.0;
				double fpRatio = fp*1.0/(fp+tn)*1.0;
				
				double[] res = {detectRatio, fpRatio};
				results.add(res); // add results into results List
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		double dr = 0.0d;
		double fr = 0.0d;
		
		for(double[] res: results){
//			System.out.println("detect ratio: " + res[0] + ", fp ratio: " + res[1]);
			dr += res[0];
			fr += res[1];
		}
		System.out.printf("%6.4f | %6.4f |\n", dr/10, fr/10);
		results.clear();
	}

	public static void crossValidationSMOTE(Instances dataset, Classifier classifier, int fold){
		
		dataset.randomize(new Random(0));
		dataset.stratify(fold);
		
		for(int i=0; i<fold; i++){
			Instances trains = dataset.trainCV(fold, i);
			Instances tests = dataset.testCV(fold, i);
					
			try {
				SMOTE smote = new SMOTE();
				smote.setInputFormat(trains);
				trains = Filter.useFilter(trains, smote);
				
				classifier.buildClassifier(trains);
				Evaluation eval = new Evaluation(trains);
				eval.evaluateModel(classifier, tests);
	//			System.out.println(eval.toMatrixString());
				double tp = eval.numTruePositives(0);
				double fn = eval.numFalseNegatives(0);
				double fp = eval.numFalsePositives(0);
				double tn = eval.numTrueNegatives(0);
				
	//			System.out.println(tp + "," + fn +"," + fp + "," +tn);
				double detectRatio = tp*1.0/(tp+fn)*1.0;
				double fpRatio = fp*1.0/(fp+tn)*1.0;
				
				double[] res = {detectRatio, fpRatio};
				results.add(res); // add results into results List
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		double dr = 0.0d;
		double fr = 0.0d;
		
		for(double[] res: results){
	//		System.out.println("detect ratio: " + res[0] + ", fp ratio: " + res[1]);
			dr += res[0];
			fr += res[1];
		}
		System.out.printf("%6.4f | %6.4f |\n", dr/10, fr/10);
		results.clear();
	}
}
