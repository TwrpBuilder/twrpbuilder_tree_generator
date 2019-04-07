package com.github.twrpbuilder.Task;

import com.github.twrpbuilder.Interface.Tools;
import com.github.twrpbuilder.Interface.ToolsInterface;
import com.github.twrpbuilder.Models.DeviceModel;
import com.github.twrpbuilder.Models.OptionsModel;
import com.github.twrpbuilder.mkTree.MakeTree;
import com.github.twrpbuilder.util.ExtractBackup;

public class RunCode extends Thread implements Runnable {

    private DeviceModel deviceModel=new DeviceModel();
    private ToolsInterface tool=(ToolsInterface)this;
    private OptionsModel model;

    public RunCode(String name, OptionsModel model) {
        this.model=model;
        tool.cp(name,"build.tar.gz");
        if (model.isAndroidImageKitchen()) {
            System.out.println("Using Android Image Kitchen to extract " + name);
            tool.extract("bin");
            tool.extract("unpackimg.sh");
        } else {
            tool.extract("umkbootimg");
            tool.extract("magic");
        }
    }

    public RunCode(String name, String type,OptionsModel model) {
        this.model=model;
        deviceModel.setType(type);
        tool.cp(name,"build.tar.gz");
        if (type.equals("mrvl")) {
            tool.extract("degas-umkbootimg");
            tool.command("mv degas-umkbootimg umkbootimg ");
            deviceModel.setMrvl(true);
        } else if (type.equals("mt") || type.equals("mtk")) {
            tool.extract("unpack-MTK.pl");
            tool.command("mv unpack-MTK.pl umkbootimg");
            deviceModel.setMtk(true);
        } else if (type.equals("samsung")) {
            tool.extract("umkbootimg");
            deviceModel.setSamsung(true);
        }
        if (model.isAndroidImageKitchen()) {
            tool.extract("bin");
            tool.extract("unpackimg.sh");
        }
    }

    @Override
    public void run() {
        if (model.isExtract()) {
            new ExtractBackup("build.tar.gz");
        }
        new MakeTree(deviceModel,model);
    }

}
