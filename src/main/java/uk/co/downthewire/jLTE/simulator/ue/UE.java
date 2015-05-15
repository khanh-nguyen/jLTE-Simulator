package uk.co.downthewire.jLTE.simulator.ue;

import flanagan.math.PsRandom;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import uk.co.downthewire.jLTE.simulator.Location;
import uk.co.downthewire.jLTE.simulator.UESectorTuple;
import uk.co.downthewire.jLTE.simulator.rbs.ResourceBlock;
import uk.co.downthewire.jLTE.simulator.sectors.AbstractSector;
import uk.co.downthewire.jLTE.simulator.traffic.TrafficGenerator;
import uk.co.downthewire.jLTE.simulator.traffic.TrafficType;
import uk.co.downthewire.jLTE.simulator.utils.FieldNames;
import uk.co.downthewire.jLTE.simulator.utils.Utils;
import uk.co.downthewire.jLTE.simulator.utils.XMLUtils;
import uk.co.downthewire.jLTE.stats.SimpleCounter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UE {

    private final double noise;
    private static final Logger LOG = LoggerFactory.getLogger("Sim_" + Thread.currentThread().getId());

    public static UE fromXML(Configuration config, TrafficGenerator trafficGenerator, final Node xml) {
        int id = Integer.parseInt(xml.getAttributes().getNamedItem("id").getNodeValue());
        Location loc = XMLUtils.parseLocation(xml);
        return new UE(config, id, loc, trafficGenerator);
    }

    public final int id;
    private final double SHADOWING;
    public final Location location;
    public final double txPower;

    // NOTE: See 3GPP 36.942 for these definitons
    private static final double MIN_SINR = -15;
    private static final double BANDWIDTH_PER_RB = 180000;
    private static final double BPS_TO_MBPS = 1024 * 1024;

    private final Configuration config;
    private final TrafficGenerator trafficGenerator;
    private final TrafficType trafficType;
    public UESectorTuple servingTuple;

    public final List<UESectorTuple> sectorTuples;

    //private final List<ResourceBlock> scheduledRBs;
    private final List<ResourceBlock> scheduledULRBs;
    private final List<ResourceBlock> scheduledDLRBs;

    // private int currentRBsQueued;
    private int currentULRBsQueued;
    private int currentDLRBsQueued;

    private final SimpleCounter totalULDatarates;
    private final SimpleCounter totalDLDatarates;
    private final SimpleCounter totalULSinr;
    private final SimpleCounter totalDLSinr;

    // private final SimpleCounter totalRBsServed;
    private final SimpleCounter ulRBsServed;
    private final SimpleCounter dlRBsServed;

    //private final SimpleCounter totalRBsQueued;
    private final SimpleCounter ulRBsQueued;
    private final SimpleCounter dlRBsQueued;

    //private final SimpleCounter[] signalPerRB;
    private final SimpleCounter[] signalPerULRB;
    private final SimpleCounter[] signalPerDLRB;

    private boolean isEdge;

    /**
     * Create a UE at a random location.
     */
    public UE(Configuration configuration, final int Id, int numeNodeBs,
              List<AbstractSector> sectors, TrafficGenerator trafficGenerator) {
        this(configuration, Id, Location.generateRandomLocation(configuration, numeNodeBs, sectors), trafficGenerator);
    }

    public UE(Configuration configuration, final int id, Location location, TrafficGenerator trafficGenerator) {
        this.config = configuration;
        this.trafficGenerator = trafficGenerator;
        this.id = id;
        this.location = location;
        this.noise = Math.pow(10, 0.1 * config.getDouble(FieldNames.NIQUIST_NOISE_PER_RB)) / 1000;
        this.txPower = configuration.getDouble(FieldNames.UE_TX_POWER);

        this.sectorTuples = new ArrayList<>();

        //this.totalRBsServed = new SimpleCounter();
        this.ulRBsServed = new SimpleCounter();
        this.dlRBsServed = new SimpleCounter();
        //this.totalRBsQueued = new SimpleCounter();
        this.ulRBsQueued = new SimpleCounter();
        this.dlRBsQueued = new SimpleCounter();
        this.totalULDatarates = new SimpleCounter();
        this.totalDLDatarates = new SimpleCounter();
        this.totalULSinr = new SimpleCounter();
        this.totalDLSinr = new SimpleCounter();

        //signalPerRB = new SimpleCounter[config.getInt(FieldNames.RBS_PER_SECTOR)];
        signalPerULRB = new SimpleCounter[config.getInt(FieldNames.RBS_PER_SECTOR)];
        signalPerDLRB = new SimpleCounter[config.getInt(FieldNames.RBS_PER_SECTOR)];
        for (int RB = 0; RB < config.getInt(FieldNames.RBS_PER_SECTOR); RB++) {
            signalPerULRB[RB] = new SimpleCounter();
            signalPerDLRB[RB] = new SimpleCounter();
        }

        //this.scheduledRBs = new ArrayList<>();
        this.scheduledULRBs = new ArrayList<>();
        this.scheduledDLRBs = new ArrayList<>();

        PsRandom shadowRandom = (PsRandom) config.getProperty(FieldNames.RANDOM_SHADOWING);
        this.SHADOWING = shadowRandom.nextGaussian(0, 8);

        PsRandom generalRandom = (PsRandom) config.getProperty(FieldNames.RANDOM_GENERAL);
        this.trafficType = trafficGenerator.getTrafficType(generalRandom.nextDouble());

        this.isEdge = false;
    }

    public void resetScheduledStatus() {
        //scheduledRBs.clear();
        this.scheduledULRBs.clear();
        this.scheduledDLRBs.clear();
    }

    /**
     * Update the DL/UL queue for the UE.
     */
    public void generateTraffic(double random) {
        int numRbs = trafficGenerator.generateTraffic(trafficType, random);
        // decide if demand traffic is UL or DL
        if (random <= config.getDouble(FieldNames.TRAFFIC_UPLINK_PROB)) {
            // accumulate UL queue
            ulRBsQueued.accumulate(numRbs);
            currentULRBsQueued += numRbs;
        }
        else {
            // accumulate DL queue
            dlRBsQueued.accumulate(numRbs);
            currentDLRBsQueued += numRbs;
        }
    }

    public void schedule(ResourceBlock RB, boolean isDL) {
        //scheduledRBs.add(RB);
        if (isDL) {
            // dlRBsQueued.accumulate(-1.0);
            currentDLRBsQueued--;
            dlRBsServed.accumulate(1.0);
            scheduledDLRBs.add(RB);
        }
        else {
            // ulRBsQueued.accumulate(-1.0);
            currentULRBsQueued--;
            ulRBsServed.accumulate(1.0);
            scheduledULRBs.add(RB);
        }
//        currentRBsQueued -= 1;
//        totalRBsServed.accumulate(1.0);
    }

    @SuppressWarnings("boxing")
    // calculate the signal first
    public void calculateSignalAcrossAllRBs() {
        for (ResourceBlock RB : servingTuple.sector.getResourceBlocks()) {
            double ulSignal = calculateSignal(RB, false);
            double dlSignal = calculateSignal(RB, true);
            LOG.trace("calculateSignalAcrossAllRBs - UL: UE[{}], RB[{}], signal={}", id, RB.id, ulSignal);
            LOG.trace("calculateSignalAcrossAllRBs - DL: UE[{}], RB[{}], signal={}", id, RB.id, dlSignal);
        }
    }

    public double getSignalOnRB(int RB, boolean isDL) {
        return isDL ? signalPerDLRB[RB].getLastSample() : signalPerULRB[RB].getLastSample();
    }

    public double getRelativeSignalOnRB(int RB, boolean isDL) {
        return isDL ? signalPerDLRB[RB].getLastSample() / signalPerDLRB[RB].getAverage()
                    : signalPerULRB[RB].getLastSample() / signalPerULRB[RB].getAverage();
    }

    public double getAverageSinr(boolean isDL) {
        return isDL ? totalDLSinr.getAverage() : totalULSinr.getAverage();
    }

    @SuppressWarnings("boxing")
    // check all RBs that has been scheduled for UL and DL
    // for each RB, accumulate its datarate and sinr
    // FIXME: where are we going to use this info? in statistic?
    public void accumulateDatarate(int subframe) {
        double accumulatedULDatarate = 0.0, accumulatedDLDatarate = 0.0;
        double accumulatedULSinr = 0.0, accumulatedDLSinr = 0.0;
        // calculate the signal first
        for (ResourceBlock RB : scheduledULRBs) {
            double signal = signalPerULRB[RB.id].getLastSample();
            double interference = calculateInterference(RB, subframe); // TODO: is it ok to use the same formula for both UL, DL?
            double sinr = calcSinr(signal, interference);
            double datarate = calculateMbpsFromSinr(sinr);

            accumulatedULDatarate += datarate;
            accumulatedULSinr += sinr / config.getInt(FieldNames.RBS_PER_SECTOR);

            RB.accumulateULDataRate(datarate);
            RB.accumulateULSinr(sinr);

            LOG.trace("calculateULDatarate: UE[{}], RB[{}], signal={}, interference={}, sinr={}, datarate={}",
                    id, RB.id, signal, interference, sinr, datarate);
        }

        for (ResourceBlock RB : scheduledDLRBs) {
            double signal = signalPerDLRB[RB.id].getLastSample();
            double interference = calculateInterference(RB, subframe); // TODO: is it ok to use the same formula for both UL, DL?
            double sinr = calcSinr(signal, interference);
            double datarate = calculateMbpsFromSinr(sinr);

            accumulatedDLDatarate += datarate;
            accumulatedDLSinr += sinr / config.getInt(FieldNames.RBS_PER_SECTOR);

            RB.accumulateDLDataRate(datarate);
            RB.accumulateDLSinr(sinr);

            LOG.trace("calculateDLDatarate: UE[{}], RB[{}], signal={}, interference={}, sinr={}, datarate={}",
                    id, RB.id, signal, interference, sinr, datarate);
        }

        totalULDatarates.accumulate(accumulatedULDatarate);
        totalULSinr.accumulate(accumulatedULSinr);
        totalDLDatarates.accumulate(accumulatedDLDatarate);
        totalDLSinr.accumulate(accumulatedDLSinr);
    }

    private double calculateSignal(ResourceBlock RB, boolean isDL) {
        if (isDL) {
            double downlinkPower = servingTuple.getDownlinkPower(RB);
            double scheduledPowerFactor = servingTuple.sector.getScheduledPowerFactor(RB);
            double rxSignal = downlinkPower * scheduledPowerFactor;
            signalPerDLRB[RB.id].accumulate(rxSignal);
            return rxSignal;
        }
        double uplinkPower = servingTuple.getUplinkPower(RB);
        double scheduledPowerFactor = servingTuple.sector.getScheduledPowerFactor(RB);
        double rxSignal = uplinkPower * scheduledPowerFactor;
        signalPerULRB[RB.id].accumulate(rxSignal);
        return rxSignal;
    }

    private double calculateInterference(ResourceBlock RB, int subframe) {
        double totalLinearInterference = 0.0;
        for (final UESectorTuple tuple : sectorTuples) {
            if (tuple.sector.id == servingTuple.sector.id &&
                tuple.sector.servingENodeBId == servingTuple.sector.servingENodeBId) {
                continue;
            }
            if (tuple.sector.isRBScheduled(RB)) {
                //double interference = tuple.getDownlinkPower(RB) * tuple.sector.getScheduledPowerFactor(RB);
                // NOTE: tuple must know if RB was scheduled for UL or DL !!!
                double interference = tuple.getPower(RB, subframe) * tuple.sector.getScheduledPowerFactor(RB);
                totalLinearInterference += interference;
            }
        }
        return totalLinearInterference;
    }

    private double calcSinr(double signal, double interference) {
        return signal / (noise + interference);
    }

    /**
     * Given the SINR (in dB) what would the datarate be (in Mbps).
     * This estimates the maximum datarate based on the most applicable Modulation and Coding Scheme (MCS).
     * See 3GPP 36.942: section A.1
     */
    private static double calculateMbpsFromSinr(double sinr) {
        double bps_per_hz = sinr > MIN_SINR ? Utils.log2(1 + sinr) * UETypeInfo.ATTENUATION_FACTOR : 0;
        if (bps_per_hz > 4.4) {
            bps_per_hz = 4.4;
        }
        return bps_per_hz * BANDWIDTH_PER_RB / BPS_TO_MBPS;
    }

    public double calculateAverageULDatarate() {
        return totalULDatarates.getAverage();
    }

    public double calculateAverageDLDatarate() {
        return totalDLDatarates.getAverage();
    }

    public double calculateAverageULSinr() {
        return totalULSinr.getAverage();
    }

    public double calculateAverageDLSinr() {
        return totalDLSinr.getAverage();
    }

    public void setEdge(boolean edge) {
        isEdge = edge;
    }

    public boolean isEdge() {
        return isEdge;
    }

    public int getCurrentRBsQueued(boolean isDL) {
        return isDL ? (int) dlRBsQueued.getCount() : (int) ulRBsQueued.getCount();
    }

//    public int getCurrentRBsQueued() {
//        return currentRBsQueued;
//    }

    // TODO: should we distinguish UL served from DL served?
    public int getTotalNumDLRBsServed() {
        return (int) dlRBsServed.getCount();
    }

    public int getTotalNumULRBsServed() {
        return (int) ulRBsServed.getCount();
    }

    public int getTotalNumDLRBsQueued() {
        return (int) dlRBsQueued.getCount();
    }

    public int getTotalNumULRBsQueued() {
        return (int) ulRBsQueued.getCount();
    }

//    public double getLastDatarate() {
//        return totalDatarates.getLastSample();
//    }

    public double getLastULDatarate() {
        return totalULDatarates.getLastSample();
    }

    public double getLastDLDatarate() {
        return totalDLDatarates.getLastSample();
    }

    @SuppressWarnings("boxing")
    public void logLine() {
        final DecimalFormat df = new DecimalFormat("00.000E00");
        final DecimalFormat dd = new DecimalFormat("00.00");
        LOG.trace("UEs|\t{}\ts{}:{}\t({},{})\t\t{}\t\t{}\t{}\t{}\t{}\t{}\t{}\t\t{}\t\t\t{}",
                id, //
                servingTuple.sector.servingENodeBId, //
                servingTuple.sector.id, //
                dd.format(location.x), //
                dd.format(location.y), //
                dd.format(SHADOWING), //
                scheduledULRBs.size(),
                scheduledDLRBs.size(),
                df.format(totalULDatarates.getLastSample()), //
                df.format(totalDLDatarates.getLastSample()), //
                ulRBsServed.getCount(), //
                dlRBsServed.getCount(), //
                scheduledULRBs, //
                scheduledDLRBs);
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format("UE[%d]", id);
    }
}
