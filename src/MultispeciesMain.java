public class MultispeciesMain {

    public static void main(String[] args) {

        int nCores = 10; //no. of cores used to run the simulations on the cluster
        int nReps = 5; //no. of simulations run on each core

        //ratios for the rates taken from the biofilm_threshold_theory paper
        //arrays containing the ratios in the format:
        //[threshold_N_ratio, immigration_ratio, migration_ratio, deterioration_ratio]
        //Figure 4c ratios:
        double[] ratios4c1 = new double[]{1.17, 0.7, 0.8, 0.5};
        double[] ratios4c2 = new double[]{1.17, 0.8, 0.8, 0.5};
        double[] ratios4c3 = new double[]{1.17, 0.9, 0.8, 0.5};
        //this one is inteded to approach the lower end of the figure 4c parameter regime
        double[] ratios4cEXTRA = new double[]{1.17, 0.51, 0.8, 0.5};

        //Figure 4d ratios:
        double[] ratios4d1 = new double[]{0.7, 0.4, 0.8, 0.9};
        double[] ratios4d2 = new double[]{0.7, 0.5, 0.8, 0.9};

        //BioSystem.replicateFigure4Solo("ratios4cEXTRA", nCores, nReps, ratios4cEXTRA);
        BioSystem.oneVeryLongSimulation("ratios4cEXTRA", 10, ratios4cEXTRA);
    }



}
