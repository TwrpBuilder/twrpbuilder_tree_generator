package com.github.twrpbuilder.util;

import java.io.File;

import static com.github.twrpbuilder.MainActivity.rName;

public class Config {
    public static String outDir="tmp"+ File.separator;
    public static String recoveryFile=recovery()+".img";
    public static String recovery(){
        if (rName==null)
        {
            return "recovery";
        }
        else
        {
            if (rName.contains(".img"))
            {
                String rename=rName.replace(".img","");
                return rename;
            }
            else {
                return rName;
            }
        }
    }
}
