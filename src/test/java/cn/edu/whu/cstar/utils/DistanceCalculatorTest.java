<<<<<<< HEAD
package cn.edu.whu.cstar.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

/***
 * <p>This class <b>DistanceCalculatorTest</b> is used to test 2 methods in class <b>DistanceCalculator</b>.</p>
 *
 */
public class DistanceCalculatorTest {
	
	@Test
	public void testDistanceEculidean_0(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(3.0d, res, 0.00001d);
	}

	@Test
	public void testDistanceEculidean_1(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		ls2.add(5.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(5.0d, res, 0.00001d);
	}
	
	@Test
	public void testDistanceEculidean_2(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		ls1.add(2.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		ls2.add(5.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(-1.0d, res, 0.00001d);
	}
	
	@Test
	public void testDistanceEculidean_3(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		ls2.add(5.0d);
		ls2.add(2.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(-1.0d, res, 0.00001d);
	}
	
	@Test
	public void testDistanceEculidean_4(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(3.0d);
		ls2.add(3.0d);
		ls2.add(2.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(3.0d, res, 0.00001d);
	}
	
	@Test
	public void testFindKDistance_0(){
		double[] lsDouble = {3, 5, 1, 7};
		double kins1 = DistanceCalculator.findKDistance(lsDouble, 1);
		Assert.assertEquals(1, kins1, 0.00001d);
		
		double kins2 = DistanceCalculator.findKDistance(lsDouble, 2);
		Assert.assertEquals(3, kins2, 0.00001d);
		
		double kins3 = DistanceCalculator.findKDistance(lsDouble, 3);
		Assert.assertEquals(5, kins3, 0.00001d);
		
		double kins4 = DistanceCalculator.findKDistance(lsDouble, 4);
		Assert.assertEquals(7, kins4, 0.00001d);
	}
	
	@Test
	public void testFindKDistance_1(){
		double[] lsDouble = {3, 5, 1, 7};
		double kins = DistanceCalculator.findKDistance(lsDouble, 5);
		Assert.assertEquals(-1, kins, 0.00001d);
		
	}
	
}
=======
package cn.edu.whu.cstar.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

/***
 * <p>This class <b>DistanceCalculatorTest</b> is used to test 2 methods in class <b>DistanceCalculator</b>.</p>
 *
 */
public class DistanceCalculatorTest {
	
	@Test
	public void testDistanceEculidean_0(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(3.0d, res, 0.00001d);
	}

	@Test
	public void testDistanceEculidean_1(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		ls2.add(5.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(5.0d, res, 0.00001d);
	}
	
	@Test
	public void testDistanceEculidean_2(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		ls1.add(2.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		ls2.add(5.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(-1.0d, res, 0.00001d);
	}
	
	@Test
	public void testDistanceEculidean_3(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(4.0d);
		ls2.add(5.0d);
		ls2.add(2.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(-1.0d, res, 0.00001d);
	}
	
	@Test
	public void testDistanceEculidean_4(){
		List<Double> ls1 = new ArrayList<Double>();
		ls1.add(1.0d);
		ls1.add(1.0d);
		ls1.add(1.0d);
		
		List<Double> ls2 = new ArrayList<Double>();
		ls2.add(3.0d);
		ls2.add(3.0d);
		ls2.add(2.0d);
		double res = DistanceCalculator.distanceEculidean(ls1, ls2);
		
		Assert.assertEquals(3.0d, res, 0.00001d);
	}
	
	@Test
	public void testFindKDistance_0(){
		double[] lsDouble = {3, 5, 1, 7};
		double kins1 = DistanceCalculator.findKDistance(lsDouble, 1);
		Assert.assertEquals(1, kins1, 0.00001d);
		
		double kins2 = DistanceCalculator.findKDistance(lsDouble, 2);
		Assert.assertEquals(3, kins2, 0.00001d);
		
		double kins3 = DistanceCalculator.findKDistance(lsDouble, 3);
		Assert.assertEquals(5, kins3, 0.00001d);
		
		double kins4 = DistanceCalculator.findKDistance(lsDouble, 4);
		Assert.assertEquals(7, kins4, 0.00001d);
	}
	
	@Test
	public void testFindKDistance_1(){
		double[] lsDouble = {3, 5, 1, 7};
		double kins = DistanceCalculator.findKDistance(lsDouble, 5);
		Assert.assertEquals(-1, kins, 0.00001d);
		
	}
	
}
>>>>>>> f0830c4d980e2c4fc2c766a548e344fff698ecbf
