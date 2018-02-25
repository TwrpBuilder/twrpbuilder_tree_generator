package mkTree;

import util.FWriter;
import util.GetBuildInfo;
import util.ShellExecuter;

public class MkBoardConfig {
	private boolean samsung;
	private String idata =ShellExecuter.CopyRight();
	public MkBoardConfig(){
		new FWriter("BoardConfig.mk",getData());

	}
	public MkBoardConfig(String type){
		if(type.equals("mrvl"))
		{
			idata+="include device/generic/twrpbuilder/mrvl.mk\n";
		}else if(type.equals("mt") || type.equals("mtk")){
			idata+="include device/generic/twrpbuilder/mtk.mk\n";
		}else if(type.equals("samsung"))
		{
			samsung=true;
		}

		new FWriter("BoardConfig.mk",getData());
	
	}
	
	private String getData() {
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
	
			System.out.println("found "+ GetBuildInfo.getPlatform() + " platform" );
	
			if(samsung==true)
			{
				idata+="BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/seEnforcing.mk\n";	
			}
			
		if(GetBuildInfo.getApi().equals("armeabi-v7a"))
		{
			System.out.println("Found 32 bit arch");
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk";
		}else if(GetBuildInfo.getApi().equals("arm64-v8a"))
		{
			System.out.println("Found 64 bit arch");
			idata+="include device/generic/twrpbuilder/BoardConfig64.mk";
		}else {
			System.out.println("no arch defined using 32 bit");
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk";
		}
		return idata;
	}
	
}
