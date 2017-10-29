package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;

import cn.edu.whu.cstar.utils.ARFFReader;
import weka.core.Instance;
import weka.core.Instances;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DBSCAN;

/***
 * <p><b>DBSCAN</b> is a classical clustering algorithm, which can be used to detect outliers. 
 * We regard those clusters which is small size as the outliers.</p> 
 *
 */
public class DBSCANs {
	private static Instances dataset;
	/** Epsilon in DBSCAN*/
	private static final double Epsilon = 1.5d;
	/** MinPoints in DBSCAN*/
	private static final int MinPoints = 5;
	
	private static List<CrashNode> nodeset = new ArrayList<CrashNode>();
	
	/**To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.*/
	public DBSCANs(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
//		dataset.deleteAttributeAt(dataset.numAttributes()-1); //DBSCAN is a unsuperviesd method.
		for(int i=0; i<dataset.numInstances(); i++){
			Instance currentInstance = dataset.get(i);
			CrashNode node = new CrashNode(currentInstance);
			nodeset.add(node);
		}
		
		try {
			clusteringByDBSCAN(path);
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}
	
	/***
	 * <p> To calculate the probability of each node.</p>
	 * @param path
	 * @throws Exception 
	 */
	private static void clusteringByDBSCAN(String path) throws Exception{
		DBSCAN dbscan = new DBSCAN();
		dbscan.setEpsilon(Epsilon);
		dbscan.setMinPoints(MinPoints);
		
		dbscan.buildClusterer(dataset);
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(dbscan);
		eval.evaluateClusterer(dataset);
		double[] num = eval.getClusterAssignments();
//		System.out.println(num.length);
		
		for(int i=0; i<nodeset.size(); i++){
			CrashNode currentNode = nodeset.get(i);
			currentNode.setClusterIndex(num[i]);
			if(num[i] < 0){
				currentNode.setPrelabel("outlier");
			}
		}
		
	}
	
	/** To show the detection results by HilOut algorithm.*/
	public void showResults(){
		System.out.println("\nExperiments Results of <" + dataset.relationName() + "> By Using DBSCAN Outlier Detection Method.");
		System.out.println("\n---------------- Detected Outliers ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(nodeset.get(i).isOutlier())
				System.out.println(nodeset.get(i).getClusterIndex() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("\n---------------- Detected Normals ------------------\n");
		for(int i=0; i<nodeset.size(); i++){
			if(!nodeset.get(i).isOutlier())
				System.out.println(nodeset.get(i).getClusterIndex() + ", Label: " + nodeset.get(i).getLabel());
		}
		System.out.println("----------------------------------");
	}
	
	/***
	 * <p>This class <b>CrashNode</b> is used to simulate the characteristic of each instance.</p>
	 * <p></p>
	 *
	 */
	class CrashNode{
		
		private String label; // class label
		
		private String prelabel = "normal"; // outlier or normal
		
		private List<Double> lsAttr = new ArrayList<Double>(); // feature list
		
		private double clusterIndex = -1.0d; // cluster index
		
		/**To initialize the instance with features and class label */
		CrashNode(Instance instance){
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
		
		public void setClusterIndex(double clusterIndex){
			this.clusterIndex = clusterIndex;
		}
		
		public double getClusterIndex(){
			return this.clusterIndex;
		}			
	}
}
