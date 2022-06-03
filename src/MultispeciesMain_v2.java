public class MultispeciesMain_v2 {
    public static void main(String[] args) {

        // TODO - this main class is used to run the key methods used to replicate the figures used in the bftt paper
        // TODO - but with the correct logistic growth implementation this time
        // TODO - once the simulation is done, remove the "TODO"

        int nCores = Integer.parseInt(args[0]); //no. of cores used to run the simulations on the cluster
        //int nCores = 1;
        // TODO - Figure 3 (the t1 phase diagram - the one with the pink deterministic boundary surrounded by 4 histograms)
        // TODO - SET nCores TO 25 FOR THIS ONE, WE DON'T DO MULTIPLE REPS CURRENTLY
        //Time to nth microhab params
        int microhab_lim = 1; //this is the microhab index we're measuring the time to reach
        // values are results directory name, immigration ratio, migration ratio, K
        Object[] phase_diag_params = new Object[]{"timeTo1Microhab_phaseDiagram_bigK_v2", 0.8, 0.8, 10000};
        BioSystem.timeToNthMicrohabPhaseDiagram(phase_diag_params, nCores, microhab_lim);
        // TODO - Maybe do a second batch, if so NEED TO CHANGE FILE NAME IN timeToNthMicrohabPhaseDiagram
        // TODO? BioSystem.timeToNthMicrohabPhaseDiagram(phase_diag_params, nCores, microhab_lim);


        // TODO - Figure 3 (the t1 phase diagram - the 4 histograms surrounding the central phase diagram
        // TODO - ACTUALLY WAIT UNTIL THE PHASE DIAGRAM IS DONE BEFORE WE CHOOSE THE HISTOGRAMS TO SIMULATE EXTRA RUNS FOR
        // TODO - DO THIS LATER


        // TODO - Figure 4 (the blue-green-purple comparisons of stochastic and deterministic models)
        // TODO - ONLY NEED TO REDO THE 4c VERSIONS
        // ratios for the rates taken from the biofilm_threshold_theory paper
        // Figure 4c ratios:
        // arrays containing the ratios in the format:
        // [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        double[] ratios4c_rImmig_0_55 = new double[]{1.17, 0.55, 0.8, 0.5};
        double[] ratios4c_rImmig_0_6  = new double[]{1.17, 0.6,  0.8, 0.5};
        double[] ratios4c_rImmig_0_65 = new double[]{1.17, 0.65, 0.8, 0.5};

        int nBlocks = 5;
        // TODO BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nBlocks, ratios4c_rImmig_0_55);
        // TODO BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_6",  nCores, nBlocks, ratios4c_rImmig_0_6);
        // TODO BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_65", nCores, nBlocks, ratios4c_rImmig_0_65);

        // int nBlocks = 5;
        // TODO - Supplementary Figure 4 (the three figures about increasing K from 1000 -> 10,000)
        // TODO - HAVE ALREADY DONE K = 10,000, duration = 100 IN THE ABOVE rImm = 0.55 SIMULATION
        // TODO - NEED TO CHANGE PARAMS IN replicateFigure4Solo AND CHANGE FILENAMES (CHECK bf_thresh_theory_plotter.ipynb)
        // TODO - K = 1000, duration = 100
        // TODO BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nBlocks, ratios4c_rImmig_0_55);
        // TODO - K = 10,000, duration = 10,000
        // TODO BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nBlocks, ratios4c_rImmig_0_55);

        // TODO - Supplementary figure 5 (the 4 figures about different growth dynamics)
        // TODO - have already done r_imm = 0.55 & 0.6 in the above simulations
        // [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        double[] ratios4c_rImmig_0_7 = new double[]{1.17, 0.7, 0.8, 0.5};
        double[] ratios4c_rImmig_0_8 = new double[]{1.17, 0.8, 0.8, 0.5};

//        BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_55", nCores, nBlocks, ratios4c_rImmig_0_55);




    }
}
