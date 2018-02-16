package util;
import mkTree.MkAndroid;
import mkTree.MkAndroidProducts;
import mkTree.MkBoardConfig;
import mkTree.MkFstab;
import mkTree.MkOmni;
import mkTree.MkKernel;

public class RunCode  implements Runnable{

	private static String name;

	public RunCode(String name) {
		RunCode.name=name;
	}
	
	@Override
	public void run() {
		new ExtractBackup(name);
    	if(!ShellExecuter.mkdir(GetBuildInfo.getPathS()))
    	{
    		System.out.println("Failed to make dir");
    		System.exit(0);
    	}
    	new MkAndroid();
    	new MkAndroidProducts();
    	new MkOmni();
    	new GetAsset("umkbootimg");
    	new MkKernel();
    	new MkBoardConfig();
    	new MkFstab();
		new Clean();
	}
	
	public static String getName() {
		return name;
	}

}
