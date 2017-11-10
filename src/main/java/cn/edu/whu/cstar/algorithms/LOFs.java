package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import weka.filters.Filter;
import weka.filters.unsupervised.attribute.LOF;
import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.MeasureCalculator;
import weka.core.Instance;
import weka.core.Instances;

/***
 * <p><b>LOF</b>(Local Outlier Factor) algorithm is one of the most famous density-based outlier detection
 *  methods, it's provided by Breuning et al. in 2000. The main steps are as follows,</p> 
 *
 * <li>1. Get k-distance of each instance.</li>
 * <li>2. Get K-nearest neighbors of each instance.</li>
 * <li>3. Get reach-distance of any pair of instances.</li>
 * <li>4. Get local reach-ability density of each instance.</li>
 * <li>5. Get LOF factor of each instance, the top-N instances with high LOF are detected as outliers.</li>
 */
public class LOFs {
	private static Instances dataset;
	/** top-N outliers*/
	public static final double N = 0.05;
	/** MinPointsLowerBound in LOF*/
	public static final String MinPointsLowerBound = "2";
	/** MinPointsUpperBound in LOF*/
	public static final String MinPointsUpperBound = "5";
	
	private static List<LOFNode> nodeset = new ArrayList<LOFNode>();
	
	/**To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.*/
	public LOFs(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		for(int i=0; i<dataset.numInstances(); i++){
			Instance currentInstance = dataset.get(i);
			LOFNode node = new LOFNode(currentInstance);
			nodeset.add(node);
		}
		
		try {
			calculateLOF(path);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		rankingByLOF();
		
	}
	
	/***
	 * <p> To calculate the probability of each node.</p>
	 * @param path
	 * @throws Exception 
	 */
	private static void calculateLOF(String path) throws Exception{

		LOF lof = new LOF();
		lof.setMinPointsLowerBound(MinPointsLowerBound);
		lof.setMinPointsUpperBound(MinPointsUpperBound);
//		lof.setNNSearch();
		lof.setInputFormat(dataset);
		dataset = Filter.useFilter(dataset, lof);
		
		for(int i=0; i<nodeset.size(); i++){
			
			nodeset.get(i).setLOF(dataset.get(i).value(dataset.numAttributes()-1));
			
		}
	}
	
	private void rankingByLOF(){
		Collections.sort(nodeset, new LOFComparator());
		int topNum = (int)(N*nodeset.size());
		
		for(int i=0; i<topNum; i++){
			nodeset.get(i).setPrelabel("outlier");
		}
	}
	
	/** To show the detection results by HilOut algorithm.*/
	public void showResults(){
		System.out.println("\nExperiments Results of <" + dataset.relationName() + "> By Using LOF Outlier Detection Method.");
		System.out.println("\n---------------- Detected Outliers ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(nodeset.get(i).isOutlier())
				System.out.println("lof: " + nodeset.get(i).getLOF() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("\n---------------- Detected Normals ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(!nodeset.get(i).isOutlier())
				System.out.println("lof: " + nodeset.get(i).getLOF() + ", Label: " + nodeset.get(i).getLabel());
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
 * <p>This class <b>LOFNode</b> is used to simulate the characteristic of each instance.</p>
 *
 */
class LOFNode extends CrashNode{
	
	private String label; // class label
	
	private String prelabel = "normal"; // outlier or normal
	
	private List<Double> lsAttr = new ArrayList<Double>(); // feature list
	
	private double lof = 0.0d; // weight value
	
	/**To initialize the instance with features and class label */
	LOFNode(Instance instance){
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
	
	public void setLOF(double lof){
		this.lof = lof;
	}
	
	public double getLOF(){
		return this.lof;
	}			
}

class LOFComparator implements Comparator<LOFNode>{

	public int compare(LOFNode o1, LOFNode o2) {
		if(o1.getLOF() > o2.getLOF()){
			return -1;
		}else if(o1.getLOF() < o2.getLOF()){
			return 1;
		}else{
			return 0;
		}
	}	
}
