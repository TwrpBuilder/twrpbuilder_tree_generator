package util;

import java.io.File;

public class Clean {
	
	public Clean(){
		ShellExecuter.commandnoapp("rm -rf build.prop recovery.img " );
		if (new File("mounts").exists())
		{
			ShellExecuter.commandnoapp("rm mounts ");
		}
		if (new File("umkbootimg").exists())
		{
			ShellExecuter.commandnoapp("rm umkbootimg");
		}
		
		if (new File(Config.outDir).exists())
		{
			ShellExecuter.commandnoapp("rm -rf "+Config.outDir);
		}
		
		if(new File("unpack-MTK.pl").exists())
		{
			ShellExecuter.command("rm unpack-MTK.pl");
		}
	}

}
