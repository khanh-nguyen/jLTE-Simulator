package uk.co.downthewire.jLTE.simulator.results;

import java.util.ArrayList;
import java.util.List;

public class SimulationResultsAggregator {

    private final List<SimulationResults> results;

    public SimulationResultsAggregator() {
        results = new ArrayList<>();
    }

    public void aggregate(List<SimulationResults> resultList) {
        results.addAll(resultList);
    }

    public void aggregate(SimulationResults result) {
        results.add(result);
    }

    @SuppressWarnings("boxing")
    public AggregatedSimulationResults getResult() {
//        List<Double> averageTputs = new ArrayList<>();
//        List<Double> percentileTputs = new ArrayList<>();
//        List<Double> maxTputs = new ArrayList<>();
        List<Double> averageULTputs = new ArrayList<>();
        List<Double> averageDLTputs = new ArrayList<>();
        List<Double> percentileULTputs = new ArrayList<>();
        List<Double> percentileDLTputs = new ArrayList<>();
        List<Double> maxULTputs = new ArrayList<>();
        List<Double> maxDLTputs = new ArrayList<>();

        for (SimulationResults result : results) {
//            averageTputs.add(result.avergeTput);
//            percentileTputs.add(result.percentileTput);
//            maxTputs.add(result.maxUETput);
            averageULTputs.add(result.avergeULTput);
            averageDLTputs.add(result.avergeDLTput);
            percentileULTputs.add(result.percentileULTput);
            percentileDLTputs.add(result.percentileDLTput);
            maxULTputs.add(result.maxUEULTput);
            maxDLTputs.add(result.maxUEDLTput);
        }

        //return new AggregatedSimulationResults(mean(percentileTputs), mean(averageTputs), mean(maxTputs), results.get(0).configuration, stddev(percentileTputs), stddev(averageTputs));
        return new AggregatedSimulationResults(mean(percentileULTputs), mean(percentileDLTputs),
                mean(averageULTputs), mean(averageDLTputs),
                mean(maxULTputs), mean(maxDLTputs),
                results.get(0).configuration,
                stddev(percentileULTputs), stddev(percentileDLTputs),
                stddev(averageULTputs), stddev(averageDLTputs)
        );
    }

    private static double mean(List<Double> inputs) {
        double total = 0;
        for (double number : inputs) {
            total += number;
        }
        return total / inputs.size();
    }

    private static double stddev(List<Double> inputs) {
        double mean = mean(inputs);

        double totalSquaredDifferences = 0;
        for (double number : inputs) {
            double difference = mean - number;
            totalSquaredDifferences += difference * difference;
        }

        return Math.sqrt(totalSquaredDifferences / inputs.size());
    }
}
