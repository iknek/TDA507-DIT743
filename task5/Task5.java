package task5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Task5 {

    // Smallest number in trajectory file
    protected static Double smallestP;

    // Largest number in trajectory file
    protected static Double largestP;

    // Set the number of bins to use for discretization
    protected static int numBins = 100;

    protected static List<List<Double>> binnedData;

    protected static int[][] countMatrix;

    public static void main(String[] args) throws IOException {

        String path = findFiles();
        // Creates a list with all the numbers in the trajectory file
        List<Double> doubles = readFile(path);

        // Finds smallest and largest number in 'doubles'.
        findRange(doubles);

        // Discretizes the numbers
        binnedData = discretize(doubles);

        // Saves histogram to a file named 'histogram.txt'
        makeHistogram(1);

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

        File file = new File("transitionMatrix.txt");
        FileWriter writer = new FileWriter(file);
        for (double[] mat : tMatrix) {
            for (double i : mat) {
                String str = String.format("%.2f", i);
                writer.write(str + "  ");
            }
            writer.write("\n");
        }
        System.out.println("Transition matrix generated successfully in location: " + file.getAbsolutePath());
        writer.close();
        return tMatrix;
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

        //Saving to file
        File file = new File("countMatrix.txt");
        FileWriter writer = new FileWriter(file);
        for (int[] mat : countMatrix) {
            for (int i : mat) {
                // Formats so each row has the same total number of characters
                writer.write(String.format("%3d", i));
            }
            writer.write("\n");
        }
        writer.close();
        System.out.println("Count matrix generated successfully in location: " + file.getAbsolutePath());
        return countMatrix;
    }

    /** Exercise 3
     * Takes the discretized list (list of list of doubles), and prints a histogram to a file.
     * int compactRatio allows for more compact printing
     *      Ex: When set to 1, it will skip every other "*", compacting the histogram bars.
     *          Useful when a bin has more numbers that one can print characters in a row.
     */
    public static void makeHistogram(int compactRatio) throws IOException {
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
                k += compactRatio;
            }
            writer.write("\n");
        }
        System.out.println("Histogram generated successfully in location: " + file.getAbsolutePath());

        writer.close();
    }

    /** Exercise 2
     * Discretizes a list of doubles into a number of bins, decided by numBins.
     */
    public static List<List<Double>> discretize(List<Double> numbers) {

        // Calculate the size of each bin
        double binSize = (largestP - smallestP) / numBins;

        // Adds an empty list for each bin, to the list "bins".
        List<List<Double>> bins = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            bins.add(new ArrayList<>());
        }

        /* Loops through every number in numbers
           Finds the right bin for it, and saves that to binIndex
           Gets the bin at that index, and places the number into it.
        */
        for (double number : numbers) {
            int binIndex = (int) ((number - smallestP) % numBins / binSize % numBins);
            bins.get(binIndex).add(number);
        }

        System.out.println("List discretized successfully.");
        return bins;
    }

    /** Sets smallest/largestP to the largest and smallest number in a list. */
    private static void findRange(List<Double> numbers){
        smallestP = Collections.min(numbers);
        largestP = Collections.max(numbers);
    }

    /** Exercise 1
     * Reads in a file and returns a collection of all the numbers in it.
     */
    private static List<Double> readFile(String path) throws FileNotFoundException {
        List<Double> numbList = new ArrayList<>();
        Scanner scanner = new Scanner(new File(path));
        while(scanner.hasNextLine()) {
            Double numb = Double.valueOf(scanner.nextLine().replaceAll(" ", ""));
            numbList.add(numb);
        }
        System.out.println("Trajectory file read in successfully.");
        return numbList;
    }


    /** Used to prompt a user for which trajectory file to read in. */
    public static String findFiles() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose file 1. Please write just the file name! "+ "\n" + "File Name:  " );
        String fileName = scanner.nextLine();
        return ("task5/" + fileName + ".txt");
    }
}