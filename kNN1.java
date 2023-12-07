import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class kNN1{
    public static void main(String[] args) {
        Process();
    }

    public static void Process(){
        Double trd[][] = GetTrainingData();
        Double tsd[][] = GetTestingData();
        int[] trl = GetTrainingLabel();
        int[] tsl = GetTestLabel();
        String values = "";

        for(int x = 0; x < 200; x++){
            int nearestIndex = EuclideanCompare(trd[x], tsd);
            values += " " + trl[nearestIndex];
        }
        WriteClassData(values);
        int[] out = GetOutputLabel();
        System.out.println(CompareLabels(out, tsl));
    }

    /*
     * Gets the training data from train_data.txt
     * Returns a 2-D array with each row representing each line
     * and the columns, each piece of data on that row
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
     * Gets the testing data from test_data.txt
     * Returns a 2-D array with each row representing each line
     * and the columns, each piece of data on that row
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
     * Gets the training data labels from train_label.txt
     * Returns an integer array with each item representing a classifiction
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
            int[] temp = {};
            return temp;
        }
    }

    /*
     * Work out the difference of one test row to every other row in training data
     * Returns array of those distances - one item for each row
     */
    public static int EuclideanCompare(Double[] testingData, Double[][] trainingData){
        Double[] differences = new Double[200];
        for(int row = 0; row < 200; row++){
            differences[row] = Double.parseDouble("0");
            for(int column = 0; column < 61; column++){
                differences[row] = differences[row] + (Math.abs((trainingData[row][column]) - (testingData[column]) * Math.abs((trainingData[row][column]) - (testingData[column]))));
            }
            differences[row] = Math.sqrt(differences[row]);
            //System.out.println(differences[row]);
            //System.out.println("Row difference: " + differences[row]);
        }
        int index = NearNeigh(differences);
        return index;
    }

    /*
     * Works out smallest difference between each of the rows
     * ie. the two closest rows in the test and train data
     * k=1 - only bases kNN off of 1 value
     * Returns the index of that row
     */
    public static int NearNeigh(Double[] euclDist){
        Double nearestNeigh = Double.parseDouble("99999");
        int index = 0;
        int nearestIndex = -1;
        for(Double temp : euclDist){
            if(temp < nearestNeigh){
                nearestNeigh = temp;
                nearestIndex = index;
                index++;
            }
        }
        return nearestIndex;
    }

    /*
     * Returns the class of an item, based on its index
     */
    public int getClass(int index, int[]tdClasses){
        return tdClasses[index];
    }

    /*
     * A method to write a string (made from an array of labels) into output.txt
     */
    public static void WriteClassData(String itemClass){
        try{
            File classData = new File("output1.txt");
                FileWriter fwr = new FileWriter(classData);
                fwr.write(itemClass);
                fwr.close();
        }
        catch(Exception e){
            System.out.println("File already exists");
        }
    }

    /*
     * Method to check alikeness of two labels
     * Returns a double containing % alikeness
     */
    public static Double CompareLabels(int[] out, int[] tsl){
        int y=0;
        for(int x = 0; x < 200; x++){
            if(out[x] == tsl[x]){
                y++;
            }
        }
        System.out.println(y);
        Double temp = (Double.parseDouble(String.valueOf(y)))*(Double.parseDouble("100"))/(Double.parseDouble("200"));
        return temp;
    }
}