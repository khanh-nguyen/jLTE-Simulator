package uk.co.downthewire.jLTE.simulator.results;

import org.apache.commons.configuration.Configuration;

public class AggregatedSimulationResults extends SimulationResults {

//    public final double percentileStdDev;
//    public final double averageTputStdDev;
    public final double ulPercentileStdDev, dlPercentileStdDev;
    public final double averageULTputStdDev, averageDLTputStdDev;

    public AggregatedSimulationResults(double ulPercentile, double dlPercentile,
                                       double ulAverage, double dlAverage,
                                       double ulMax, double dlMax,
                                       Configuration config,
                                       double ulPercentileStdDev, double dlPercentileStdDev,
                                       double averageULTputStdDev, double averageDLTputStdDev
    ) {
        super(ulPercentile, dlPercentile, ulAverage, dlAverage, ulMax, dlMax, config);
//        this.percentileStdDev = percentileStdDev;
//        this.averageTputStdDev = averageTputStdDev;
        this.ulPercentileStdDev = ulPercentileStdDev;
        this.dlPercentileStdDev = dlPercentileStdDev;
        this.averageULTputStdDev = averageULTputStdDev;
        this.averageDLTputStdDev = averageDLTputStdDev;
    }

    public static String header() {
        return "averageTput,averageTput-stddev,percentileTput,percentileTput-sdtdev";
    }

    @Override
    public String toString() {
//        return avergeTput + "," + averageTputStdDev + "," + percentileTput + "," + percentileStdDev;
        return avergeULTput + "," + avergeDLTput + "," + averageULTputStdDev + "," + averageDLTputStdDev + "," +
                percentileULTput + "," + percentileDLTput + "," + ulPercentileStdDev + "," + dlPercentileStdDev;
    }
}
