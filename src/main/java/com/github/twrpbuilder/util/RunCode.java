package com.github.twrpbuilder.util;
import com.github.twrpbuilder.mkTree.MakeTree;

public class RunCode  implements Runnable{

	private static String name;
	private boolean mrvl;
	private static String type;
	private boolean mtk;
	private boolean samsung;
	public static boolean extract;
	private ShellExecutor shell;
	public static boolean AndroidImageKitchen;
	public RunCode(String name) {
		RunCode.name=name;
        if (AndroidImageKitchen)
		{
			System.out.println("Using Android Image Kitchen to extract "+name);
			new GetAsset("bin");
			new GetAsset("unpackimg.sh");
		}else {
			new GetAsset("umkbootimg");
			new GetAsset("magic");
		}
	}

	public RunCode(String name,String type) {
		RunCode.name=name;
		RunCode.type=type;
		shell=new ShellExecutor();

		if(type.equals("mrvl"))
    	{
    		new GetAsset("degas-umkbootimg");
    		shell.commandnoapp("mv degas-umkbootimg umkbootimg ");
    		mrvl=true;
    	}
		else if(type.equals("mt") || type.equals("mtk"))
    	{
    		new GetAsset("unpack-MTK.pl");
    		shell.commandnoapp("mv unpack-MTK.pl umkbootimg");
    		mtk=true;
    	}
		else if(type.equals("samsung"))
    	{
            new GetAsset("umkbootimg");
            samsung=true;
    	}
		if (AndroidImageKitchen)
		{
			new GetAsset("bin");
			new GetAsset("unpackimg.sh");
		}
	}


	@Override
	public void run() {
		if(extract)
		{
		new ExtractBackup(name);
		}
		if (AndroidImageKitchen) {
			MakeTree.AndroidImageKitchen = true;
		}
		if(mtk==true )
            {
                new MakeTree(true,type);
            }else if (mrvl==true || samsung==true){
                new MakeTree(false,type);
            }
            else{
                new MakeTree(false,"none");
        }
	}

	public static String getName() {
		return name;
	}

}
