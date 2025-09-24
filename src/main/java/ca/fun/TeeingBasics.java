package ca.fun;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Understand the basic structure of teeing with easy data types.
 */
public class TeeingBasics {


    public static void main(String[] args) {
        List<Integer> randomList = randomList(10, 100, 10);
        printSumCountUsingTeeing(randomList);
        printMinMaxUsingTeeing(randomList);
        printEvensAndOddsUsingTeeing(randomList);
        printNumberAndSquareUsingTeeing(randomList);
    }


    private static void printSumCountUsingTeeing(List<Integer> randomList) {
        SumCount sumCount = computeSumCount(randomList);
        System.out.println(randomList);
        System.out.println(sumCount);
    }

    private static void printMinMaxUsingTeeing(List<Integer> randomList) {
        MinMax minMax = computeMinMax(randomList);
        System.out.println(randomList);
        System.out.println(minMax);
    }

    private static void printEvensAndOddsUsingTeeing(List<Integer> randomList) {
        EvensAndOdds evensAndOdds = computeEvenAndOdds(randomList);
        System.out.println(randomList);
        System.out.println(evensAndOdds);
    }

    private static void printNumberAndSquareUsingTeeing(List<Integer> randomList) {
        NumberAndSquare numberAndSquare = computeNumberAndSquare(randomList);
        System.out.println(randomList);
        System.out.println(numberAndSquare);
    }



    private static SumCount computeSumCount(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.teeing(
                        // left collector
                        Collectors.summingInt(Integer::intValue),
                        // right collector
                        Collectors.counting(),
                        // merger -> this is nothing but a cute lil. BiFunction
                        SumCount::new // same as (sum, count) -> new SumCount(sum, count)
                ));
    }

    private static MinMax computeMinMax(List<Integer> numbers) {
        // minBy and maxBy returns an Optional because there is a chance a stream can be empty
        // so, we should make sure
        return numbers.stream()
                .collect(Collectors.teeing(
                        // left collector
                        Collectors.minBy(Integer::compareTo),
                        // right collector
                        Collectors.maxBy(Integer::compareTo),
                        // merger function -> a bi function
                        (minOptional, maxOptional) ->
                                new MinMax(minOptional.orElseThrow(), maxOptional.orElseThrow())
                ));
    }

    private static EvensAndOdds computeEvenAndOdds(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.teeing(
                        // left collector
                        Collectors.filtering(TeeingBasics::isEven, Collectors.toList()),
                        // right collector
                        Collectors.filtering(TeeingBasics::isOdd, Collectors.toList()),
                        // merger
                        EvensAndOdds::new
                ));
    }

    private static NumberAndSquare computeNumberAndSquare(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.teeing(
                        // left collector
                        Collectors.mapping(i -> i, Collectors.toList()),
                        // right collector
                        Collectors.mapping(i -> Math.pow(i, 2), Collectors.toList()),
                        // merger
                        NumberAndSquare::new
                ));
    }

    private record NumberAndSquare(List<Integer> numbers, List<Double> squares) {}

    private record SumCount(int sum, long count) {
    }

    private record MinMax(int min, int max) {
    }

    private record EvensAndOdds(List<Integer> evens, List<Integer> odds) {
    }

    private static List<Integer> randomList(int lowerBound, int upperBound, int size) {
        Random random = new Random(lowerBound);
        return random.ints(size, lowerBound, upperBound)
                .boxed()
                .toList();
    }

    private static boolean isEven(int number) {
        return number % 2 == 0;
    }

    private static boolean isOdd(int number) {
        return number % 2 != 0;
    }
}