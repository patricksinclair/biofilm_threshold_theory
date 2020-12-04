import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Toolbox {

    static void writeNewMicrohabTimesToFile_v2(String directoryName, String filename, int N, DataBox[] dataBoxes){
        //this method is used for the stochastic lag time simulations
        //these simulations are run for a long time to ensure that all the runs reach N microhabs so, each entry should be size N
        //in the off chance they aren't, we can do some handling there
        //output of this will be a csv file, each row is the times new microhabitats were added in each run
        //first column is the run ID, subsequent columns are the times taken to reach microhabitat n

        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(directoryName+"/"+filename+".csv");

        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int[] max_indices = DataBox.maxIndices(dataBoxes);
            int mh_times_max = max_indices[2];

            //write the headers to the file
            bw.write("runID");
            //time taken to reach microhabitat n
            for(int n = 0; n < N; n++){
                bw.write(", t_mh_"+n);
            }


            //write the data in rows
            //iterate over all the runs
            int nRuns = dataBoxes.length;
            for(int r = 0; r < nRuns; r++){

                bw.newLine();
                String data_string = ""+dataBoxes[r].getRunID();

                //iterate over the times in each databox
                for(int i = 0; i < N; i++){
                    //this avoids arrayoutofbounds exceptions
                    if (i < dataBoxes[r].getNew_microhab_times().size()) data_string += String.format(",%.3f", dataBoxes[r].getNew_microhab_times().get(i));
                    else data_string += ",";
                }

                bw.write(data_string);
            }

            bw.close();

        }catch (IOException e){}

    }

    static void writePopOverTimeToFile(String directoryName, String filename, DataBox dataBox){

        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(directoryName+"/"+filename+".csv");

        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            ArrayList<Double> times = dataBox.getTimes();
            ArrayList<Double> popOverTime = dataBox.getPop_over_time();


            int string_length = 12;

            bw.write("t, N");
            bw.newLine();

            for(int t = 0; t < popOverTime.size(); t++){

                bw.write(times.get(t) + ", " + popOverTime.get(t));
                bw.newLine();

            }

            bw.close();

        }catch (IOException e){}
    }


    static void writeNewMicrohabTimesToFile(String directoryName, String filename, DataBox dataBox){

        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(directoryName+"/"+filename+".csv");

        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            ArrayList<Double> newMicrohabTimes = dataBox.getNew_microhab_times();


            bw.write("index, microhab_time");
            bw.newLine();

            for(int t = 0; t < newMicrohabTimes.size(); t++){

                bw.write(t + ", " + newMicrohabTimes.get(t));
                bw.newLine();

            }

            bw.close();

        }catch (IOException e){}
    }

    static void writePopOverTimeToFile(String directoryName, String filename, DataBox[] dataBoxes){

        DataBox averagedDB = DataBox.averageDataBoxes(dataBoxes);

        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(directoryName+"/"+filename+".csv");

        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int[] max_indices = DataBox.maxIndices(dataBoxes);
            int pop_max = max_indices[1];

            //write the headers to the file
            bw.write("t");
            for(DataBox db : dataBoxes){
                bw.write(","+db.getRunID());
            }


            //write the data in columns
            for(int t = 0; t < pop_max; t++){

                bw.newLine();
                bw.write(String.format("%.2f", averagedDB.getTimes().get(t)));

                for(DataBox db : dataBoxes){
//                    System.out.println(db.getRunID());
//                    System.out.println(db.getTimes());
//                    System.out.println(db.getPop_over_time());
//                    System.out.println();

                    if(t < db.getPop_over_time().size()) bw.write(","+db.getPop_over_time().get(t));
                    else bw.write(",");
                }
            }

            bw.close();

        }catch (IOException e){}
    }


    static void writeNewMicrohabTimesToFile(String directoryName, String filename, DataBox[] dataBoxes){

        DataBox averagedDB = DataBox.averageDataBoxes(dataBoxes);

        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(directoryName+"/"+filename+".csv");

        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int[] max_indices = DataBox.maxIndices(dataBoxes);
            int mh_times_max = max_indices[2];

            //write the headers to the file
            bw.write("t");
            for(DataBox db : dataBoxes){
                bw.write(","+db.getRunID());
            }


            //write the data in columns
            for(int t = 0; t < mh_times_max; t++){

                bw.newLine();
                bw.write(String.valueOf(averagedDB.getTimes().get(t)));

                for(DataBox db : dataBoxes){
                    if(t < db.getNew_microhab_times().size()) bw.write(","+db.getNew_microhab_times().get(t));
                    else bw.write(",");
                }
            }

            bw.close();

        }catch (IOException e){}
    }


    static void writeTimeToNthMicrohabDataToFile(String directoryName, String fileName, String[] headers, DataBox[]dataBoxes){
        //writes all the runs of a parameter pair to a csv file.  These csv files can be combined later.

        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(directoryName+"/"+fileName+".csv");

        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            //write the headers to the file
            //removed the whitespace stuff so it's simpler to read into
            //a pandas dataframe
            String file_header = "";
            for(int i = 0; i < headers.length-1; i++){
                file_header += headers[i]+",";
            }
            file_header += headers[headers.length-1];
            bw.write(file_header);


            //now write all the data from the seperate runs to the file
            //only 3 entries so can just do it manually
            for(DataBox db : dataBoxes){
                bw.newLine();
                String output = String.format("%.3f,%.3f,%.3f,%.3f", db.getBiofilm_threshold(), db.getR_det_ratio(), db.getTime_n1(), db.getTime_elapsed());
                bw.write(output);
            }


            bw.close();
        }catch (IOException e){}

    }



    public static void writeGenosOverTimeToCSV(String directoryName, DataBox dataBox){
        //new method of saving the data for the big geno distb runs.
        //instead of putting everything in one big file, each run will have a dedicated sub-directory
        //In each of these sub-directories there will be several csv files which contain the genos in each microhabitat
        //at a specific timestep.
        //For simplicity, we'll write these csv files such that each row represents a microhabitat,
        //then we can transpose the dataframes in python later


        //All the data in a run is stored in a databox.
        //Here, for each databox passed to this method, we iterate through the times list and the 3D arraylist containing all the geno data
        //use outer index of arraylist to iterate through geno(t) and the times list
        //use times list for filenames

        try{

            //each run has its own directory so we add the run ID here
            //subDirectoryName += dataBox.getRunID();
            directoryName += "/geno_distbs/runID_"+dataBox.getRunID();
            File directory = new File(directoryName);
            if(!directory.exists()) directory.mkdirs();

            //iterate over all the timesteps stored
            for(int t = 0; t < dataBox.getAll_microhab_pops().size(); t++) {

                //create a new file for each timestep
                //
                String file_name = "geno_distb"+String.format("-t=%.2f", dataBox.getTimes().get(t));
                File file = new File(directoryName+"/"+file_name+".csv");
                //if(!file.exists()) file.createNewFile();

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                //iterate over all the microhabitats, new line for each one
                for(int mh = 0; mh < dataBox.getAll_microhab_pops().get(t).size(); mh++) {
                    String geno_distb = "mh_"+mh;

                    //now iterate over all the genos in each microhab
                    for(int g = 0; g < dataBox.getAll_microhab_pops().get(t).get(mh).size(); g++) {
                        geno_distb += String.format(",%.8f", dataBox.getAll_microhab_pops().get(t).get(mh).get(g));
                    }
                    bw.write(geno_distb);
                    bw.newLine();

                }
                bw.close();
            }

        }catch (IOException e){}


    }







    public static String millisToShortDHMS(long duration) {
        String res = "";
        long days  = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        if (days == 0) {
            res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        else {
            res = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return res;
    }


}
