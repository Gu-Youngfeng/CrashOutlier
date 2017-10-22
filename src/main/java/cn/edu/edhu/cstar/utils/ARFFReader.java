package cn.edu.edhu.cstar.utils;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ARFFReader {
	
	private Instances dataset;
	
	public ARFFReader(String path){
		try {
			dataset = DataSource.read(path);
			dataset.setClassIndex(dataset.numAttributes()-1);

		} catch (Exception e) {
			System.out.println("Reading files error!");
			e.printStackTrace();
		}
	}
	
	public Instances getDataset(){
		return dataset;
	}

}
