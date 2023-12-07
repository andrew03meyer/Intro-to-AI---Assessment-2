import jdk.jfr.Category;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;
import java.io.File;
import java.io.FileWriter;

public class kNN2 {

    //Array list for closest train row of each test row
    public static ArrayList<Integer> testRowClass = new ArrayList<>();

    public static void main(String[] args){
        GAColumnComparison();
    }

    /*
     * Reads training data from - "train_data.txt"
     * @return - returns the data in a Double, 2D array
     */
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

    /*
     * Reads testing data from - "test_data.txt"
     * @return - returns the data in a Double, 2D array
     */
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

    /*
     * Reads training labels from - "train_label.txt"
     * @return - returns the data in an int array
     */
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
            return new int[]{};
        }
    }

    /*
     * Reads testing labels from - "test_label.txt"
     * @return - returns the data in an int array
     */
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
            return new int[]{};
        }
    }

    /*
     * Writes labels to output2.txt
     */
    public static void WriteClassData(){
        try{
            File classData = new File("output2.txt");
            FileWriter fwr = new FileWriter(classData);
            for(Integer temp : testRowClass){
                fwr.write(String.valueOf(temp) + " ");
            }

            fwr.close();
        }
        catch(Exception e){
            System.out.println("File already exists");
        }
    }

    /*
     * Reads algorithm output labels - "output2.txt"
     * @return - returns the data in an int array
     */
    public static int[] GetOutputLabel(){
        try{
            File outLabelFile = new File("output1.txt");
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
            return new int[]{};
        }
    }

    public static Double GetKnn1Accuracy(int[] testingLabels, Double[][] trainingData, int[]trainingLabels, Double[][] testingData){
        int[]knn1Values = GetOutputLabel();
        String columnSelection = "1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 ";
        return GetAccuracyIndividual(columnSelection, trainingLabels, testingLabels, trainingData, testingData);
    }

    /*
     * Creates new random 100 population
     * @calls - Tournament, Evolution, Mutation, GetAccuracy
     * @return - accuracy
     * Notes - make accuracies only compare changed columnSelections
     *       - make Nearest neighbour k=5 method
     */
    public static void GAColumnComparison(){
        //New random Population
        ArrayList<String> population = new ArrayList<String>();

        Random rnd = new Random();

        //Initial population of array
        for(int x = 0; x < 100; x++){
            String stringValue = "";

            for(int y=0; y<61; y++){
                int value = Math.abs(rnd.nextInt() % 2);
                stringValue += value + " ";
            }
            population.add(stringValue);
        }

        //Setting up variables
        Double accuracy;
        int repeats = 0;
        Double[][] trainingData = GetTrainingData();
        Double[][] testingData = GetTestingData();
        int[] testingLabels = GetTestLabel();
        int[] trainingLabels = GetTrainingLabel();

        //Print kNN1 accuracy & and set goalAccuracy
        Double prevAccuracy = GetKnn1Accuracy(testingLabels, trainingData, trainingLabels, testingData);
        System.out.println(prevAccuracy);
        Double goalAccuracy = prevAccuracy+40;

        if(goalAccuracy > 100){
            goalAccuracy = Double.parseDouble("90");
        }

        //Initial accuracy
        ArrayList<Double> accuracies = GetAccuracy(population, trainingData, testingData, testingLabels, trainingLabels);
        accuracy = GetBestAccuracy(accuracies);

        //Printing out first accuracy
        System.out.println(accuracy);

        while(accuracy < goalAccuracy && repeats < 35){

            ArrayList<ArrayList<String>> columnAccuracies = new ArrayList<>();

            //Creating 2D arraylist for Tournament selection
            for(int index = 0; index < accuracies.size(); index++){
                ArrayList<String> temp = new ArrayList<>();
                temp.add(population.get(index));
                temp.add(String.valueOf(accuracies.get(index)));
                columnAccuracies.add(temp);
            }

            //Tournament, Evolution, and Mutation
            population = Tournament(columnAccuracies);
            population = Evolve(population);
            population = Mutate(population);

            //Accuracy for whole population
            accuracies = GetAccuracy(population, trainingData, testingData, testingLabels, trainingLabels);

            //Best accuracy
            accuracy = GetBestAccuracy(accuracies);

            System.out.println(printBest(population, accuracy, accuracies));
            System.out.println("Accuracy: " + accuracy);

            repeats++;
        }

        //Update testRowClass Array to the values of the most accurate column selection
        GetAccuracyIndividual(printBest(population, accuracy, accuracies), trainingLabels, testingLabels, trainingData, testingData);
        //Write improved values
        WriteClassData();
    }

    /*
     * Finds the Accuracy for every population and returns it in an array
     * @parameters - Population, training data, testing data, testing labels, training labels
     * @calls - GetAccuracyIndividual()
     * @return - Arraylist of type double, containing all the accuracies for each population
     */
    public static ArrayList<Double> GetAccuracy(ArrayList<String> population, Double[][] trainingData, Double[][]testingData, int[]testingLabels, int[] trainingLabels){

        //For every row in population, get accuracy
        ArrayList<Double> selectionAccuracy = new ArrayList<>();
        for(String columnSelection : population){
            selectionAccuracy.add(GetAccuracyIndividual(columnSelection , trainingLabels, testingLabels, trainingData, testingData));
        }

        return selectionAccuracy;
    }

    /*
     * Works out the Manhattan distance of the test data to the training data a single columnSelection
     * Then finds the accuracy
     * @calls - GetClass()
     * @parameters - one population, training labels, test labels, training data, testing data
     * @return - accuracy of that column
     */
    public static Double GetAccuracyIndividual(String columnSelection, int[] trainingLabels, int[] testingLabels, Double[][] trainingData, Double[][] testingData){
        String[] columnArray = columnSelection.split(" ");
        testRowClass  = new ArrayList<>();

        //For every test row
        for(int testDataRow = 0; testDataRow < testingData.length; testDataRow++){
            ArrayList<ArrayList<String>> trainRowDifferences = new ArrayList<>();

            //For every train row
            for(int trainDataRow = 0; trainDataRow < trainingData.length; trainDataRow++){
                double rowDifference = Double.parseDouble("0");

                //For every column
                for(int column = 0; column < 61; column++){
                    if(columnArray[column].equals("1")) {
                        rowDifference += Math.pow(Math.abs((trainingData[trainDataRow][column]) - (testingData[testDataRow][column])), 2);
                    }
                }
                //Find total train row distance
                rowDifference = Math.sqrt(rowDifference);
                ArrayList<String> temp = new ArrayList<>();
                temp.add(String.valueOf(rowDifference));
                temp.add(String.valueOf(trainDataRow));
                trainRowDifferences.add(temp);
            }

            //Adds closest neighbor to testing row
            ArrayList<String> nn = new ArrayList<>();
            nn.add("1000");
            for(ArrayList<String> value : trainRowDifferences){
                if(Double.parseDouble(value.get(0)) < Double.parseDouble(nn.get(0))){
                    nn = value;
                }

            }
            //Add classification for every row of test data
            testRowClass.add(GetClass(nn, trainingLabels));
        }

        //test accuracy
        int count = 0;
        for(int category = 0; category < testingLabels.length; category++){
            if(testingLabels[category] == testRowClass.get(category)){
                count++;
            }
        }
        return ((double)count)/200*100;
    }

    /*
     * Finds the highest accuracy value
     * @parameters - accuracies for each population
     * @return - Double containing the highest
     */
    public static Double GetBestAccuracy(ArrayList<Double> accuracies){
        Double best = Double.parseDouble("0");
        for(Double temp : accuracies){
            if(temp > best) {
                best = temp;
            }
        }
        return best;
    }

    /*
     * find the class of item based on kNN (k=1)
     * @parameters - NN Value and index
     * @return 0/1 based on closest value
     */
    public static int GetClass(ArrayList<String> closestNeighbour, int[] testingLabels){
        return testingLabels[Integer.parseInt(closestNeighbour.get(1))];
    }

    /*
     * Picks new 100 based on their accuracy
     * Picks three random values, the one with the highest accuracy is picked
     * @param - 2D Arraylist (each population, with corresponding accuracy)
     * @return - String ArrayList containing new parents
     */
    public static ArrayList<String> Tournament(ArrayList<ArrayList<String>> columnAccuracy){
        Random rnd1 = new Random();
        ArrayList<String> population = new ArrayList<>();

        for(int x = 0; x <100; x++) {
            ArrayList<ArrayList<String>> temp = new ArrayList<>();
            temp.add(columnAccuracy.get(rnd1.nextInt(100)));
            temp.add(columnAccuracy.get(rnd1.nextInt(100)));
            temp.add(columnAccuracy.get(rnd1.nextInt(100)));

            Double max = Double.parseDouble("0");
            String pop = "";
            for (ArrayList<String> value : temp) {
                if (Double.parseDouble(value.get(1)) > max) {
                    pop = value.get(0);
                    max = Double.parseDouble(value.get(1));
                }
            }
            population.add(pop);
        }
        return population;
    }

    /*
     * Take 2 of the population, 80% chance to swap sections
     * Swap depends on a random, pivot point
     * @param - String arraylist of parents
     * @return - string arraylist of new parents
     */
    public static ArrayList<String> Evolve(ArrayList<String> population){
        Random rnd = new Random();

        ArrayList<String> parents = new ArrayList<String>();

        //Split population into two
        ArrayList<String> firstHalf = new ArrayList<String>(population.subList(0, (population.size()/2)));
        ArrayList<String> secondHalf = new ArrayList<String>(population.subList(population.size()/2, population.size()));

        for(int index = 0; index < firstHalf.size(); index++){

            //80% for each parent to crossover
            int random = rnd.nextInt(100);
            if(random <= 80) {

                //Select random pivot point
                random = rnd.nextInt(61)*2;

                //Each part of first values
                String ffhString = firstHalf.get(index).substring(0, random);
                String fshString = firstHalf.get(index).substring(random, 122);

                //Each part of second values
                String sfhString = secondHalf.get(index).substring(0, random);
                String sshString = secondHalf.get(index).substring(random, 122);

                //Add to new arraylist
                parents.add(sfhString + fshString);
                parents.add(ffhString + sshString);
            }
            //20% chance to stay the same
            else{
                parents.add(firstHalf.get(index));
                parents.add(secondHalf.get(index));
            }
        }

        return parents;
    }

    /*
     * For each population, 5% chance to mutate
     * Mutation - swap a random character to the opposite
     * @param - String arraylist of population
     * @return - String arraylist of new population
     */
    public static ArrayList<String> Mutate(ArrayList<String> population){
        ArrayList<String> newPopulation = new ArrayList<>();

        //For every row
        for(String row : population) {
            String concatenation = "";

            //5% chance to mutate
            Random rnd = new Random();
            int random = rnd.nextInt(100);
            if(random <= 5) {
                char[] charArray = row.toCharArray();

                //Swapping random character
                int randChar = rnd.nextInt(61)*2;
                if(charArray[randChar] == '0'){
                    charArray[randChar] = '1';
                }
                else{
                    charArray[randChar] = '0';
                }

                //Concatenation of char array
                for(char c : charArray){
                    concatenation += c;
                }
            }
            //95% chance to not mutate
            else{
                concatenation = row;
            }
            newPopulation.add(concatenation);
        }
        return newPopulation;
    }


    /*
     *¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬*
     *              Testing Methods              *
     *¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬*
     */


    /*
     * Tells me how many of each column selections are in the population
     * @Parameters - Population
     */
    public static void PrintRepeats(ArrayList<String> population){
        HashMap<String, Integer> hash1 = new HashMap<>();
        for(String temp : population){
            if(hash1.containsKey(temp)){
                hash1.put(temp, hash1.get(temp)+1);
            }
            else{
                hash1.put(temp, 1);
            }
        }
        System.out.println(hash1.keySet());
        System.out.println(hash1.values());
    }

    /*
     * Prints column selection for the best accuracy
     * @parameters - population, best accuracy, all accuracies
     */
    public static String printBest(ArrayList<String> population, Double accuracy, ArrayList<Double> accuracies){
        int index = accuracies.indexOf(accuracy);
        return population.get(index);
    }
}