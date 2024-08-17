import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;


// Node class
class Node {
    String id;
    String diagnosis;
    double[] stats;
    Node left, right;

    public Node(double[] s, String i, String d) {
        this.id = i;
        this.diagnosis = d;
        this.stats = s;
        this.left = this.right = null;
    }
}

// Node Distance class (Stores a node with its associated distance, used when finding nearest neighbors)
class NodeDistance implements Comparable<NodeDistance> {
    Node node;
    double dist;

    NodeDistance(Node n, double dis) {
        this.node = n;
        this.dist = dis;
    }

    public int compareTo(NodeDistance other) {
        return Double.compare(other.dist, this.dist);
    }
    
}


// Stopwatch class
class Stopwatch {
    private final long start;

    public Stopwatch() {
        start = System.nanoTime();
    }

    public double elapsedTime() {
        long now = System.nanoTime();
        return (now - start) / 1000000000.0;
    }
}

// Shell sort code from assignment 2
class Shellgap1 {

	// Exchange code
	private static void exch(NodeDistance[] a, int i, int j)
	 { NodeDistance t = a[i]; a[i] = a[j]; a[j] = t; }

	// Comparison code
	private static boolean less(double v, double w)
	 { return v < w; }
	
	// Shell sort with gap of 4x + 2
	public static void sort(NodeDistance[] a)
	   {  
		  // Sort a[] into increasing order with Gap of 4x + 2
	      int N = a.length;
	      int h = 1;
	      while (h < N/4) h = 4*h + 2;
	      while (h >= 1)
	      { 
	         for (int i = h; i < N; i++)
	         {  
	            for (int j = i; j >= h && less(a[j].dist, a[j-h].dist); j -= h)
	               exch(a, j, j-h);
	         }
	         h = h/4;
	      }
	   }
	
	}
	

// KDTree class
class As3 {

	// 10 dimensions
    int k = 10;

    // Code to insert node
    public Node newNode(double[] arr, String i, String d) {
        return new Node(arr, i, d);
    }

    public Node insertRec(Node root, double[] stats, String i, String d, int depth) {
        if (root == null) {
            return newNode(stats, i, d);
        }

        int cd = depth % k;
        if (stats[cd] < root.stats[cd]) {
            root.left = insertRec(root.left, stats, i, d, depth + 1);
        } else {
            root.right = insertRec(root.right, stats, i, d, depth + 1);
        }

        return root;
    }

    public Node insert(Node root, double[] stats, String i, String d) {
        return insertRec(root, stats, i, d, 0);
    }

    // Check if two instances are the same (used when searching for id / diagnosis)
    public boolean areStatsSame(double[] stats1, double[] stats2) {
        for (int i = 0; i < k; ++i) {
            if (stats1[i] != stats2[i]) {
                return false;
            }
        }
        return true;
    }

    //check if instance exists by searching its stats
    public boolean searchRec(Node root, double[] stats, int depth) {
        if (root == null) {
            return false;
        }
        if (areStatsSame(root.stats, stats)) {
            return true;
        }

        int cd = depth % k;
        if (stats[cd] < root.stats[cd]) {
            return searchRec(root.left, stats, depth + 1);
        }
        return searchRec(root.right, stats, depth + 1);
    }

    public boolean search(Node root, double[] stats) {
        return searchRec(root, stats, 0);
    }

    // search for instance and return id (used to verify if code was working during debugging)
    public String findIDRec(Node root, double[] stats, int depth) {
        if (root == null) {
            return "No match";
        }
        if (areStatsSame(root.stats, stats)) {
            return root.id;
        }

        int cd = depth % k;
        if (stats[cd] < root.stats[cd]) {
            return findIDRec(root.left, stats, depth + 1);
        }
        return findIDRec(root.right, stats, depth + 1);
    }

    public String findID(Node root, double[] stats) {
        return findIDRec(root, stats, 0);
    }

    
    // return diagnosis based on stats (used to verify accuracy)
    public String findDiagRec(Node root, double[] stats, int depth) {
        if (root == null) {
            return "No match";
        }
        if (areStatsSame(root.stats, stats)) {
            return root.diagnosis;
        }

        int cd = depth % k;
        if (stats[cd] < root.stats[cd]) {
            return findDiagRec(root.left, stats, depth + 1);
        }
        return findDiagRec(root.right, stats, depth + 1);
    }

    public String findDiag(Node root, double[] stats) {
        return findDiagRec(root, stats, 0);
    }

    // code to shuffle (used to randomize instances for training/testing)
    private static void exch(String[][] instanceList, int i, int j) {
        String[] t = instanceList[i];
        instanceList[i] = instanceList[j];
        instanceList[j] = t;
    }

    public static void shuffle(String[][] instanceList) {
        Random rand = new Random();
        for (int i = 0; i < instanceList.length; i++) {
            int r = rand.nextInt(i + 1);
            exch(instanceList, i, r);
        }
    }

    // calculate euclidean distance
    public static double distance(double[] a, double[] b) {
        double total = 0;
        for (int i = 0; i < a.length; i++) {
            total = (total + Math.pow((a[i] - b[i]), 2));
        }
        double distance = Math.sqrt(total);
        return distance;
    }


    // find k nearest neighbors
    public void nearestNeighbors(Node root, double[] target, int k, NodeDistance[] distances, int depth) {
    	if (root == null) {
            return;
        }
    	   
    	// find distance to current root
        double dist = distance(root.stats, target);
     
        
        // if array is not full, add current distance and traverse
        for (int i = 0; i < distances.length; i++) {
        	if (distances[i] == null) {
        		distances[i] = new NodeDistance(root, dist);
        		 int cd = depth % this.k;
        	        if (target[cd] < root.stats[cd]) {
        	            nearestNeighbors(root.left, target, k, distances, depth + 1);   
        	        } else {
        	        // if the current stat is more than the current root stat, sort the array then traverse right	
        	            nearestNeighbors(root.right, target, k, distances, depth + 1);           
        	        }
        	}   
        }
        
        // if it is full, check if the current distance is smaller than the current max in the array, and replace it
        Shellgap1.sort(distances);	
        if (dist < distances[k-1].dist) {
        		 distances[k-1] = new NodeDistance(root,dist);  
        	}
        
 
        // if the current stat is less than the current root stat, sort the array then traverse left
        int cd = depth % this.k;
        if (target[cd] < root.stats[cd]) {
        	
            nearestNeighbors(root.left, target, k, distances, depth + 1);   
        } else {
        // if the current stat is more than the current root stat, sort the array then traverse right
        	
            nearestNeighbors(root.right, target, k, distances, depth + 1);           
        }
    }

 
    public static void main(String[] args) throws Exception {

        // Create a full K-d Tree with all instances (will be used to check accuracy later)
        As3 kdTree = new As3();
        String line = "";

        String filePath = "C:/Users/marco/eclipse-workspace/As3/data.csv"; //Insert filepath to data here
        
        Node root = null;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null) {
            String[] temp = line.split(",");
            String id = temp[0];
            String diag = temp[1];
            double[] stats = new double[10];
            for (int i = 2; i < 12; i++) {
                stats[i - 2] = Double.parseDouble(temp[i]);
            }
            root = kdTree.insert(root, stats, id, diag);
        }

        reader.close();

        // Create a 2-d array of all the instances, shuffle it, then add N instances to a training kd tree and save T instances for testing
        String line2 = "";

        String[][] instanceList = new String[569][32];

        int count = 0;
        BufferedReader reader2 = new BufferedReader(new FileReader(filePath));
        while ((line2 = reader2.readLine()) != null) {
            String[] temp2 = line2.split(",");

            for (int i = 0; i < 32; i++) {
                instanceList[count][i] = temp2[i];
            }
            count++;
        }

        reader2.close();

        shuffle(instanceList);

        // N items for training, T items for testing. Ratio N/T = 1/4. If N = 500, the remaining 69 are used for training
        int N = 500;
        int T;
        if (N == 500) {
            T = 69;
        } else T = N / 4;

        int count2 = 0;

        double[][] trainingList = new double[N][10];
        String[] trainingDiag = new String[N];
        String[] trainingID = new String[N];
        while (count2 < N) {
            for (int i = 2; i < 12; i++) {
                trainingList[count2][i - 2] = Double.parseDouble(instanceList[count2][i]);
            }
            trainingDiag[count2] = instanceList[count2][1];
            trainingID[count2] = instanceList[count2][0];
            count2++;
        }

        
        As3 trainingTree = new As3();
        Node root2 = null;
        for (int i = 0; i < N; i++) {
            root2 = trainingTree.insert(root2, trainingList[i], trainingID[i], trainingDiag[i]);
        }

        double[][] testList = new double[T][10];
        while (count2 < (N + T)) {
            for (int i = 2; i < 12; i++) {
                testList[count2 - N][i - 2] = Double.parseDouble(instanceList[count2][i]);
            }
            count2++;
        }
        
        // How many nearest neighbors to consider
        int k = 7;
        
        String foundDiags[] = new String[T];
        double timeElapsed;
        // Start testing stopwatch
        Stopwatch timer = new Stopwatch();
        for (int i = 0; i < T; i++) {
            NodeDistance[] distances = new NodeDistance[k]; 
            trainingTree.nearestNeighbors(root2, testList[i], k, distances, 0);
            
            int freqM = 0;
            int freqB = 0;
            for (int g = 0; g < k; g++) {
            	if (distances[g].node.diagnosis.charAt(0) == 'M') {
            		freqM++;
            	}else freqB++;
            	
            }  	
            if (freqM > freqB) {
                foundDiags[i] = "M";
            } else {
                foundDiags[i] = "B";
            }
        }

        // End testing stopwatch
        timeElapsed = timer.elapsedTime();
    
        // Calculate accuracy and display results
        double counter = 0;
        for (int r = 0; r < T; r++) {
            if (foundDiags[r].charAt(0) == (kdTree.findDiag(root, testList[r])).charAt(0)) {
                counter++;
            }
        }
                
        double accuracy = (counter / T) * 100;

        System.out.println("N: " + N);
        System.out.println("T: " + T);
        System.out.println("k: " + k);
        System.out.println("Accuracy: " + accuracy + "%");
        System.out.println("Elapsed Time: " + timeElapsed + " seconds");
    }
}
