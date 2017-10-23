package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.edu.whu.cstar.algorithms.HilOut.CrashNode;
import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.DistanceCalculator;
import weka.core.Instance;
import weka.core.Instances;

public class HilOut {
	
	private static Instances dataset;
	
	private static final int K = 10;
	
	private static final double P = 0.1;
	
	private static List<CrashNode> nodeset = new ArrayList<CrashNode>();
	
	private static List<double[]> lsDistance = new ArrayList<double[]>();;
	
	/**
	 * <p>To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.</p>
	 * @param path dataset path
	 */
	public HilOut(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		for(int i=0; i<dataset.numInstances(); i++){
			CrashNode node = new CrashNode(dataset.get(i));
			nodeset.add(node);
		}
		
		calculateKNeighbors();
	}
	
	public Instance getIns(int index){
		return dataset.get(index);
	}
	
	public void calculateKNeighbors(){
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
			CrashNode currentInstance = nodeset.get(i);
			for(int j=0; j<lsDistance.size(); j++){
				if(currentInstance.getDistanceToOther(nodeset.get(j)) <= kdis && j != i){
					currentInstance.setNeighbor(nodeset.get(j));
				}	
			}	
			currentInstance.setWeight();
		}
				
	}
	
	/**
	 * <p>To</p>
	 */
	public void rankingByWeights(){
		nodeset.sort(new WeightComparator());
		int outlierNum = (int)(nodeset.size()*P); 
		
	}
	
	public void showResult(){
		for(int i=0; i<nodeset.size(); i++){
			System.out.println("Weight: " + nodeset.get(i).getWeight());
		}
	}
	
	/***
	 * <p>This class <b>CrashNode</b> is used to simulate the characteristic of each instance.</p>
	 * <p></p>
	 *
	 */
	class CrashNode{
		
		private List<CrashNode> KNeighbors = new ArrayList<CrashNode>();
		
		private String label;
		
		private List<Double> lsAttr = new ArrayList<Double>();
		
		private double weight = 0.0d;
		
		CrashNode(Instance instance){
			int lenAttr = instance.numAttributes();
			label = instance.stringValue(lenAttr-1); // set true label
			for(int i=0; i<lenAttr-1; i++){ // set feature-values
				lsAttr.add(instance.value(i));
			}
		}
		
		/**
		 * <p>To get <b>feature-values</b> of instance.</p>
		 * @return feature list
		 */
		public List<Double> getAttr(){
			return lsAttr;
		}
		
		/**
		 * <p>To get distance to other <b>node</b>. Note the default distance is the Euclidean Distance.</p>
		 * @param node CrashNode
		 * @return distance
		 */
		public double getDistanceToOther(CrashNode node){
			double distance = 0.0d;
			List<Double> attr1 = lsAttr;
			List<Double> attr2 = node.getAttr();
			
			distance = DistanceCalculator.distanceEculidean(attr1, attr2); //List<Double> ls1, List<Double> ls2
			
			return distance;
		}
		
		/**
		 * <p>To add <b>node</b> into its K-nearest neighbors list.</p>
		 * @param node a CrashNode
		 */
		public void setNeighbor(CrashNode node){
			if(KNeighbors.size() < K)
				KNeighbors.add(node);
		}
		
		/**
		 * <p>To set weight( <b>sum distance to its k-nearest neighbors</b> ) of instance.</p>
		 * @return
		 */
		public void setWeight(){		
			for(CrashNode nodes: KNeighbors){
				weight += getDistanceToOther(nodes);
			}		
		}
		
		/**
		 * <p>To get weight( <b>sum distance to its k-nearest neighbors</b> ) of instance.</p>
		 * @return
		 */
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

}


/**
 * <p>Construct a comparator to sort the top-n instances which have the highest weight.</p>
 * @param <CrashNode>
 */
class WeightComparator implements Comparator<CrashNode>{

	public int compare(CrashNode o1, CrashNode o2) {
		if(o1.getWeight() > o2.getWeight()){
			return -1;
		}else if(o1.getWeight() < o2.getWeight()){
			return 1;
		}else{
			return 0;
		}

	}

}


