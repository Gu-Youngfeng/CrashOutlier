package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;

import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.MeasureCalculator;
import weka.classifiers.misc.IsolationForest;
import weka.core.Instance;
import weka.core.Instances;

/***
 * <p><b>Isolation Forest</b> is a classical model-based outlier detection algorithm which can be used 
 * in a large and high-dimension dataset. It's provided by Liu et al. in 2008.</p> 
 *
 * <li>1. Randomly selecting some subsets of dataset.</li>
 * <li>2. Using these subsets to construct <b>iTrees</b> by randomly split a randomly selected feature.</li>
 * <li>3. The nodes(instances) that have the longer <b>average path length</b> are detected as outliers.</li>
 */
public class IsolationForests {
	
	private static Instances dataset;
	/** Number of instances in each iTree*/
	public static final int SubSampleSize = 40;
	/** Number of iTrees*/
	public static final int TreeNum = 25;
	/** Anomaly score threshold*/
	public static final double s = 0.62;
	
	private static List<IFNode> nodeset = new ArrayList<IFNode>();
	
	/**To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.*/
	public IsolationForests(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		
		for(int i=0; i<dataset.numInstances(); i++){
			Instance currentInstance = dataset.get(i);
			IFNode node = new IFNode(currentInstance);
			nodeset.add(node);
		}
		
		try {
			setAnomalyScore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
	
	public void setAnomalyScore() throws Exception{
		
		IsolationForest iforest = new IsolationForest();
		iforest.setNumTrees(TreeNum);
		iforest.setSubsampleSize(SubSampleSize);
		iforest.buildClassifier(dataset);
		
		for(int i=0; i<nodeset.size(); i++){
			double score = iforest.distributionForInstance(dataset.get(i))[1];
			nodeset.get(i).setScore(score);
			if(score >= s){
				nodeset.get(i).setPrelabel("outlier");	
			}
		}
		
	}
	
	/** To show the detection results by HilOut algorithm.
	 * @throws Exception */
	public void showResults(){
		
		System.out.println("\nExperiments Results of <" + dataset.relationName() + "> By Using Isolation Forest Outlier Detection Method.");
		System.out.println("\n---------------- Detected Outliers ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(nodeset.get(i).isOutlier())
				System.out.println(nodeset.get(i).getScore() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("\n---------------- Detected Normals ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(!nodeset.get(i).isOutlier())
				System.out.println(nodeset.get(i).getScore() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("----------------------------------");

		MeasureCalculator mc = new MeasureCalculator(nodeset);
		
		System.out.println("TP:" + mc.getTP());
		System.out.println("TN:" + mc.getTN());
		System.out.println("FP:" + mc.getFP());
		System.out.println("FN:" + mc.getFN());
		
		System.out.println("PRECISION:" + mc.getPRECISION());
		System.out.println("RECALL:" + mc.getRECALL());
		System.out.println("F-MEASURE:" + mc.getFMEASURE());
		System.out.println("ACCURACY:" + mc.getCORRECTRATIO());
	}
	
	
}

/***
 * <p>This class <b>IFNode</b> is used to simulate the characteristic of each instance.</p>
 *
 */
class IFNode extends CrashNode{
	
	private String label; // class label
	
	private String prelabel = "normal"; // outlier or normal
	
	private List<Double> lsAttr = new ArrayList<Double>(); // feature list
	
	private double score = 0.0d; // cluster index
	
	/**To initialize the instance with features and class label */
	IFNode(Instance instance){
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
	public String getLabel(){
		return label;
	}
	
	/**To judge whether the instance is predicted as a outlier. */
	public boolean isOutlier(){
		if(prelabel == "outlier"){
			return true;
		}else{
			return false;
		}
	}
	
	public void setScore(double s){
		this.score = s;
	}
	
	public double getScore(){
		return this.score;
	}			
}

