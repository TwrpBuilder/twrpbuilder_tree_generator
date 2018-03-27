package com.github.twrpbuilder.util;

import java.io.File;

public class GetBuildInfo {

	private static String model,product,brand,codename,platform,api,size,fingerprint;
	private static  Config config;
	private static String out=config.outDir;
	private static ShellExecutor shell;

	public static String propFile() {
		shell=new ShellExecutor();
		config=new Config();
		String prop=null;
		if(new File("build.prop").exists())
		{
			prop="build.prop";
		}
		else
			if(new File(out+"default.prop").exists())
			{
				prop=out+"default.prop";
			}
			else {
				prop="null";
			}
		return prop;
	}
	
	public static String getModel() {
  	model= shell.commandnoapp("cat "+propFile()+" | grep ro.product.model= | cut -d = -f 2");
	return model;
	}
	
	public static String getProduct(){
		product= shell.commandnoapp("cat "+propFile()+" | grep ro.build.product= | cut -d = -f 2");
		return product;
	}

	public static String getBrand() {
		brand= shell.commandnoapp("cat "+propFile()+" | grep ro.product.brand= | cut -d = -f 2");
		               if(brand.contains("-"))
			               {
		            	   String newstr=brand.replace("-", "_");
		            	   return newstr;
			              }else {
			            	  return brand;
			            	 }
	}

	public static String getCodename() {
		codename= shell.commandnoapp("cat "+propFile()+" | grep ro.build.product= | cut -d = -f 2");
        if(codename.contains("-"))
        {
        	String newstr=codename.replace("-", "_");
        	return newstr;
       }
        else if (codename.contains(" "))
       {
		String str=codename.replace(" ", "_");
		return str;
	}else {
       	return codename;

       }
	}

	public static String getPlatform() {
		platform= shell.commandnoapp("cat "+propFile()+" | grep ro.board.platform= | cut -d = -f 2");
		if (platform.isEmpty())
		{
			platform= shell.commandnoapp("cat "+propFile()+" | grep ro.mediatek.platform= | cut -d = -f 2");
			if(platform.isEmpty())
			{
				System.out.println("Device not supported");
				System.exit(1);
			}
		}
		return platform;
	}

	public static String getApi() {
		api= shell.commandnoapp("cat "+propFile()+" | grep ro.product.cpu.abi= | cut -d = -f 2");
		return api;
	}

	public static String getFingerPrint() {
	fingerprint= shell.commandnoapp("cat "+propFile()+" | grep ro.build.fingerprint= | cut -d = -f 2");
	return fingerprint;
	}
	
	public static String getSize() {
		size= shell.commandnoapp("wc -c < recovery.img");
		return size;
	}
	
	public static String getPathS() {
		String path="device"+File.separator+getBrand()+File.separator+getCodename()+File.separator;
		return path;
	}
	
	public static String getPath() {
		String path="device"+File.separator+getBrand()+File.separator+getCodename();
		return path;
	}

	
}
