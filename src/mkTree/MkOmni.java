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

public class MkOmni {

	private GetBuildInfo info;
	private String idata;
	
	public MkOmni() {
		System.out.println("Making omni_"+info.getCodename()+".mk");
		new FWriter("omni_"+info.getCodename()+".mk",getData());
	}
	
	private String getData() {
		idata =ShellExecuter.CopyRight();
		idata+="$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base.mk)\n" + 
				"\n" + 
				"PRODUCT_COPY_FILES += device/"+info.getBrand()+File.separator+info.getCodename()+"/kernel:kernel\n" + 
				"\n" + 
				"PRODUCT_DEVICE :="+ info.getCodename()+"\n" + 
				"PRODUCT_NAME := omni_"+info.getCodename()+"\n" + 
				"PRODUCT_BRAND := "+info.getBrand()+"\n" + 
				"PRODUCT_MODEL := "+info.getModel()+"\n" + 
				"PRODUCT_MANUFACTURER := "+info.getBrand();
		return idata;
	}
	
}
