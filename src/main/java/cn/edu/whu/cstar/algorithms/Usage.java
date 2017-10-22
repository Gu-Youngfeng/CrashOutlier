package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.List;

import cn.edu.whu.cstar.utils.DistanceCalculator;

public class Usage {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DistanceBased db = new DistanceBased("assembly/Jsqlparser.arff");
		db.showResult();
		
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(10.0d);
		ls1.add(10.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(8.0d);
		ls2.add(8.0d);
		
		System.out.println(DistanceCalculator.distanceEculidean(ls1, ls2));
		
	}

}
