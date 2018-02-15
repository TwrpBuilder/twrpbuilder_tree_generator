import mkTree.MkAndroid;
import mkTree.MkAndroidProducts;
import mkTree.MkBoardConfig;
import mkTree.MkFstab;
import mkTree.MkOmni;
import mkTree.MkKernel;
import util.Clean;
import util.ExtractBackup;
import util.GetAsset;
import util.GetBuildInfo;
import util.ShellExecuter;

public class RunCode  implements Runnable{

	private String name;
	
	public RunCode(String name) {
		this.name=name;
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

}
