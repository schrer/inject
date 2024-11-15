package at.schrer.inject.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class StringUtils {

    private StringUtils(){}

    public static String reverse(String s) {
        if (s == null) {
            return null;
        }

        int length = s.length();
        if (length == 0) {
            return "";
        }

        StringBuilder reversed = new StringBuilder();
        for (int i = length - 1; i >= 0; i--) {
            reversed.append(s.charAt(i));
        }

        return reversed.toString();
    }

    public static String removeChar(char target, String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != target) {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static boolean isEmpty(String s){
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s){
        return s == null || s.isBlank();
    }

    public static boolean isPalindrome(String s){
        if (s == null) {
            return false;
        }

        if (s.isBlank()) {
            return true;
        }

        int length = s.length();
        String inputLc = s.toLowerCase();
        for (int i = 0; i < length/2 ; i++) {
            int iEnd = length - i - 1;
            if (inputLc.charAt(i) != inputLc.charAt(iEnd)) {
                return false;
            }
        }
        return true;
    }

    public static String sort(String s){
        if (s == null || s.isBlank()) {
            return s;
        }

        List<Character> chars = new ArrayList<>();
        for(char c : s.toCharArray()) {
            chars.add(c);
        }

        List<Character> sorted = new ArrayList<>();

        for (int i = 0; i<s.length(); i++) {
            char lowest = chars.getFirst();
            int lowestInd = 0;
            for (int j=0; j<chars.size(); j++) {
                char current = chars.get(j);
                if (current < lowest) {
                    lowest = current;
                    lowestInd = j;
                }
            }
            sorted.add(lowest);
            chars.remove(lowestInd);
        }

        Collector<Character, StringBuilder, String> coll = new Collector<>() {
            @Override
            public Supplier<StringBuilder> supplier() {
                return StringBuilder::new;
            }

            @Override
            public BiConsumer<StringBuilder, Character> accumulator() {
                return StringBuilder::append;
            }

            @Override
            public BinaryOperator<StringBuilder> combiner() {
                return StringBuilder::append;
            }

            @Override
            public Function<StringBuilder, String> finisher() {
                return StringBuilder::toString;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of();
            }
        };
        return sorted.stream().collect(coll);
    }

    public static String sum(String first, String second) throws NumberFormatException{
        long firstLong = Long.parseLong(first);
        long secondLong = Long.parseLong(second);
        return "" + (firstLong + secondLong);
    }

    public static int length(String s){
        if (s == null) {
            return 0;
        }
        return s.length();
    }
}
