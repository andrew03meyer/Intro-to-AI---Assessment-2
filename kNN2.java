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

        Random rnd = new Random();

        //Initial population of array
        for (int x = 0; x < 100; x++) {
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

        //Initial accuracy
        ArrayList<Double> accuracies = GetAccuracy(population, trainingData, testingData, testingLabels);

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

            accuracies = GetAccuracy(population, trainingData, testingData, testingLabels);
            accuracy = GetBestAccuracy(accuracies);

            //GetBestAccuracies() Shuffles in reverse order
            //Collections.shuffle(population);
            PrintRepeats(population);
            System.out.println("Population 0: " + population.get(0));
            System.out.println("Accuracy: " + accuracy);
            repeats++;
        }
    }

    public static ArrayList<Double> GetAccuracy(ArrayList<String> population, Double[][] trainingData, Double[][] testingData, int[] testingLabels) {

        //For every row in population, get accuracy
        ArrayList<Double> selectionAccuracy = new ArrayList<>();
        for (String columnSelection : population) {
            ArrayList<ArrayList<String>> testRowDistances = EuclideanDistance(columnSelection, trainingData, testingData);
            ArrayList<ArrayList<String>> closestNeighbour = GetFiveNN(testRowDistances);
        }

        return selectionAccuracy;
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

    /*
     * Works out the difference of the test data to the training data based on columnSelection
     * Then finds the accuracy
     * @calls GetClass()
     * @parameters - String column selection, testLabels, TrainingData, TestingData
     * @return - accuracy of that column
     */
    public static ArrayList<ArrayList<String>> EuclideanDistance(String columnSelection, Double[][] trainingData, Double[][] testingData) {

        String[] columnArray = columnSelection.split(" ");

        //For every test row, an array of distances
        ArrayList<ArrayList<String>>testRowDistances = new ArrayList<>();

        //For every test row
        for (int testDataRow = 0; testDataRow < testingData.length; testDataRow++) {
            ArrayList<String> trainRowDifferences = new ArrayList<>();

            //For every train row
            for (int trainDataRow = 0; trainDataRow < trainingData.length; trainDataRow++) {
                double rowDifference = Double.parseDouble("0");

                //For every column
                for (int column = 0; column < 61; column++) {
                    if (columnArray[column].equals("1")) {
                        rowDifference += Math.abs((trainingData[trainDataRow][column]) - (testingData[testDataRow][column]));
                    }
                }

                //Find total train row distance
                rowDifference = Math.sqrt(rowDifference);
                trainRowDifferences.add(String.valueOf(testDataRow));
                trainRowDifferences.add(String.valueOf(rowDifference));
            }
            testRowDistances.add(trainRowDifferences);
        }
        return testRowDistances;
    }


    public static ArrayList<ArrayList<String>> GetFiveNN(ArrayList<ArrayList<String>> testRowDistances){
        ArrayList<ArrayList<String>> top5 = new ArrayList<>();
        //top5.add(testRowDistances.get(0));
        ArrayList<String> highest = new ArrayList<>();
        highest.add("temp");
        highest.add("-1");
        int index=0;

        for(ArrayList<String> testRow : testRowDistances){
            if(top5.size() == 5) {
                //If the new value is less than highest value
                if (Double.parseDouble(testRow.get(1)) < Double.parseDouble(highest.get(1))){
                    top5.add(top5.indexOf(highest), testRow);

                    //find new highest
                    for (ArrayList<String> temp : top5) {
                        if(Double.parseDouble(temp.get(1)) > Double.parseDouble(highest.get(1))){
                            highest = temp;
                            highest.add(String.valueOf(index));
                        }
                    }
                }
            }
            else{
                top5.add(testRow);
                //if the new value is the highest, set it
                if(Double.parseDouble(testRow.get(1)) > Double.parseDouble(highest.get(1))){
                    highest = testRow;
                    highest.add(String.valueOf(index));
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
            if(testLabels[Integer.parseInt(temp.get(2))] == 0){
                non++;
            }
            else{
                alc++;
            }
        }
        return Math.max(non, alc);
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