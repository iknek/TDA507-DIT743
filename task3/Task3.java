import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task3 {

    public static void main(String[] args) throws IOException {
        File file = new File("task3/1cdh.pdb");
        cleanPDB(file);
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

    public static void cleanPDB(File file) throws IOException {
        //File newPDB = new File("TDA507-DIT743/task3", file.getName()+ "new");

        BufferedWriter writer = new BufferedWriter(new FileWriter("cleaned", true));
        Scanner scanner = new Scanner(file);
        int count = 0;
        while(scanner.hasNextLine()){
            String string = scanner.nextLine();
            if(string.startsWith("ATOM")) {
                writer.write(string);
                writer.newLine();
            }
        }
        writer.close();
        System.out.println(count);
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
