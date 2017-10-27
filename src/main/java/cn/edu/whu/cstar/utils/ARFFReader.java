package cn.edu.whu.cstar.utils;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/***
 * <p>Class <b>ARFFReader</b> provides the reading functionality of a dataset. 
 * We can get dataset by using function {@link#getDataset()}, get &mu; and &sigma in each dimension 
 * by using {@link#getMu()} and {@link#getStd()}.</p>
 */
public class ARFFReader {
	
	private Instances dataset;
	
	private double[] mu;
	
	private double[] std;
	
	public ARFFReader(String path){
		try {
			dataset = DataSource.read(path);
			dataset.setClassIndex(dataset.numAttributes()-1);

		} catch (Exception e) {
			System.out.println("Reading files error!");
			e.printStackTrace();
		}
		
		setMu();
		
		setStd();
	}
	
	/**get Dataset (Instances) from file content.*/
	public Instances getDataset(){
		return dataset;
	}
	
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

}
