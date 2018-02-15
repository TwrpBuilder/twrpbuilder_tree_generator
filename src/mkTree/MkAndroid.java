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

public class MkAndroid {
	private GetBuildInfo info;
	private String idata;
	
	public MkAndroid() {
		System.out.println("Making Android.mk");
		new FWriter("Android.mk",getData());

	}
	
	private String getData() {
		idata =ShellExecuter.CopyRight();
		idata+="ifneq ($(filter "+info.getCodename()+",(TARGET_DEVICE)),)\n" + 
				"\n" + 
				"LOCAL_PATH := device/"+info.getBrand()+File.separator+info.getCodename()+"\n" + 
				"\n" + 
				"include $(call all-makefiles-under,$(LOCAL_PATH))\n" + 
				"\n" + 
				"endif";
		return idata;
	}
	
}
