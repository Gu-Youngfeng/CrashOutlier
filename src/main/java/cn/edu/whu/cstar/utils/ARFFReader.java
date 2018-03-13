package cn.edu.whu.cstar.utils;

import java.util.HashSet;
import java.util.Set;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
//import weka.filters.Filter;
//import weka.filters.unsupervised.attribute.Normalize;

/***
 * <p>Class <b>ARFFReader</b> provides the reading functionality of a dataset. 
 * We can get dataset by using function {@link#getDataset()}, get &mu; and &sigma in each dimension 
 * by using {@link#getMu()} and {@link#getStd()}.</p>
 */
public class ARFFReader {
	
	/** loaded dataset*/
	private Instances dataset;
	/** mean-value &mu; in each dimension*/
	private double[] mu;
	/** standard deviation &sigma; in each dimension*/
	private double[] std;
	/** labels in dataset*/
	private Set<String> labels = new HashSet<String>();
	
	/** constructor can provide the loading and normalization functionalities */
	public ARFFReader(String path){
		/************************ Loading the dataset ****************************/
		try {
			dataset = DataSource.read(path);
			dataset.setClassIndex(dataset.numAttributes()-1); // Remember to comment it when using DBSCAN algorithm
			
			/** attribute-value normalization operation.*/
//			Normalize nm = new Normalize();
//			nm.setInputFormat(dataset);
//			dataset = Filter.useFilter(dataset, nm);

		} catch (Exception e) {
			System.out.println("Loading files error!");
			e.printStackTrace();
		}
		/****************** Collecting the labels of dataset *********************/
		for(Instance ins: dataset){
			String label = ins.stringValue(dataset.classAttribute());
			labels.add(label);
		}
		
		/**************** Calculating the parameters of mu and std ***************/
		try{
			setMu();
			setStd();
		}catch(Exception e){
			System.out.println("Calculating parameters error!");
			e.printStackTrace();
		}
		
	}
	
	/** constructor can provide the loading and normalization functionalities.
	 *  this ARFFReader is only used for DBSCAN.
	 *  */
	public ARFFReader(String path, boolean flag){
		/************************ Loading the dataset ****************************/
		try {
			dataset = DataSource.read(path);
//			dataset.setClassIndex(dataset.numAttributes()-1); // Remember to comment it when using DBSCAN algorithm
			
			/** attribute-value normalization operation.*/
//			Normalize nm = new Normalize();
//			nm.setInputFormat(dataset);
//			dataset = Filter.useFilter(dataset, nm);

		} catch (Exception e) {
			System.out.println("Loading files error!");
			e.printStackTrace();
		}
		/****************** Collecting the labels of dataset *********************/
//		for(Instance ins: dataset){
//			String label = ins.stringValue(dataset.classAttribute());
//			labels.add(label);
//		}
		
		/**************** Calculating the parameters of mu and std ***************/
		try{
			setMu();
			setStd();
		}catch(Exception e){
			System.out.println("Calculating parameters error!");
			e.printStackTrace();
		}
		
	}
	
	/**get Dataset (Instances) from file content.*/
	public Instances getDataset(){
		return dataset;
	}
	
	/**set mean value of each attribute*/
	public void setMu(){
		int attrNum = this.dataset.numAttributes();
		int insNum = this.dataset.numInstances();
		double[] mus = new double[attrNum - 1];
		
		for(int i=0; i<attrNum-1; i++){
			double sum = 0.0d;
			for(int j=0; j <insNum; j++){
				sum += dataset.get(j).value(i);
			}
			mus[i] = (sum*1.0)/(insNum*1.0);
		}
		
		this.mu = mus;
	}
	
	/** get <b>&mu;</b> array in each dimension.*/
	public double[] getMu(){
		return this.mu;
	}
	
	/**set standard divation of each attribute*/
	public void setStd(){
		int attrNum = this.dataset.numAttributes();
		int insNum = this.dataset.numInstances();
		double[] stds = new double[attrNum - 1];
		
		for(int i=0; i<attrNum-1; i++){
			double delt = 0.0d;
			for(int j=0; j <insNum; j++){
				delt += Math.pow((dataset.get(j).value(i) - mu[i]), 2);
			}
			stds[i] = Math.sqrt((delt*1.0)/((insNum-1)*1.0));
		}
		
		this.std = stds;
		
	}
	
	/** get <b>&sigma;</b> array in each dimension.*/
	public double[] getStd(){
		return this.std;
	}
	
	/** get the basic information of the loaded dataset.*/
	public void showDataset(){
		System.out.println("----------   Dataset Basic Information   ----------");
		System.out.println("(1) Relation Name : " + dataset.relationName());
		System.out.println("(2) Instances     : " + dataset.numInstances());
		System.out.println("(3) Attributes    : " + (dataset.numAttributes()-1));
		System.out.println("(4) Class Labels  : " + labels);
	}

}
