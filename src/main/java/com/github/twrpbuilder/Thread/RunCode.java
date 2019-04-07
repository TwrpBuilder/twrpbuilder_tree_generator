package com.github.twrpbuilder.Thread;

import com.github.twrpbuilder.Interface.Tools;
import com.github.twrpbuilder.Models.DeviceModel;
import com.github.twrpbuilder.mkTree.MakeTree;
import com.github.twrpbuilder.util.ExtractBackup;

public class RunCode extends Tools implements Runnable {

    public static boolean extract;
    public static boolean AndroidImageKitchen;
    private DeviceModel deviceModel=new DeviceModel();

    public RunCode(String name) {
        cp(name,"build.tar.gz");
        if (AndroidImageKitchen) {
            System.out.println("Using Android Image Kitchen to extract " + name);
            extract("bin");
            extract("unpackimg.sh");
        } else {
            extract("umkbootimg");
            extract("magic");
        }
    }

    public RunCode(String name, String type) {
        deviceModel.setType(type);
        cp(name,"build.tar.gz");
        if (type.equals("mrvl")) {
            extract("degas-umkbootimg");
            command("mv degas-umkbootimg umkbootimg ");
            deviceModel.setMrvl(true);
        } else if (type.equals("mt") || type.equals("mtk")) {
            extract("unpack-MTK.pl");
            command("mv unpack-MTK.pl umkbootimg");
            deviceModel.setMtk(true);
        } else if (type.equals("samsung")) {
            extract("umkbootimg");
            deviceModel.setSamsung(true);
        }
        if (AndroidImageKitchen) {
            extract("bin");
            extract("unpackimg.sh");
        }
    }

    @Override
    public void run() {
        if (extract) {
            new ExtractBackup("build.tar.gz");
        }
        if (AndroidImageKitchen) {
            MakeTree.AndroidImageKitchen = true;
        }
        new MakeTree(deviceModel);
    }

}
