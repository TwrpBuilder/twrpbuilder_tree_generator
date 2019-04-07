package com.github.twrpbuilder.Interface;

import com.github.twrpbuilder.Models.DeviceModel;
import com.github.twrpbuilder.Models.PropData;
import com.github.twrpbuilder.util.Config;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedList;

import static com.github.twrpbuilder.MainActivity.rName;

public class Tools implements ToolsInterface {

    public long size;
    public Config config = null;
    public String out = config.outDir;

    @Override
    public boolean fexist(String name) {
        if (new File(name).exists())
            return true;
        else
            return false;
    }

    @Override
    public String command(String run) {
        Process process;
        String o = null;
        StringBuilder sb = null;
        String[] commands = new String[]{"/bin/bash", "-c", run};
        StringBuilder linkedList = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(commands);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((o = bufferedReader.readLine()) != null) {
                linkedList.append(o.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linkedList.toString();
    }

    @Override
    public LinkedList command(String run, boolean LinkList) {
        Process process;
        String o = null;
        StringBuilder sb = null;
        String[] commands = new String[]{"/bin/bash", "-c", run};
        LinkedList<String> linkedList = new LinkedList();
        try {
            process = Runtime.getRuntime().exec(commands);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((o = bufferedReader.readLine()) != null) {
                linkedList.add(o.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linkedList;
    }

    @Override
    public boolean mkdir(String name) {
        File theDir = new File(name).getAbsoluteFile();

        // if the directory does not exist, create it
        if (!theDir.isDirectory()) {
            boolean result = false;

            try{
                theDir.mkdirs();
                result = true;
            }
            catch(SecurityException se){
                System.out.println("Failed to make dir "+name);
                System.exit(0);
            }
            if(result) {
            }
        }else
        {
            System.out.println("Dir: "+name+" already exist");
        }
        return theDir.isDirectory();
    }

    @Override
    public boolean rm(String name) {
        if (fexist(name)) {
            File file = new File(name);
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (file.isFile()) {
                file.delete();
                return true;
            } else {
                return false;
            }
        } else
            return false;
    }

    @Override
    public String CopyRight() {
        String copy = "#\n" +
                "# Copyright (C) 2018 The TwrpBuilder Open-Source Project\n" +
                "#\n" +
                "# Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "# you may not use this file except in compliance with the License.\n" +
                "# You may obtain a copy of the License at\n" +
                "#\n" +
                "# http://www.apache.org/licenses/LICENSE-2.0\n" +
                "#\n" +
                "# Unless required by applicable law or agreed to in writing, software\n" +
                "# distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "# See the License for the specific language governing permissions and\n" +
                "# limitations under the License.\n" +
                "#\n" +
                "\n";
        return copy;
    }

    @Override
    public void cp(String from, String to) {
        File f = new File(from);
        File t = new File(to);
        if (t.exists()) {
            rm(t.getAbsolutePath());
        }
        try {
            Files.copy(f.toPath(), t.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String propFile() {
        config = new Config();
        String prop = null;
        if (new File("build.prop").exists()) {
            prop = "build.prop";
        } else if (new File(out + "default.prop").exists()) {
            prop = out + "default.prop";
        } else {
            prop = "null";
        }
        return prop;
    }


    LinkedList<PropData>  propDataArray=new LinkedList<>();

    public void PropData(OnDataRequest request){
        DeviceModel deviceModel=new DeviceModel();
        propDataArray.add(new PropData(commonStr("ro.product.model"),"model"));
        propDataArray.add(new PropData(commonStr("ro.product.brand"),"brand"));
        propDataArray.add(new PropData(commonStr("ro.build.product"),"codename"));
        propDataArray.add(new PropData(commonStr("ro.board.platform"),"platform"));
        propDataArray.add(new PropData(commonStr("ro.product.cpu.abi"),"abi"));
        propDataArray.add(new PropData(commonStr("ro.build.fingerprint"),"fingerprint"));

        Iterator<PropData> iterator=propDataArray.iterator();
        while (iterator.hasNext())
        {
            PropData propData=iterator.next();
            String chs;
            chs= command(propData.getCommand());
            switch (propData.getType())
            {
                case "model":
                    deviceModel.setModel(chs);
                    break;
                case "brand":
                    if (chs.contains("-")) {
                        chs = chs.replace("-", "_");
                    } else if (chs.contains(" ")) {
                        chs = chs.replace(" ", "_");
                    }
                    deviceModel.setBrand(chs);
                    break;
                case "codename":
                    if (chs.equals(deviceModel.getBrand()) || chs.isEmpty())
                    {
                        chs=checkData(deviceModel.getModel()).toLowerCase();
                    }
                    deviceModel.setCodename(chs);
                    break;
                case "platform":
                    if (chs.isEmpty()) {
                        chs = command(commonStr("ro.mediatek.platform"));
                        if (chs.isEmpty()) {
                            chs="generic";
                        }
                    }
                    deviceModel.setPlatform(chs);
                    break;
                case "abi":
                    deviceModel.setAbi(chs);
                    break;
                case "fingerprint":
                    deviceModel.setFingerprint(chs);
                    break;
            }
        }
        String path = "device" + seprator + deviceModel.getBrand() + seprator + deviceModel.getCodename() + seprator;
        deviceModel.setPath(path);
        request.getData(deviceModel);
    }


    private String commonStr(String data){
        return "cat " + propFile() + " | grep -m 1 "+data+"= | cut -d = -f 2";
    }


    private String checkData(String data){
        if (data.contains("-")) {
            String newstr = data.replace("-", "_");
            return newstr;
        } else if (data.contains(" ")) {
            String str = data.replace(" ", "_");
            return str;
        }
        else if(data.contains("+"))
        {
            String wut=data.replace("+","");
            return wut;
        }
        else {
            return data;
        }
    }


    public Long getSize() {
        size = new File(Config.recoveryFile).length();
        return size;
    }

    @Override
    public void Write(String name, String data) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream( name, false));
            writer.println(data);
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private boolean file(String name) {
        if (new File(name).exists()) {
            rm(name);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void Clean() {
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
        file("build.tar.gz");
    }

    @Override
    public void extract(String name) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        String resourceName = seprator + name;
        try {
            stream = Tools.class.getResourceAsStream(seprator + "asset" + resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = System.getProperty("user.dir");
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            stream.close();
            resStreamOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
