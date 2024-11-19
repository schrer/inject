package at.schrer.inject.utils;

public final class StringUtils {

    private StringUtils(){}

    public static boolean isEmpty(String s){
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s){
        return s == null || s.isBlank();
    }
}
