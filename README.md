# CrashOutlier
Project <b>CrashOutlier</b> provides different outlier detection algorithms in detection task. These outlier detection algorithms include: Statistical-based(<code><i>Gauss-based</i></code>), Distance-based(<code><i>HilOut</i></code>), Density-based(<code><i>LOF</i></code>), Cluster-based(<code><i>DBSCAN</i></code>), High dimensional techniques(<code><i>Isolation Forest</i></code>).

## 1. Usage Example

We have implemented outlier detection algorithm in each Java class, we just need instantiate a object and invoke its corresponding showResults() function to show the detection results, here is the code to invoke <i>Gauss-Based</i> outlier detection to detect outliers,
```java
GaussBased gb = new GaussBased("XXX.arff");
gb.showResults();
```

## 2. Detection Output

In the original version of our project, we only output the <i>anomaly score</i> (vary from one algorithm to another) and <i>class label</i> of each instance. Maybe we can add some evaluation measure in the future. And here is the sample output,
```html
Experiments Results of <XXX.arff> By Using Gauss-Based Outlier Detection Method.

---------------- Detected Outliers ------------------

probability: 1.001344193860821E-151, Label: difficult
probability: 1.001344193860821E-151, Label: difficult
probability: 2.87218414628287E-151, Label: difficult
probability: 2.5188243831817195E-150, Label: difficult
probability: 4.1834795368155787E-119, Label: easy
probability: 3.1286018242800934E-109, Label: easy
...
  
---------------- Detected Normals ------------------

probability: 1.4089300821916898E-46, Label: easy
probability: 3.705113213490469E-44, Label: easy
...
----------------------------------
TP:11
TN:169
FP:9
FN:11
PRECISION:0.55
RECALL:0.5
F-MEASURE:0.5238095238095238
```

## 3. Limitation
We can only load or read datasets in <code><i>arff</i></code> or <code><i>csv</i></code> formats just now. 

<i>arff</i> format:
```
@relation Jsqlparser

@attribute featureNameI  value numeric
@attribute featureNameII value numeric
...
@data
XX, XX, XX, XX, ..., flagA
XX, XX, XX, XX, ..., flagB
...
```
<i>csv</i> format:
```
featureNameI, featureNameII, ..., flag
XX, XX, ..., flagA
XX, XX, .., flagB
...
```
