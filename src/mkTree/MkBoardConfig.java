package mkTree;

import util.FWriter;
import util.GetBuildInfo;
import util.ShellExecuter;

public class MkBoardConfig {
	private String idata;

	public MkBoardConfig(){
		new FWriter("BoardConfig.mk",getData());

	}
	
	private String getData() {
		idata =ShellExecuter.CopyRight();
		idata+="LOCAL_PATH := "+GetBuildInfo.getPath()+"\n" + 
				"\n" + 
				"TARGET_BOARD_PLATFORM := "+GetBuildInfo.getPlatform()+"\n" + 
				"TARGET_BOOTLOADER_BOARD_NAME := "+GetBuildInfo.getCodename()+"\n" + 
				"\n" + 
				"# Recovery\n" + 
				"TARGET_USERIMAGES_USE_EXT4 := true\n" + 
				"BOARD_RECOVERYIMAGE_PARTITION_SIZE := "+GetBuildInfo.getSize()+" \n" + 
				"BOARD_FLASH_BLOCK_SIZE := 1000000\n" + 
				"BOARD_HAS_NO_REAL_SDCARD := true\n" + 
				"TW_EXCLUDE_SUPERSU := true\n"
				+ "include $(LOCAL_PATH)/kernel.mk\n";
		if(GetBuildInfo.getApi()=="armeabi-v7a")
		{
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk";
		}else if(GetBuildInfo.getApi()=="arm64-v8a")
		{
			idata+="include device/generic/twrpbuilder/BoardConfig64.mk";
		}else {
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk";
		}
		return idata;
	}
	
}
