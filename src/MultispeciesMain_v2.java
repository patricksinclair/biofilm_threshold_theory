public class MultispeciesMain_v2 {
    public static void main(String[] args) {
        int nCores = Integer.parseInt(args[0]); //no. of cores used to run the simulations on the cluster

        // TODO - this main class is used to run the key methods used to replicate the figures used in the bftt paper
        // TODO - but with the correct logistic growth implementation this time
        // TODO - once the simulation is done, remove the "TODO"

        // TODO - Following the meeting on 17/6/22, the stochastic figures in the main text were elected to be
        // TODO - redone with K = 1000.  The phase diagram has already been done, now need to do the phase diagram histograms
        // TODO - and the Figure 4 pop vs t curves

        // TODO - phase diagram histograms
        // do 20 blocks on 25 cores = 500 runs at a time.
        // should be doable on 1 week queue
        //int nBlocks = 20;
        int microhab_lim = 1; //this is the microhab index we're measuring the time to reach
        // values are [results directory name, immigration ratio, migration ratio, K]
        Object[] histogram_diag_params = new Object[]{"timeTo1Microhab_powerLaw_K_1000_results_v2", 0.8, 0.8, 1000};
        // order of array is [n_thresh, det_ratio]
        double[] t1_v2_K_1000 = new double[]{1.050, 0.90}; // largest value of t1
        double[] s1_v2_K_1000 = new double[]{0.825, 1.35}; // largest stdev of t1
        double[] r1_v2_K_1000 = new double[]{0.600, 0.3}; // random selection t1
        double[] r2_v2_K_1000 = new double[]{0.450, 1.2}; // random selection t1

//        BioSystem.t1_powerLaw(histogram_diag_params, nCores, nBlocks, microhab_lim, t1_v2_K_1000);
//        BioSystem.t1_powerLaw(histogram_diag_params, nCores, nBlocks, microhab_lim, s1_v2_K_1000);
//        BioSystem.t1_powerLaw(histogram_diag_params, nCores, nBlocks, microhab_lim, r1_v2_K_1000);
//        BioSystem.t1_powerLaw(histogram_diag_params, nCores, nBlocks, microhab_lim, r2_v2_K_1000);


        // TODO - Figure 4, K=1000 - MAKE SURE K = 1000 IS SET IN Biosystem.replicateFigure4Solo()
        // TODO - ALSO MAKE SURE THE NO. OF CORES IS SET TO 20
        // TODO - Do the N* = 1.17 runs first, as the choice of r_imm is more important for them.
        // TODO - First batch of rImms [0.65, 0.7, 0.75] (already done 0.7 for N* = 1.17)
        double[] ratios4c_rImmig_0_6  = new double[]{1.17, 0.6,  0.8, 0.5};
        double[] ratios4c_rImmig_0_65 = new double[]{1.17, 0.65, 0.8, 0.5};
        double[] ratios4c_rImmig_0_7  = new double[]{1.17, 0.7,  0.8, 0.5};
        double[] ratios4c_rImmig_0_725= new double[]{1.17, 0.725,  0.8, 0.5};
        double[] ratios4c_rImmig_0_75 = new double[]{1.17, 0.75, 0.8, 0.5};
        double[] ratios4c_rImmig_0_783= new double[]{1.17, 0.783,  0.8, 0.5};

        int nBlocks = 5;
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_6",  nCores, nBlocks, ratios4c_rImmig_0_6);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_65", nCores, nBlocks, ratios4c_rImmig_0_65);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_7",  nCores, nBlocks, ratios4c_rImmig_0_7);
        BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_725", nCores, nBlocks, ratios4c_rImmig_0_725);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_75", nCores, nBlocks, ratios4c_rImmig_0_75);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_783",  nCores, nBlocks, ratios4c_rImmig_0_783);

        // TODO - Now do the N* = 0.7 figure 4 runs with the same immigration rates that we use for the N* = 1.17 case






















        // TODO - EVERYTHING BELOW HERE WAS DONE FOR K=10,000 (unless specified otherwise)
        // TODO - THIS SECTION OF THE CODE IS NOW DONE AND DUSTED, JUST KEPT FOR POSTERITY
        //int nCores = 1;
        // TODO - Figure 3 (the t1 phase diagram - the one with the pink deterministic boundary surrounded by 4 histograms)
        // TODO - SET nCores TO 25 FOR THIS ONE, WE DON'T DO MULTIPLE REPS CURRENTLY
        //Time to nth microhab params
        //int microhab_lim = 1; //this is the microhab index we're measuring the time to reach
        // values are [results directory name, immigration ratio, migration ratio, K]
        //Object[] phase_diag_params = new Object[]{"timeTo1Microhab_phaseDiagram_K_1000_v2", 0.8, 0.8, 1000};
        // BioSystem.timeToNthMicrohabPhaseDiagram(phase_diag_params, nCores, microhab_lim);
        // TODO - Maybe do a second batch, if so NEED TO CHANGE FILE NAME IN timeToNthMicrohabPhaseDiagram
        // TODO? BioSystem.timeToNthMicrohabPhaseDiagram(phase_diag_params, nCores, microhab_lim);


        // TODO - Figure 3 (the t1 phase diagram - the 4 histograms surrounding the central phase diagram.
        // TODO - Histograms (b-d) were not in the pseudologistic regime, so only need to redo (a) - with new params
        // do 20 blocks on 25 cores = 500 runs at a time.
        // should be doable on 1 week queue
        //int nBlocks = 20;
        //Object[] histogram_diag_params = new Object[]{"timeTo1Microhab_powerLaw_bigK_results_v2", 0.8, 0.8, 10000};
        // order of array is [n_thresh, det_ratio]
        //double[] t1_big_1_v2 = new double[]{1.050, 0.750};
        //BioSystem.t1_powerLaw(histogram_diag_params, nCores, nBlocks, microhab_lim, t1_big_1_v2);


        // Figure 4 (the blue-green-purple comparisons of stochastic and deterministic models)
        // ONLY NEED TO REDO THE 4c VERSIONS (the N* < K ones should still be ok)
        // ratios for the rates taken from the biofilm_threshold_theory paper
        // Figure 4c ratios:
        // arrays containing the ratios in the format:
        // [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //double[] ratios4c_rImmig_0_55 = new double[]{1.17, 0.55, 0.8, 0.5};
        //double[] ratios4c_rImmig_0_6  = new double[]{1.17, 0.6,  0.8, 0.5};
        //double[] ratios4c_rImmig_0_65 = new double[]{1.17, 0.65, 0.8, 0.5};
        //double[] ratios4c_rImmig_0_7  = new double[]{1.17, 0.7,  0.8, 0.5};
        //int nBlocks = 5;
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nBlocks, ratios4c_rImmig_0_55);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_6",  nCores, nBlocks, ratios4c_rImmig_0_6);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_65", nCores, nBlocks, ratios4c_rImmig_0_65);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_7",  nCores, nBlocks, ratios4c_rImmig_0_7);

        //int nBlocks = 5;
        // TODO - Supplementary Figure 4 (the three figures about increasing K from 1000 -> 10,000)
        // TODO - NEED TO CHANGE PARAMS IN replicateFigure4Solo AND CHANGE FILENAMES (CHECK bf_thresh_theory_plotter.ipynb)
        // K = 1000, duration = 100
        // BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nBlocks, ratios4c_rImmig_0_55);
        // TODO - K = 10,000, duration = 10,000 - ACTUALLY IT'S PROBABLY NOT WORTH DOING THE LONG SIMULATION FOR rImm = 0.55 (4c ratio)
        // TODO BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nBlocks, ratios4c_rImmig_0_55);
        // TODO - Might be worth doing the K comparison plots with a v2 Fig4c ratio that might actually get close to N*.
        // TODO - Let's try rImm = 0.7 (this doesn't transition at K=10,000) - UPDATE: This does transition at K=1000.
        // rImm = 0.7, K = 1000, duration = 100
        // BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_7", nCores, nBlocks, ratios4c_rImmig_0_7);
        // TODO - rImm = 0.7, K = 10,000, duration = 10,000
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_7", nCores, nBlocks, ratios4c_rImmig_0_7);


        // Supplementary figure 5 (the 4 figures about different growth dynamics)
        // have already done r_imm = 0.55 & 0.6 in the above simulations
        // do some intermediate values between 0.7 and 0.8, see if they differ from deterministic model (THEY DO)
        // [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //double[] ratios4c_rImmig_0_75  = new double[]{1.17, 0.75,  0.8, 0.5};
        //double[] ratios4c_rImmig_0_775 = new double[]{1.17, 0.775, 0.8, 0.5};
        //double[] ratios4c_rImmig_0_8   = new double[]{1.17, 0.8,   0.8, 0.5};
        //double[] ratios4c_rImmig_0_9   = new double[]{1.17, 0.9,   0.8, 0.5};

        //int nBlocks = 5;
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_75",  nCores, nBlocks, ratios4c_rImmig_0_75);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_775", nCores, nBlocks, ratios4c_rImmig_0_775);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_8", nCores, nBlocks, ratios4c_rImmig_0_8);
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_9",   nCores, nBlocks, ratios4c_rImmig_0_9);


        // Supplementary figure S6 (the 2 figures about different growth dynamics)
        // The original versions of these should actually be ok, as N* < K, but good to make sure - ORIGINALS ARE OK
        // [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //double[] ratiosS6_rImmig_0_4 = new double[]{0.7, 0.4, 0.8, 0.9};
        //double[] ratiosS6_rImmig_0_5 = new double[]{0.7, 0.5, 0.8, 0.9};
        //int nBlocks = 5;
        //BioSystem.replicateFigure4Solo("ratiosS6_rImmig_0_4", nCores, nBlocks, ratiosS6_rImmig_0_4);
        //BioSystem.replicateFigure4Solo("ratiosS6_rImmig_0_5", nCores, nBlocks, ratiosS6_rImmig_0_5);


        // TODO - The inclusion of logistic death means that rImm needs to be increased to get the behaviour that the model
        // TODO - used to show (for cases where N* > K).  So to recreate the old Figure 4c, we'll change the immigration rates
        // TODO - from rImm = (0.55, 0.6, 0.65) -> rImm_v2 = (0.7, 0.75, 0.78).  The first two are already done above.
        // [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //double[] ratios4c_rImmig_0_783  = new double[]{1.17, 0.783,  0.8, 0.5};

        //int nBlocks = 5;
        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_783",  nCores, nBlocks, ratios4c_rImmig_0_783);

        // TODO - Also need to do the corresponding parameter set for Figure 4a (previously 4d) where N* = 0.7
        // TODO - Keep the naming convention as 4d, I know the whole scheme is a mess at this point
        // [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //double[] ratios4d_rImmig_0_7  = new double[]{0.7, 0.7,  0.8, 0.5};
        //double[] ratios4d_rImmig_0_75 = new double[]{0.7, 0.75, 0.8, 0.5};
        //double[] ratios4d_rImmig_0_78 = new double[]{0.7, 0.78, 0.8, 0.5};

        //BioSystem.replicateFigure4Solo("ratios4d_rImmig_0_7",   nCores, nBlocks, ratios4d_rImmig_0_7);
        //BioSystem.replicateFigure4Solo("ratios4d_rImmig_0_75",  nCores, nBlocks, ratios4d_rImmig_0_75);
        //BioSystem.replicateFigure4Solo("ratios4d_rImmig_0_78",  nCores, nBlocks, ratios4d_rImmig_0_78);

    }
}
