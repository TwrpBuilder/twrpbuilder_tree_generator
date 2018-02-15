
import java.io.File;

import mkTree.MkAndroid;
import mkTree.MkAndroidProducts;
import mkTree.MkOmni;
import mkTree.MkKernel;
import util.Clean;
import util.ExtractBackup;
import util.GetAsset;
import util.ShellExecuter;
import util.GetBuildInfo;

public class MainActivity {
	
	private static int status;
	private static Runnable extract;
	private static Thread tExtract;
	private static String name;
	public static void main(String[] a)
	{
		try {
			System.out.println("Building tree from: "+a[0]);
			name=a[0];
			if (new File(a[0]).exists())
			{
			new Thread(new RunCode(a[0])).start();
			}else {
				System.out.println("File not found");
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("invalid file name");
			System.exit(status);
		}		
		
	}	
}
