import org.apache.commons.math3.distribution.LogNormalDistribution;
import java.util.ArrayList;
import java.util.Random;

class Microhabitat {

    private LogNormalDistribution MIC_distribution;
    Random rand = new Random(); //used for checking species distribution.

    private double c; //concn of antimicrobial
    private ArrayList<Double> population; //list of MICs of bacteria in microhab

    private int K; //karrying kapacity
    private boolean surface = false, biofilm_region, immigration_zone = false;
    private double max_gRate; //max growth rate =  2/day
    //private double uniform_dRate = 0.018; //all bacteria have this death rate (removed here)
    double biofilm_threshold; //fraction occupied needed to transition to biofilm
    double r_mig; //migration rate

    Microhabitat(int K, double max_gRate, double biofilm_threshold, double r_migration){
        this.c = 0;
        this.K = K;
        this.max_gRate = max_gRate;
        this.population = new ArrayList<>(K);
        this.biofilm_threshold = biofilm_threshold;
        this.r_mig = r_migration;
        this.biofilm_region = false;
    }


    int getN(){
        return population.size();
    }

    boolean isSurface(){
        return surface;
    }

    boolean isBiofilm_region(){
        return biofilm_region;
    }

    boolean isImmigration_zone(){
        return immigration_zone;
    }

    ArrayList<Double> getPopulation(){
        return population;
    }

    void setSurface(){
        this.surface = true;
    }

    void setBiofilm_region(){
        this.biofilm_region = true;
    }

    void setImmigration_zone(boolean immigration_zone){
        this.immigration_zone = immigration_zone;
    }


    private double fractionFull(){
        return getN()/(double) K;
    }

    boolean atBiofilmThreshold(){
        return fractionFull() >= biofilm_threshold;
    }

    double migrate_rate(){
        //returns 0.5*b for the microhabitat next to the ship hull, to account for the inability to migrate into the hull
        //also for the microhabitat that's the biofilm edge
        return (surface || immigration_zone) ? 0.5*r_mig : r_mig;
    }

    private double beta(int index){
        return population.get(index);
    }

//    private double phi_c(int index){
//        //pharmacodynamic function
//        double cB = c/beta(index);
//        return 1. - (6.*cB*cB)/(5. + cB*cB);
//    }

    double replicationRate(int index){
        //there's no biocide so we can take out the pharmacodynamic function
        return max_gRate*(1. - getN()/(double)K);
    }


//    double[] replicationAndDeathRates(int index){
//        //returns either the growth rate and the uniform death rate if the bacteria is resistant,
//        //or the sums of the uniform and pharmacodyncamic death rates is the batceria is susceptible
//        double phi_c_scaled = max_gRate*phi_c(index);
//        double gRate = phi_c_scaled > 0. ? phi_c_scaled*(1. - getN()/(double) K) : 0.;
//        double dRate = phi_c_scaled > 0. ? uniform_dRate : phi_c_scaled + uniform_dRate;
//
//        return new double[]{gRate, dRate};
//    }


    void addARandomBacterium_x_N(int n_bacteria){
        //No biocide so just have all the bacteria have the same beta, let's set it to 5
        //UPDATE
        //now we want to check the species distribution for the immigration and deterioration regimes
        //so we'll incorporate a species diversity thing via a uniform random distribution of genotypes.
        //we only want to determine if there's a difference in species composition due to the parameters
        //so the genotypes have no effect on the bacteria's fitness
        for(int i = 0; i < n_bacteria; i++) {
            population.add(rand.nextDouble());
        }
    }

    void replicateABacterium_x_N(int index, int nReps){
        for(int i = 0; i < nReps; i++) {
            population.add(population.get(index));
        }
    }

    void addABacterium(double MIC){
        population.add(MIC);
    }

    void removeABacterium(int index){
        population.remove(index);
    }


}
