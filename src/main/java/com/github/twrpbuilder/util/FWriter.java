package com.github.twrpbuilder.util;

import com.github.twrpbuilder.Interface.Tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FWriter extends Tools {

    public FWriter(String name, String data) {
        run(name, data, false);
    }

    public FWriter(String name, String data, boolean o) {
        run(name, data, true);
    }


    private void run(String name, String data, boolean over) {
        PrintWriter writer;
        try {
            if (over == true) {
                writer = new PrintWriter(new FileOutputStream(getPathS() + name, true));
            } else {
                writer = new PrintWriter(new FileOutputStream(getPathS() + name, false));
            }
            writer.println(data);
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}