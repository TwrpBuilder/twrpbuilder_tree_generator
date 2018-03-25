package util;
import mkTree.MakeTree;

public class RunCode  implements Runnable{

	private static String name;
	private boolean mrvl;
	private static String type;
	private boolean mtk;
	private boolean samsung;
	public static boolean extract;
	private ShellExecutor shell;
	public RunCode(String name) {
		RunCode.name=name;
        new GetAsset("umkbootimg");
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
	}
	
	
	@Override
	public void run() {
		if(extract)
		{
		new ExtractBackup(name);
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

		new Clean();
	}
	
	public static String getName() {
		return name;
	}

}
