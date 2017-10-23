package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;

import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.DistanceCalculator;
import weka.core.Instance;
import weka.core.Instances;

public class HilOut {
	
	private static Instances dataset;
	
	private static List<CrashNode> nodeset = new ArrayList<CrashNode>();
	
	public HilOut(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		for(int i=0; i<dataset.numInstances(); i++){
			CrashNode node = new CrashNode(dataset.get(i));
			nodeset.add(node);
		}
	}
	
	public void calculateKNeighbors(){
		for(CrashNode nodes: nodeset){
			
		}
	}
	
	public void showResult(){
		nodeset.get(0).showNode();
	}

}

class CrashNode{
	
	private static final int K = 10;
	
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
	
	public List<Double> getAttr(){
		return lsAttr;
	}
	
	public double getDistanceToOther(CrashNode node){
		double distance = 0.0d;
		List<Double> attr1 = lsAttr;
		List<Double> attr2 = node.getAttr();
		
		distance = DistanceCalculator.distanceEculidean(attr1, attr2); //List<Double> ls1, List<Double> ls2
		
		return distance;
	}
	
	public void setNeighbor(CrashNode node){
		if(KNeighbors.size() > K){
			System.out.println("Neighbors increase out of bounds!");
		}else{
			KNeighbors.add(node);
		}
	}
	
	public void showNode(){
		System.out.println("Node Details: [" + label + "]\n---------------------");
		for(double feature: lsAttr){
			System.out.print(feature + ", ");
		}
		System.out.println("");
	}
	
}
