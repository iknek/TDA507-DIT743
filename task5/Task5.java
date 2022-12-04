package task5;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class Task5 {


    static double smallestP;
    
    static double largestP;

    public static void main(String[] args) {
        List<Double> doubles = readFile("task5/traj-2.txt");
        //Collections.sort(doubles);
        findRange(doubles);
        divIntoLists(doubles);
        //System.out.println(discretiser(doubles).size());
    }

    public static List<List<Double>> divIntoLists(List<Double> doubles){
        final AtomicInteger counter = new AtomicInteger();
        
        Collection<List<Double>> result = doubles.stream().collect
        (Collectors.groupingBy(it -> counter.getAndIncrement() / 500)).values();

        return result.stream().collect(Collectors.toList());
    }

    /*public static List<List<Double>> discretiseList (List<List<Double>> listDoubles){
        List<List<Double>> binnedList = new ArrayList<>();
        List<Double> tmpList = new ArrayList<>();
        double count = 0;

        for (List<Double> subList : listDoubles) {
            count+=1;
            for (int i = 0; i < subList.size(); i++) {
                tmpList.add(count);
            }
            binnedList.add(tmpList);
            tmpList.clear();
        }

        return binnedList;
    }*/

    public static List<Double> readFile(String path){
        File file = new File(path);
        List<Double> intList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                Double numb = Double.parseDouble(scanner.nextLine().replaceAll(" ",""));
                intList.add(numb);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return intList;
    }

    public static void findRange(List<Double> numbers){
        double smallest = numbers.get(0);
        double largest = numbers.get(1);

        for (Double double1 : numbers) {
            if(double1 < smallest){
                smallest = double1;
            }
            else if(double1 > largest){
                largest = double1;
            }
        }

        smallestP = smallest;
        largestP = largest;

        System.out.println(smallest + "  " + largest);
    }
}