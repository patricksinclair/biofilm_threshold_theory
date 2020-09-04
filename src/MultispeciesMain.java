import javax.print.attribute.standard.NumberOfInterveningJobs;

public class MultispeciesMain {

    public static void main(String[] args) {

        int nCores = Integer.parseInt(args[0]); //no. of cores used to run the simulations on the cluster
        int nReps = 10; //no. of simulations run on each core

        //ratios for the rates taken from the biofilm_threshold_theory paper
        //arrays containing the ratios in the format:
        //[threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //Figure 4c ratios:
        //this one is inteded to approach the lower end of the figure 4c parameter regime
        double[] ratios4c_rImmig_0_51 = new double[]{1.17, 0.51, 0.8, 0.5};
        //do some intermediate values between 0.51 and 0.7
        double[] ratios4c_rImmig_0_55 = new double[]{1.17, 0.55, 0.8, 0.5};
        double[] ratios4c_rImmig_0_6 = new double[]{1.17, 0.6, 0.8, 0.5};
        double[] ratios4c_rImmig_0_65 = new double[]{1.17, 0.65, 0.8, 0.5};
        //these are the ones from the biofilm_threshold_theory fig 4c
        double[] ratios4c_rImmig_0_7 = new double[]{1.17, 0.7, 0.8, 0.5};
        double[] ratios4c_rImmig_0_8 = new double[]{1.17, 0.8, 0.8, 0.5};
        double[] ratios4c_rImmig_0_9 = new double[]{1.17, 0.9, 0.8, 0.5};


        //Figure 4d ratios:
        double[] ratios4d_rImmig_0_4 = new double[]{0.7, 0.4, 0.8, 0.9};
        double[] ratios4d_rImmig_0_5 = new double[]{0.7, 0.5, 0.8, 0.9};

        //Time to nth microhab params
        int microhab_lim = 1; //this is the microhab index we're measuring the time to reach
        //values are results directory name, immigration ratio, migration ratio, K
        Object[] phase_diag_params = new Object[]{"timeTo1Microhab_phaseDiagram_bigK", 0.8, 0.8, 10000};

        //BioSystem.replicateFigure4Solo("ratios4c_rImmig_0_6", nCores, nReps, ratios4c_rImmig_0_6);
        //BioSystem.oneVeryLongSimulation("ratios4c_rImmig_0_55", 10, ratios4c_rImmig_0_55);
        BioSystem.stochasticWaitingTime("ratios4c_rImmig_0_55", nCores, nReps, ratios4c_rImmig_0_55);
        //BioSystem.timeToNthMicrohabPhaseDiagram(phase_diag_params, nCores, microhab_lim);
    }
}
