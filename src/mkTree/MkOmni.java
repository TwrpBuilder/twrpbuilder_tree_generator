package mkTree;

import java.io.File;
import util.ShellExecuter;
import util.FWriter;
import util.GetBuildInfo;

public class MkOmni {
	private String idata;
	
	public MkOmni() {
		System.out.println("Making omni_"+GetBuildInfo.getCodename()+".mk");
		new FWriter("omni_"+GetBuildInfo.getCodename()+".mk",getData());
	}
	
	private String getData() {
		idata =ShellExecuter.CopyRight();
		idata+="$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base.mk)\n" + 
				"\n" + 
				"PRODUCT_COPY_FILES += device/"+GetBuildInfo.getBrand()+File.separator+GetBuildInfo.getCodename()+"/kernel:kernel\n" + 
				"\n" + 
				"PRODUCT_DEVICE :="+ GetBuildInfo.getCodename()+"\n" + 
				"PRODUCT_NAME := omni_"+GetBuildInfo.getCodename()+"\n" + 
				"PRODUCT_BRAND := "+GetBuildInfo.getBrand()+"\n" + 
				"PRODUCT_MODEL := "+GetBuildInfo.getModel()+"\n" + 
				"PRODUCT_MANUFACTURER := "+GetBuildInfo.getBrand();
		return idata;
	}
	
}
