package com.github.twrpbuilder.Models;

import java.io.Serializable;

public class DeviceModel implements Serializable {

    private String brand;
    private String codename;
    private String model;
    private String platform;
    private String type;
    private boolean mtk;
    private boolean encrypted;
    private boolean mrvl;
    private boolean samsung;

    public DeviceModel(){}

    public DeviceModel(
            String brand,
            String codename,
            String model,
            String type,
            String platform,
            boolean mtk,
            boolean mrvl,
            boolean samsung,
            boolean encrypted
    ){
        this.brand=brand;
        this.codename=codename;
        this.model=model;
        this.platform=platform;
        this.type=type;
        this.mtk=mtk;
        this.encrypted=encrypted;
        this.mrvl=mrvl;
        this.samsung=samsung;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getCodename() {
        return codename;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public void setMtk(boolean mtk) {
        this.mtk = mtk;
    }

    public boolean isMtk() {
        return mtk;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setMrvl(boolean mrvl) {
        this.mrvl = mrvl;
    }

    public boolean isMrvl() {
        return mrvl;
    }

    public void setSamsung(boolean samsung) {
        this.samsung = samsung;
    }

    public boolean isSamsung() {
        return samsung;
    }

}
