package uk.co.downthewire.jLTE.simulator.rbs;

import uk.co.downthewire.jLTE.stats.SimpleCounter;

import java.util.Comparator;

public class ResourceBlock {

    public final int id;
    private boolean isFullPower;
    private boolean isScheduled;
//    private final SimpleCounter datarate;
    private final SimpleCounter ulDatarate;
    private final SimpleCounter dlDatarate;
//    private final SimpleCounter sinr;
    private final SimpleCounter ulSinr;
    private final SimpleCounter dlSinr;

    public ResourceBlock(int id) {
        this.id = id;
        this.isFullPower = true;
        this.isScheduled = false;
//        this.datarate = new SimpleCounter();
//        this.sinr = new SimpleCounter();
        this.ulDatarate = new SimpleCounter();
        this.dlDatarate = new SimpleCounter();
        this.ulSinr = new SimpleCounter();
        this.dlSinr = new SimpleCounter();
    }

    public void resetScheduledStatus() {
        isScheduled = false;
    }

    public boolean isFullPowerRB() {
        return isFullPower;
    }

    public void setFullPower() {
        isFullPower = true;
    }

    public void setLowPower() {
        isFullPower = false;
    }

    public void schedule() {
        isScheduled = true;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceBlock other = (ResourceBlock) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @SuppressWarnings("hiding")
//    public void accumulateDataRate(double datarate) {
//        this.datarate.accumulate(datarate);
//    }
    public void accumulateULDataRate(double datarate) {
        this.ulDatarate.accumulate(datarate);
    }

    public void accumulateDLDataRate(double datarate) {
        this.dlDatarate.accumulate(datarate);
    }


    @SuppressWarnings("hiding")
    public void accumulateULSinr(double sinr) {
        this.ulSinr.accumulate(sinr);
    }

    public void accumulateDLSinr(double sinr) {
        this.dlSinr.accumulate(sinr);
    }

    public double getAverageULSinr() {
        return ulSinr.getAverage();
    }

    public double getAverageDLSinr() {
        return dlSinr.getAverage();
    }

    public static final Comparator<ResourceBlock> RB_UL_SINR_COMPARATOR = new Comparator<ResourceBlock>() {
        @Override
        public int compare(final ResourceBlock rb1, final ResourceBlock rb2) {
//            double avgSinr1 = rb1.getAverageSinr();
//            double avgSinr2 = rb2.getAverageSinr();
            double avgSinr1 = rb1.getAverageULSinr();
            double avgSinr2 = rb2.getAverageULSinr();

            return Double.valueOf(avgSinr1).compareTo(avgSinr2);
        }
    };

    public static final Comparator<ResourceBlock> RB_DL_SINR_COMPARATOR = new Comparator<ResourceBlock>() {
        @Override
        public int compare(final ResourceBlock rb1, final ResourceBlock rb2) {
            double avgSinr1 = rb1.getAverageDLSinr();
            double avgSinr2 = rb2.getAverageDLSinr();

            return Double.valueOf(avgSinr1).compareTo(avgSinr2);
        }
    };

    public static final Comparator<ResourceBlock> RB_SINR_COMPARATOR = new Comparator<ResourceBlock>() {
        @Override
        public int compare(final ResourceBlock rb1, final ResourceBlock rb2) {
            double avgSinr1 = rb1.getAverageDLSinr() + rb1.getAverageULSinr();
            double avgSinr2 = rb2.getAverageDLSinr() + rb2.getAverageULSinr();

            return Double.valueOf(avgSinr1).compareTo(avgSinr2);
        }
    };

}
