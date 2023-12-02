import jdk.jfr.Category;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.Collections;

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
     * find the class of item based on kNN (k=1)
     * Parameters - NN Value and index
     * Return 0/1 based on closest value
     */
    public static int GetClass(ArrayList<String> closestNeighbour, int[] testingLabels){
        //int[] testLabels = GetTestLabel();

        /*for(String temp : closestNeighbour){
            System.out.println(temp);
        }
        System.out.println(testLabels[Integer.parseInt(closestNeighbour.get(1))]);*/

        return testingLabels[Integer.parseInt(closestNeighbour.get(1))];
    }

    /*
     * Works out the difference of the test data to the training data based on columnSelection
     * Then finds the accuracy
     * @calls GetTrainingData(), GetTestingData(), GetClass()
     * @parameters - String column selection
     * @return - array of total distances per row for that column selection
     */
    public static Double GetAccuracyIndividual(String columnSelection, int[] testingLabels, Double[][] trainingData, Double[][] testingData){
        //Double[][] trainingData = GetTrainingData();
        //Double[][] testingData = GetTestingData();

        //System.out.println(columnSelection);
        String[] columnArray = columnSelection.split(" ");
        ArrayList<Double> rowAccuracy = new ArrayList<>();
        ArrayList<Integer> classes = new ArrayList<>();

        //Array list for closest train row of each test row
        ArrayList<Integer> testRowClass = new ArrayList<>();

        //For every test row
        for(int testDataRow = 0; testDataRow < testingData.length; testDataRow++){
            ArrayList<ArrayList<String>> trainRowDifferences = new ArrayList<>();

            //For every train row
            for(int trainDataRow = 0; trainDataRow < trainingData.length; trainDataRow++){
                double rowDifference = Double.parseDouble("0");

                //For every column
                for(int column = 0; column < 61; column++){
                    if(columnArray[column].equals("1")) {
                        rowDifference += (Math.abs((trainingData[trainDataRow][column]) - (testingData[testDataRow][column]) * Math.abs((trainingData[trainDataRow][column]) - (testingData[testDataRow][column]))));
                    }
                }
                //Find total train row distance
                rowDifference = Math.sqrt(rowDifference);
                ArrayList<String> temp = new ArrayList<>();
                temp.add(String.valueOf(rowDifference));
                temp.add(String.valueOf(trainDataRow));
                //System.out.println(temp);
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
            //System.out.println("Nearest Neighbor: " + nn);
            //Add classification for every row of test data
            testRowClass.add(GetClass(nn, testingLabels));
        }

        //test accuracy
        //int[] desiredLabels = GetTestLabel();
        int count = 0;
        for(int category = 0; category < testingLabels.length; category++){
            if(testingLabels[category] == testRowClass.get(category)){
                count++;
            }
        }
        return ((double)count)/200*100;
    }

    /*
     * Creates new random 100 population
     * @calls - testFunction, picker, mutation
     * @return - accuracy
     */
    public static void GAColumnComparison(){
        //New Population
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

        int goalAccuracy = 90;
        Double accuracy = Double.parseDouble("0");
        int repeats = 0;
        while(accuracy < goalAccuracy && repeats < 100){
            ArrayList<Double> accuracies = GetAccuracy(population);
            ArrayList<ArrayList<String>> columnAccuracies = new ArrayList<>();
            for(int index = 0; index < accuracies.size(); index++){
                ArrayList<String> temp = new ArrayList<>();
                temp.add(population.get(index));
                temp.add(String.valueOf(accuracies.get(index)));
                columnAccuracies.add(temp);
            }
            population = Tournament(columnAccuracies);
            population = Evolve(population);

            //5% chance for mutation
            int rand = rnd.nextInt(100);
            if (rand <= 5) {
                population = Mutate(population);
            }
            accuracy = GetBestAccuracy(GetAccuracy(population));
            System.out.println(accuracy);
            repeats++;
        }
    }

    public static ArrayList<Double> GetAccuracy(ArrayList<String> population){
        Double[][] trainingData = GetTrainingData();
        Double[][] testingData = GetTestingData();
        int[] testingLabels = GetTestLabel();

        System.out.println("Get Accuracy method");
        //For every row in population, get accuracy
        ArrayList<Double> selectionAccuracy = new ArrayList<>();
        for(String columnSelection : population){
            selectionAccuracy.add(GetAccuracyIndividual(columnSelection , testingLabels, trainingData, testingData));
            //System.out.println(selectionAccuracy.get(selectionAccuracy.size()-1));
        }
        return selectionAccuracy;
    }

    public static Double GetBestAccuracy(ArrayList<Double> accuracies){
        System.out.println("Get Best Accuracy method");
        Collections.sort(accuracies);
        return accuracies.get(0);
    }
    /*
     * Picks new 100 based on their accuracy
     * @param - HashMap Key: accuracy, Value: column selection
     * @return - String ArrayList containing new parents
     */
    public static ArrayList<String> Tournament(ArrayList<ArrayList<String>> columnAccuracy){
        System.out.println("Tournament method");
        Random rnd1 = new Random();
        ArrayList<String> population = new ArrayList<>();

        for(int x = 0; x <100; x++) {
            ArrayList<ArrayList<String>> temp = new ArrayList<>();
            temp.add(columnAccuracy.get(rnd1.nextInt(columnAccuracy.size())));
            temp.add(columnAccuracy.get(rnd1.nextInt(columnAccuracy.size())));
            temp.add(columnAccuracy.get(rnd1.nextInt(columnAccuracy.size())));
            //System.out.println("Rand 1: " + temp.get(0).get(0));
            //System.out.println("Rand 2: " + temp.get(1).get(0));
            //System.out.println("Rand 3: " + temp.get(2).get(0));

            Double max = Double.parseDouble("0");
            for (ArrayList<String> value : temp) {
                //System.out.println(value.get(0));
                if (Double.parseDouble(value.get(1)) > max) {
                    population.add(value.get(0));
                    max = Double.parseDouble(value.get(1));
                }
            }
        }
        return population;
    }

    /*
     * Mix up 2 rows of data
     * @param - 2D array of parents
     * @return - 2D array of new parents
     */
    public static ArrayList<String> Evolve(ArrayList<String> population){
        Random rnd = new Random();

        ArrayList<String> parents = new ArrayList<String>();

        //make it random
        Collections.shuffle(population);

        //Split population into two
        ArrayList<String> firstHalf = new ArrayList<String>(population.subList(0, (population.size()/2)));
        ArrayList<String> secondHalf = new ArrayList<String>(population.subList(population.size()/2, population.size()));

        for(int index = 0; index < firstHalf.size(); index++){
            //80% for each parent to crossover
            int random = rnd.nextInt(100);
            if(random <= 80) {
                //Each half of first values
                String ffhString = firstHalf.get(index).substring(0, 61);
                String fshString = firstHalf.get(index).substring(61, 122);

                //Each half of second values
                String sfhString = secondHalf.get(index).substring(0, 61);
                String sshString = secondHalf.get(index).substring(61, 122);

                parents.add(sfhString + fshString);
                parents.add(ffhString + sshString);
            }
            else{
                parents.add(firstHalf.get(index));
                parents.add(secondHalf.get(index));
            }
            //System.out.println("First Half: " + parents.get(parents.size()-2     ) + "\nSecond half: " + parents.get(parents.size()-1));
        }
        System.out.println("Evolved");

        return parents;
    }

    /*
     * Mutate 50 rows
     * @param - 2D ArrayList of columns
     * @return - 2D ArrayList of mutated columns
     */
    public static ArrayList<String> Mutate(ArrayList<String> population){
        System.out.println("In mutation method");
        for(int x = 0; x < 5; x++){
            //System.out.println(population.get(x));
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
            //System.out.println(population.get(x));
        }
        return population;
    }
}

