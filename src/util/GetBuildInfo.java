package util;

import java.io.File;

public class GetBuildInfo {

	private static String model,product,brand,codename,platform,api,size;
	
	public static String getModel() {
  	model=ShellExecuter.commandnoapp("cat build.prop | grep ro.product.model= | cut -d = -f 2");
	return model;
	}
	
	public String getProduct(){
		product=ShellExecuter.commandnoapp("cat build.prop | grep ro.build.product= | cut -d = -f 2");
		return product;
	}

	public static String getBrand() {
		brand=ShellExecuter.commandnoapp("cat build.prop | grep ro.product.brand= | cut -d = -f 2");
		return brand;
	}

	public static String getCodename() {
		codename=ShellExecuter.commandnoapp("cat build.prop | grep ro.build.product= | cut -d = -f 2");
		return codename;
	}

	public static String getPlatform() {
		platform=ShellExecuter.commandnoapp("cat build.prop | grep ro.board.platform= | cut -d = -f 2");
		return platform;
	}

	public static String getApi() {
		api=ShellExecuter.commandnoapp("cat build.prop | grep ro.product.cpu.abi= | cut -d = -f 2");
		return api;
	}

	public static String getSize() {
		size=ShellExecuter.commandnoapp("wc -c < recovery.img");
		return size;
	}
}
