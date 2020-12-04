import javax.print.attribute.standard.NumberOfInterveningJobs;

public class MultispeciesMain {

    public static void main(String[] args) {

        //todo - changed nreps from 10 to 5 for the Allen comparison sims
        int nCores = Integer.parseInt(args[0]); //no. of cores used to run the simulations on the cluster
        int nBlocks = 10; //no. of blocks of simulations (1 block = 1 rep on all n cores)
        //Time to nth microhab params
        int microhab_lim = 1; //this is the microhab index we're measuring the time to reach

        //values are results directory name, immigration ratio, migration ratio, K
        Object[] powerLaw_histogram_params = new Object[]{"timeTo1Microhab_powerLaw_bigK_results", 0.8, 0.8, 10000};
        //Do 6 parameter pairs.  2 largest values of t1, next two with biggest values of std(t1), and two random ones
        // order of array is [n_thresh, det_ratio
        double[] t1_big_1 = new double[]{1.35, 0.600}; //avg(t1) = 10.745, stdev(t1) = 2.865178
        double[] t1_big_2 = new double[]{1.20, 0.675}; //avg(t1) = 9.393, stdev(t1) = 2.869919
        double[] t1_stDev_big_1 = new double[]{0.825, 1.200}; //avg(t1) = 2.8975, stdev(t1) = 5.9702
        double[] t1_stDev_big_2 = new double[]{0.750, 1.350}; //avg(t1) = 4.7610, stdev(t1) = 1.6565
        double[] t1_rand_1 = new double[]{0.6, 0.3}; //avg(t1) = 0.67143, stdev(t1) = 0.010807
        double[] t1_rand_2 = new double[]{0.45, 1.2}; //avg(t1) = 0.656565, stdev(t1) = 0.019256

        //BioSystem.t1_powerLaw(powerLaw_histogram_params, nCores, nBlocks, microhab_lim, t1_stDev_big_1);

        //For further insight, we'll plot N over time for t1_stDev_big_1.  And t1_rand_1 as well for comparison.
        //array of the rates used for the N over time method
        //[threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        double[] t1_stDev_big_1_ratios = new double[]{0.825, 0.8, 0.8, 1.200};
        double[] t1_rand_1_ratios      = new double[]{0.6,   0.8, 0.8, 0.2};
        //fileIDs used for the results .csv file
        String t1_stDev_big_1_fileID = String.format("nThresh=%.3f_rDet=%.3f", t1_stDev_big_1_ratios[0], t1_stDev_big_1_ratios[3]);
        String t1_rand_1_fileID      = String.format("nThresh=%.3f_rDet=%.3f", t1_rand_1[0], t1_rand_1[3]);;

        BioSystem.stochasticWaitingTime(t1_rand_1_fileID, nCores, nBlocks, t1_rand_1_ratios);






        //params used in the species composition simulations
        //want to have N* < K for both of these and keep the immigration rate fixed, vary deterioration rate to change
        //what regime we are in
        //values are results_directory_name, file_ID, threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio, K
        //increased file formatting to avoid mistakenly double counting genotypes
//        Object[] ms_phase2_params = new Object[]{"speciesComp-phase2-varyingImm-bigK-mostPrecise", 0.7, 1., 0.8, 0.5, 10000}; //immigration dominated r_im/Kr_det > 1
//        Object[] ms_phase4_params = new Object[]{"speciesComp-phase4-varyingImm-bigK-mostPrecise", 0.7, 0.25, 0.8, 0.5, 10000}; //deterioration dominated r_im/Kr_det < 1
//
//        BioSystem.speciesComposition(nCores, nReps, ms_phase2_params);
//
//        //The ratios in this section are used for a more fair comparison between the effects of N*, very similar to the ones used for the stocastic
//        //versions of figures 4c and d.  The difference being that the two parameter sets now only differ in their values of
//        //N*, and the deterioration rate is the same for both.  We'll also just do 50 reps instead of 100 for these.
//        //we'll run the N* = 1.17 runs in the existing fig4c folders, and the 0.7 in the fig4d ones.  Can revert them back later if needs be
//        //N* = 1.17 parameters [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
//        //todo - changed the replicateFigure4solo method so things are saved in a different directory (for the other parameters being equal runs)
//        double[] ratios4c_rImmig_0_4 = new double[]{1.17, 0.4, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_5 = new double[]{1.17, 0.5, 0.8, 0.5};
        double[] ratios4c_rImmig_0_55 = new double[]{1.17, 0.55, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_6 = new double[]{1.17, 0.6, 0.8, 0.5};
        double[] ratios4c_rImmig_0_65 = new double[]{1.17, 0.65, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_7 = new double[]{1.17, 0.7, 0.8, 0.5};
//        //N* = 0.7 parameters [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
//        double[] ratios4d_rImmig_0_4 = new double[]{0.7, 0.4, 0.8, 0.5};
//        double[] ratios4d_rImmig_0_5 = new double[]{0.7, 0.5, 0.8, 0.5};
        double[] ratios4d_rImmig_0_55 = new double[]{0.7, 0.55, 0.8, 0.5};
//        double[] ratios4d_rImmig_0_6 = new double[]{0.7, 0.6, 0.8, 0.5};
        double[] ratios4d_rImmig_0_65 = new double[]{0.7, 0.65, 0.8, 0.5};
//        double[] ratios4d_rImmig_0_7 = new double[]{0.7, 0.7, 0.8, 0.5};

        //for further comparison on the effects of N*, now doing a third intermidiate value of N* = 0.9
        //just for "ease" of bookkeeping, this parameter set will be referred to as fig4e_ratios
//        double[] ratios4e_rImmig_0_4 = new double[]{0.9, 0.4, 0.8, 0.5};
//        double[] ratios4e_rImmig_0_5 = new double[]{0.9, 0.5, 0.8, 0.5};
//        double[] ratios4e_rImmig_0_6 = new double[]{0.9, 0.6, 0.8, 0.5};
//        double[] ratios4e_rImmig_0_7 = new double[]{0.9, 0.7, 0.8, 0.5};
//
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nReps, ratios4c_rImmig_0_55);



        //ratios for the rates taken from the biofilm_threshold_theory paper
        //arrays containing the ratios in the format:
        //[threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //Figure 4c ratios:
//        //this one is inteded to approach the lower end of the figure 4c parameter regime
//        double[] ratios4c_rImmig_0_51 = new double[]{1.17, 0.51, 0.8, 0.5};
//        //do some intermediate values between 0.51 and 0.7
//        double[] ratios4c_rImmig_0_55 = new double[]{1.17, 0.55, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_575 = new double[]{1.17, 0.575, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_6 = new double[]{1.17, 0.6, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_625 = new double[]{1.17, 0.625, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_65 = new double[]{1.17, 0.65, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_675 = new double[]{1.17, 0.675, 0.8, 0.5};
//        //these are the ones from the biofilm_threshold_theory fig 4c
//        double[] ratios4c_rImmig_0_7 = new double[]{1.17, 0.7, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_8 = new double[]{1.17, 0.8, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_9 = new double[]{1.17, 0.9, 0.8, 0.5};
//
//        //Figure 4d ratios:
//        double[] ratios4d_rImmig_0_4 = new double[]{0.7, 0.4, 0.8, 0.9};
//        double[] ratios4d_rImmig_0_5 = new double[]{0.7, 0.5, 0.8, 0.9};

        //Time to nth microhab params
        //int microhab_lim = 1; //this is the microhab index we're measuring the time to reach
        //values are results directory name, immigration ratio, migration ratio, K
        //Object[] phase_diag_params = new Object[]{"timeTo1Microhab_phaseDiagram_bigK", 0.8, 0.8, 10000};

        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_6", nCores, nReps, ratios4c_rImmig_0_6);
        //BioSystem.oneVeryLongSimulation("ratios4c_rImmig_0_55", 10, ratios4c_rImmig_0_55);
        //BioSystem.stochasticWaitingTime("ratios4c_rImmig_0_65", nCores, nReps, ratios4c_rImmig_0_65);
        //BioSystem.timeToNthMicrohabPhaseDiagram(phase_diag_params, nCores, microhab_lim);
    }
}
