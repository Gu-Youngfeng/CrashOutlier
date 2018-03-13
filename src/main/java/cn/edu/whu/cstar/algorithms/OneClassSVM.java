package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.FilesSearcher;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.ConverterUtils.DataSink;

public class OneClassSVM {
	
	private static List<double[]> results = new ArrayList<double[]>();
	
	/** These data set contains only <b>easy</b> as well as <b>?</b> */
	private static String[] dataPaths = {"files/one-class-test/jsieve-nominal.arff", "files/one-class-test/jxpath-nominal.arff", "files/one-class-test/spatial4j-nominal.arff", "files/one-class-test/time-nominal.arff"};

	/** Project names array*/
	private static String[] proNames = {"jsieve", "jxpath", "spatial4j", "time"};
	
	public static void main(String[] args) throws Exception {
		
		forProject(10);
			
	}
	
	public static void forProject(int fold) throws Exception{
		for(int i=0; i<dataPaths.length; i++){
			Instances data = (new ARFFReader(dataPaths[i])).getDataset(); // get the project dataset
			
			data.randomize(new Random(0));
			data.stratify(fold);
					
			String[] testPaths = FilesSearcher.search("files/", proNames[i]); // find all test set under project
			System.out.println("* Dealing with <" + proNames[i] + ">\n");
			LibSVM svm = new LibSVM();
			String[] options = new String[2];
			options[0] = "-S";
			options[1] = "2";
			
			try {
				svm.setOptions(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for(int j=0; j<fold; j++){
				Instances trains = data.trainCV(fold, j);
				Instances tests = (new ARFFReader(testPaths[j])).getDataset();
				
				/***/
				evaluateTest(trains, tests, svm);
			}
			
			double detectSum = 0.0d;
			double fpSum = 0.0d;
			for(int k=0; k<results.size(); k++){
				detectSum += results.get(k)[0];
				fpSum += results.get(k)[1];
			}
			
			System.out.printf("| One Class SVM | %6.4f | %6.4f |\n", detectSum/10, fpSum/10);
			results.clear();
			
		}
	}
	
	public static void evaluateTest(Instances easyIns, Instances testIns, Classifier cls) throws Exception{

		Instances newTest = changeLabelToEasy(testIns);

		List<INS> lsINS = new ArrayList<INS>();
		for(int i=0; i<testIns.size(); i++){
			INS ins = new INS(testIns.get(i));
			lsINS.add(ins);
//			System.out.println(">>" + ins.getOriginLabel());
		}
		
		cls.buildClassifier(easyIns);
		Evaluation eval= new Evaluation(easyIns);
		eval.evaluateModel(cls, newTest);
		
		for(int i=0; i<newTest.size(); i++){
			double resIns = eval.evaluateModelOnce(cls, newTest.get(i));
//			System.out.println(">> " + resIns);
			lsINS.get(i).setDeLabel(resIns);
			
		}
		
		double tp=0.0d, fn=0.0d, fp=0.0d, tn=0.0d;
		for(int i=0; i<lsINS.size(); i++){
			String flag = lsINS.get(i).getFlag();
			/** print the detection results */
//			System.out.println(lsINS.get(i).getOriginLabel() + " >> " + lsINS.get(i).getDetectLabel() + " -- " + flag);
			if(flag.equals("TP")){
				tp += 1;
			}else if(flag.equals("FN")){
				fn += 1;
			}else if(flag.equals("FP")){
				fp += 1;
			}else{
				tn += 1;
			}
		}
		
		/** print the confusion matrix */
//		System.out.println("TP: " + tp);
//		System.out.println("FN: " + fn);
//		System.out.println("FP: " + fp);
//		System.out.println("TN: " + tn);
		
		double detectRate = tp*1.0/(tp+fn)*1.0;
		double fpRate = fp*1.0/(fp+tn)*1.0;
		double[] res = {detectRate, fpRate};
		results.add(res);
	}
	
	public static void splitNormal(String path){
		
		ARFFReader reader = new ARFFReader(path);
		Instances origin = reader.getDataset();
		
		/************* Add normal data point of origin into normal  **************/
		int numAttr = origin.numAttributes();
		ArrayList<Attribute> lsAttr = new ArrayList<Attribute>();
		for(int i=0; i<numAttr; i++){
			lsAttr.add(origin.attribute(i));
		}
		
		Instances normal = new Instances(path, lsAttr, 1);
		for(Instance ins: origin){
			if(ins.stringValue(numAttr-1).equals("easy")){
				normal.add(ins);
			}
		}
			
		/************** Change the Class label from {difficult,easy} to {easy}  ****************/
		normal = changeLabelToEasy(normal);
		
		/************** Write the normal dataset *************/
		System.out.println(path + ": " + normal.size()); // print the normal data point number
		
		try {
			DataSink.write("src/main/resources/" + selectName(path) + "-easy.arff", normal); //save the normal instances
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static String selectName(String path){
		String namePro = "";
		namePro = path.substring(0, path.lastIndexOf(".arff"));
		return namePro;
	}
	
	/** Change the Class label from {difficult,easy} to {easy} */
	public static Instances changeLabelToEasy(Instances origin){
		
		Instances newTest = new Instances(origin);
		
		int numAttr = newTest.numAttributes();
		
		List<String> m_nominal_values = new ArrayList<String>(3);
		m_nominal_values.add("easy");
		Attribute newFlag = new Attribute("flag", m_nominal_values);
		
		newTest.replaceAttributeAt(newFlag, numAttr-1);
		
		for(Instance ins: newTest){
			ins.setValue(numAttr-1, "easy");
		}
		
		return newTest;
	}

}

class INS{
	Instance ins;
	String orLabel;
	String deLabel;
	String flag;
	
	INS(Instance ins){
		this.ins = ins;
		this.orLabel = ins.stringValue(ins.classAttribute());
	}
	
	public void setDeLabel(double detect){
		if(detect == 0.0d){
			this.deLabel = "easy";
		}else{
			this.deLabel = "difficult";
		}
		setFlag();
	}
	
	public void setFlag(){
		if(this.orLabel.equals("difficult")&&this.deLabel.equals("difficult")){
			this.flag = "TP";
		}else if(this.orLabel.equals("difficult")&&this.deLabel.equals("easy")){
			this.flag = "FN";
		}else if(this.orLabel.equals("easy")&&this.deLabel.equals("difficult")){
			this.flag = "FP";
		}else{
			this.flag = "TN";
		}
	}
	
	public String getFlag(){
		return this.flag;
	}
	
	public String getDetectLabel(){
		return this.deLabel;
	}
	
	public String getOriginLabel(){
		return this.orLabel;
	}
	
} 
