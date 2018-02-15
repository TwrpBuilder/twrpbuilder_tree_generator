package util;
import mkTree.MkAndroid;
import mkTree.MkAndroidProducts;
import mkTree.MkBoardConfig;
import mkTree.MkFstab;
import mkTree.MkOmni;
import mkTree.MkKernel;

public class RunCode  implements Runnable{

	private static String name;
	public static int count = 0;

	public RunCode(String name) {
		RunCode.name=name;
	}
	
    private volatile boolean flag = true;

	@Override
	public void run() {
		new ExtractBackup(name);
        while(flag) {
    		ShellExecuter.mkdir(GetBuildInfo.getCodename());
    		new MkAndroid();
    		new MkAndroidProducts();
    		new MkOmni();
    		if(GetBuildInfo.getPlatform().charAt(0)=='m' && GetBuildInfo.getPlatform().charAt(1) == 't')
    		{
        		new GetAsset("unpack-MTK.pl");
    		}else {
    		new GetAsset("umkbootimg");
    		}
    		new MkKernel();
    		new MkBoardConfig();
    		new MkFstab();
    		flag=false;
         }
		new Clean();
	}
	
	public static String getName() {
		return name;
	}
	
	public static int getCount() {
		count++;
		return count;
	}

}
