import jdk.jfr.Category;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;
import java.io.File;
import java.io.FileWriter;

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
            return new int[]{};
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
            return new int[]{};
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
            return new int[]{};
        }
    }

    /*
     * find the class of item based on kNN (k=1)
     * Parameters - NN Value and index
     * Return 0/1 based on closest value
     */
    public static int GetClass(ArrayList<String> closestNeighbour, int[] testingLabels){
        return testingLabels[Integer.parseInt(closestNeighbour.get(1))];
    }

    /*
     * Creates new random 100 population
     * @calls - testFunction, picker, mutation
     * @return - accuracy
     * Notes - make accuracies only compare changed columnSelections
     *       - make Nearest neighbour k=5 method
     */
    public static void GAColumnComparison(){
        //New Population
        ArrayList<String> population = new ArrayList<String>();

        Random rnd = new Random();

        //Initial population of array
        //population.add("0 0 0 0 0 1 1 1 0 0 0 0 0 0 1 0 1 0 0 1 0 0 1 1 1 1 0 1 1 0 1 1 0 1 0 0 0 1 1 0 1 0 1 1 1 1 0 0 0 0 0 0 1 1 1 1 0 0 1 0 1 ");
        for(int x = 0; x < 100; x++){
            String stringValue = "";

            for(int y=0; y<61; y++){
                int value = Math.abs(rnd.nextInt() % 2);
                stringValue += String.valueOf(value) + " ";
            }
            population.add(stringValue);
        }

        int goalAccuracy = 95;
        Double accuracy = Double.parseDouble("0");
        int repeats = 0;
        Double[][] trainingData = GetTrainingData();
        Double[][] testingData = GetTestingData();
        int[] testingLabels = GetTestLabel();
        int[] trainingLabels = GetTrainingLabel();

        //System.out.println(GetAccuracyIndividual(population.get(0), trainingLabels, testingLabels, trainingData, testingData));

        //Initial accuracy
        ArrayList<Double> accuracies = GetAccuracy(population, trainingData, testingData, testingLabels, trainingLabels);

        while(accuracy < goalAccuracy && repeats < 100){
            ArrayList<ArrayList<String>> columnAccuracies = new ArrayList<>();
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

            System.out.println(accuracies);
            //System.out.println("Population 0: " + population.get(0));
            printBest(population, accuracy, accuracies);
            System.out.println("Accuracy: " + accuracy);
            repeats++;
        }
    }

    public static void printBest(ArrayList<String> population, Double accuracy, ArrayList<Double> accuracies){
        int index = accuracies.indexOf(accuracy);
        System.out.println(population.get(index));
    }
    public static ArrayList<Double> GetAccuracy(ArrayList<String> population, Double[][] trainingData, Double[][]testingData, int[]testingLabels, int[] trainingLabels){

        //measuring time
        Double timeStart = Double.parseDouble(String.valueOf(System.nanoTime()));

        //For every row in population, get accuracy
        ArrayList<Double> selectionAccuracy = new ArrayList<>();
        for(String columnSelection : population){
            //System.out.println("Pop: " + population.size());
            selectionAccuracy.add(GetAccuracyIndividual(columnSelection , trainingLabels, testingLabels, trainingData, testingData));
        }

        //time
        Double timeFinish = Double.parseDouble(String.valueOf(System.nanoTime()));
        System.out.println("Time taken: " + (timeFinish-timeStart)/1000000000);

        return selectionAccuracy;
    }

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
     * Works out the difference of the test data to the training data based on columnSelection
     * Then finds the accuracy
     * @calls GetClass()
     * @parameters - String column selection, testLabels, TrainingData, TestingData
     * @return - accuracy of that column
     */
    public static Double GetAccuracyIndividual(String columnSelection, int[] trainingLabels, int[] testingLabels, Double[][] trainingData, Double[][] testingData){
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
                        rowDifference += Math.abs((trainingData[trainDataRow][column]) - (testingData[testDataRow][column]));
                    }
                }
                //Find total train row distance
                //rowDifference = Math.sqrt(rowDifference);
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
        //Collections.shuffle(firstHalf);
        //Collections.shuffle(secondHalf);

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

    public static ArrayList<String> Mutate(ArrayList<String> population){
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
            }
            else{
                concatenation = row;
            }
            newPopulation.add(concatenation);
        }
        return newPopulation;
    }
}