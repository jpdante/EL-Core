package com.ellisiumx.elrankup.utils;

import java.text.DecimalFormat;

public class UtilFormat {

    private static DecimalFormat doubleFormat = new DecimalFormat("#.##");

    public String FormatMoney(double money) {
        if(money >= 1_000L) {
            return doubleFormat.format(money / 1_000L) + "K";
        } else if(money >= 1_000_000L) {
            return doubleFormat.format(money / 1_000_000L) + "M";
        } else if(money >= 1_000_000_000L) {
            return doubleFormat.format(money / 1_000_000_000L) + "G";
        } else if(money >= 1_000_000_000_000L) {
            return doubleFormat.format(money / 1_000_000_000_000L) + "T";
        } else if(money >= 1_000_000_000_000_000L) {
            return doubleFormat.format(money / 1_000_000_000_000_000L) + "P";
        } else if(money >= 1_000_000_000_000_000_000L) {
            return doubleFormat.format(money / 1_000_000_000_000_000_000L) + "E";
        } return doubleFormat.format(money);
    }

}
