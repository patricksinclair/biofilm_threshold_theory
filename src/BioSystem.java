import org.apache.commons.math3.distribution.PoissonDistribution;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

class BioSystem {

    private Random rand = new Random();
    //need to initialise these in the performAction method in order to incorporate the tau halving
    private PoissonDistribution poiss_immigration;
    private PoissonDistribution poiss_deterioration;
    private PoissonDistribution poiss_migration;
    private PoissonDistribution poiss_migration_edge;

    private double alpha, c_max; //steepness and max val of antimicrobial concn
    private double scale, sigma; //mic distb shape parameters
    private ArrayList<Microhabitat> microhabitats;

    //exit time is the time it took for the biofilm to reach the thickness limit, if it did
    //failure time is the time it took for the system to "fail", in this case form a single biofilm section
    private double time_elapsed, exit_time, failure_time;

    private ArrayList<Double> newMicrohabTimes; //This will be used to store the times at which a new Microhabitat is added to the system

    private int immigration_index;

    private int K;
    private double max_gRate = 0.083;

    //the values of the below rates are set depending on the ratios used in the simulations
    private double biofilm_threshold;
    private double r_migration;
    private double r_immigration;
    private double r_deterioration;

    //    private double deterioration_rate = 0.0168;
//    private double biofilm_threshold = 0.75;
//    private double immigration_rate = 0.8;
//    private double migration_rate = 0.2;
    private double tau = 0.1; //much larger value now that the bug is fixed
    private double delta_x = 1.; //thickness of a microhabitat in microns

    //this is how big the system can get before we exit. should reduce overall simulation duration
    private int thickness_limit = 8;
    //this is how thick the biofilm can get before the system is deemed to have "failed"
    private int failure_limit = 1;
    private int n_detachments = 0, n_deaths = 0, n_replications = 0, n_immigrations = 0, n_migrations = 0, n_tauHalves;


    private BioSystem(int K, double threshold_N_ratio, double immigration_ratio, double migration_ratio, double deterioration_ratio){

        this.K = K;

        this.biofilm_threshold = threshold_N_ratio; //the code uses a ratio for biofilm threshold already, so no need to multiply by K
        this.r_immigration = immigration_ratio*(K*max_gRate);
        this.r_migration = migration_ratio*max_gRate;
        this.r_deterioration = deterioration_ratio*max_gRate;

        this.microhabitats = new ArrayList<>();
        this.newMicrohabTimes = new ArrayList<>();

        this.time_elapsed = 0.;
        this.exit_time = 0.;
        this.failure_time = 0.;
        this.immigration_index = 0;

        microhabitats.add(new Microhabitat(K, max_gRate, biofilm_threshold, r_migration));
        microhabitats.get(0).setSurface();
        microhabitats.get(0).addARandomBacterium_x_N(1);
    }


    private BioSystem(int K, int thickness_limit, double threshold_N_ratio, double immigration_ratio, double migration_ratio, double deterioration_ratio){
        //this constructor is used for the stochastic lag time sims, allows us to explicity set the thickness limit
        this.K = K;
        this.thickness_limit = thickness_limit;
        this.biofilm_threshold = threshold_N_ratio; //the code uses a ratio for biofilm threshold already, so no need to multiply by K
        this.r_immigration = immigration_ratio*(K*max_gRate);
        this.r_migration = migration_ratio*max_gRate;
        this.r_deterioration = deterioration_ratio*max_gRate;

        this.microhabitats = new ArrayList<>();
        this.newMicrohabTimes = new ArrayList<>();

        this.time_elapsed = 0.;
        this.exit_time = 0.;
        this.failure_time = 0.;
        this.immigration_index = 0;

        microhabitats.add(new Microhabitat(K, max_gRate, biofilm_threshold, r_migration));
        microhabitats.get(0).setSurface();
        microhabitats.get(0).addARandomBacterium_x_N(1);
    }


    private double getDeterioration_rate(){ return r_deterioration; }
    private double getBiofilm_threshold(){ return biofilm_threshold; }
    private int getN_detachments(){ return n_detachments; }
    private int getN_deaths(){ return n_deaths; }
    private int getN_replications(){ return n_replications; }
    private int getN_immigrations(){ return n_immigrations; }
    private int getN_migrations(){ return n_migrations; }
    private double getTimeElapsed(){return time_elapsed;}
    private double getExit_time(){return exit_time;}
    private int getSystemSize(){return microhabitats.size();}
    private double getFailure_time(){return failure_time;}




    private int getTotalN(){
        int runningTotal = 0;
        for(Microhabitat m : microhabitats) {
            runningTotal += m.getN();
        }
        return runningTotal;
    }

    private int getBiofilmEdge(){
        int edgeIndex = 0;
        for(int i = 0; i < microhabitats.size(); i++){
            if(microhabitats.get(i).isBiofilm_region()) edgeIndex = i;
        }
        return edgeIndex;
    }

    private int getBiofilmThickness(){
        int thickness = 0;
        for(int i = 0; i < microhabitats.size(); i++){
            if(microhabitats.get(i).isBiofilm_region()) thickness = i+1;
        }
        return thickness;
    }

    private ArrayList<ArrayList<Double>> getMicrohabPopulations(){

        ArrayList<ArrayList<Double>> mh_pops = new ArrayList<>();

        for(Microhabitat m : microhabitats) {
            ArrayList<Double> mh_pop = new ArrayList<>();
            for(Double geno : m.getPopulation()) {
                mh_pop.add(geno);
            }
            mh_pops.add(mh_pop);
        }

        return mh_pops;
    }



    private void immigrate(int mh_index, int n_immigrants){
        microhabitats.get(mh_index).addARandomBacterium_x_N(n_immigrants);
    }




    private static double calc_C_i(int i, double c_max, double alpha, double delta_x){
        return c_max*Math.exp(-alpha*i*delta_x);
    }


    public void migrate(int mh_index, int bac_index){

        double migrating_bac = microhabitats.get(mh_index).getPopulation().get(bac_index);
        microhabitats.get(mh_index).removeABacterium(bac_index);

        if(microhabitats.get(mh_index).isSurface()){
            microhabitats.get(mh_index+1).addABacterium(migrating_bac);
        }else if(microhabitats.get(mh_index).isImmigration_zone()){
            microhabitats.get(mh_index-1).addABacterium(migrating_bac);
        }else{
            if(rand.nextBoolean()){
                microhabitats.get(mh_index+1).addABacterium(migrating_bac);
            }else{
                microhabitats.get(mh_index-1).addABacterium(migrating_bac);
            }
        }
    }


    private void updateBiofilmSize(){
        //once the edge microhabitat is sufficiently populated, this adds another microhabitat onto the system list
        //which is then used as the immigration zone
        if(microhabitats.get(immigration_index).atBiofilmThreshold()){

            microhabitats.get(immigration_index).setBiofilm_region();
            microhabitats.get(immigration_index).setImmigration_zone(false);

            int i = microhabitats.size();
            microhabitats.add(new Microhabitat(K, max_gRate, biofilm_threshold, r_migration));
            immigration_index = i;
            microhabitats.get(immigration_index).setImmigration_zone(true);

            //record the time at which this happened
            newMicrohabTimes.add(time_elapsed);
        }

        //todo - use this condition for the species composition simulations (or the figure 4 stuff)
        //todo - slightly changed the exit condition to make sure we get the correct no. of datapoints for the stochastic
        //todo - lag time sims
        //todo - this might actually be a better way to do things, seeing as now we won't have to do an arraylist size check each time
//        this stops sims going onn unnecessarily too long. if the biofilm reaches the thickness limit then we record the
//        time this happened at and move on
        //if(getSystemSize()==thickness_limit){
        if(immigration_index == thickness_limit){
            exit_time = time_elapsed;
            time_elapsed = 9e9; //this way the time elapsed is now way above the duration value, so the simulation will stop
        }

        //todo - use this condition for the time to failure simulations
        //if immigration index is the same as the failure limit, then we also move on
//        if(getSystemSize() == thickness_limit || immigration_index == failure_limit) {
//            exit_time = time_elapsed;
//            failure_time = time_elapsed;
//            time_elapsed = 9e9; //this way the time elapsed is now way above the duration value, so the simulation will stop
//        }
    }


    public void performAction(){

        //need to take out all the death stuff here as there's no biocide or uniform death rate

        double tau_step = tau;

        int system_size = microhabitats.size();
        int[][] replication_allocations;
        int[][] migration_allocations;
        int[] detachment_allocations;
        int[] original_popsizes;
        int n_immigrants;

        whileloop:
        while(true) {
            poiss_immigration = new PoissonDistribution(r_immigration*tau_step);
            poiss_deterioration = new PoissonDistribution(r_deterioration*tau_step);
            poiss_migration = new PoissonDistribution(r_migration*tau_step);
            poiss_migration_edge = new PoissonDistribution(0.5*r_migration*tau_step);

            replication_allocations = new int[system_size][];
            migration_allocations = new int[system_size][];
            original_popsizes = new int[system_size];
            detachment_allocations = new int[microhabitats.get(immigration_index).getN()];

            for(int mh_index = 0; mh_index < system_size; mh_index++) {

                //we iterate through all the bacteria and calculate the events which they'll experience
                int mh_pop = microhabitats.get(mh_index).getN();
                int[] n_replications = new int[mh_pop];
                int[] n_deaths = new int[mh_pop];
                int[] n_migrations = new int[mh_pop];

                for(int bac_index = 0; bac_index < mh_pop; bac_index++) {
                    ///////// REPLICATIONS ///////////////////
                    double g_rate = microhabitats.get(mh_index).replicationRate(bac_index);

                    if(g_rate > 0.) {
                        PoissonDistribution poiss_replication = new PoissonDistribution(g_rate*tau_step);
                        poiss_replication.reseedRandomGenerator(rand.nextLong());
                        n_replications[bac_index] = poiss_replication.sample();
                    }

                    ///////// MIGRATIONS AND DETACHMENTS //////////////////////
                    //only non-dead bacteria can migrate or detach
                    if(n_deaths[bac_index] == 0) {

                        //firstly work out the migrations
                        //do edge cases and bulk, then do detachments and set detaching migrations to 0
                        //only do migrations if there's multiple microhabs
                        if(immigration_index > 0) {
                            //migration rate is halved for the edge cases
                            if(mh_index == 0 || mh_index == immigration_index) {
                                n_migrations[bac_index] = poiss_migration_edge.sample();
                            } else {
                                n_migrations[bac_index] = poiss_migration.sample();
                            }
                            //check for double events
                            if(n_migrations[bac_index] > 1) {
                                //tau_halves_counter++;
                                tau_step /= 2.;
                                continue whileloop;
                            }
                        }

                        //Now do detachments
                        //detaching bacteria can't migrate
                        if(mh_index == immigration_index){
                            detachment_allocations[bac_index] = poiss_deterioration.sample();
                            //check for double events
                            if(detachment_allocations[bac_index] > 1) {
                                //tau_halves_counter++;
                                tau_step /= 2.;
                                continue whileloop;
                            }
                            //bacteria can only migrate if it's not detaching
                            if(detachment_allocations[bac_index] > 0) {
                                n_migrations[bac_index] = 0;
                            }

                        }

                    }
                    //////////////////////////////////////////////////////
                }
                replication_allocations[mh_index] = n_replications;
                migration_allocations[mh_index] = n_migrations;
                original_popsizes[mh_index] = microhabitats.get(mh_index).getN();
            }
            n_immigrants = poiss_immigration.sample();
            break whileloop;
        }


        //now we carry out the actions
        for(int mh_index = 0; mh_index < system_size; mh_index++){
            //iterate backwards over the bacteria so we can remove them without getting index errors
            for(int bac_index = original_popsizes[mh_index]-1; bac_index >= 0; bac_index--){


                microhabitats.get(mh_index).replicateABacterium_x_N(bac_index, replication_allocations[mh_index][bac_index]);
                n_replications += replication_allocations[mh_index][bac_index];

                if(system_size > 1){
                    if(migration_allocations[mh_index][bac_index] != 0) migrate(mh_index, bac_index);
                }

                if(mh_index == immigration_index){
                    if(detachment_allocations[bac_index] != 0) {
                        microhabitats.get(mh_index).removeABacterium(bac_index);
                        n_detachments++;
                    }
                }

            }
        }

        immigrate(immigration_index, n_immigrants);
        n_immigrations += n_immigrants;
        updateBiofilmSize();
        //update the time elapsed in the system by the value of tau used in the final events
        //System.out.println("ts: "+tau_step);
        time_elapsed += tau_step;

    }


    static void replicateFigure4(String fileID, int nCores, int nBlocks, double[] rate_ratios){

        int K = 1000; //carrying capacity of each microhabitat

        //method to replicate figure 4 in the biofilm_threshold_theory notes
        double duration = 100.; //100 hour duration
        int nSamples = 120; //no. of measurements taken during each run

        int nRuns = nCores*nBlocks; //total number of simulations performed

        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/results";
        String pop_filename = fileID+"-stochastic_pop_over_time"; //file to save all the populations over time
        String microhab_filename = fileID+"-stochastic_microhabs_over_time"; //file to save the times at which new microhabs are created

        DataBox[] dataBoxes = new DataBox[nRuns]; //array to store all the results

        for(int j = 0; j < nBlocks; j++){
            System.out.println("section: "+j);

            IntStream.range(j*nCores, (j+1)*nCores).parallel().forEach(i ->
                    dataBoxes[i] = replicateFigure4_subroutine(duration, nSamples, i, K, rate_ratios));

        }

        DataBox averagedData = DataBox.averageDataBoxes(dataBoxes);

        Toolbox.writePopOverTimeToFile(results_directory, pop_filename, averagedData);
        Toolbox.writeNewMicrohabTimesToFile(results_directory, microhab_filename, averagedData);

    }

    private static DataBox replicateFigure4_subroutine(double duration, int nSamples, int runID, int K, double[] rate_ratios){
        double threshold_N_ratio = rate_ratios[0];
        double immigration_ratio = rate_ratios[1];
        double migration_ratio = rate_ratios[2];
        double deterioration_ratio = rate_ratios[3];

        double interval = duration/(double)nSamples;
        boolean alreadyRecorded = false;


        BioSystem bs = new BioSystem(K, threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio);
        ArrayList<Double> times = new ArrayList<>(); //sample times
        ArrayList<Double> total_pop_over_time = new ArrayList<>(); //size of population over time

        while(bs.time_elapsed < duration+0.2*interval){


            if((bs.getTimeElapsed()%interval <= 0.1*interval) && !alreadyRecorded){


                System.out.println("runID: "+runID+"\tt: "+bs.time_elapsed);
                times.add(bs.time_elapsed);
                total_pop_over_time.add((double)bs.getTotalN());

                alreadyRecorded = true;
            }

            if(bs.getTimeElapsed()%interval >= 0.1*interval) alreadyRecorded = false;

            bs.performAction();

        }

        return new DataBox(runID, times, total_pop_over_time, bs.newMicrohabTimes);
    }


    static void replicateFigure4Solo(String fileID, int nCores, int nBlocks, double[] rate_ratios){
        //in order to try and highlight the stochastic effects of these simulations, this method doesn't average the runs
        //and instead saves them all individually

        int K = 10000; //carrying capacity of each microhabitat (increased to 10,000 from 1000 here)

        //method to replicate figure 4 in the biofilm_threshold_theory notes
        double duration = 100.; //100 hour duration
        int nSamples = 90; //no. of measurements taken during each run

        int nRuns = nCores*nBlocks; //total number of simulations performed

        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/solo_results_bigK";
        //String results_directory = "solo_results";
        String pop_filename = fileID+"-stochastic_pop_over_time"; //file to save all the populations over time
        String microhab_filename = fileID+"-stochastic_microhabs_over_time"; //file to save the times at which new microhabs are created

        DataBox[] dataBoxes = new DataBox[nRuns]; //array to store all the results

        for(int j = 0; j < nBlocks; j++){
            System.out.println("section: "+j);

            IntStream.range(j*nCores, (j+1)*nCores).parallel().forEach(i ->
                    dataBoxes[i] = replicateFigure4_subroutine(duration, nSamples, i, K, rate_ratios));

        }


        Toolbox.writePopOverTimeToFile(results_directory, pop_filename, dataBoxes);
        Toolbox.writeNewMicrohabTimesToFile(results_directory, microhab_filename, dataBoxes);
    }



    public static void stochasticWaitingTime(String fileID, int nCores, int nBlocks, double[] rate_ratios){
        //this method is used to quantify the "stochasticity" of the paramaters
        //we shall use the stDev of the time taken to reach microhabitat N, divided by the mean of this time as the measure of stochasticity
        //this method won't have a fixed duration like the other ones, but will instead run until a certain no. of microhabitats
        //cba figuring out a nicer way of doing this, so we'll just set the duration to a long time and make sure the
        //exit condition is properly implemented in updateBiofilmSize()
        //we'll just save all the runs and the time taken to reach each microhab to a dataframe
        //todo - make sure the exit condition is set to 10 microhabitats
        //do 100 reps for each parameter set

        int K = 10000;
        int thickness_limit = 10;
        double duration = 1e5; //very long duration, this is only to make sure that we don't miss any datapoints

        int nRuns = nCores*nBlocks; //total number of simulations performed
        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/stochastic_lagTime_bigK";
        String pop_filename = fileID+"-stochastic_pop_over_time"; //file to save all the populations over time
        String microhab_filename = fileID+"-stochastic_microhabs_over_time"; //file to save the times at which new microhabs are created

        DataBox[] dataBoxes = new DataBox[nRuns];

        for(int j = 0; j < nBlocks; j++){
            System.out.println("section: "+j);

            IntStream.range(j*nCores, (j+1)*nCores).parallel().forEach(i ->
                    dataBoxes[i] = stochasticWaitingTime_subroutine(duration, i, K, thickness_limit, rate_ratios));

        }

        Toolbox.writeNewMicrohabTimesToFile_v2(results_directory, microhab_filename, thickness_limit, dataBoxes);
        Toolbox.writePopOverTimeToFile(results_directory, pop_filename, dataBoxes);
    }

    private static DataBox stochasticWaitingTime_subroutine(double duration, int runID, int K, int thickness_limit, double[] rate_ratios){
        double threshold_N_ratio = rate_ratios[0];
        double immigration_ratio = rate_ratios[1];
        double migration_ratio = rate_ratios[2];
        double deterioration_ratio = rate_ratios[3];
        int nSamples = 999; //this is just used to print output now

        ArrayList<Double> times = new ArrayList<>(); //sample times
        ArrayList<Double> total_pop_over_time = new ArrayList<>(); //size of population over time

        double interval = duration/(double)nSamples;
        boolean alreadyRecorded = false;

        BioSystem bs = new BioSystem(K, thickness_limit, threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio);

        while(bs.time_elapsed < duration+0.2*interval){

            System.out.println("runID: "+runID+"\tt: "+bs.time_elapsed);
            if((bs.getTimeElapsed()%interval <= 0.2*interval) && !alreadyRecorded){

                System.out.println("runID: "+runID+"\tt: "+bs.time_elapsed);
                times.add(bs.time_elapsed);
                total_pop_over_time.add((double)bs.getTotalN());

                alreadyRecorded = true;
            }
            if(bs.getTimeElapsed()%interval >= 0.1*interval) alreadyRecorded = false;

            bs.performAction();
        }

        return new DataBox(runID, times, total_pop_over_time, bs.newMicrohabTimes);
    }





    static void oneVeryLongSimulation(String fileID, int nCores, double[] rate_ratios){
        //this is another investigation into the low end of the parameter regime for figure 4c.
        //here we'll set the immigration ratio to 0.51, like in some previous simulations.
        //However this time we'll only run a few simulations (10 or so) but for a very long time,
        //to see if any of them manage to cross the threshold to the next microhabitat

        //in order to try and highlight the stochastic effects of these simulations, this method doesn't average the runs
        //and instead saves them all individually

        int K = 1000; //carrying capacity of each microhabitat

        //method to replicate figure 4 in the biofilm_threshold_theory notes
        double duration = 10000.; //10000 hour duration
        int nSamples = 999; //no. of measurements taken during each run


        int nRuns = nCores; //total number of simulations performed

        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/veryLongSim_results";
        String pop_filename = fileID+"-stochastic_pop_over_time"; //file to save all the populations over time
        String microhab_filename = fileID+"-stochastic_microhabs_over_time"; //file to save the times at which new microhabs are created

        DataBox[] dataBoxes = new DataBox[nRuns]; //array to store all the results

        //only doing a few runs, so no need to chunk them up with a for loop
        IntStream.range(0, nRuns).parallel().forEach(i -> dataBoxes[i] = replicateFigure4_subroutine(duration, nSamples, i, K, rate_ratios));



        Toolbox.writePopOverTimeToFile(results_directory, pop_filename, dataBoxes);
        Toolbox.writeNewMicrohabTimesToFile(results_directory, microhab_filename, dataBoxes);
    }





//    private static DataBox getEventCountersAndRunPops_Subroutine(double duration, int nSamples, int runID, double scale, double sigma){
//
//        int K = 120;
//        double c_max = 10.0, alpha = 0.01;
//        double interval = duration/nSamples;
//        boolean alreadyRecorded = false;
//
//        BioSystem bs = new BioSystem(alpha, c_max, scale, sigma);
//        ArrayList<ArrayList<ArrayList<Double>>> mh_pops_over_time = new ArrayList<>();
//        ArrayList<Double> times = new ArrayList<>();
//
//        while(bs.time_elapsed <= duration+0.02*interval){
//
//            if((bs.getTimeElapsed()%interval <= 0.02*interval) && !alreadyRecorded){
//
//                int max_poss_pop = bs.getSystemSize()*K;
//                System.out.println("runID: "+runID+"\tt: "+bs.getTimeElapsed()+"\tpop size: "+bs.getTotalN()+"/"+max_poss_pop+"\tbf_edge: "+bs.getBiofilmEdge()+"\tK*: "+bs.biofilm_threshold+"\tdet_r: "+bs.deterioration_rate);
//                alreadyRecorded = true;
//
//                times.add(bs.getTimeElapsed());
//                mh_pops_over_time.add(bs.getMicrohabPopulations()); //added in now
//
//            }
//            if(bs.getTimeElapsed()%interval >= 0.1*interval) alreadyRecorded = false;
//
//            bs.performAction();
//        }
//        if((int)bs.exit_time == 0) bs.exit_time = duration;
//
//        int[] event_counters = new int[]{runID, bs.getBiofilmThickness(), bs.getTotalN(), bs.getN_deaths(), bs.getN_detachments(), bs.getN_immigrations(), bs.getN_replications(), bs.getN_migrations(), (int)bs.getExit_time()};
//
//        return new DataBox(runID, event_counters, times, mh_pops_over_time);
//    }
//
//
//
//    static void getEventCountersAndRunPopulations(int nCores, int nBlocks, double scale, double sigma, String folderID){
//        //this is the method for the big runs to get the population distributions over time
//        //it returns a csv file that for each run contains the event counters
//        //it also returns a folder full of the bacteria distributions for each run, sampled at regular intervals
//        //these are used to make those pink and blue plots of the geno distbs over space and time
//        long startTime = System.currentTimeMillis();
//        int nRuns = nCores*nBlocks; //total number of simulations performed
//        int nSamples = 100; //no of samples taken during the runs
//
//        double duration = 26.*7.*24.; //26 week duration
//        //double duration = 52.*7.*24.; //1 year duration
//        //double duration = 1000.;
//        //double duration = 2048.;
//
//        String results_directory_name = "all_run_populations"+folderID;
//        String[] headers = new String[]{"run_ID", "bf thickness", "final_pop", "n_deaths", "n_detachments", "n_immigrations", "n_replications", "n_migrations", "exit time"};
//        DataBox[] dataBoxes = new DataBox[nRuns];
//        String event_counters_filename = "multispecies-t="+String.format("%.3f", duration)+"-event_counters-sigma="+String.format("%.5f", sigma);
//        String mh_pops_over_time_filename = "multispecies-t="+String.format("%.3f", duration)+"-sigma="+String.format("%.5f", sigma)+"-mh_pops-runID=";
//
//        for(int j = 0; j < nBlocks; j++){
//            System.out.println("section: "+j);
//
//            IntStream.range(j*nCores, (j+1)*nCores).parallel().forEach(i ->
//                    dataBoxes[i] = getEventCountersAndRunPops_Subroutine(duration, nSamples, i, scale, sigma));
//        }
//
//
//        Toolbox.writeDataboxEventCountersToFile(results_directory_name, event_counters_filename, headers, dataBoxes);
//
//        for(int i = 0; i < dataBoxes.length; i++){
//            String run_filename = mh_pops_over_time_filename+String.valueOf(dataBoxes[i].getRunID());
//            Toolbox.writeDataboxMicrohabPopsToFile(results_directory_name, run_filename, dataBoxes[i]);
//        }
//
//
//        long finishTime = System.currentTimeMillis();
//        String diff = Toolbox.millisToShortDHMS(finishTime - startTime);
//        System.out.println("results written to file");
//        System.out.println("Time taken: "+diff);
//
//    }
//
//    private static DataBox getEventCountersAndRunPops_Subroutine(double duration, int nSamples, int runID, double scale, double sigma){
//
//        int K = 120;
//        double c_max = 10.0, alpha = 0.01;
//        double interval = duration/nSamples;
//        boolean alreadyRecorded = false;
//
//        BioSystem bs = new BioSystem(alpha, c_max, scale, sigma);
//        ArrayList<ArrayList<ArrayList<Double>>> mh_pops_over_time = new ArrayList<>();
//        ArrayList<Double> times = new ArrayList<>();
//
//        while(bs.time_elapsed <= duration+0.02*interval){
//
//            if((bs.getTimeElapsed()%interval <= 0.02*interval) && !alreadyRecorded){
//
//                int max_poss_pop = bs.getSystemSize()*K;
//                System.out.println("runID: "+runID+"\tt: "+bs.getTimeElapsed()+"\tpop size: "+bs.getTotalN()+"/"+max_poss_pop+"\tbf_edge: "+bs.getBiofilmEdge()+"\tK*: "+bs.biofilm_threshold+"\tdet_r: "+bs.deterioration_rate);
//                alreadyRecorded = true;
//
//                times.add(bs.getTimeElapsed());
//                mh_pops_over_time.add(bs.getMicrohabPopulations()); //added in now
//
//            }
//            if(bs.getTimeElapsed()%interval >= 0.1*interval) alreadyRecorded = false;
//
//            bs.performAction();
//        }
//        if((int)bs.exit_time == 0) bs.exit_time = duration;
//
//        int[] event_counters = new int[]{runID, bs.getBiofilmThickness(), bs.getTotalN(), bs.getN_deaths(), bs.getN_detachments(), bs.getN_immigrations(), bs.getN_replications(), bs.getN_migrations(), (int)bs.getExit_time()};
//
//        return new DataBox(runID, event_counters, times, mh_pops_over_time);
//    }
//
//
//
//    static void timeToFailure(int nCores, int nBlocks, double scale, double sigma, String folderID){
//        //this method is used to find the time to failure as a function of various paramters (in this case % resistant)
//        //the simulations run until they reach a failure criteria (currently a thickness of 1)
//        long startTime = System.currentTimeMillis();
//
////        int n_runs_per_section = 20;
////        int n_sections = nReps/n_runs_per_section;
////        int nMeasurements = 100;
//        int nRuns = nCores*nBlocks; //total number of simulations performed
//        int nSamples = 100; //no of samples taken during the runs
//
//        double duration = 52.*7.*24.; //1 year duration
//        //double duration = 1000.;
//        //double duration = 2048.;
//
//        String results_directory_name = "time_to_failure"+folderID;
//        String[] headers = new String[]{"run_ID", "bf thickness", "final_pop", "n_deaths", "n_detachments", "n_immigrations", "n_replications",
//                "n_migrations", "exit time", "failure time"};
//        DataBox[] dataBoxes = new DataBox[nRuns];
//        String event_counters_filename = "multispecies-t="+String.valueOf(duration)+"-parallel-event_counters_sigma="+String.format("%.5f", sigma);
//
//        for(int j = 0; j < nBlocks; j++){
//            System.out.println("section: "+j);
//
//            IntStream.range(j*nCores, (j+1)*nCores).parallel().forEach(i ->
//                    dataBoxes[i] = timeToFailure_subroutine(duration, nSamples, i, scale, sigma));
//        }
//
//
//        Toolbox.writeDataboxEventCountersToFile(results_directory_name, event_counters_filename, headers, dataBoxes);
//
//
//        long finishTime = System.currentTimeMillis();
//        String diff = Toolbox.millisToShortDHMS(finishTime - startTime);
//        System.out.println("results written to file");
//        System.out.println("Time taken: "+diff);
//    }
//
//
//    private static DataBox timeToFailure_subroutine(double duration, int nSamples, int runID, double scale, double sigma){
//
//        int K = 120;
//        double c_max = 10.0, alpha = 0.01;
//        double interval = duration/nSamples;
//        boolean alreadyRecorded = false;
//
//        BioSystem bs = new BioSystem(alpha, c_max, scale, sigma);
//        ArrayList<ArrayList<ArrayList<Double>>> mh_pops_over_time = new ArrayList<>();
//        ArrayList<Double> times = new ArrayList<>();
//
//        while(bs.time_elapsed <= duration+0.02*interval){
//
//            if((bs.getTimeElapsed()%interval <= 0.02*interval) && !alreadyRecorded){
//
//                int max_poss_pop = bs.getSystemSize()*K;
//                System.out.println("runID: "+runID+"\tt: "+bs.getTimeElapsed()+"\tpop size: "+bs.getTotalN()+"/"+max_poss_pop+"\tbf_edge: "+bs.getBiofilmEdge()+"\tK*: "+bs.biofilm_threshold+"\tdet_r: "+bs.deterioration_rate);
//                alreadyRecorded = true;
//
//                //times.add(bs.getTimeElapsed());
//                //mh_pops_over_time.add(bs.getMicrohabPopulations()); //don't need these for the time to failure stuff
//
//            }
//            if(bs.getTimeElapsed()%interval >= 0.1*interval) alreadyRecorded = false;
//
//            bs.performAction();
//        }
//        if((int)bs.exit_time == 0) bs.exit_time = duration;
//
//        int[] event_counters = new int[]{runID, bs.getBiofilmThickness(), bs.getTotalN(), bs.getN_deaths(), bs.getN_detachments(),
//                bs.getN_immigrations(), bs.getN_replications(), bs.getN_migrations(), (int)bs.getExit_time(), (int)bs.getFailure_time()};
//
//        return new DataBox(runID, event_counters, times, mh_pops_over_time);
//    }



}


