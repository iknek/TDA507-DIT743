package task5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Task5 {

    // Smallest number in trajectory file
    protected static double smallestP;

    // Largest number in trajectory file
    protected static double largestP;

    // Set the number of bins to use for discretization
    protected final static int numBins = 100;

    protected static List<List<Double>> binnedData;

    protected static int[][] countMatrix;

    public static void main(String[] args) throws IOException {
        // Creates a list with all the numbers in the trajectory file
        List<Double> doubles = readFile("task5/traj-2.txt");
        // Discretizes the numbers
        binnedData = discretize(doubles);

        // Finds smallest and largest number in 'doubles'.
        findRange(doubles);

        // Saves histogram to a file named 'histogram.txt'
        makeHistogram();

        // Saves the count matrix to a file names 'countMatrix.txt'
        countMatrix = createCountMatrix(doubles);

        //Doesn't "MatrixVisualizer2000" sound like some awesome tech thing from the 80s?
        MatrixVisualizer2000 matrixVisualizer = new MatrixVisualizer2000(transitionMatrix());
        matrixVisualizer.setVisible(true);
    }

    /** Exercise 5
     * This method:
     * Saves the transitional matrix to a file named transitionMatrix.txt
     * Returns it as an array.
     */
    public static double[][] transitionMatrix() throws IOException {
        double[][] tMatrix = new double[numBins][numBins];
        int rowIndex = -1;
        for (int[] row : countMatrix) {
            int colIndex = -1;
            rowIndex += 1;
            double sum = Arrays.stream(row).sum();
            for (int i = 0; i < row.length; i++) {
                colIndex += 1;
                tMatrix[rowIndex][colIndex] = (countMatrix[rowIndex][colIndex])/sum;
            }
        }

        FileWriter writer = new FileWriter("tMatrix.txt");
        for (double[] mat : tMatrix) {
            for (double i : mat) {
                String str = String.format("%.2f", i);
                writer.write(str + "  ");
            }
            writer.write("\n");
        }
        writer.close();
        return tMatrix;
    }

    /**
     * Same as above, but with the txt file made of fractions.
     */
    public static void tMatrixAsFractions() throws IOException {
        String[][] tMatrix = new String[numBins][numBins];
        int rowIndex = -1;
        for (int[] row : countMatrix) {
            int colIndex = -1;
            rowIndex += 1;
            double sum = Arrays.stream(row).sum();
            for (int i = 0; i < row.length; i++) {
                colIndex += 1;
                int number = countMatrix[rowIndex][colIndex];
                if(number == 0){
                    tMatrix[rowIndex][colIndex] = String.valueOf(number);
                }
                else{
                    tMatrix[rowIndex][colIndex] = number + "/" + (int) sum;
                }
            }
        }
        FileWriter writer = new FileWriter("transitionMatrix.txt");
        for (String[] mat : tMatrix) {
            for (String i : mat) {
                String str = String.format(i);
                writer.write(str + "  ");
            }
            writer.write("\n");
        }
        writer.close();
    }

    /** Exercise 4
     * This method:
     *      Makes a map of each number in the binned data list, and its corresponding bin.
     *      Then it goes through that a list of all numbers and counts the number of transitions, by incrementing a specific call in the (count) matrix.
     *      Saves the resulting matrix to a file.
     */
    public static int[][] createCountMatrix(List<Double> doubles) throws IOException {
        int[][] countMatrix = new int[numBins][numBins];
        Map<Double, Integer> stateMap = new HashMap<>();

        // Create a map that maps each number in the doubles list to its corresponding state in the binnedData list
        for (int j = 0; j < binnedData.size(); j++) {
            for (double number : binnedData.get(j)) {
                stateMap.put(number, j);
            }
        }

        // Iterates over all numbers and increments the count for the appropriate cell in the count matrix
        for (int i = 0; i < doubles.size() - 1; i++) {
            int stateOne = stateMap.get(doubles.get(i));
            int stateTwo = stateMap.get(doubles.get(i + 1));
            countMatrix[stateOne][stateTwo]++;
        }

        FileWriter writer = new FileWriter("countMatrix.txt");
        for (int[] mat : countMatrix) {
            for (int i : mat) {
                // Formats so each row has the same total number of characters
                writer.write(String.format("%3d", i));
            }
            writer.write("\n");
        }
        writer.close();

        return countMatrix;
    }


    /** Exercise 3
     * Takes the discretized list (list of list of doubles), and prints a histogram to a file.
     */
    public static void makeHistogram() throws IOException {
        File file = new File("histogram.txt");
        FileWriter writer = new FileWriter(file);
        for (int i = 0; i < numBins; i++) {
            if (i+1 < 10) {
                writer.write("Bin " + (i+1) + ":   ");
            } else if (i+1 <= 99) {
                writer.write("Bin " + (i+1) + ":  ");
            } else {
                writer.write("Bin " + (i+1) + ": ");
            }
            for (int k = 0; k < binnedData.get(i).size(); k++) {
                writer.write("*");
            }
            writer.write("\n");
        }
        writer.close();
    }

    /** Exercise 2
     * Discretizes a list of doubles into 100 bins, of varing sizes.
     */
    public static List<List<Double>> discretize(List<Double> numbers) {
        // Calculate the size of each bin
        double binSize = (Collections.max(numbers) - Collections.min(numbers)) / numBins;
        // Discretize the numbers by placing them in appropriate bins
        List<List<Double>> bins = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            bins.add(new ArrayList<>());
        }
        for (double number : numbers) {
            int binIndex = (int) ((number - Collections.min(numbers)) / binSize);
            bins.get(binIndex).add(number);
        }
        return bins;
    }

    /** Exercise 2
     * Just returns the largest and smallest number in a list.
     */
    private static void findRange(List<Double> numbers){
        smallestP = Collections.min(numbers);
        largestP = Collections.max(numbers);
        System.out.println(smallestP + "  " + largestP);
    }

    /** Exercise 1
     * Reads in a file and returns a collection of all the numbers in it.
     */
    private static List<Double> readFile(String path) throws FileNotFoundException {
        List<Double> numbList = new ArrayList<>();
        Scanner scanner = new Scanner(new File(path));
        while(scanner.hasNextLine()) {
            Double numb = Double.parseDouble(scanner.nextLine().replaceAll(" ", ""));
            numbList.add(numb);
        }
        return numbList;
    }

    /**
     * Just used to abstract out otherwise duplicate code. Saves a matrix to a file.
     */
    private static void saveMatrixToFile(String name, double[][] matrix) throws IOException {
        File file = new File(name);
        FileWriter writer = new FileWriter(file);
        for (double[] mat : matrix) {
            for (double i : mat) {
                // Formats so each row has the same total number of characters
                writer.write(String.format("%3d", i));
            }
            writer.write("\n");
        }
        writer.close();
    }

}