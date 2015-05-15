package uk.co.downthewire.jLTE.simulator.results;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.downthewire.jLTE.simulator.Simulator;
import uk.co.downthewire.jLTE.simulator.sectors.AbstractSector;
import uk.co.downthewire.jLTE.simulator.ue.UE;
import uk.co.downthewire.jLTE.simulator.utils.FieldNames;
import uk.co.downthewire.jLTE.stats.Accumulator;
import uk.co.downthewire.jLTE.stats.Counter;

import java.text.DecimalFormat;
import java.util.List;

import static uk.co.downthewire.jLTE.simulator.Predicates.*;

public class PerformanceStats {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.######");

    private final List<UE> ues;
    private final List<AbstractSector> sectors;
    private final Configuration config;

    //private Accumulator<UE> tputAll;
    private Accumulator<UE> ulTputAll;
    private Accumulator<UE> dlTputAll;
    //private Accumulator<UE> tputEdge;
    private Accumulator<UE> ulTputEdge;
    private Accumulator<UE> dlTputEdge;
    //private Accumulator<UE> tputCentral;
    private Accumulator<UE> ulTputCentral;
    private Accumulator<UE> dlTputCentral;
    //private Accumulator<UE> sinrAll;
    private Accumulator<UE> ulSinrAll;
    private Accumulator<UE> dlSinrAll;
    //private Accumulator<UE> sinrEdge;
    private Accumulator<UE> ulSinrEdge;
    private Accumulator<UE> dlSinrEdge;
    //private Accumulator<UE> sinrCentral;
    private Accumulator<UE> ulSinrCentral;
    private Accumulator<UE> dlSinrCentral;
    private Counter<UE> uesNeverScheduled;
    private Counter<UE> uesNeverScheduldeButHadData;
    private Counter<UE> numEdgeUEs;
    private Counter<UE> numCentralUEs;
    //private Accumulator<UE> numRBsServed;
    private Accumulator<UE> numULRBsServed;
    private Accumulator<UE> numDLRBsServed;
    //private Accumulator<UE> numRBsQueued;
    private Accumulator<UE> numULRBsQueued;
    private Accumulator<UE> numDLRBsQueued;

    //private Accumulator<AbstractSector> sectorTput;
    private Accumulator<AbstractSector> sectorULTput;
    private Accumulator<AbstractSector> sectorDLTput;
    private Accumulator<AbstractSector> sectorServedUEs;
    private Accumulator<AbstractSector> sectorAvgLoad;  // TODO: should we distinguished?
    private Accumulator<AbstractSector> sectorEdgeUEs;
    private Accumulator<AbstractSector> sectorRBsBlocked;
    private Accumulator<AbstractSector> sectorTotalRBsBlocked;

    private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);

    public PerformanceStats(List<UE> ues, List<AbstractSector> sectors, Configuration config) {
        this.ues = ues;
        this.sectors = sectors;
        this.config = config;
    }

    public void calculateStats() {

        //tputAll = new Accumulator<>(UE_ALWAYS_TRUE, GET_AVERAGE_UE_TPUT);
        ulTputAll = new Accumulator<>(UE_ALWAYS_TRUE, GET_AVERAGE_UE_UL_TPUT);
        dlTputAll = new Accumulator<>(UE_ALWAYS_TRUE, GET_AVERAGE_UE_DL_TPUT);
        //tputEdge = new Accumulator<>(IS_EDGE_UE, GET_AVERAGE_UE_TPUT);
        ulTputEdge = new Accumulator<>(IS_EDGE_UE, GET_AVERAGE_UE_UL_TPUT);
        dlTputEdge = new Accumulator<>(IS_EDGE_UE, GET_AVERAGE_UE_DL_TPUT);
        //tputCentral = new Accumulator<>(IS_CENTRAL_UE, GET_AVERAGE_UE_TPUT);
        ulTputCentral = new Accumulator<>(IS_CENTRAL_UE, GET_AVERAGE_UE_UL_TPUT);
        dlTputCentral = new Accumulator<>(IS_CENTRAL_UE, GET_AVERAGE_UE_DL_TPUT);


        //sinrAll = new Accumulator<>(UE_ALWAYS_TRUE, GET_AVERAGE_UE_SINR);
        ulSinrAll = new Accumulator<>(UE_ALWAYS_TRUE, GET_AVERAGE_UE_UL_SINR);
        dlSinrAll = new Accumulator<>(UE_ALWAYS_TRUE, GET_AVERAGE_UE_DL_SINR);
        //sinrEdge = new Accumulator<>(IS_EDGE_UE, GET_AVERAGE_UE_SINR);
        ulSinrEdge = new Accumulator<>(IS_EDGE_UE, GET_AVERAGE_UE_UL_SINR);
        dlSinrEdge = new Accumulator<>(IS_EDGE_UE, GET_AVERAGE_UE_DL_SINR);
        //sinrCentral = new Accumulator<>(IS_CENTRAL_UE, GET_AVERAGE_UE_SINR);
        ulSinrCentral = new Accumulator<>(IS_CENTRAL_UE, GET_AVERAGE_UE_UL_SINR);
        dlSinrCentral = new Accumulator<>(IS_CENTRAL_UE, GET_AVERAGE_UE_DL_SINR);

        uesNeverScheduled = new Counter<>(HAS_NEVER_BEEN_SCHEDULED);
        uesNeverScheduldeButHadData = new Counter<>(HAS_NEVER_BEEN_SCHEDULED_AND_HAD_DATA);

        numEdgeUEs = new Counter<>(IS_EDGE_UE);
        numCentralUEs = new Counter<>(IS_CENTRAL_UE);

        //numRBsServed = new Accumulator<>(UE_ALWAYS_TRUE, UE_NUM_RBS_SERVED);
        numULRBsServed = new Accumulator<>(UE_ALWAYS_TRUE, UE_NUM_UL_RBS_SERVED);
        numDLRBsServed = new Accumulator<>(UE_ALWAYS_TRUE, UE_NUM_DL_RBS_SERVED);
        //numRBsQueued = new Accumulator<>(UE_ALWAYS_TRUE, UE_NUM_RBS_QUEUED);
        numULRBsQueued = new Accumulator<>(UE_ALWAYS_TRUE, UE_NUM_UL_RBS_QUEUED);
        numDLRBsQueued = new Accumulator<>(UE_ALWAYS_TRUE, UE_NUM_DL_RBS_QUEUED);

        for (final UE ue : ues) {
            //tputAll.accumulate(ue);
            ulTputAll.accumulate(ue);
            dlTputAll.accumulate(ue);
            //tputEdge.accumulate(ue);
            ulTputEdge.accumulate(ue);
            dlTputEdge.accumulate(ue);
            //tputCentral.accumulate(ue);
            ulTputCentral.accumulate(ue);
            dlTputCentral.accumulate(ue);

            //sinrAll.accumulate(ue);
            ulSinrAll.accumulate(ue);
            dlSinrAll.accumulate(ue);
            //sinrEdge.accumulate(ue);
            ulSinrEdge.accumulate(ue);
            dlSinrEdge.accumulate(ue);
            //sinrCentral.accumulate(ue);
            ulSinrCentral.accumulate(ue);
            dlSinrCentral.accumulate(ue);

            uesNeverScheduled.accumulate(ue);
            uesNeverScheduldeButHadData.accumulate(ue);

            numEdgeUEs.accumulate(ue);
            numCentralUEs.accumulate(ue);

            //numRBsQueued.accumulate(ue);
            numULRBsQueued.accumulate(ue);
            numDLRBsQueued.accumulate(ue);
            //numRBsServed.accumulate(ue);
            numULRBsServed.accumulate(ue);
            numDLRBsServed.accumulate(ue);
        }

        //sectorTput = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_TPUT);
        sectorULTput = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_UL_TPUT);
        sectorDLTput = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_DL_TPUT);
        sectorServedUEs = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_UES_SERVED);
        sectorAvgLoad = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_AVERAGE_LOAD);
        sectorEdgeUEs = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_EDGE_UES_SERVED);
        sectorRBsBlocked = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_AVERAGE_RBS_BLOCKED);
        sectorTotalRBsBlocked = new Accumulator<>(SECTOR_ALWAYS_TRUE, SECTOR_TOTAL_RBS_BLOCKED);

        for (AbstractSector sector : sectors) {
            //sectorTput.accumulate(sector);
            sectorULTput.accumulate(sector);
            sectorDLTput.accumulate(sector);
            sectorServedUEs.accumulate(sector);
            sectorAvgLoad.accumulate(sector);
            sectorEdgeUEs.accumulate(sector);
            sectorRBsBlocked.accumulate(sector);
            sectorTotalRBsBlocked.accumulate(sector);
        }

    }

    @SuppressWarnings("boxing")
    public void logStats() {

        LOG.error("General user stats");
        LOG.error("\tNum Edge UEs: {}", numEdgeUEs.getCount());
        LOG.error("\tNum Center UEs: {}", numCentralUEs.getCount());

        //LOG.error("\tTotal RBs served: {}", numRBsServed.getTotal());
        LOG.error("\tTotal UL RBs served: {}", numULRBsServed.getTotal());
        LOG.error("\tTotal DL RBs served: {}", numDLRBsServed.getTotal());
        //LOG.error("\tTotal RBs queued: {}", numRBsQueued.getTotal());
        LOG.error("\tTotal UL RBs queued: {}", numULRBsQueued.getTotal());
        LOG.error("\tTotal DL RBs queued: {}", numDLRBsQueued.getTotal());

        LOG.error("Scheduling stats");
        //LOG.error("\tAverage times UEs scheduled: {}", numRBsServed.getAverage());
        LOG.error("\tAverage times UL UEs scheduled: {}", numULRBsServed.getAverage());
        LOG.error("\tAverage times DL UEs scheduled: {}", numDLRBsServed.getAverage());
        //LOG.error("\tMax times scheduled: {}", numRBsServed.getMax());
        LOG.error("\tMax times UL scheduled: {}", numULRBsServed.getMax());
        LOG.error("\tMax times DL scheduled: {}", numDLRBsServed.getMax());
        //LOG.error("\tMin times scheduled: {}", numRBsServed.getMin());
        LOG.error("\tMin times UL scheduled: {}", numULRBsServed.getMin());
        LOG.error("\tMin times DL scheduled: {}", numDLRBsServed.getMin());
        LOG.error("\tNum UEs never scheduled: {}", uesNeverScheduled.getCount());
        LOG.error("\tNum UEs never scheduled but had data: {}", uesNeverScheduldeButHadData.getCount());

        LOG.error("SINR stats for All UEs");
        //LOG.error("\tAverage   (SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(sinrAll.getAverage()), DECIMAL_FORMAT.format(10 * Math.log10(sinrAll.getAverage())));
        LOG.error("\tAverage   (UL-SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(ulSinrAll.getAverage()),
                DECIMAL_FORMAT.format(10 * Math.log10(ulSinrAll.getAverage())));
        LOG.error("\tAverage   (DL-SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(dlSinrAll.getAverage()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrAll.getAverage())));
        //LOG.error("\tMax       (SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(sinrAll.getMax()), DECIMAL_FORMAT.format(10 * Math.log10(sinrAll.getMax())));
        LOG.error("\tMax       (UL-SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(ulSinrAll.getMax()),
                DECIMAL_FORMAT.format(10 * Math.log10(ulSinrAll.getMax())));
        LOG.error("\tMax       (DL-SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(dlSinrAll.getMax()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrAll.getMax())));
        //LOG.error("\tMin       (SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(sinrAll.getMin()), DECIMAL_FORMAT.format(10 * Math.log10(sinrAll.getMin())));
        LOG.error("\tMin       (UL-SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(ulSinrAll.getMin()),
                DECIMAL_FORMAT.format(10 * Math.log10(ulSinrAll.getMin())));
        LOG.error("\tMin       (DL-SINR-all) = {} ({} dB)", DECIMAL_FORMAT.format(dlSinrAll.getMin()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrAll.getMin())));

        LOG.error("SINR stats for Edge UEs:");
        //LOG.error("\tAverage  (SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(sinrEdge.getAverage()), DECIMAL_FORMAT.format(10 * Math.log10(sinrEdge.getAverage())));
        LOG.error("\tAverage  (UL-SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(ulSinrEdge.getAverage()),
                DECIMAL_FORMAT.format(10 * Math.log10(ulSinrEdge.getAverage())));
        LOG.error("\tAverage  (DL-SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(dlSinrEdge.getAverage()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrEdge.getAverage())));
        //LOG.error("\tMax      (SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(sinrEdge.getMax()), DECIMAL_FORMAT.format(10 * Math.log10(sinrEdge.getMax())));
        LOG.error("\tMax      (UL-SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(ulSinrEdge.getMax()),
                DECIMAL_FORMAT.format(10 * Math.log10(ulSinrEdge.getMax())));
        LOG.error("\tMax      (DL-SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(dlSinrEdge.getMax()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrEdge.getMax())));
        //LOG.error("\tMin      (SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(sinrEdge.getMin()), DECIMAL_FORMAT.format(10 * Math.log10(sinrEdge.getMin())));
        LOG.error("\tMin      (UL-SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(ulSinrEdge.getMin()),
                DECIMAL_FORMAT.format(10 * Math.log10(ulSinrEdge.getMin())));
        LOG.error("\tMin      (DL-SINR-edge) = {} ({} dB)", DECIMAL_FORMAT.format(dlSinrEdge.getMin()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrEdge.getMin())));

        LOG.error("SINR stats for Center UEs:");
        //LOG.error("\tAverage (SINR-center)= {} ({} dB)", DECIMAL_FORMAT.format(sinrCentral.getAverage()), DECIMAL_FORMAT.format(10 * Math.log10(sinrCentral.getAverage())));
        LOG.error("\tAverage (UL-SINR-center)= {} ({} dB)", DECIMAL_FORMAT.format(ulSinrCentral.getAverage()),
                DECIMAL_FORMAT.format(10 * Math.log10(ulSinrCentral.getAverage())));
        LOG.error("\tAverage (DL-SINR-center)= {} ({} dB)", DECIMAL_FORMAT.format(dlSinrCentral.getAverage()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrCentral.getAverage())));
        //LOG.error("\tMax     (SINR-center)= {} ({} dB)", DECIMAL_FORMAT.format(sinrCentral.getMax()), DECIMAL_FORMAT.format(10 * Math.log10(sinrCentral.getMax())));
        LOG.error("\tMax     (UL-SINR-center)= {} ({} dB)", DECIMAL_FORMAT.format(ulSinrCentral.getMax()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrCentral.getMax())));
        //LOG.error("\tMin     (SINR-center)= {} ({} dB)", DECIMAL_FORMAT.format(sinrCentral.getMin()), DECIMAL_FORMAT.format(10 * Math.log10(sinrCentral.getMin())));
        LOG.error("\tMin     (DL-SINR-center)= {} ({} dB)", DECIMAL_FORMAT.format(dlSinrCentral.getMin()),
                DECIMAL_FORMAT.format(10 * Math.log10(dlSinrCentral.getMin())));

        LOG.error("Tput stats for All UEs");
        //LOG.error("\tAverage   (all) = {} Mbps", DECIMAL_FORMAT.format(tputAll.getAverage()));
        LOG.error("\tAverage   (UL-all) = {} Mbps", DECIMAL_FORMAT.format(ulTputAll.getAverage()));
        LOG.error("\tAverage   (DL-all) = {} Mbps", DECIMAL_FORMAT.format(dlTputAll.getAverage()));
        //LOG.error("\tMax       (all) = {} Mbps", DECIMAL_FORMAT.format(tputAll.getMax()));
        LOG.error("\tMax       (UL-all) = {} Mbps", DECIMAL_FORMAT.format(ulTputAll.getMax()));
        LOG.error("\tMax       (DL-all) = {} Mbps", DECIMAL_FORMAT.format(dlTputAll.getMax()));
        //LOG.error("\tMin       (all) = {} Mbps", DECIMAL_FORMAT.format(tputAll.getMin()));
        LOG.error("\tMin       (UL-all) = {} Mbps", DECIMAL_FORMAT.format(ulTputAll.getMin()));
        LOG.error("\tMin       (DL-all) = {} Mbps", DECIMAL_FORMAT.format(dlTputAll.getMin()));

        LOG.error("Tput stats for Edge UEs");
        //LOG.error("\tAverage  (edge) = {} Mbps", DECIMAL_FORMAT.format(tputEdge.getAverage()));
        LOG.error("\tAverage  (UL-edge) = {} Mbps", DECIMAL_FORMAT.format(ulTputEdge.getAverage()));
        LOG.error("\tAverage  (DL-edge) = {} Mbps", DECIMAL_FORMAT.format(dlTputEdge.getAverage()));
        //LOG.error("\tMax      (edge) = {} Mbps", DECIMAL_FORMAT.format(tputEdge.getMax()));
        LOG.error("\tMax      (UL-edge) = {} Mbps", DECIMAL_FORMAT.format(ulTputEdge.getMax()));
        LOG.error("\tMax      (DL-edge) = {} Mbps", DECIMAL_FORMAT.format(dlTputEdge.getMax()));
        //LOG.error("\tMin      (edge) = {} Mbps", DECIMAL_FORMAT.format(tputEdge.getMin()));
        LOG.error("\tMin      (UL-edge) = {} Mbps", DECIMAL_FORMAT.format(ulTputEdge.getMin()));
        LOG.error("\tMin      (DL-edge) = {} Mbps", DECIMAL_FORMAT.format(dlTputEdge.getMin()));

        LOG.error("Tput stats for Center UEs");
        //LOG.error("\tAverage (center)= {} Mbps", DECIMAL_FORMAT.format(tputCentral.getAverage()));
        LOG.error("\tAverage (UL-center)= {} Mbps", DECIMAL_FORMAT.format(ulTputCentral.getAverage()));
        LOG.error("\tAverage (DL-center)= {} Mbps", DECIMAL_FORMAT.format(dlTputCentral.getAverage()));
        //LOG.error("\tMax     (center)= {} Mbps", DECIMAL_FORMAT.format(tputCentral.getMax()));
        LOG.error("\tMax     (UL-center)= {} Mbps", DECIMAL_FORMAT.format(ulTputCentral.getMax()));
        LOG.error("\tMax     (DL-center)= {} Mbps", DECIMAL_FORMAT.format(dlTputCentral.getMax()));
        //LOG.error("\tMin     (center)= {} Mbps", DECIMAL_FORMAT.format(tputCentral.getMin()));
        LOG.error("\tMin     (UL-center)= {} Mbps", DECIMAL_FORMAT.format(ulTputCentral.getMin()));
        LOG.error("\tMin     (DL-center)= {} Mbps", DECIMAL_FORMAT.format(dlTputCentral.getMin()));

        //LOG.error("5th percentile Tput = {} Mbps", DECIMAL_FORMAT.format(tputAll.get5thPercentileAverage()));
        LOG.error("5th percentile UL-Tput = {} Mbps", DECIMAL_FORMAT.format(ulTputAll.get5thPercentileAverage()));
        LOG.error("5th percentile DL-Tput = {} Mbps", DECIMAL_FORMAT.format(dlTputAll.get5thPercentileAverage()));

        LOG.error("Sector stats:");
        //LOG.error("\tAvg Sector Tput = {} Mbps", DECIMAL_FORMAT.format(sectorTput.getAverage()));
        LOG.error("\tAvg Sector UL-Tput = {} Mbps", DECIMAL_FORMAT.format(sectorULTput.getAverage()));
        LOG.error("\tAvg Sector DL-Tput = {} Mbps", DECIMAL_FORMAT.format(sectorDLTput.getAverage()));
        LOG.error("\tAvg UEs per sector = {}", sectorServedUEs.getAverage());
        LOG.error("\tAvg Edge UEs per sector = {}", sectorEdgeUEs.getAverage());
        LOG.error("\tAvg sector Load = {}", sectorAvgLoad.getAverage());
        if (config.getBoolean(FieldNames.X2_ENABLED)) {
            LOG.error("\tAvg RBs blocked per iteration with X2 = {}", sectorRBsBlocked.getAverage());
            LOG.error("\tTotal RBs blocked with X2 = {}", sectorTotalRBsBlocked.getAverage());
        }
        LOG.error("");
        LOG.error("");
    }

    @SuppressWarnings("boxing")
    public void logReducedStats(int iteration) {
        LOG.error("t = " + iteration);
        //LOG.error("Stats: Average Sector Tput = {} Mbps", sectorTput.getAverage());
        LOG.error("Stats: Average Sector UL-Tput = {} Mbps", sectorULTput.getAverage());
        LOG.error("Stats: Average Sector DL-Tput = {} Mbps", sectorDLTput.getAverage());
        //LOG.error("Stats: Average UE Tput = {} Mbps", tputAll.getAverage());
        LOG.error("Stats: Average UE UL-Tput = {} Mbps", ulTputAll.getAverage());
        LOG.error("Stats: Average UE DL-Tput = {} Mbps", dlTputAll.getAverage());
        //LOG.error("Stats: Worst UE Tput = {} Mbps", tputAll.getMin());
        LOG.error("Stats: Worst UE UL-Tput = {} Mbps", ulTputAll.getMin());
        LOG.error("Stats: Worst UE DL-Tput = {} Mbps", dlTputAll.getMin());
        //LOG.error("Stats: Best UE Tput = {} Mbps", tputAll.getMax());
        LOG.error("Stats: Best UE UL-Tput = {} Mbps", ulTputAll.getMax());
        LOG.error("Stats: Best UE DL-Tput = {} Mbps", dlTputAll.getMax());
        //LOG.error("Stats: 5th Percentile = {} Mbps", tputAll.get5thPercentileAverage());
        LOG.error("Stats: 5th Percentile UL = {} Mbps", ulTputAll.get5thPercentileAverage());
        LOG.error("Stats: 5th Percentile DL = {} Mbps", dlTputAll.get5thPercentileAverage());
    }

//    public double getMaxUETput() {
//        return tputAll.getMax();
//    }
    public double getMaxUEULTput() {
        return ulTputAll.getMax();
    }

    public double getMaxUEDLTput() {
        return dlTputAll.getMax();
    }

//    public double getAverageUETput() {
//        return tputAll.getAverage();
//    }

    public double getAverageUEULTput() {
        return ulTputAll.getAverage();
    }

    public double getAverageUEDLTput() {
        return dlTputAll.getAverage();
    }
//    public double get5thPercentileTput() {
//        return tputAll.get5thPercentileAverage();
//    }

    public double get5thPercentileULTput() {
        return ulTputAll.get5thPercentileAverage();
    }

    public double get5thPercentileDLTput() {
        return dlTputAll.get5thPercentileAverage();
    }

    public Configuration getConfiguration() {
        return config;
    }

}
