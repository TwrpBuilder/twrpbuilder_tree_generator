package mkTree;

import util.ShellExecuter;
import util.FWriter;
import util.GetBuildInfo;

public class MkAndroid {
	private String idata;
	
	public MkAndroid() {
		System.out.println("Making Android.mk");
		new FWriter("Android.mk",getData());

	}
	
	private String getData() {
		idata =ShellExecuter.CopyRight();
		idata+="ifneq ($(filter "+GetBuildInfo.getCodename()+",$(TARGET_DEVICE)),)\n" + 
				"\n" + 
				"LOCAL_PATH := device/"+GetBuildInfo.getPath()+"\n" + 
				"\n" + 
				"include $(call all-makefiles-under,$(LOCAL_PATH))\n" + 
				"\n" + 
				"endif";
		return idata;
	}
	
}
