package com.github.twrpbuilder.util;

import com.github.twrpbuilder.Interface.Tools;

import static com.github.twrpbuilder.MainActivity.rName;

public class Config extends Tools {
    public static String outDir = "tmp" + seprator;
    public static String recoveryFile = recovery() + ".img";

    public static String recovery() {
        if (rName == null) {
            return "recovery";
        } else {
            if (rName.contains(".img")) {
                String rename = rName.replace(".img", "");
                return rename;
            } else {
                return rName;
            }
        }
    }
}
