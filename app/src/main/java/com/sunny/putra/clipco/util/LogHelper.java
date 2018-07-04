package com.sunny.putra.clipco.util;

/**
 * Created by Wayan-MECS on 5/15/2018.
 */

public class LogHelper {

    public static void print_me(String str) {
        if (Globals.showLog) {
            System.out.println(str);
        }
    }
}
