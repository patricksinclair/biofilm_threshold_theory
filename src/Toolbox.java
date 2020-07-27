import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Toolbox {

    static void writePopOverTimeToFile(String directoryName, String filename, DataBox dataBox){

        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(directoryName+"/"+filename+".txt");

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

        File file = new File(directoryName+"/"+filename+".txt");

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
