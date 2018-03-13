package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.DistanceCalculator;
import cn.edu.whu.cstar.utils.MeasureCalculator;
import weka.core.Instance;
import weka.core.Instances;

/***
 * <p><b>HilOut</b> is one of the most famous distance-based outlier detection algorithms. It's provided by Angiulli et al. in 2005.</p>
 * <p>It detect outliers by calculating weight of each data point, the weight is the sum of distances 
 * between one point to its k-nearest neighbors. Thus, those data points who has the higher weight are 
 * more likely to be the outliers. The basic steps are as follows,</p>
 * <li>1. calculating distances of any pair of points.</li>
 * <li>2. getting <b>K</b>-nearest neighbors of each points.</li>
 * <li>3. counting the weight of each point.</li>
 * <li>4. ranking the points in terms of weights, the top-<b>N</b> points are detected as outliers.</li>
 */
public class HilOut {
	
	private static Instances dataset;
	
	/** K-nearest neighbors*/
	public static final int K = 5;
	/** top-N outliers*/
	public static final double N = 0.1;
	
	private static List<HILNode> nodeset = new ArrayList<HILNode>();
	
	private static List<double[]> lsDistance = new ArrayList<double[]>();;
	
	/**To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.*/
	public HilOut(String path){
		
		nodeset.clear();
		lsDistance.clear();
		
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		for(int i=0; i<dataset.numInstances(); i++){
			HILNode node = new HILNode(dataset.get(i));
			nodeset.add(node);
		}
		
		calculateKNeighbors();
		
		rankingByWeights();
	}
	
	public Instance getIns(int index){
		return dataset.get(index);
	}
	
	private void calculateKNeighbors(){
		int size = nodeset.size();
		
		/** save distance between pair of nodes into lsDistance*/
		for(int i=0; i<size; i++){ // for each instance
			double[] lsEach = new double[size];
			for(int j=0; j<size; j++){ // calculate distance from other instance
				lsEach[j] = nodeset.get(i).getDistanceToOther(nodeset.get(j));
			}
			lsDistance.add(lsEach);
		}
		
		/** set K-nearesr neighbors to each instance*/
		for(int i=0; i<lsDistance.size(); i++){
			double kdis = DistanceCalculator.findKDistance(lsDistance.get(i), K);
			HILNode currentInstance = nodeset.get(i);
			for(int j=0; j<lsDistance.size(); j++){
				if(currentInstance.getDistanceToOther(nodeset.get(j)) <= kdis && j != i){
					currentInstance.setNeighbor(nodeset.get(j));
				}	
			}	
			currentInstance.setWeight();
		}		
	}
	
	/**To rank the instance by weight-values. */
	private void rankingByWeights(){
		nodeset.sort(new WeightComparator());
		int outlierNum = (int)(nodeset.size()*N); 
		
		for(int i=0; i<outlierNum; i++){
			nodeset.get(i).setPrelabel("outlier");
		}
		
	}
	
	/** To show the detection results by HilOut algorithm.*/
	public void showResults(){
		System.out.println("Experiments Results of <" + dataset.relationName() + "> By HilOut Outlier Detection Method.");
		System.out.println("\n---------------- Detected Outliers ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(nodeset.get(i).isOutlier())
				System.out.println("weight: " + nodeset.get(i).getWeight() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("\n---------------- Detected Normals ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(!nodeset.get(i).isOutlier())
				System.out.println("weight: " + nodeset.get(i).getWeight() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("----------------------------------");
		
		MeasureCalculator mc = new MeasureCalculator(nodeset);
		
		System.out.println("TP:" + mc.getTP());
		System.out.println("TN:" + mc.getTN());
		System.out.println("FP:" + mc.getFP());
		System.out.println("FN:" + mc.getFN());
		
//		System.out.println("PRECISION:" + mc.getPRECISION());
//		System.out.println("RECALL:" + mc.getRECALL());
//		System.out.println("F-MEASURE:" + mc.getFMEASURE());
//		System.out.println("ACCURACY:" + mc.getCORRECTRATIO());
		
		System.out.println("Detection Rate: " + mc.getDetectRate());
		System.out.println("FP Rate       : " + mc.getFPRate());

	}
	
	public double getDetectionRate(){
		MeasureCalculator mc = new MeasureCalculator(nodeset);
		return mc.getDetectRate();
	}
	
	public double getFPRate(){
		MeasureCalculator mc = new MeasureCalculator(nodeset);
		return mc.getFPRate();
	}
}

/***
 * <p>This class <b>HILNode</b> is used to simulate the characteristic of each instance.</p>
 * <p>We use the <b>KNeightbors</b> to save the k nearest neighbor list, use <b>label</b> to save 
 * the original class label, use <b>lsAttr</b> to save the feature list, then use <b>prelabel</b> to
 * save the outlierness of this instance, finally, <b>weight</b> to save the weight value.</p>
 *
 */
class HILNode extends CrashNode{
	
	private List<HILNode> KNeighbors = new ArrayList<HILNode>(); // k-nearest neighbors
	
	private String label; // class label
	
	private String prelabel = "normal"; // outlier or normal
	
	private List<Double> lsAttr = new ArrayList<Double>(); // feature list
	
	private double weight = 0.0d; // weight value
	
	/**To initialize the instance with features and class label */
	HILNode(Instance instance){
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
	
	/**To get distance to other <b>node</b>. Note the default distance is the Euclidean Distance.*/
	public double getDistanceToOther(HILNode node){
		double distance = 0.0d;
		List<Double> attr1 = lsAttr;
		List<Double> attr2 = node.getAttr();
		
		distance = DistanceCalculator.distanceEculidean(attr1, attr2); //List<Double> ls1, List<Double> ls2
		
		return distance;
	}
	
	/**To add <b>node</b> into its K-nearest neighbors list. */
	public void setNeighbor(HILNode node){
		if(KNeighbors.size() < HilOut.K)
			KNeighbors.add(node);
	}
	
	/**To set weight( <b>sum distance to its k-nearest neighbors</b> ) of instance. */
	public void setWeight(){		
		for(HILNode nodes: KNeighbors){
			weight += getDistanceToOther(nodes);
		}		
	}
	
	/**To get weight( <b>sum distance to its k-nearest neighbors</b> ) of instance.*/
	public double getWeight(){					
		return weight;
	}
	
	/**
	 * <p>To show the detailed information ( <b>feature & label</b> ) of instance</p>
	 * <pre> Instance Details: [Label]
	 * ------------------
	 * x1, x2, x3, x4, x5, ..., xn
	 * </pre>
	 */
	public void showNode(){
		System.out.println("Instance Details: [" + label + "]\n---------------------");
		for(double feature: lsAttr){
			System.out.print(feature + ", ");
		}
		System.out.println("");
	}
}


/**
 * <p>Construct a comparator to sort the top-n instances which have the highest weight.</p>
 * @param <CrashNode>
 */
class WeightComparator implements Comparator<HILNode>{

	public int compare(HILNode o1, HILNode o2) {
		if(o1.getWeight() > o2.getWeight()){
			return -1;
		}else if(o1.getWeight() < o2.getWeight()){
			return 1;
		}else{
			return 0;
		}

	}

}
