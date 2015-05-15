package uk.co.downthewire.jLTE.simulator.results;

import org.apache.commons.configuration.Configuration;

public class SimulationResults {

//    public final double percentileTput;
//    public final double avergeTput;
//    public final double maxUETput;
//    public final Configuration configuration;
    public final double percentileULTput;
    public final double percentileDLTput;
    public final double avergeULTput;
    public final double avergeDLTput;
    public final double maxUEULTput;
    public final double maxUEDLTput;
    public final Configuration configuration;

    public static SimulationResults forTesting(double ulPercentile, double dlPercentile,
                                               double ulAverage, double dlAverage,
                                               double ulMax, double dlMax) {
        //return new SimulationResults(percentile, average, max, null);
        return new SimulationResults(ulPercentile, dlPercentile, ulAverage, dlAverage, ulMax, dlMax, null);
    }

    public SimulationResults(double ulPercentile, double dlPercentile,
                             double ulAverage, double dlAverage,
                             double ulMax, double dlMax,
                             Configuration config) {
//        this.percentileTput = percentile;
//        this.avergeTput = average;
//        this.maxUETput = max;
//        this.configuration = config;
        this.percentileULTput = ulPercentile;
        this.percentileDLTput = dlPercentile;
        this.avergeULTput = ulAverage;
        this.avergeDLTput = dlAverage;
        this.maxUEULTput = ulMax;
        this.maxUEDLTput = dlMax;
        this.configuration = config;
    }

    public SimulationResults(PerformanceStats stats) {
//        this.percentileTput = stats.get5thPercentileTput();
//        this.avergeTput = stats.getAverageUETput();
//        this.maxUETput = stats.getMaxUETput();
//        this.configuration = stats.getConfiguration();
        this.percentileULTput = stats.get5thPercentileULTput();
        this.percentileDLTput = stats.get5thPercentileDLTput();
        this.avergeULTput = stats.getAverageUEULTput();
        this.avergeDLTput = stats.getAverageUEDLTput();
        this.maxUEULTput = stats.getMaxUEULTput();
        this.maxUEDLTput = stats.getMaxUEDLTput();
        this.configuration = stats.getConfiguration();
    }

    @Override
    public String toString() {
        return "avgULTput:" + avergeULTput + "," + "avgDLTput:" +avergeDLTput + "," +
                "percentileULTput:" + percentileULTput + "," + "percentileDLTput:" + percentileDLTput;
    }
}
