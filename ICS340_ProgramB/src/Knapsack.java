import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

//import Knapsack.Items;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Selects file to calculate the knapsack problem in recursion method and
 * dynamic method.
 * 
 * @author Paty Vang
 *
 */
public class Knapsack extends Application {

	final FileChooser fileChooser = new FileChooser();
	File selectedFile;
	int weightTotal;
	int maxWeight;
	static int[] value = new int[12];
	static int[] weight = new int[12];
	static int[] quantity = new int[12];
	static String[] item = new String[12];
	static int rCount = 0;
	static int dCount = 0;
	static String[] bag = new String[12];
	ArrayList<Items> take = new ArrayList<Items>();
	static ArrayList<Items> objects = new ArrayList<Items>();

	@Override
	public void start(final Stage stage) throws IOException {
		selectedFile = fileChooser.showOpenDialog(stage);
		BufferedReader textfile = new BufferedReader(new FileReader(selectedFile));
		System.out.println("Enter total weight Knapsack can carry");
		Scanner in = new Scanner(System.in);
		int maxWeight = in.nextInt();
		processFile(textfile);
		processItems(item, value, weight, quantity);
		//System.out.println("Recursion method max value: "+recursionMethod(maxWeight, objects, take, objects.size()));
		int[] dWeight = new int [40];
		int[] dValue = new int [40];
		for (int i = 0;i<=26;i++){
			dWeight[i] = objects.get(i).getWeight();
			dValue [i] = objects.get(i).getValue();
		}
		PrintWriter writer = new PrintWriter("Knapsack_output_file.txt");
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("Dynamic method max value: " + dynamicMethod(maxWeight, dWeight, dValue, objects.size()));
		lines.add("Recursion method max value: "+recursionMethod(maxWeight, objects, take, objects.size()));
		lines.add("Dynamic count is: "+dCount);
		lines.add("Recursion count is: "+ rCount);
		lines.add("Below are the items that were selected: ");
			for (int i = 0; i<lines.size();i++){
				writer.println(lines.get(i));
				writer.println();}
				
			
			for (int i = 0; i<take.size();i++){
				writer.println(take.get(i).toString());
			}
		writer.close();
		in.close();
	}
	/**
	 * Takes in arrays and calculates items according to quantity.
	 * @param items
	 * @param values
	 * @param weights
	 * @param quantities
	 */
	public void processItems(String[] i, int[] v, int[] w, int[] q) {
		Items itemObjects;
		int n = 0;
		int a = 0;
		while (n < q.length) {
			for (int j = q[a]; j != 0; j--) {
				itemObjects = new Items(i[a], v[a], w[a]);
				objects.add(itemObjects);
			}
			a++;
			n++;
		}
	}

	/**
	 * Reads in text file and puts them into the appropriate array lists.
	 * @param textfile
	 * @throws IOException
	 */
	private void processFile(BufferedReader textfile) throws IOException {
		String line;
		BufferedReader readFile = new BufferedReader(textfile);
		int i = 0;
		try {
			line = readFile.readLine();
			while (line != null) {
				StringTokenizer stringData = new StringTokenizer(line);
				while (stringData.hasMoreTokens()) {
					item[i] = stringData.nextToken();
					value[i] = Integer.parseInt(stringData.nextToken());
					weight[i] = Integer.parseInt(stringData.nextToken());
					quantity[i] = Integer.parseInt(stringData.nextToken());
				}
				i++;
				line = readFile.readLine();
			}
		} finally {
			readFile.close();
		}
	}
	public class Items {
		String item;
		int value, weight;
		public Items(String i, int v, int w) {
			item = i;
			value = v;
			weight = w;
		}
		public String getItem() {
			return item;
		}
		public int getValue() {
			return value;
		}
		public int getWeight() {
			return weight;
		}
		@Override
		public String toString() {
			return "Items [item=" + item + ", value=" + value + ", weight=" + weight + "]";
		}
	}
	/**
	 * Resource: https://stackoverflow.com/questions/31699619/getting-object-list-in-knapsack
	 * @param weight
	 * @param item
	 * @param optimalChoice
	 * @param number of items
	 * @return Maximum value of items that can be in knapsack
	 */
    public static int recursionMethod(double weight, ArrayList<Items> item, List<Items> optimalChoice, int n){
    	rCount++;
        if(n == 0 || weight == 0)
            return 0;

        if(item.get(n-1).getWeight() > weight) {
            List<Items> subOptimalChoice = new ArrayList<>();
            int optimalCost =recursionMethod(weight, item, subOptimalChoice, n-1);
            optimalChoice.addAll(subOptimalChoice);
            return optimalCost;
        }
        else{
            List<Items> includeOptimalChoice = new ArrayList<>();
            List<Items> excludeOptimalChoice = new ArrayList<>();
            int include_cost = item.get(n-1).getValue() + recursionMethod((weight-item.get(n-1).getWeight()), item, includeOptimalChoice, n-1);
            int exclude_cost = recursionMethod(weight, item, excludeOptimalChoice, n-1);
            if(include_cost > exclude_cost){
                optimalChoice.addAll(includeOptimalChoice);
                optimalChoice.add(item.get(n - 1));
                return include_cost;
            }
            else{
                optimalChoice.addAll(excludeOptimalChoice);
                return exclude_cost;
            }
        }
    }

	/**
	 * Function that returns maximum of two integers. Resource used:
	 * http://www.geeksforgeeks.org/knapsack-problem/
	 * @param first integer
	 * @param second integer
	 * @return the bigger integer of the two
	 */
	static int max(int a, int b) {
		return ((a > b) ? a : b);
	}

	/**
	 * Resource used: http://www.geeksforgeeks.org/knapsack-problem/
	 * @param Maximum weight that the knapsack can carry
	 * @param weight array
	 * @param value array
	 * @param length of array
	 * @param count of recursion
	 * @return total value in knapsack
	 * @throws FileNotFoundException
	 */
	static int dynamicMethod(int weightTotal, int wt[], int val[], int n) throws FileNotFoundException {
		int i, w;
		int K[][] = new int[n + 1][weightTotal + 1];

		// Build table K[][] in bottom up manner
		for (i = 0; i <= n; i++) {
			for (w = 0; w <= weightTotal; w++) {
				if (i == 0 || w == 0)
					K[i][w] = 0;
				else if (wt[i - 1] <= w)
					K[i][w] = max(val[i - 1] + K[i - 1][w - wt[i - 1]], K[i - 1][w]);
				else
					K[i][w] = K[i - 1][w];
			}
			dCount++;
		}
		int result = K[n][weightTotal];
		return result;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
