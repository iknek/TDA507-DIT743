import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task2 {

    public static void main(String[] args) {
        readFile("src/test_q1.txt");
        List<Atom> atomList = atomFactory(readFile("src/data_q1.txt"));

        findNeighboursAdv(atomList);

        for (Atom atom: smartSort(atomList)) {
            System.out.println(atom.number);
        }
    }

    public static Atom startChain(List<Atom> atomList){
        for (Atom atom: atomList) {
            if(atom.neighbour.size() == 1){
                return atom;
            }
        }
        return null;
    }

    public static List<Atom> smartSort(List<Atom> aList){
        List<Atom> initList = new ArrayList<>(aList);
        List<Atom> finalList = new ArrayList<>();

        finalList.add(startChain(aList));
        initList.remove(startChain(aList));

        for(int j = 0; j < aList.size(); j++) {
            for (int i = 0; i < initList.size(); i++) {
                if(finalList.get(j).neighbour.contains(initList.get(i))){
                    finalList.add(initList.get(i));
                    initList.remove(initList.get(i));
                }
            }
        }
        return finalList;
    }

    public static List<Atom> sorter(List<Atom> atomList){
        List<Atom> initList = new ArrayList<>(atomList);
        List<Atom> finalList = new ArrayList<>();

        finalList.add(initList.get(0).neighbour.get(0));
        finalList.add(initList.get(0));
        finalList.add(initList.get(0).neighbour.get(1));

        initList.remove(initList.get(0));
        while(!finalList.get(0).neighbour.get(0).equals(finalList.get(1)) || !finalList.get(0).neighbour.get(1).equals(finalList.get(1))){
            for (int j = 0; j <= 1; j++) {
                if (finalList.size() < initList.size()) {
                    if (!finalList.get(0).neighbour.get(j).equals(finalList.get(1)))
                        finalList.add(0, finalList.get(0).neighbour.get(j));
                }
                else{
                    for (Atom atom: initList) {
                        if(!finalList.contains(atom)) finalList.add(atom);
                    }
                    return finalList;
                }
            }
        }
        for (Atom atom: initList) {
            if(!finalList.contains(atom)) finalList.add(atom);
        }
        return finalList;
    }

    /**
     * Assigns neighbors to each atoms
     */
    public static void findNeighboursAdv(List<Atom> atomList) {
        for (int i = 0; i < atomList.size(); i++) {
            for (Atom atom : atomList) {
                if (distance(atomList.get(i), atom) <= 3.86 && !atomList.get(i).equals(atom)) {
                    atomList.get(i).neighbour.add(atom);
                }
            }
        }
        for (Atom atom : atomList) {
            if (atom.neighbour.size() < 1) {
                Atom temp = null;
                System.out.println("Less than 2 neighbours found for atom no: " + atom.number);
                if(!atomList.get(0).equals(atom)) {
                    temp = atomList.get(0);
                    for (int j = 1; j < atomList.size(); j++) {
                        if (distance(atom, atomList.get(j)) < distance(atom, temp) && !atomList.get(j).equals(atom))
                            temp = atomList.get(j);
                    }
                }
                atom.neighbour.add(temp);
            }
        }
    }

    /**
     * Finds distance between 2 atoms.
     */
    public static double distance(Atom atom1, Atom atom2){
        return Math.sqrt(Math.pow(atom2.getX() - atom1.getX(),2) + Math.pow(atom2.getY() - atom1.getY(),2) + Math.pow(atom2.getZ() - atom1.getZ(),2));
    }

    /**
     * Reads a txt file and puts all data from it into a matrix.
     */
    public static double[][] readFile(String path){
        double[][] value = null;
        File file = new File(path);
        try {
            Scanner sizeScanner = new Scanner(file);
            int rows = (sizeScanner.nextLine().split(" ")).length;
            int col = sizeScanner.findAll("\n").toList().size() + 1;
            sizeScanner.close();

            Scanner scanner = new Scanner(file);
            value = new double[col][rows];
            for (int i = 0; i < col; i++) {
                String[] numbers = scanner.nextLine().split(" ");
                for (int j = 0; j < rows; j++) {
                    value[i][j] = Double.parseDouble(numbers[j]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Goes through the created matrix, and makes objects of type atom for each entry.
     */
    public static List<Atom> atomFactory(double[][] mat){
        List<Atom> atomList = new ArrayList<>();
        for (int i = 0; i < mat.length; i++) {
            double number = mat[i][0];
            atomList.add(new Atom(number, mat[i][1],mat[i][2],mat[i][3]));
        }
        return atomList;
    }

    /**
     * Simply there for troubleshooting and checking imports work. Prints a matrix of undefined size.
     */
    public static void printMatrix(double[][] mat){
        for (int i = 0; i < mat.length; i++){
            System.out.println();
            // Loop through all elements of current row
            for (int j = 0; j < mat[i].length; j++)
                System.out.print(mat[i][j] + " ");
        }
    }

    /**
     * Class for objects of type atom.
     */
    public static class Atom{
        private final double number;
        private final double x;
        private final double y;
        private final double z;
        private List<Atom> neighbour = new ArrayList<>();

        public Atom(double number, double x, double y, double z){
            this.number = number;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getNumber() {
            return number;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
