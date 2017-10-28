package cn.edu.whu.cstar.algorithms;

/***
 * <p>This <b>Usage</b> class provide the basic usage of each algorithm in this package.
 * Just instantiate the object of algorithm class, and show results. In maven, you can also use commands like,
 * <pre>mvn exec:java -Dexec.mainClass="cn.edu.whu.cstar.algorithms.Usage"</pre>
 *
 */
public class Usage {

	public static void main(String[] args) {

//		GaussBased gb = new GaussBased("Jsqlparser.arff");
//		gb.showResults();
//		
//		HilOut ho = new HilOut("Jsqlparser.arff");
//		ho.showResults();
		
		LOFs lofs = new LOFs("Jsqlparser.arff");
		lofs.showResults();
		
	}

}
