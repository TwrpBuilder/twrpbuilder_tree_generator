package mkTree;

import java.io.File;

import util.FWriter;
import util.GetBuildInfo;
import util.ShellExecuter;

public class MkBoardConfig {
	private String idata;
	private GetBuildInfo info;

	public MkBoardConfig(){
		new FWriter("BoardConfig.mk",getData());

	}
	
	private String getData() {
		idata =ShellExecuter.CopyRight();
		idata+="LOCAL_PATH := device/"+info.getBrand()+File.separator+info.getCodename()+"\n" + 
				"\n" + 
				"TARGET_BOARD_PLATFORM := "+info.getPlatform()+"\n" + 
				"TARGET_BOOTLOADER_BOARD_NAME := "+info.getCodename()+"\n" + 
				"\n" + 
				"# Recovery\n" + 
				"TARGET_USERIMAGES_USE_EXT4 := true\n" + 
				"BOARD_RECOVERYIMAGE_PARTITION_SIZE := "+info.getSize()+" \n" + 
				"BOARD_FLASH_BLOCK_SIZE := 1000000\n" + 
				"BOARD_HAS_NO_REAL_SDCARD := true\n" + 
				"TW_EXCLUDE_SUPERSU := true\n"
				+ "include $(LOCAL_PATH)/kernel.mk\n";
		if(info.getApi()=="armeabi-v7a")
		{
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk";
		}else if(info.getApi()=="arm64-v8a")
		{
			idata+="include device/generic/twrpbuilder/BoardConfig64.mk";
		}else {
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk";
		}
		return idata;
	}
	
}
