import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task2 {

    public static void main(String[] args) {
        List<Atom> atomList = atomFactory(readFile("task2/data_q1.txt"));

        findNeighboursAdv(atomList);

        for (Atom atom: sort(atomList)) {
            System.out.println(atom.number);
        }
    }

    /**
     * Helper method for sort, simply find an atom with only 1 neighbour, to start the chain with.
     */
    public static Atom startChain(List<Atom> atomList){
        for (Atom atom: atomList) {
            if(atom.neighbour.size() == 1){
                return atom;
            }
        }
        return null;
    }

    /**
     * Takes the first atom found in startChain, then adds its neighbours in order.
     */
    public static List<Atom> sort(List<Atom> aList){
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

    /**
     * Assigns neighbors to each atoms. 3.86 å seems to be a good value for the max distance.
     * In the event that no neighbours are found, it'll simply assign the closest instead.
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
                //System.out.println("Less than 2 neighbours found for atom no: " + atom.number);
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
        return Math.sqrt(Math.pow(atom2.x - atom1.x,2) + Math.pow(atom2.y - atom1.y,2) + Math.pow(atom2.z - atom1.z,2));
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
     * Simply there for troubleshooting and checking imports work. Prints a matrix of any size.
     */
    public static void printMatrix(double[][] mat){
        for (int i = 0; i < mat.length; i++){
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
    }
}
