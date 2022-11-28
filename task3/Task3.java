import java.io.*;
import java.util.*;

public class Task3 {

    public static void main(String[] args) throws IOException {
        File file = new File("task3/1cdh.pdb");
        File fileTwo = new File("task3/2csn.pdb");

        checkOverlap(file, fileTwo);
    }

    /**
     * Populates matrix with atoms from first file
     */
    public static Pair[][][] matrixFiller(List<Atom> atomList, Pair[][][] initMatrix){
        Pair[][][] matrix = initMatrix;
        for (Atom atom: atomList) {
            double atomX = Math.round(atom.x);
            double atomY = Math.round(atom.y);
            double atomZ = Math.round(atom.z);
            for (int i = 0; i < 73; i++) {
                for (int j = 0; j < 72; j++) {
                    for (int k = 0; k < 96; k++) {
                        if(matrix[i][j][k].getX() == atomX && matrix[i][j][k].getY() == atomY && matrix[i][j][k].getZ() == atomZ){
                            matrix[i][j][k].addToListOne(atom);
                        }
                    }
                }
            }
        }
        return matrix;
    }
    /**
     * Populates matrix with atoms from second file
     */
    public static Pair[][][] matrixFillerTwo(List<Atom> atomList, Pair[][][] initMatrix){
        Pair[][][] matrix = initMatrix;
        for (Atom atom: atomList) {
            double atomX = Math.round(atom.x);
            double atomY = Math.round(atom.y);
            double atomZ = Math.round(atom.z);
            for (int i = 0; i < 73; i++) {
                for (int j = 0; j < 72; j++) {
                    for (int k = 0; k < 96; k++) {
                        if(matrix[i][j][k].getX() == atomX && matrix[i][j][k].getY() == atomY && matrix[i][j][k].getZ() == atomZ){
                            matrix[i][j][k].addToListTwo(atom);
                        }
                    }
                }
            }
        }
        return matrix;
    }

    /**
     * Primary method for checking for overlap.
     */
    public static void checkOverlap(File file, File file2) throws IOException {
        Pair[][][] matrix = matrixFillerTwo(txtToAtom(file2), matrixFiller(txtToAtom(file), initMatrix()));
        int overlapsFound = 0;
        int comparisons = 0;
        for (int i = 0; i < 73; i++) {
            for (int j = 0; j < 72; j++) {
                for (int k = 0; k < 96; k++) {
                    if(matrix[i][j][k].list.size() != 0){
                        for (Atom atom : matrix[i][j][k].list) {
                            overlapsFound += neighbours(i,j,k,matrix,atom).get(0);
                            //comparisons += neighbours(i,j,k,matrix,atom).get(1);
                        }
                    }
                }
            }
        }
        System.out.println("Overlaps " + overlapsFound);
        System.out.println("Comparisons " + comparisons);
    }

    /**
     * Gets all neighbours for a cell, check if they have an atom, and then double checks distance to atom(s).
     */
    public static List<Integer> neighbours(int x, int y, int z, Pair[][][] matrix, Atom atomOrg) throws IOException {
        List<Atom> atoms = new ArrayList<>();
        int countComp = 0;
        int countOverlap = 0;
        for (int i = -1; i <= 1; i++) {
            if(x+i < 73 && x+i > -1){
                for (int j = -1; j <= 1; j++) {
                    if(y+j < 72 && y+j > -1){
                        for (int k = -1; k <= 1; k++) {
                            if(z+k < 96 && z+k > -1) {
                                if (matrix[x + i][y + j][z + k].listTwo.size() != 0) atoms.addAll(matrix[x + i][y + j][z + k].listTwo);
                            }
                        }
                    }
                }
            }
        }
        for (Atom atom: atoms) {
            countComp += 1;
            if(distance(atom, atomOrg) <= 2){
                countOverlap += 1;
                writeToFile(atom,atomOrg);
            }
        }
        List<Integer> figures = new ArrayList<>();
        figures.add(countOverlap);
        figures.add(countComp);
        return figures;
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
        private final double x;
        private final double y;
        private final double z;

        public Atom(double number, double x, double y, double z, String name){
            this.number = number;
            this.name   = name;
            this.x      = x;
            this.y      = y;
            this.z      = z;
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

        public List<Atom> getList() {
            return list;
        }

        public void addToListOne(Atom atom) {
            this.list.add(atom);
        }

        public void addToListTwo(Atom atom) {
            this.listTwo.add(atom);
        }
    }

    public static void writeToFile(Atom atom1, Atom atom2) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("cleaned", true));
        writer.write(atom1.number + " " + atom1.name + "  " + atom2.number + " " + atom2.name);
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
                int number = Integer.parseInt(splitString.get(1));
                double xCord = Double.parseDouble(splitString.get(6));
                double yCord = Double.parseDouble(splitString.get(7));
                double zCord = Double.parseDouble(splitString.get(8));
                Atom atom = new Atom(number,xCord,yCord,zCord,name);
                atomList.add(atom);
            }
        }
        scanner.close();
        return atomList;
    }



}
