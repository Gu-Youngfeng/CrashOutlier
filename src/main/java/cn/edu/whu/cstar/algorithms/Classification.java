package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.edu.whu.cstar.utils.ARFFReader;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class Classification {

	private static List<double[]> results = new ArrayList<double[]>();
	
	private static String[] dataPaths = {"jsieve-nominal.arff", "jxpath-nominal.arff", "spatial4j-nominal.arff", "time-nominal.arff"};
	
	public static void main(String[] args) {
	
		for(String path: dataPaths){
			System.out.println("Dealing with <" + path + ">");
			calculate(path);
			System.out.println("");
		}
	}
	
	public static void calculate(String path){
		ARFFReader reader = new ARFFReader(path);
		Instances dataset = reader.getDataset();
		
		RandomForest rf = new RandomForest();
		J48 j48 = new J48();
		IBk ibk = new IBk();
		BayesNet bn = new BayesNet();
		LibSVM svm = new LibSVM();
		
		Classifier[] cls = {rf, j48, ibk, bn, svm};
		for(Classifier clss: cls){
			System.out.printf("| %-15s |", clss.getClass().getSimpleName());
			crossValidation(dataset, 10, clss);	
		}
			
	}
	
	/***
	 * To get validation of each <b>fold</b> using <b>classifier</b> on <b>dataset</b>.
	 * @param dataset
	 * @param fold
	 * @param classifier
	 */
	public static void crossValidation(Instances dataset, int fold, Classifier classifier){
		
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

}
