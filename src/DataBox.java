import javax.xml.crypto.Data;
import java.util.ArrayList;

public class DataBox {

    private int runID;
    private ArrayList<Double> times;
    private ArrayList<Double> pop_over_time;
    private ArrayList<Double> new_microhab_times;

    public DataBox(ArrayList<Double> times, ArrayList<Double> pop_over_time, ArrayList<Double> new_microhab_times){
        this.runID = -999;
        this.times = times;
        this.pop_over_time = pop_over_time;
        this.new_microhab_times = new_microhab_times;
    }

    public DataBox(int runID, ArrayList<Double> times, ArrayList<Double> pop_over_time, ArrayList<Double> new_microhab_times){
        this.runID = runID;
        this.times = times;
        this.pop_over_time = pop_over_time;
        this.new_microhab_times = new_microhab_times;
    }


    public int getRunID(){return runID;}
    public ArrayList<Double> getTimes(){return times;}
    public ArrayList<Double> getPop_over_time(){return pop_over_time;}
    public ArrayList<Double> getNew_microhab_times(){return new_microhab_times;}

    public static int[] maxIndices(DataBox[] dataBoxes){

        //not all of the arraylists in the DataBox will be the same length, so need to iterate through and find the largest one
        int times_maxSize = 0;
        int pop_over_time_maxSize = 0;
        int new_microhab_times_maxSize = 0;

        for(DataBox db : dataBoxes){
            if(db.getTimes().size() > times_maxSize) times_maxSize = db.getTimes().size();
            if(db.getPop_over_time().size() > pop_over_time_maxSize) pop_over_time_maxSize = db.getPop_over_time().size();
            if(db.getNew_microhab_times().size() > new_microhab_times_maxSize) new_microhab_times_maxSize = db.getNew_microhab_times().size();
        }

        return new int[]{times_maxSize, pop_over_time_maxSize, new_microhab_times_maxSize};
    }

    public static DataBox averageDataBoxes(DataBox[] dataBoxes){
        //arraylists to store the new values
        ArrayList<Double> avgTimes = new ArrayList<>();
        ArrayList<Double> avgPopOverTime = new ArrayList<>();
        ArrayList<Double> avgNewMicrohabTimes = new ArrayList<>();

        //not all of the arraylists in the DataBox will be the same length, so need to iterate through and find the largest one
        int times_maxSize = 0;
        int pop_over_time_maxSize = 0;
        int new_microhab_times_maxSize = 0;

        for(DataBox db : dataBoxes){
            if(db.getTimes().size() > times_maxSize) times_maxSize = db.getTimes().size();
            if(db.getPop_over_time().size() > pop_over_time_maxSize) pop_over_time_maxSize = db.getPop_over_time().size();
            if(db.getNew_microhab_times().size() > new_microhab_times_maxSize) new_microhab_times_maxSize = db.getNew_microhab_times().size();
        }

        //average the times
        //This should be the same size as the pop over time arraylist, but just to be on the safe side
        for(int i = 0; i < times_maxSize; i++){
            double avgTime = 0.;
            int nVals = 0;

            for(DataBox db : dataBoxes){
                if(i < db.getTimes().size()){
                    avgTime += db.getTimes().get(i);
                    nVals++;
                }
            }
            int denominator = Math.max(1, nVals);
            avgTimes.add(avgTime/(double)denominator);
        }

        //average the population over time
        for(int i = 0; i < pop_over_time_maxSize; i++){
            double avgPopSize = 0.;
            int nVals = 0;

            for(DataBox db : dataBoxes){
                if(i < db.getPop_over_time().size()){
                    avgPopSize += db.getPop_over_time().get(i);
                    nVals++;
                }
            }
            int denominator = Math.max(1, nVals);
            avgPopOverTime.add(avgPopSize/denominator);
        }

        //average the times at which the new microhabitats are added
        for(int i = 0; i < new_microhab_times_maxSize; i++){
            double avgNewMicrohabTime = 0;
            int nVals = 0;

            for(DataBox db : dataBoxes){
                if(i < db.getNew_microhab_times().size()){
                    avgNewMicrohabTime += db.getNew_microhab_times().get(i);
                    nVals++;
                }
            }
            int denominator = Math.max(1, nVals);
            avgNewMicrohabTimes.add(avgNewMicrohabTime/denominator);
        }

        return new DataBox(avgTimes, avgPopOverTime, avgNewMicrohabTimes);
    }
}
