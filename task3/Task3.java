import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task3 {

    public static void main(String[] args) throws IOException {
        File file = new File("task3/1cdh.pdb");
        txtToAtom(file);
        lowest(file);
    }

    public static void lowest(File file) throws FileNotFoundException {
        List<Atom> list = txtToAtom(file);
        //printMatrix(matrix3d(list));
        matrix3d(list);
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
                String name  = splitString.get(2);
                int number   = Integer.parseInt(splitString.get(1));
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

    public static Atom[][][] matrix3d(List<Atom> atomList){
        Atom[][][] matrix = new Atom[76][84][96];
        int count = 0;
        for (Atom atom: atomList) {
            double atomX = atom.x - (atom.x % 2);
            double atomY = atom.y - (atom.y % 2);
            double atomZ = atom.z - (atom.z % 2);
            matrix[(int) atomX][(int) atomY][(int) atomZ] = atom;
            /*
            outer: for (int i = -48; i <= 28; i++) {
                if(i == atomX){
                    for (int j = 12; j <= 72; j++) {
                        if(j == atomY){
                            for (int k = 20; k <= 96; k++) {
                                if(k == atomZ){
                                    matrix[i][j][k] = atom;
                                    count+=1;
                                    System.out.println(count);
                                    break outer;
                                }
                            }
                        }
                    }
                }
            }*/
        }
        System.out.println(count);
        return matrix;
    }
    /**
     * Finds distance between 2 atoms.
     */
    public static double distance(Atom atom1, Atom atom2){
        return Math.sqrt(Math.pow(atom2.x - atom1.x,2) + Math.pow(atom2.y - atom1.y,2) + Math.pow(atom2.z - atom1.z,2));
    }


    /**
     * Simply there for troubleshooting and checking imports work. Prints a matrix of any size.
     */
    public static void printMatrix(Atom[][][] mat){
        for (int i = 0; i <= mat[i].length; i++){
            for (int j = 0; j <= mat[j].length; j++)
                for (int k = 0; k <= mat[k].length; j++)
                    System.out.print(mat[i][j][k] + " ");
        }
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
}
