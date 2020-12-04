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
        //todo - can just add a +1 to make it equivalent to the old one
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
            //todo - need to handle deterioration rate being 0 in the phase diagram runs
            if(Math.abs(r_deterioration - 0) < 1e-7) poiss_deterioration = new PoissonDistribution(1000);
            else poiss_deterioration = new PoissonDistribution(r_deterioration*tau_step);
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

                            //todo - special case for the phase diagram code where r_det is 0
                            if(Math.abs(r_deterioration - 0) < 1e-7) detachment_allocations[bac_index] = 0;
                            else detachment_allocations[bac_index] = poiss_deterioration.sample();
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


    static void speciesComposition(int nCores, int nBlocks, Object[] params){
        //method to investigate the effect on the parameter regimes on the species composition
        //Outputs both the overall population over time for the runs like the other bftt runs,
        //and also the genos over time like in the multispecies runs
        int K = (int)params[5];
        double duration = 300;
        int nSamples = 150;
        int nRuns = nCores*nBlocks; //total number of simulations performed

        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/species_comp_results/"+params[0];
        String pop_filename = "rIm-"+params[2]+"_rDet-"+params[4]+"-pop_over_time"; //file to save the population size over time
        String microhab_filename = "rIm-"+params[2]+"_rDet-"+params[4]+"-microhabs_over_time"; //file to save the times new microhabitats are added at

        DataBox[] dataBoxes = new DataBox[nRuns]; //array to store all the results in

        for(int j = 0; j < nBlocks; j++){
            System.out.println("section: "+j);

            IntStream.range(j*nCores, (j+1)*nCores).parallel().forEach(i ->
                    dataBoxes[i] = speciesComposition_subroutine(duration, nSamples, i, params));

        }


        Toolbox.writePopOverTimeToFile(results_directory, pop_filename, dataBoxes);
        Toolbox.writeNewMicrohabTimesToFile(results_directory, microhab_filename, dataBoxes);
        for (DataBox dataBox : dataBoxes) {
            Toolbox.writeGenosOverTimeToCSV(results_directory, dataBox);
        }

    }


    private static DataBox speciesComposition_subroutine(double duration, int nSamples, int runID, Object[] params){

        //threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio, K
        double threshold_N_ratio = (double)params[1];
        double immigration_ratio = (double)params[2];
        double migration_ratio = (double)params[3];
        double deterioration_ratio = (double)params[4];
        int K = (int)params[5];

        double interval = duration/(double)nSamples;
        boolean alreadyRecorded = false;


        BioSystem bs = new BioSystem(K, threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio);
        ArrayList<Double> times = new ArrayList<>(); //sample times
        ArrayList<Double> total_pop_over_time = new ArrayList<>(); //size of population over time
        ArrayList<ArrayList<ArrayList<Double>>> mh_pops_over_time = new ArrayList<>(); //distribution of genotypes over time

        while(bs.time_elapsed < duration+0.2*interval){


            if((bs.getTimeElapsed()%interval <= 0.1*interval) && !alreadyRecorded){


                System.out.println("runID: "+runID+"\tt: "+bs.time_elapsed);
                times.add(bs.time_elapsed);
                total_pop_over_time.add((double)bs.getTotalN());
                mh_pops_over_time.add(bs.getMicrohabPopulations());

                alreadyRecorded = true;
            }

            if(bs.getTimeElapsed()%interval >= 0.1*interval) alreadyRecorded = false;

            bs.performAction();

        }

        return new DataBox(runID, times, total_pop_over_time, bs.newMicrohabTimes, mh_pops_over_time);


    }



    static void replicateFigure4Solo(String fileID, int nCores, int nBlocks, double[] rate_ratios){
        //in order to try and highlight the stochastic effects of these simulations, this method doesn't average the runs
        //and instead saves them all individually

        int K = 10000; //carrying capacity of each microhabitat (increased to 10,000 from 1000 here)

        //method to replicate figure 4 in the biofilm_threshold_theory notes
        double duration = 100.; //100 hour duration
        int nSamples = 90; //no. of measurements taken during each run

        int nRuns = nCores*nBlocks; //total number of simulations performed

        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/allen_presentation_bigK";
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






    public static void stochasticWaitingTime(String fileID, int nCores, int nBlocks, double[] rate_ratios){
        //this method is used to quantify the "stochasticity" of the paramaters
        //we shall use the stDev of the time taken to reach microhabitat N, divided by the mean of this time as the measure of stochasticity
        //this method won't have a fixed duration like the other ones, but will instead run until a certain no. of microhabitats
        //cba figuring out a nicer way of doing this, so we'll just set the duration to a long time and make sure the
        //exit condition is properly implemented in updateBiofilmSize()
        //we'll just save all the runs and the time taken to reach each microhab to a dataframe
        //4/12/20 - this method is now being used for the t1 power law parameter pairs.  Using it to get plots of the
        //population size over N.  Only interested in the first microhabitat, so set exit condition to 2 microhabs
        //do 100 reps for each parameter set

        int thickness_limit = 2;
        int K = 10000;
        double duration = 1e4; //very long duration, this is only to make sure that we don't miss any datapoints
        int nSamples = 100; //this is just used to print output now

        int nRuns = nCores*nBlocks; //total number of simulations performed
        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/timeTo1Microhab_powerLaw_bigK_results/t1PowerLaw_pops_over_time";
        String pop_filename = fileID+"-stochastic_pop_over_time"; //file to save all the populations over time
        String microhab_filename = fileID+"-stochastic_microhabs_over_time"; //file to save the times at which new microhabs are created

        DataBox[] dataBoxes = new DataBox[nRuns];

        for(int j = 0; j < nBlocks; j++){
            System.out.println("section: "+j);

            IntStream.range(j*nCores, (j+1)*nCores).parallel().forEach(i ->
                    dataBoxes[i] = stochasticWaitingTime_subroutine(duration, nSamples, i, K, thickness_limit, rate_ratios));

        }

        Toolbox.writeNewMicrohabTimesToFile_v2(results_directory, microhab_filename, thickness_limit, dataBoxes);
        Toolbox.writePopOverTimeToFile(results_directory, pop_filename, dataBoxes);
    }

    private static DataBox stochasticWaitingTime_subroutine(double duration, int nSamples, int runID, int K, int thickness_limit, double[] rate_ratios){
        double threshold_N_ratio = rate_ratios[0];
        double immigration_ratio = rate_ratios[1];
        double migration_ratio = rate_ratios[2];
        double deterioration_ratio = rate_ratios[3];


        ArrayList<Double> times = new ArrayList<>(); //sample times
        ArrayList<Double> total_pop_over_time = new ArrayList<>(); //size of population over time

        double interval = duration/(double)nSamples;
        boolean alreadyRecorded = false;

        BioSystem bs = new BioSystem(K, thickness_limit, threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio);

        while(bs.time_elapsed < duration+0.2*interval){


            if((bs.getTimeElapsed()%interval <= 0.01*interval) && !alreadyRecorded){

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




    static void timeToNthMicrohabPhaseDiagram(Object[] params, int nCores, int microhab_lim){
        //this method is used to make a colour plot of the time taken to reach the Nth microhabitat as
        //a function of N* and r_det
        //save all the values for each of the runs in seperate dataframes, then do averaging etc later manually
        //todo - make sure the failure limit thing is set up correctly

        String[] headers = new String[]{"n_thresh", "det_ratio", "time_to_n", "time_elapsed"};
        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/"+params[0];

        double duration = 1000.;
        int nMeasurements = 20;
        double N_thresh_min = 0., N_thresh_max = 1.5;
        double N_thresh_increment = (N_thresh_max - N_thresh_min)/nMeasurements;
        double r_det_ratio_min = 0., r_det_ratio_max = 1.5;
        double r_det_ratio_increment = (r_det_ratio_max - r_det_ratio_min)/nMeasurements;
        //ArrayList<DataBox[]> dataBoxes = new ArrayList<>();

        for(int nt = 0; nt <= nMeasurements; nt++){
            for(int dr = 0; dr <= nMeasurements; dr++){
                double n_thresh = N_thresh_min + (nt*N_thresh_increment);
                double det_ratio = r_det_ratio_min + (dr*r_det_ratio_increment);

                DataBox[] dataBoxes = timeToNthMicrohabPhaseDiagram_subroutine(nCores, duration, params, n_thresh, det_ratio, microhab_lim);
                String filename = String.format("mhLim-%d_N^-%.3f_rDet-%.3f", microhab_lim, n_thresh, det_ratio);
                Toolbox.writeTimeToNthMicrohabDataToFile(results_directory, filename, headers, dataBoxes);
            }
        }
    }


    private static DataBox[] timeToNthMicrohabPhaseDiagram_subroutine(int n_reps, double duration, Object[] params, double n_thresh, double det_ratio, int microhab_lim){
        //here we run several reps of the same parameters sets in parallel
        //returns all of the reps, which are then saved in a csv file.  There's a csv file for each parameter pair

        DataBox[] dataBoxes = new DataBox[n_reps];

        IntStream.range(0, n_reps).parallel().forEach(i -> dataBoxes[i] = BioSystem.timeToNthMicrohabPhaseDiagram_subsubroutine(i, duration, params, n_thresh, det_ratio, microhab_lim));

        return dataBoxes;
    }


    private static DataBox timeToNthMicrohabPhaseDiagram_subsubroutine(int i, double duration, Object[] params, double n_thresh, double det_ratio, int microhab_lim){
        //returns the time taken to reach the nth microhabitat
        int nMeasurements = 50;
        double interval = duration/nMeasurements;
        boolean alreadyRecorded = false;
        int K = (int)params[3];
        double immigration_ratio = (double)params[1], migration_ratio = (double)params[2];


        BioSystem bs = new BioSystem(K, microhab_lim, n_thresh, immigration_ratio, migration_ratio, det_ratio);


        while(bs.time_elapsed <= (duration+0.001*interval)){
            //got rid of the alreadyRecorded stuff as it doesn't really matter here
            if((bs.getTimeElapsed()%interval >= 0. && bs.getTimeElapsed()%interval <= 0.1*interval)){

                System.out.println("rep : "+i+"\ttau: "+bs.tau+"\tN*: "+bs.biofilm_threshold+"\td_rate: "+bs.r_deterioration+"\tt: "+bs.getTimeElapsed()+"\tpop size: "+bs.getTotalN()+"\tbf_edge: "+bs.getBiofilmEdge()+"\tsystem size: "+bs.getSystemSize()+"\tc_max: "+bs.c_max);
                //alreadyRecorded = true;
            }

            //if(bs.getTimeElapsed()%interval >= 0.1*interval) alreadyRecorded = false;

            bs.performAction();
        }


        return new DataBox(n_thresh, det_ratio, bs.exit_time, bs.time_elapsed);
    }







    public static void t1_powerLaw(Object[] params, int nCores, int nBlocks, int microhab_lim, double[] param_pairs){
        // This method is used to investigate what the shapes of the distributions of the time taken to reach the first microhabitat look like.
        // We'll pick a few parameter pairs from the t1 heatmap, do 100 reps of each of these and then plot a histogram of the time taken.
        // 100 reps = 10 runs on 10 cores.  Try 24 hour queue first.
        // params = [results directory name, immigration ratio, migration ratio, K

        // file structure will be a .csv file for each parameter pair, containing the time taken to reach the first microhabitat.

        // can use the time_to first microhabitat subroutine to get the data

        int nRuns = nCores*nBlocks;
        DataBox[] dataBoxes = new DataBox[nRuns]; //array to store the t1 results in

        double n_thresh = param_pairs[0], det_ratio = param_pairs[1];
        double duration = 1000.;

        String[] headers = new String[]{"n_thresh", "det_ratio", "time_to_n", "time_elapsed"};
        String results_directory = "/Disk/ds-sopa-personal/s1212500/multispecies-sims/biofilm_threshold_theory/"+params[0];
        String filename = "t1_histogram-N_thresh="+String.format("%.3f", n_thresh)+"-r_det_ratio="+String.format("%.3f", det_ratio);

        for(int nb = 0; nb < nBlocks; nb++){
            IntStream.range(nb, (nb+1)*nCores).parallel().forEach(i -> dataBoxes[i] = BioSystem.timeToNthMicrohabPhaseDiagram_subsubroutine(i, duration, params, n_thresh, det_ratio, microhab_lim));
        }

        Toolbox.writeTimeToNthMicrohabDataToFile(results_directory, filename, headers, dataBoxes);

        System.out.println("Complete");

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
}


