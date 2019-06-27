package com.bang.demo.loglibrary;

import android.text.TextUtils;
import android.util.Log;

public class LogUtilHelper {

    public static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    public static void printLine(String tag, boolean isTop) {
        if (BuildConfig.DEBUG) {
            if (isTop) {
                Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
            } else {
                Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
            }
        }

    }

    public static void printDivider(String tag) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }
}
