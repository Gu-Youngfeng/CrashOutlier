package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;

import cn.edu.whu.cstar.utils.ARFFReader;
import weka.core.Instance;
import weka.core.Instances;

/***
 * <p>The <b>Gauss-Based</b> outlier detection is a classical Model-Based (or Statistical-Based) methods.
 * It assume that the instance distribution is under the Guass Distribution, and the instances with 
 * low probability is detected as the outliers. The main steps are as follows,</p>
 * 
 * <li>1. Get mean value <b>&mu;</b> of each dimension (feature).</li>
 * <li>2. Get the standard deviation <b>&sigma;</b> of each dimension (feature).</li>
 * <li>3. Give probability threshold <b>&epsilon;</b>, if the probability <b>p</b> of a instance is less than 
 * the threshold, the instance is detected as a outlier, note that <b>p(x)</b> is defined as follows,</li>
 * 
 * <pre><b>p(x) = &prod; 1/(sqrt(2&pi;)*&sigma;j) * exp((-1)*(xj-&mu;j)^2/(2*&sigma;^2))</b></pre>
 */
public class GaussBased {
	
private static Instances dataset;
	
	private static final double epsilon = 10;
	
	private static List<CrashNode> nodeset = new ArrayList<CrashNode>();
	
	/**To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.*/
	public GaussBased(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		for(int i=0; i<dataset.numInstances(); i++){
			CrashNode node = new CrashNode(dataset.get(i));
			nodeset.add(node);
		}
		
		for(int i=0; i<dataset.numAttributes()-1; i++){
			System.out.println(dataset.attribute(i).name() + ": mu=" + reader.getMu()[i] + ", std=" + reader.getStd()[i]);
		}
		
	}
	
	public static void calculateP(){
		
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
		
		private double probability = 0.0d; // weight value
		
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
		
		
	}

}

