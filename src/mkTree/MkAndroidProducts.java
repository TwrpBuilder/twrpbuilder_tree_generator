package mkTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import util.ShellExecuter;
import util.FWriter;
import util.GetBuildInfo;

public class MkAndroidProducts {

	private GetBuildInfo info;
	private String idata;
	
	public MkAndroidProducts() {
		System.out.println("Making AndroidProducts.mk");
		new FWriter("AndroidProducts.mk",getData());

	}
	
	private String getData() {
		idata =ShellExecuter.CopyRight();
		idata+="LOCAL_PATH := device/"+info.getBrand()+File.separator+info.getCodename()+"\n" + 
				"\n" + 
				"PRODUCT_MAKEFILES := $(LOCAL_PATH)/omni_"+info.getCodename()+".mk";
		return idata;
	}
	
}
