package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.MeasureCalculator;
import weka.core.Instance;
import weka.core.Instances;

/***
 * <p>The <b>Gauss-Based</b> outlier detection is a classical Model-Based (or Statistical-Based) methods.
 * It assume that the instance distribution is under the Guass Distribution, and the instances with 
 * low probability is detected as the outliers. The main steps are as follows,</p>
 * 
 * <li>1. Get mean value <b>&mu;</b> of each dimension (feature).</li>
 * <li>2. Get the standard deviation <b>&sigma;</b> of each dimension (feature).</li>
 * <li>3. Get <b>p(x)</b> of each instance x, and select the top-<b>N</b> instances who
 * has the smallest p(x), they are detected as a outlier. Note that <b>p(x)</b> is defined as follows,</li>
 * 
 * <pre><b>p(x) = &prod; 1/(sqrt(2&pi;)*&sigma;j) * exp((-1)*(xj-&mu;j)^2/(2*&sigma;^2))</b></pre>
 */
public class GaussBased {
	
	private static Instances dataset;
	/** top-N outliers*/
	public static final double N = 0.05;
	
	private static List<GAUNode> nodeset = new ArrayList<GAUNode>();
	
	/**To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.*/
	public GaussBased(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		for(int i=0; i<dataset.numInstances(); i++){
			Instance currentInstance = dataset.get(i);
			GAUNode node = new GAUNode(currentInstance);
			nodeset.add(node);
		}
		
		calculateProbability(path);
		
		rankingByProbability();
		
//		for(int i=0; i<nodeset.size(); i++){
//			System.out.println(i + ">>" + nodeset.get(i).getProbability());
//		}

//		reader.showDataset();
	}
	
	/***
	 * <p> To calculate the probability of each node.</p>
	 * @param path
	 */
	private static void calculateProbability(String path){

		ARFFReader reader = new ARFFReader(path);
		double[] mus = reader.getMu();
		double[] stds = reader.getStd();

		for(int i=0; i<nodeset.size(); i++){
			List<Double> lsAttr = nodeset.get(i).getAttr();
			double pi = 1.0d;
			
			for(int j=0; j<dataset.numAttributes()-1; j++){
				double x_j = lsAttr.get(j);
				double currentMu = mus[j];
				double currentStd = stds[j];
				if(currentStd-0 == 0){ // if some dimension's std is equal to 0.
					continue;
				}
				pi *= (1.0/(Math.sqrt(2.0*Math.PI)*currentStd)) * Math.exp((-1.0)*(x_j - currentMu)*(x_j - currentMu)/(2*currentStd*currentStd));
			}
			nodeset.get(i).setProbability(pi);
		}
	}
	
	private void rankingByProbability(){
		Collections.sort(nodeset, new ProbabilityComparator());
		int topNum = (int)(N*nodeset.size());
		
		for(int i=0; i<topNum; i++){
			nodeset.get(i).setPrelabel("outlier");
		}
	}
	
	/** To show the detection results by HilOut algorithm.*/
	public void showResults(){
		System.out.println("Experiments Results of <" + dataset.relationName() + "> By Using Gauss-Based Outlier Detection Method.");
		System.out.println("\n---------------- Detected Outliers ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(nodeset.get(i).isOutlier())
				System.out.println("probability: " + nodeset.get(i).getProbability() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("\n---------------- Detected Normals ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(!nodeset.get(i).isOutlier())
				System.out.println("probability: " + nodeset.get(i).getProbability() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("----------------------------------");
		
		MeasureCalculator mc = new MeasureCalculator(nodeset);
		
		System.out.println("TP: " + mc.getTP());
		System.out.println("TN: " + mc.getTN());
		System.out.println("FP: " + mc.getFP());
		System.out.println("FN: " + mc.getFN());
		
		System.out.println("PRECISION: " + mc.getPRECISION());
		System.out.println("RECALL: " + mc.getRECALL());
		System.out.println("F-MEASURE: " + mc.getFMEASURE());
		System.out.println("ACCURACY: " + mc.getCORRECTRATIO());
		System.out.println("ACCURACY:" + mc.getCORRECTRATIO());
	}
	
	
}

/***
 * <p>This class <b>GAUNode</b> is used to simulate the characteristic of each instance.</p>
 * <p></p>
 *
 */
class GAUNode extends CrashNode{
	
	private String label; // class label
	
	private String prelabel = "normal"; // outlier or normal
	
	private List<Double> lsAttr = new ArrayList<Double>(); // feature list
	
	private double probability = 0.0d; // weight value
	
	/**To initialize the instance with features and class label */
	GAUNode(Instance instance){
		int lenAttr = instance.numAttributes();
		label = instance.stringValue(lenAttr-1); // set true label
		for(int i=0; i<lenAttr-1; i++){ // set feature-values
			lsAttr.add(instance.value(i));
		}
	}
	
	/**<p>To get <b>feature-values</b> of instance. */
	public List<Double> getAttr(){
		return lsAttr;
	}
	
	/**To save predicted flag, i.e., '<b>normal</b>' or '<b>outlier</b>'.*/
	public void setPrelabel(String flag){
		this.prelabel = flag;
	}
	
	/**To get the original class label.*/
	@Override
	public String getLabel(){
		return label;
	}
	
	/**To judge whether the instance is predicted as a outlier. */
	@Override
	public boolean isOutlier(){
		if(prelabel == "outlier"){
			return true;
		}else{
			return false;
		}
	}
	
	public void setProbability(double p){
		this.probability = p;
	}
	
	public double getProbability(){
		return this.probability;
	}			
}

class ProbabilityComparator implements Comparator<GAUNode>{

	public int compare(GAUNode o1, GAUNode o2) {
		if(o1.getProbability() > o2.getProbability()){
			return 1;
		}else if(o1.getProbability() < o2.getProbability()){
			return -1;
		}else{
			return 0;
		}
	}	
}

