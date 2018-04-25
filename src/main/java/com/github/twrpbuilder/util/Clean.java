package com.github.twrpbuilder.util;

import com.github.twrpbuilder.Interface.Tools;

import java.io.File;

import static com.github.twrpbuilder.MainActivity.rName;

public class Clean extends Tools {
    public Clean() {
        file("build.prop");
        if (rName == null)
            file(Config.recoveryFile);
        file("mounts");
        file("umkbootimg");
        file(Config.outDir);
        file("unpack-MTK.pl");
        file("unpackimg.sh");
        file("bin");
        file("magic");
        file("androidbootimg.magic");

    }

    private boolean file(String name) {
        if (new File(name).exists()) {
            rm(name);
            return true;
        } else {
            return false;
        }
    }
}
