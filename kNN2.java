import javax.lang.model.type.ArrayType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class kNN2 {
    public static void main(String[] args){
        GAColumnComparison();
    }

    public static Double[][] GetTrainingData(){
        Double[][] trainingData = new Double[200][61];

        try{
            File trainingFile = new File("train_data.txt");
            Scanner tfScanner = new Scanner(trainingFile);
            
            int rowIndex = 0;
            int columnIndex;

            while(tfScanner.hasNextLine()){    
                columnIndex = 0;
                //Split line by space and insert into the 2D array
                String[] temp = tfScanner.nextLine().split(" ");
                for(String item : temp){
                    trainingData[rowIndex][columnIndex] = Double.parseDouble(item);
                    columnIndex++;
                }
                rowIndex++;
            }
            tfScanner.close();
        }
        catch(Exception e){
            System.out.println("Exception: " + e);
        }
        return trainingData;
    }

    public static Double[][] GetTestingData(){
        Double[][] testingData = new Double[200][61];

        try{
            File testingFile = new File("test_data.txt");
            Scanner tsfScanner = new Scanner(testingFile);
            
            int rowIndex = 0;
            int columnIndex;

            while(tsfScanner.hasNextLine()){    
                columnIndex = 0;
                //Split line by space and insert into the 2D array
                String[] temp = tsfScanner.nextLine().split(" ");
                for(String item : temp){
                    testingData[rowIndex][columnIndex] = Double.parseDouble(item);
                    columnIndex++;
                }
                rowIndex++;
            }
            tsfScanner.close();
        }
        catch(Exception e){
            System.out.println("Exception: " + e);
        }
        return testingData;
    }

    public static int[] GetTrainingLabel(){
        try{
            File trainingLabelFile = new File("train_label.txt");
            Scanner tlScanner = new Scanner(trainingLabelFile);

            String[] temp = tlScanner.nextLine().stripLeading().split(" ");
            int index = 0;
            int[] trainingLabels = new int[200];

            for(String items:temp){
                trainingLabels[index] = Integer.parseInt(items);
                index++;
            }
            tlScanner.close();
            return trainingLabels;
        }
        catch(Exception e){
            System.out.println("Exception: " + e);
            int[] temp = {};
            return temp;
        }
    }

    public static int[] GetTestLabel(){
        try{
            File testLabelFile = new File("test_label.txt");
            Scanner tslScanner = new Scanner(testLabelFile);

            String[] temp = tslScanner.nextLine().stripLeading().split(" ");
            int index = 0;
            int[] testLabels = new int[200];

            for(String items:temp){
                testLabels[index] = Integer.parseInt(items);
                index++;
            }
            tslScanner.close();
            return testLabels;
        }
        catch(Exception e){
            System.out.println("Exception: " + e);
            int[] temp = {};
            return temp;
        }
    }

    public static int[] GetOutputLabel(){
        try{
            File outLabelFile = new File("output2.txt");
            Scanner outScanner = new Scanner(outLabelFile);

            String[] temp = outScanner.nextLine().stripLeading().split(" ");
            int index = 0;
            int[] outLabels = new int[200];

            for(String items:temp){
                outLabels[index] = Integer.parseInt(items);
                index++;
            }
            outScanner.close();
            return outLabels;
        }
        catch(Exception e){
            System.out.println("Exception: " + e);
            int[] temp = {};
            return temp;
        }
    }

    /*
     * find the class of item based on kNN (k=5)
     * Parameters - Array of Euclidean distances of each row
     * Return 0/1 based on 5 closest values
     */
    public static int getClass(Double[] euclDist){

        //Create a HashMap to negate loss of index
        HashMap<Double, Integer> hash1 = new HashMap<Double, Integer>();
        for(int y = 0; y<200; y++){
            hash1.put(euclDist[y], y);
        }

        //Sort the array
        Arrays.sort(euclDist);

        //Variables for non/alchoholic count
        int alc = 0;
        int non = 0;
        int[] trainingLabel = GetTrainingLabel();

        //Take the top 5 items
        for(int x = 0; x < 5; x++){
            //Find the index of the item
            int index = hash1.get(euclDist[x]);
            //If the training label of that index is 0, increment non
            if(trainingLabel[index] == 0){
                non++;
            }
            //Otherwise, increment alc
            else{
                alc++;
            }
        }
        //Return which class, based on alc & non counts
        if(non > alc){return 0;}else{return 1;}
    }

    /*
     * Write the String param to a file (output2.txt)
     */
    public static void WriteClassData(String itemClass){
        try{
            File classData = new File("output2.txt");
                FileWriter fwr = new FileWriter(classData);
                fwr.write(itemClass);
                fwr.close();
        }
        catch(Exception e){
            System.out.println("File already exists");
        }
    }

    /*
     * Parameters - knn2 output, real classifications
     * Returns percentage accuracy
     */
    public static Double CompareLabels(int[] out, int[] tsl){
        int y=0;
        for(int x = 0; x < 200; x++){
            if(out[x] == tsl[x]){
                y++;
            }
        }

        Double temp = (Double.parseDouble(String.valueOf(y)))*(Double.parseDouble("100"))/(Double.parseDouble("200"));
        //System.out.println("Percentage: " + temp);
        return temp;
    }

    /*
     * Works out the difference of one test row to every other row in training data
     * @calls - getClass
     * @parameters - row of test data, complete train data, the array of column selection
     * @return - classification of that row
     */
    public static int EuclideanCompare(Double[] testingData, Double[][] trainingData, int[] columnArray){
        Double[] differences = new Double[200];
        for(int row = 0; row < 200; row++){

            differences[row] = Double.parseDouble("0");
            for(int column = 0; column < 61; column++){
                if(columnArray[column] != 0){
                    differences[row] = differences[row] + (Math.abs((trainingData[row][column]) - (testingData[column]) * Math.abs((trainingData[row][column]) - (testingData[column]))));
                }
            }
            differences[row] = Math.sqrt(differences[row]);
        }
        int index = getClass(differences);
        return index;
    }

    public static Double bestAccuracy(ArrayList<Double> acc){
        Collections.sort(acc);
        System.out.println(acc.get(0));
        return acc.get(0);
    }

    /*
     * Creates new random 100 population
     * @calls - testFunction, picker, mutation
     * @return - accuracy
     */
    public static void GAColumnComparison(){
        Double[][] trainingData = GetTrainingData();
        Double[][] testingData = GetTestingData();
        int[] testLabel = GetTestLabel();
        ArrayList<String> population = new ArrayList<String>();
        Random rnd = new Random();
        ArrayList<ArrayList<String>> selectionAccuracy = new ArrayList<ArrayList<String>>();
        
        //Initial population of array
        for(int x = 0; x < 100; x++){
            String stringValue = "";

            for(int y=0; y<61; y++){
                int value = Math.abs(rnd.nextInt() % 2);
                stringValue += String.valueOf(value) + " ";
            }
            population.add(stringValue);
        }

        int index = 0;
        Double highest = Double.parseDouble("0");

        while(index < 5 && highest < 90) {
            System.out.println("New Population");
            ArrayList<Double> values = new ArrayList<Double>();
            //Call test function per row
            for (String selection : population) {
                ArrayList<String> columnAccuracy = testFunction(selection, trainingData, testingData, testLabel);
                selectionAccuracy.add(columnAccuracy);
                values.add(Double.parseDouble(columnAccuracy.get(1)));
            }
            System.out.println("Values[0]" + values.get(0));

            //Call picker
            population = picker(selectionAccuracy);

            //Call evolve
            population = evolve(population);

            //20% chance for mutation
            int rand = rnd.nextInt(100);
            if (rand <= 20) {
                population = mutation(population);
            }

            System.out.println("Population(0):" + population.get(0));

            highest = bestAccuracy(values);
            index++;
        }
    }

    /*
    * @param - One row of population, trainingData, testingData, testLabel
    * @calls - EuclideanCompare, CompareLabels
    * @return - String array with population and accuracy
     */
    public static ArrayList<String> testFunction(String population, Double[][]trainingData, Double[][] testingData, int[] testLabel){

        ArrayList<String> columnAccuracy = new ArrayList<String>();

        //Individual column as string array
        String[] columnArray = population.stripTrailing().split(" ");
        //Parsing string array into int array
        int[] intColumnArray = new int[columnArray.length];
        for(int x = 0; x < columnArray.length; x++){
            intColumnArray[x] = Integer.parseInt(columnArray[x]);
        }

        //Getting each row classification for that set of columns
        int[] classification = new int[200];
        for(int x = 0; x < 200; x++){
            classification[x] = EuclideanCompare(testingData[x], trainingData, intColumnArray);
        }

        //putting the column string and the corresponding accuracy in a hashmap
        columnAccuracy.add(population);
        columnAccuracy.add(String.valueOf(CompareLabels(classification, testLabel)));

        return columnAccuracy;
    }

    /*
     * Picks new 100 based on their accuracy
     * @param - HashMap Key: accuracy, Value: column selection
     * @return - String ArrayList containing new parents
     */
    public static ArrayList<String> picker(ArrayList<ArrayList<String>> columnAccuracy){
        ArrayList<Double> values = new ArrayList<Double>();
        ArrayList<String> parents = new ArrayList<String>();

        //find the sum
        Double sum = Double.parseDouble("0");
        for(ArrayList<String> temp : columnAccuracy){
            sum += Double.parseDouble(temp.get(1));
            values.add(Double.parseDouble(temp.get(1)));
        }

        //pick 100 values (based on chance)
        for(int x = 0; x < 100; x++){
            int index = 0;
            Random rnd1 = new Random();
            Double randNum = rnd1.nextDouble() * sum;
            Double partSum = Double.parseDouble("0");

            //repeat until found value
            while(partSum < randNum){
                partSum += values.get(index);
                index++;
            }

            //add the key
            parents.add(columnAccuracy.get(x).get(0));
        }
        return parents;
    }

    /*
     * Mix up 2 rows of data
     * @param - 2D array of parents
     * @return - 2D array of new parents
     */
    public static ArrayList<String> evolve(ArrayList<String> population){
        ArrayList<String> parents = new ArrayList<String>();

        //Split population into two
        ArrayList<String> firstHalf = new ArrayList<String>(population.subList(0, (population.size()/2)));
        ArrayList<String> secondHalf = new ArrayList<String>(population.subList(population.size()/2, population.size()));

        for(int index = 0; index < firstHalf.size(); index++){
            //Each half of first values
            String ffhString = firstHalf.get(index).substring(0, 61);
            String fshString = firstHalf.get(index).substring(61, 122);

            //Each half of second values
            String sfhString = secondHalf.get(index).substring(0, 61);
            String sshString = secondHalf.get(index).substring(61, 122);

            parents.add(sfhString + fshString);
            parents.add(ffhString + sshString);

            //System.out.println("First Half: " + parents.get(parents.size()-2     ) + "\nSecond half: " + parents.get(parents.size()-1));
        }
        System.out.println("Evolved");

        Collections.shuffle(parents);

        return parents;
    }

    /*
     * Mutate 20 rows
     * @param - 2D ArrayList of columns
     * @return - 2D ArrayList of mutated columns
     */
    public static ArrayList<String> mutation(ArrayList<String> population){
        System.out.println("Mutated");
        for(int x = 0; x < 20; x++){
            String selection = population.get(x).replaceAll("\\s+", "");
            String newSelection = "";

            //For each character in that selection
            for(char value : selection.toCharArray()){
                if(value == '0'){
                    newSelection += "1 ";
                }
                else{
                    newSelection += "0 ";
                }
            }
            population.add(x, newSelection);
        }
        Collections.shuffle(population);

        return population;
    }
}

