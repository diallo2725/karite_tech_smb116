package com.melitech.karitetech.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class AmountFormatter {

    private static final Locale LOCALE_XOF = new Locale("fr", "CI"); // ou "fr", "CI", etc.
    public static String formatSansDecimales(double montant) {
        NumberFormat format = NumberFormat.getInstance(LOCALE_XOF);
        format.setMaximumFractionDigits(0);
        return format.format(montant) + " FCFA";
    }

    public static String cleanCurrencyValue(String input) {
        if (input == null) return "";
        return input.replaceAll("\\s+", "")  // Supprime tous les espaces
                .replace("FCFA", "");
    }

}

