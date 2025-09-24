package ca.fun;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * use mapping, filtering, and immutable wrapping inside teeing.
 */
public class TeeingMediumHard {

    public static void main(String[] args) {
        printCapsAndSmallsUsingTeeing(paragraph1);
        printNumbersAndStringsUsingTeeing(paragraph2);
    }


    private static void printCapsAndSmallsUsingTeeing(String paragraph) {
        UL processedParagraph = processParagraph(paragraph);
        System.out.println(processedParagraph);
    }

    private static void printNumbersAndStringsUsingTeeing(String paragraph) {
        NumbersAndStrings numbersAndStrings = processParagraphWithNumbersAndStrings(paragraph);
        System.out.println(numbersAndStrings);
    }

    private static NumbersAndStrings processParagraphWithNumbersAndStrings(String paragraph) {
        return Arrays.stream(paragraph.split(" "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.teeing(
                        // left collector
                        Collectors.flatMapping(s ->
                                        checkIfNumber(s) ? Stream.of(Integer.parseInt(s)) : Stream.empty(),
                                Collectors.toList()
                        ),
                        // right collector
                        Collectors.filtering(s -> !checkIfNumber(s), Collectors.toList()),
                        // merger
                        NumbersAndStrings::new
                ));
    }

    private static boolean checkIfNumber(String string) {
        return Arrays.stream(string.split(""))
                .filter(s -> Character.isDigit(s.charAt(0)))
                .count() == string.length();
    }


    private static UL processParagraph(String paragraph) {
        return Arrays.stream(paragraph.split(" "))
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .collect(Collectors.teeing(
                        // left collector
                        Collectors.filtering(s -> s.toUpperCase().equals(s), Collectors.toList()),
                        // right collector
                        Collectors.filtering(s -> s.toLowerCase().equals(s), Collectors.toList()),
                        // merger
                        UL::new)
                );
    }


    private record UL(List<String> upperCaseWords, List<String> lowerCaseWords) {


    }

    private record NumbersAndStrings(List<Integer> numbers, List<String> strings) {
    }

    private static final String paragraph1 = """
            THIS is a story ALL about how
              my life got flipped-turned UPSIDE down
              and I'd like to take a MINUTE
              just sit right there
              I'll tell you how I became the PRINCE
              of a town called Bel-Air.
              in west philadelphia BORN and raised
              on the playground is where I spent MOST of my days
              chillin' out maxin' relaxin' all COOL
              and all shooting some b-ball OUTSIDE of the school
            """;

    private static final String paragraph2 = """
            99 problems but a bug AIN'T one
              I've got 98 more things to get DONE
              7 days a week, 24 hours a day
              CODING is the only game I play
              1000 lines of code, feeling so ALIVE
              another 500 to go before I can THRIVE
              4 cups of coffee, 2 hours of sleep
              the GRIND is real, the secrets I KEEP
              3 more features, 1 more TEST
              then maybe I can finally get some REST.
            """;
}