package uk.co.downthewire.jLTE.simulator.ue;

import com.google.common.base.Predicate;

import java.util.Comparator;

public final class UEComparators {

    public static Predicate<UE> hasRBsQueued(final boolean isDL) {
        return new Predicate<UE>() {
            @Override
            public boolean apply(UE ue) {
                return ue.getCurrentRBsQueued(isDL) > 0;
            }
        };
    }

    public static final Comparator<UE> SECTOR_ORDER = new Comparator<UE>() {
        @Override
        public int compare(final UE u1, final UE u2) {
            final String r1 = String.valueOf(u1.servingTuple.sector.servingENodeBId) + String.valueOf(u1.servingTuple.sector.id);
            final String r2 = String.valueOf(u2.servingTuple.sector.servingENodeBId) + String.valueOf(u2.servingTuple.sector.id);
            return r1.compareTo(r2);
        }
    };

    public static Comparator<UE> getRBSignalComparator(final int RB, final boolean isDL) {
        return new Comparator<UE>() {
            @Override
            public int compare(final UE u1, final UE u2) {
                //return Double.valueOf(u1.getSignalOnRB(RB)).compareTo(u2.getSignalOnRB(RB));
                final Double s1 = u1.getSignalOnRB(RB, isDL);
                final Double s2 = u2.getSignalOnRB(RB, isDL);
                return s1.compareTo(s2);
            }
        };
    }

    public static Comparator<UE> getRelativeSignalComparator(final int RB, final boolean isDL) {
        return new Comparator<UE>() {
            @Override
            public int compare(final UE u1, final UE u2) {
                //return Double.valueOf(u1.getRelativeSignalOnRB(RB)).compareTo(u2.getRelativeSignalOnRB(RB));
                final Double s1 = u1.getRelativeSignalOnRB(RB, isDL);
                final Double s2 = u2.getRelativeSignalOnRB(RB, isDL);
                return s1.compareTo(s2);
            }
        };
    }

    public static final Comparator<UE> SINR_ORDER = new Comparator<UE>() {
        @SuppressWarnings("boxing")
        @Override
        public int compare(final UE u1, final UE u2) {
//            final Double r1 = u1.calculateAverageSinr();
//            final Double r2 = u2.calculateAverageSinr();
            final Double r1 = u1.calculateAverageULSinr() + u1.calculateAverageDLSinr();
            final Double r2 = u2.calculateAverageULSinr() + u2.calculateAverageDLSinr();
            return r1.compareTo(r2);
        }
    };

    private UEComparators() {
    }
}
