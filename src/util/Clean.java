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
		
		if (new File("out").exists())
		{
			ShellExecuter.commandnoapp("rm -rf out");
		}
	}

}
