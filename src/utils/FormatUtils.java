package utils;

import java.text.DecimalFormat;

public class FormatUtils {
    
    private static final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private static final DecimalFormat numberFormat = new DecimalFormat("#,##0");
    
    public static String formatCurrency(double amount) {
        return currencyFormat.format(amount) + " đ";
    }
    
    public static String formatNumber(int number) {
        return numberFormat.format(number);
    }
    
    public static String formatNumber(long number) {
        return numberFormat.format(number);
    }
    
    public static String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }
}
