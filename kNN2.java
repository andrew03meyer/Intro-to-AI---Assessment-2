import jdk.jfr.Category;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;
import java.io.File;
import java.io.FileWriter;

public class kNN2 {
    public static void main(String[] args) {
        GAColumnComparison();
    }

    public static Double[][] GetTrainingData() {
        Double[][] trainingData = new Double[200][61];

        try {
            File trainingFile = new File("train_data.txt");
            Scanner tfScanner = new Scanner(trainingFile);

            int rowIndex = 0;
            int columnIndex;

            while (tfScanner.hasNextLine()) {
                columnIndex = 0;
                //Split line by space and insert into the 2D array
                String[] temp = tfScanner.nextLine().split(" ");
                for (String item : temp) {
                    trainingData[rowIndex][columnIndex] = Double.parseDouble(item);
                    columnIndex++;
                }
                rowIndex++;
            }
            tfScanner.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return trainingData;
    }

    public static Double[][] GetTestingData() {
        Double[][] testingData = new Double[200][61];

        try {
            File testingFile = new File("test_data.txt");
            Scanner tsfScanner = new Scanner(testingFile);

            int rowIndex = 0;
            int columnIndex;

            while (tsfScanner.hasNextLine()) {
                columnIndex = 0;
                //Split line by space and insert into the 2D array
                String[] temp = tsfScanner.nextLine().split(" ");
                for (String item : temp) {
                    testingData[rowIndex][columnIndex] = Double.parseDouble(item);
                    columnIndex++;
                }
                rowIndex++;
            }
            tsfScanner.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return testingData;
    }

    public static int[] GetTrainingLabel() {
        try {
            File trainingLabelFile = new File("train_label.txt");
            Scanner tlScanner = new Scanner(trainingLabelFile);

            String[] temp = tlScanner.nextLine().stripLeading().split(" ");
            int index = 0;
            int[] trainingLabels = new int[200];

            for (String items : temp) {
                trainingLabels[index] = Integer.parseInt(items);
                index++;
            }
            tlScanner.close();
            return trainingLabels;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return new int[]{};
        }
    }

    public static int[] GetTestLabel() {
        try {
            File testLabelFile = new File("test_label.txt");
            Scanner tslScanner = new Scanner(testLabelFile);

            String[] temp = tslScanner.nextLine().stripLeading().split(" ");
            int index = 0;
            int[] testLabels = new int[200];

            for (String items : temp) {
                testLabels[index] = Integer.parseInt(items);
                index++;
            }
            tslScanner.close();
            return testLabels;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return new int[]{};
        }
    }

    public static void WriteClassData(String itemClass) {
        try {
            File classData = new File("output2.txt");
            FileWriter fwr = new FileWriter(classData);
            fwr.write(itemClass);
            fwr.close();
        } catch (Exception e) {
            System.out.println("File already exists");
        }
    }

    public static int[] GetOutputLabel() {
        try {
            File outLabelFile = new File("output2.txt");
            Scanner outScanner = new Scanner(outLabelFile);

            String[] temp = outScanner.nextLine().stripLeading().split(" ");
            int index = 0;
            int[] outLabels = new int[200];

            for (String items : temp) {
                outLabels[index] = Integer.parseInt(items);
                index++;
            }
            outScanner.close();
            return outLabels;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return new int[]{};
        }
    }



    /*
     * Creates new random 100 population
     * @calls - testFunction, picker, mutation
     * @return - accuracy
     * Notes - make accuracies only compare changed columnSelections
     *       - make Nearest neighbour k=5 method
     */
    public static void GAColumnComparison() {
        //New Population
        ArrayList<String> population = new ArrayList<String>();

        //testing
        population.add("0 0 0 0 0 1 1 1 0 0 0 0 0 0 1 0 1 0 0 1 0 0 1 1 1 1 0 1 1 0 1 1 0 1 0 0 0 1 1 0 1 0 1 1 1 1 0 0 0 0 0 0 1 1 1 1 0 0 1 0 1 ");

        Random rnd = new Random();

        //Initial population of array
        for (int x = 0; x < 99; x++) {
            String stringValue = "";

            for (int y = 0; y < 61; y++) {
                int value = Math.abs(rnd.nextInt() % 2);
                stringValue += String.valueOf(value) + " ";
            }
            population.add(stringValue);
        }

        int goalAccuracy = 90;
        Double accuracy = Double.parseDouble("0");
        int repeats = 0;
        Double[][] trainingData = GetTrainingData();
        Double[][] testingData = GetTestingData();
        int[] testingLabels = GetTestLabel();
        int[] trainingLabels = GetTrainingLabel();

        //ArrayList<ArrayList<String>> distance = EuclideanDistance(val, trainingData, testingData);
        //ArrayList<ArrayList<String>> closest = GetFiveNN(distance);

        //Initial accuracy
        ArrayList<Double> accuracies = GetAccuracy(population, trainingData, testingData, testingLabels, trainingLabels);

        while (accuracy < goalAccuracy && repeats < 100) {
            ArrayList<ArrayList<String>> columnAccuracies = new ArrayList<>();
            for (int index = 0; index < accuracies.size(); index++) {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(population.get(index));
                temp.add(String.valueOf(accuracies.get(index)));
                columnAccuracies.add(temp);
            }
            population = Tournament(columnAccuracies);
            population = Evolve(population);

            //5% chance for mutation
            int rand = rnd.nextInt(100);
            if (rand <= 20) {
                population = NewMutate(population);
            }

            accuracies = GetAccuracy(population, trainingData, testingData, testingLabels, trainingLabels);
            accuracy = GetBestAccuracy(accuracies);

            //GetBestAccuracies() Shuffles in reverse order
            //Collections.shuffle(population);
            PrintRepeats(population);
            System.out.println("Population 0: " + population.get(0));
            System.out.println("Accuracy: " + accuracy);
            repeats++;
        }
    }

    public static ArrayList<Double> GetAccuracy(ArrayList<String> population, Double[][] trainingData, Double[][] testingData, int[] testingLabels, int[] trainingLabels) {
        ArrayList<Double> populationAccuracy = new ArrayList<>();
        //For every population
        for (String columnSelection : population) {
            ArrayList<Integer> rowClassification = new ArrayList<>();
            //For every test row
            for (Double[] testingRow : testingData) {
                //arraylist of differences for that row
                ArrayList<String> testRowDistances = EuclideanDistance(columnSelection, trainingData, testingRow);
                ArrayList<ArrayList<String>> nearestFive = GetFiveNN(testRowDistances);
                System.out.println(nearestFive.size());
                rowClassification.add(GetClass(nearestFive, trainingLabels));
            }
            populationAccuracy.add(GetAccuracyColumn(rowClassification, testingLabels));
            System.out.println(populationAccuracy.get(0));
        }

        return populationAccuracy;
    }



    /*
     * Works out the difference of one row of test data to the training data based on columnSelection
     * Then finds the accuracy
     * @calls GetClass()
     * @parameters - String column selection, testLabels, TrainingData, TestingData
     * @return - accuracy of that column
     */
    public static ArrayList<String> EuclideanDistance(String columnSelection, Double[][] trainingData, Double[]testingData) {
        //System.out.println(columnSelection);
        String[] columnArray = columnSelection.split(" ");

        //Array of distances for that test row
        ArrayList<String>testRowDistances = new ArrayList<>();

        //For every train row
        for (int trainDataRow = 0; trainDataRow < trainingData.length; trainDataRow++) {
            double rowDifference = Double.parseDouble("0");

            //For every column
            for (int column = 0; column < 61; column++) {
                if (columnArray[column].equals("1")) {
                    rowDifference += Math.abs((trainingData[trainDataRow][column]) - (testingData[column]));
                }
            }

            //Find total train row distance
            //rowDifference = Math.sqrt(rowDifference);
            testRowDistances.add(String.valueOf(rowDifference));
        }

        return testRowDistances;
    }


    //arraylist of differences for that test row
    public static ArrayList<ArrayList<String>> GetFiveNN(ArrayList<String> trainRowDistances){
        ArrayList<ArrayList<String>> top5 = new ArrayList<>();
        ArrayList<String> highest = new ArrayList<>();
        highest.add("temp");
        highest.add("-1");
        int index=0;

        for(String trainRow : trainRowDistances){
            if(top5.size() > 4) {
                //If the new value is less than highest value
                System.out.println("hi");
                if (Double.parseDouble(trainRow) < Double.parseDouble(highest.get(1))){
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(trainRow);
                    temp.add(String.valueOf(trainRowDistances.indexOf(trainRow)));
                    //Replace current highest with new value
                    top5.add(Integer.parseInt(highest.get(1)), temp);

                    //find new highest
                    for (ArrayList<String> value : top5) {
                        if(Double.parseDouble(value.get(0)) > Double.parseDouble(highest.get(0))){
                            highest = temp;
                            //highest.add(String.valueOf(index));
                        }
                    }
                }
            }
            else{
                ArrayList<String> temp = new ArrayList<>();
                temp.add(trainRow);
                temp.add(String.valueOf(trainRowDistances.indexOf(trainRow)));
                top5.add(temp);
                //if the new value is the highest, set it
                if (Double.parseDouble(trainRow) > Double.parseDouble(highest.get(1))){
                    highest = temp;
                }
            }
            index++;
        }

        return top5;
    }

    /*
     * find the class of item based on kNN (k=5)
     * Parameters - NN Values and testlabels
     * Return 0/1 based on closest value
     */
    public static int GetClass(ArrayList<ArrayList<String>> NearestNeighbour, int[] testLabels){
        int non=0;
        int alc=0;

        for(ArrayList<String> temp : NearestNeighbour){
            if(testLabels[Integer.parseInt(temp.get(1))] == 0){
                non++;
            }
            else{
                alc++;
            }
        }
        return Math.max(non, alc);
    }

    public static Double GetAccuracyColumn(ArrayList<Integer> classifications, int[] testingLabels){
        int count = 0;
        for(int labelIndex = 0; labelIndex < testingLabels.length; labelIndex++){
            if(classifications.get(labelIndex) == testingLabels[labelIndex]){
                count++;
            }
        }

        //Equivalent of accuracy/200*100
        return Double.parseDouble(String.valueOf(count)) / 2;
    }

    public static void PrintRepeats(ArrayList<String> population) {
        HashMap<String, Integer> hash1 = new HashMap<>();
        for (String temp : population) {
            if (hash1.containsKey(temp)) {
                hash1.put(temp, hash1.get(temp) + 1);
            } else {
                hash1.put(temp, 1);
            }
        }
        System.out.println(hash1.values());
    }
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
     * Picks new 100 based on their accuracy
     * @param - HashMap Key: accuracy, Value: column selection
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
     * Mix up 2 rows of data
     * @param - 2D array of parents
     * @return - 2D array of new parents
     */
    public static ArrayList<String> Evolve(ArrayList<String> population){
        Random rnd = new Random();

        ArrayList<String> parents = new ArrayList<String>();

        //Split population into two
        ArrayList<String> firstHalf = new ArrayList<String>(population.subList(0, (population.size()/2)));
        ArrayList<String> secondHalf = new ArrayList<String>(population.subList(population.size()/2, population.size()));

        for(int index = 0; index < firstHalf.size(); index++){
            //System.out.println("Full row: " + firstHalf.get(index));
            //System.out.println("Full row: " + secondHalf.get(index));
            //80% for each parent to crossover
            int random = rnd.nextInt(100);
            if(random <= 80) {

                random = rnd.nextInt(61)*2;
                //Each half of first values
                String ffhString = firstHalf.get(index).substring(0, random);
                String fshString = firstHalf.get(index).substring(random, 122);
                //System.out.println("First pop:  " + ffhString + "|" + fshString);

                //Each half of second values
                String sfhString = secondHalf.get(index).substring(0, random);
                String sshString = secondHalf.get(index).substring(random, 122);
                //System.out.println("Second pop: " + sfhString + "|" + sshString);

                parents.add(sfhString + fshString);
                parents.add(ffhString + sshString);
                //System.out.println("First pop:  " + sfhString+fshString + "\nSecond pop: " + ffhString+sshString);
            }
            else{
                parents.add(firstHalf.get(index));
                parents.add(secondHalf.get(index));
            }
        }

        return parents;
    }

    /*
     * Mutate 50 rows
     * @param - 2D ArrayList of columns
     * @return - 2D ArrayList of mutated columns
     */
    public static ArrayList<String> Mutate(ArrayList<String> population){
        for(int x = 0; x < 5; x++){
            Random rnd = new Random();
            int random = rnd.nextInt(population.size());
            String selection = population.remove(random).replaceAll("\\s+", "");
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
            population.add(random, newSelection);
        }
        return population;
    }

    public static ArrayList<String> NewMutate(ArrayList<String> population){
        //For every row
        ArrayList<String> newPopulation = new ArrayList<>();
        for(String row : population) {
            //population.remove(row);
            String concatenation = "";

            //Chance to mutate
            Random rnd = new Random();
            int random = rnd.nextInt(100);
            if(random <= 5) {
                //System.out.println("Row before: " + row);
                char[] charArray = row.toCharArray();
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
                //System.out.println("Row after: " + concatenation);
            }
            else{
                concatenation = row;
            }
            newPopulation.add(concatenation);
        }
        return newPopulation;
    }
}