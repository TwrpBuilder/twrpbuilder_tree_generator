package util;

import java.io.File;

public class GetBuildInfo {

	private static String model,product,brand,codename,platform,api,size,fingerprint;
	
	public static String propFile() {
		String prop=null;
		if(new File("build.prop").exists())
		{
			prop="build.prop";
		}
		else
			if(new File("out/default.prop").exists())
			{
				prop="out/default.prop";
			}
			else {
				prop="null";
			}
		return prop;
	}
	
	public static String getModel() {
  	model=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.product.model= | cut -d = -f 2");
	return model;
	}
	
	public static String getProduct(){
		product=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.build.product= | cut -d = -f 2");
		return product;
	}

	public static String getBrand() {
		brand=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.product.brand= | cut -d = -f 2");
		return brand;
	}

	public static String getCodename() {
		codename=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.build.product= | cut -d = -f 2");
		return codename;
	}

	public static String getPlatform() {
		platform=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.board.platform= | cut -d = -f 2");
		if (platform.isEmpty())
		{
			platform=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.mediatek.platform= | cut -d = -f 2");
			if(platform.isEmpty())
			{
				System.out.println("Device not supported");
				System.exit(1);
			}
		}
		return platform;
	}

	public static String getApi() {
		api=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.product.cpu.abi= | cut -d = -f 2");
		return api;
	}

	public static String getFingerPrint() {
	fingerprint=ShellExecuter.commandnoapp("cat "+propFile()+" | grep ro.build.fingerprint= | cut -d = -f 2");
	return fingerprint;
	}
	
	public static String getSize() {
		size=ShellExecuter.commandnoapp("wc -c < recovery.img");
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
