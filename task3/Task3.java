import java.io.*;
import java.util.*;

public class Task3 {
    static int countComp = 0;
    static int countOverlap = 0;

    static File fileA;

    static String fileAName;

    static String fileBName;

    static File fileB;

    /**
     * The approach that I have taken is as follows:
     * 1. The program reads in all lines from a text file, and makes them into objects of type atom (if line starts with ATOM or HETATM).
     * 2. It creates an empty 3-d matrix of type Pair with a (currently) hard coded size, corresponding to the maximum X/Y/Z coordinates. This matrix is a
     * representation of the space of the protein.
     *      2.5. The matrix has objects of type Pair, which itself contain a list of atoms from file1, and a list from file2, alongside an x,y, and z coordinate
     * 3. The matrix is populated/filled in 2 steps.
     * 4. The program goes through the matrix. Upon finding a cell with a non-empty list1 (i.e. a cell with at least 1 atom from file1), it then checks all
     * neighbours of that cell.
     * 5. The results are saved to a file.
     *      5.5. Since this file may contain duplicates (in the event an atom overlaps with more than 1 other atom), the program cleans the
     *      list and updates the total number of overlapping atoms accordingly.
     */

    public static void main(String[] args) throws IOException {

        findFiles();

        checkOverlap(fileA,fileB);

        //Results are saved to a file named "output".
        stripDuplicatesFromFile(fileAName + "_" + fileBName);

        System.out.println("Overlaps " + countOverlap);
        System.out.println("Comparisons " + countComp);

    }

    /**
     * Small scanner to let users choose which files to import.
     * Note these files must be in the same folder (of name task3) as the code file.
     */
    public static void findFiles(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose file 1. Please write just the file name!");
        fileAName = scanner.nextLine();
        String fileAPath = ("task3/" + fileAName + ".pdb");

        System.out.println("Choose file 2. Please write just the file name!");
        fileBName =  scanner.nextLine();
        String fileBPath = ("task3/" + fileBName + ".pdb");

        fileA = new File(fileAPath);
        fileB = new File(fileBPath);
    }

    /**
     * Populates matrix with atoms from first file
     */
    public static Pair[][][] matrixFiller(List<Atom> atomList, Pair[][][] initMatrix){
        for (Atom atom: atomList) {
            double atomX = Math.round(atom.x);
            double atomY = Math.round(atom.y);
            double atomZ = Math.round(atom.z);
            for (int i = 0; i < 73; i++) {
                for (int j = 0; j < 72; j++) {
                    for (int k = 0; k < 96; k++) {
                        if(initMatrix[i][j][k].getX() == atomX && initMatrix[i][j][k].getY() == atomY && initMatrix[i][j][k].getZ() == atomZ){
                            initMatrix[i][j][k].addToListOne(atom);
                        }
                    }
                }
            }
        }
        return initMatrix;
    }

    /**
     * Populates matrix with atoms from second file
     */
    public static Pair[][][] matrixFillerTwo(List<Atom> atomList, Pair[][][] initMatrix){
        for (Atom atom: atomList) {

            double atomX = Math.round(atom.x);
            double atomY = Math.round(atom.y);
            double atomZ = Math.round(atom.z);
            for (int i = 0; i < 73; i++) {
                for (int j = 0; j < 72; j++) {
                    for (int k = 0; k < 96; k++) {
                        if(initMatrix[i][j][k].getX() == atomX && initMatrix[i][j][k].getY() == atomY && initMatrix[i][j][k].getZ() == atomZ){
                            initMatrix[i][j][k].addToListTwo(atom);
                        }
                    }
                }
            }
        }
        return initMatrix;
    }

     /**
     * Goes through the fully populated matrix, and calls neighbours when a non-empty cell is encountered.
     * Any overlapping atoms which are found are removed from the matrix, in order to reduce redundant comparisons.
     */
    public static void checkOverlap(File file, File file2) throws IOException {
        Pair[][][] matrix = matrixFillerTwo(txtToAtom(file2), matrixFiller(txtToAtom(file), initMatrix()));
        for (int i = 0; i < 73; i++) {
            for (int j = 0; j < 72; j++) {
                for (int k = 0; k < 96; k++) {
                    if(matrix[i][j][k].list.size() != 0 ){
                        for (Atom atom : matrix[i][j][k].list) {
                            matrix[i][j][k].listTwo.removeAll(neighbours(i,j,k,matrix,atom));
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all neighbours for a cell, check if they have an atom, and then double checks distance to atom(s)
     * (needed to due to previous rounding).
     */
    public static List<Atom> neighbours(int x, int y, int z, Pair[][][] matrix, Atom atomOrg) throws IOException {
        List<Atom> atoms = new ArrayList<>();
        List<Atom> atomToRemove = new ArrayList<>();

        for (int i = -4; i <= 4; i++) {
            if(x+i < 73 && x+i > -1){
                for (int j = -4; j <= 4; j++) {
                    if(y+j < 72 && y+j > -1){
                        for (int k = -4; k <= 4; k++) {
                            if(z+k < 96 && z+k > -1) {
                                if (matrix[x + i][y + j][z + k].listTwo.size() != 0){
                                    atoms.addAll(matrix[x + i][y + j][z + k].listTwo);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (Atom atom: atoms) {
            countComp += 1;
            if(distance(atom, atomOrg) <= 4){
                countOverlap += 1;
                atomToRemove.add(atom);
                writeToFile(atom);
            }
        }
        return atomToRemove;
    }

    /**
     * Makes an "empty" matrix with Pair(s).
     */
    public static Pair[][][] initMatrix(){
        Pair[][][] matrix = new Pair[73][72][96];
        for (int i = 0; i < 73; i++) {
            for (int j = 0; j < 72; j++) {
                for (int k = 0; k < 96; k++) {
                    List<Atom> list = new ArrayList<>();
                    matrix[i][j][k] = new Pair(i-46,j+13,k+21,list);
                }
            }
        }
        return matrix;
    }

    /**
     * Class for objects of type atom.
     */
    public static class Atom{
        private final double number;
        private final String name;
        private final String el;
        private final double x;
        private final double y;
        private final double z;
        private final double numb2;

        public Atom(double number, double x, double y, double z, String name, double numb2, String el){
            this.number = number;
            this.name   = name;
            this.x      = x;
            this.y      = y;
            this.z      = z;
            this.el = el;
            this.numb2 = numb2;
        }
    }

    /**
     * Class for objects of type pair.
     */
    public static class Pair {
        private int x;
        private int y;
        private int z;
        private List<Atom> list;
        private List<Atom> listTwo = new ArrayList<>();

        public Pair(int x, int y, int z, List<Atom> atomList){
            this.x = x;
            this.y = y;
            this.z = z;
            this.list = atomList;
        }

        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public int getZ() {
            return z;
        }

        public void addToListOne(Atom atom) {
            this.list.add(atom);
        }

        public void addToListTwo(Atom atom) {
            this.listTwo.add(atom);
        }
    }

    /**
     * Just a small method to write Atoms to a file.
     */
    public static void writeToFile(Atom atom1) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileAName+"_"+fileBName, true));
        writer.write(atom1.number + "  " + atom1.name + "   " + atom1.numb2 + "  " + atom1.el);
        writer.newLine();
        writer.close();
    }

    /**
     * Finds distance between 2 atoms.
     */
    public static double distance(Atom atom1, Atom atom2){
        return Math.sqrt(Math.pow(atom2.x - atom1.x,2) + Math.pow(atom2.y - atom1.y,2) + Math.pow(atom2.z - atom1.z,2));
    }
    
    /**
     * Takes all atoms/hetatm in file, makes them into atom objects, then returns a list of them.
     */
    public static List<Atom> txtToAtom(File file) throws FileNotFoundException {
        List<Atom> atomList = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()){
            String string = scanner.nextLine().replaceAll("\\s+"," ");
            if(string.startsWith("ATOM") || string.startsWith("HETATM")){
                List<String> splitString = List.of(string.split(" "));
                String name  = splitString.get(3);
                String el = splitString.get(11);
                int number = Integer.parseInt(splitString.get(1));
                double number2 = Integer.parseInt(splitString.get(5));
                double xCord = Double.parseDouble(splitString.get(6));
                double yCord = Double.parseDouble(splitString.get(7));
                double zCord = Double.parseDouble(splitString.get(8));
                Atom atom = new Atom(number,xCord,yCord,zCord,name, number2, el);
                atomList.add(atom);
            }
        }
        scanner.close();
        return atomList;
    }

    /**
     * Strips duplicates from the file made in writeToFile, and updates the total number of overlapping
     * atoms accordingly.
     */
    public static void stripDuplicatesFromFile(String filename) throws IOException {
        int count = 0;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Set<String> lines = new HashSet<>(5000);
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String unique : lines) {
            writer.write(unique);
            writer.newLine();
            count += 1;
        }
        writer.write("Number of clashing atoms:" + count);
        writer.newLine();
        writer.write("Number of comparisons:" + countComp);
        writer.close();
        countOverlap = count;
    }
}
