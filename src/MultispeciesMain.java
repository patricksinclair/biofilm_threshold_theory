import javax.print.attribute.standard.NumberOfInterveningJobs;

public class MultispeciesMain {

    public static void main(String[] args) {

        //todo - changed nreps from 10 to 5 for the Allen comparison sims
        int nCores = Integer.parseInt(args[0]); //no. of cores used to run the simulations on the cluster
        int nReps = 5; //no. of simulations run on each core




        //params used in the species composition simulations
        //want to have N* < K for both of these and keep the immigration rate fixed, vary deterioration rate to change
        //what regime we are in
        //values are results_directory_name, file_ID, threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio, K
        //increased file formatting to avoid mistakenly double counting genotypes
        Object[] ms_phase2_params = new Object[]{"speciesComp-phase2-fixedImm-bigK-mostPrecise", 0.7, 0.5, 0.8, 0.25, 10000}; //immigration dominated r_im/Kr_det > 1
        Object[] ms_phase4_params = new Object[]{"speciesComp-phase4-fixedImm-bigK-mostPrecise", 0.7, 0.5, 0.8, 1., 10000}; //deterioration dominated r_im/Kr_det < 1

        BioSystem.speciesComposition(nCores, nReps, ms_phase2_params);
//
//        //The ratios in this section are used for a more fair comparison between the effects of N*, very similar to the ones used for the stocastic
//        //versions of figures 4c and d.  The difference being that the two parameter sets now only differ in their values of
//        //N*, and the deterioration rate is the same for both.  We'll also just do 50 reps instead of 100 for these.
//        //we'll run the N* = 1.17 runs in the existing fig4c folders, and the 0.7 in the fig4d ones.  Can revert them back later if needs be
//        //N* = 1.17 parameters [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
//        //todo - changed the replicateFigure4solo method so things are saved in a different directory
//        double[] ratios4c_rImmig_0_4 = new double[]{1.17, 0.4, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_5 = new double[]{1.17, 0.5, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_6 = new double[]{1.17, 0.6, 0.8, 0.5};
//        double[] ratios4c_rImmig_0_7 = new double[]{1.17, 0.7, 0.8, 0.5};
//        //N* = 0.7 parameters [threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
//        double[] ratios4d_rImmig_0_4 = new double[]{0.7, 0.4, 0.8, 0.5};
//        double[] ratios4d_rImmig_0_5 = new double[]{0.7, 0.5, 0.8, 0.5};
//        double[] ratios4d_rImmig_0_6 = new double[]{0.7, 0.6, 0.8, 0.5};
//        double[] ratios4d_rImmig_0_7 = new double[]{0.7, 0.7, 0.8, 0.5};

        //for further comparison on the effects of N*, now doing a third intermidiate value of N* = 0.9
        //just for "ease" of bookkeeping, this parameter set will be referred to as fig4e_ratios
//        double[] ratios4e_rImmig_0_4 = new double[]{0.9, 0.4, 0.8, 0.5};
//        double[] ratios4e_rImmig_0_5 = new double[]{0.9, 0.5, 0.8, 0.5};
//        double[] ratios4e_rImmig_0_6 = new double[]{0.9, 0.6, 0.8, 0.5};
//        double[] ratios4e_rImmig_0_7 = new double[]{0.9, 0.7, 0.8, 0.5};
//
//        BioSystem.replicateFigure4Solo("ratios4e_rImmig_0_4", nCores, nReps, ratios4e_rImmig_0_4);



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
