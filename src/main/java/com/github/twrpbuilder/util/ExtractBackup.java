package com.github.twrpbuilder.util;

import com.github.twrpbuilder.Interface.Tools;

public class ExtractBackup extends Tools {

    private String rooted;
    private String not_rooted;

    public ExtractBackup(String name) {
        rooted = command("file --mime-type " + name + " | grep -w 'gzip'  | cut -d / -f 2 | cut -d \"-\" -f 2");
        not_rooted = command("file --mime-type " + name + " | grep -w 'zip'  | cut -d / -f 2 | cut -d \"-\" -f 2");
        if (rooted.equals("gzip")) {
            extractGzip(name);
            System.out.println("Archive type gzip");
        } else if (not_rooted.equals("zip")) {
            extractZip(name);
            System.out.println("Archive type zip");
        }

    }

    private void extractGzip(String file) {
        command("tar -xvf " + file);
    }

    private void extractZip(String file) {
        command("unzip -o " + file);
        command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' " + propFile() + "  | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
    }
}
