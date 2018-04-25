package com.github.twrpbuilder.Thread;

import com.github.twrpbuilder.Interface.Tools;
import com.github.twrpbuilder.mkTree.MakeTree;
import com.github.twrpbuilder.util.ExtractBackup;
import com.github.twrpbuilder.util.GetAsset;

public class RunCode extends Tools implements Runnable {

    public static boolean extract;
    public static boolean AndroidImageKitchen;
    private static String name;
    private static String type;
    private boolean mrvl;
    private boolean mtk;
    private boolean samsung;

    public RunCode(String name) {
        RunCode.name = name;
        if (AndroidImageKitchen) {
            System.out.println("Using Android Image Kitchen to extract " + name);
            new GetAsset("bin");
            new GetAsset("unpackimg.sh");
        } else {
            new GetAsset("umkbootimg");
            new GetAsset("magic");
        }
    }

    public RunCode(String name, String type) {
        RunCode.name = name;
        RunCode.type = type;

        if (type.equals("mrvl")) {
            new GetAsset("degas-umkbootimg");
            command("mv degas-umkbootimg umkbootimg ");
            mrvl = true;
        } else if (type.equals("mt") || type.equals("mtk")) {
            new GetAsset("unpack-MTK.pl");
            command("mv unpack-MTK.pl umkbootimg");
            mtk = true;
        } else if (type.equals("samsung")) {
            new GetAsset("umkbootimg");
            samsung = true;
        }
        if (AndroidImageKitchen) {
            new GetAsset("bin");
            new GetAsset("unpackimg.sh");
        }
    }

    public static String getName() {
        return name;
    }

    @Override
    public void run() {
        if (extract) {
            new ExtractBackup(name);
        }
        if (AndroidImageKitchen) {
            MakeTree.AndroidImageKitchen = true;
        }
        if (mtk == true) {
            new MakeTree(true, type);
        } else if (mrvl == true || samsung == true) {
            new MakeTree(false, type);
        } else {
            new MakeTree(false, "none");
        }
    }

}
