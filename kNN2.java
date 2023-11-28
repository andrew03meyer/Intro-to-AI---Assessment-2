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
        Process();
    }

    public static void Process(){
        Double trd[][] = GetTrainingData();
        Double tsd[][] = GetTestingData();
        //int[] trl = GetTrainingLabel();
        int[] tsl = GetTestLabel();
        int[] out = GetOutputLabel();
        String classes = "";

        for(int x = 0; x < 200; x++){
            classes += " " + String.valueOf(EuclideanCompare(tsd[x], trd));
        }
        WriteClassData(classes);
        //System.out.println(CompareLabels(out, tsl));
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
     * Works out the difference of one test row to every other row in training data
     */
    public static int EuclideanCompare(Double[] testingData, Double[][] trainingData){
        Double[] differences = new Double[200];
        for(int row = 0; row < 200; row++){
            differences[row] = Double.parseDouble("0");
            for(int column = 0; column < 61; column++){
                differences[row] = differences[row] + (Math.abs((trainingData[row][column]) - (testingData[column]) * Math.abs((trainingData[row][column]) - (testingData[column]))));
            }
            differences[row] = Math.sqrt(differences[row]);
        }
        int index = getClass(differences);
        return index;
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

        //Take the top 5 items
        for(int x = 0; x < 5; x++){
            //Find the index of the item
            int index = hash1.get(euclDist[x]);
            //If the training label of that index is 0, increment non
            if(GetTrainingLabel()[index] == 0){
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
        //System.out.println(y);
        Double temp = (Double.parseDouble(String.valueOf(y)))*(Double.parseDouble("100"))/(Double.parseDouble("200"));
        System.out.println("Percentage: " + temp);
        return temp;
    }

    public static void GAColumnComparison(){
        ArrayList<String> population = new ArrayList<String>();
        Random rnd = new Random();
        
        //Initial population of array
        for(int x = 0; x < 100; x++){
            String stringValue = "";

            for(int y=0; y<61; y++){
                int value = Math.abs(rnd.nextInt() % 2);
                stringValue += String.valueOf(value) + " ";
            }

            population.add(stringValue);
        }
        testFunction(population);
    }

    public static void testFunction(ArrayList<String> population){
        Double[][] trainingData = GetTrainingData();
        Double[][] testingData = GetTestingData();
        HashMap<Double,String> columnAccuracy = new HashMap<Double,String>();

        //Checking each column selection
        for(String columnSelection : population){
            //Individual column as string array
            String[] columnArray = columnSelection.stripTrailing().split(" ");
            //Parsing string array into int array
            int[] intColumnArray = new int[columnArray.length];
            for(int x = 0; x < columnArray.length; x++){
                intColumnArray[x] = Integer.parseInt(columnArray[x]);
            }
            //Storage for accuracy per row
            int[] classification = new int[200];

            //Getting each row classification for that set of columns
            for(int x = 0; x < 200; x++){
                classification[x] = EuclideanCompare2(testingData[x], trainingData, intColumnArray);
            }

            //putting the column string and the corresponding accuracy in a hashmap
            columnAccuracy.put(CompareLabels(classification, GetTestLabel()), columnSelection);
        }
        picker(columnAccuracy);
    }

    public static ArrayList<ArrayList> picker(HashMap<Double, String> columnAccuracy){
        ArrayList<Double> values = new ArrayList<Double>();
        ArrayList<ArrayList> parents = new ArrayList<ArrayList>();

        //find the sum
        Double sum = Double.parseDouble("0");
        for(Double temp : columnAccuracy.keySet()){
            sum += temp;
            values.add(temp);
        }

        //pick 100 values
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

            ArrayList<String> temp = new ArrayList<String>();
            temp.add(columnAccuracy.get(values.get(index)));         //add the key
            temp.add(String.valueOf(values.get(index)));               //Add the value

            System.out.println("Random Numb: " + randNum);
            System.out.println("Random Numb: " + columnAccuracy.get(values.get(index)));

            parents.add(temp);                                      //add to parents arraylist
        }
        return parents;
    }

    /*
     * Works out the difference of one test row to every other row in training data
     * parameters - the test data, the train data, the array of column selection
     * returns - the classification given by the getClass() function
     */
    public static int EuclideanCompare2(Double[] testingData, Double[][] trainingData, int[] columnArray){
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
}

