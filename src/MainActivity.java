
import java.io.File;

import util.RunCode;
import util.ShellExecuter;

public class MainActivity {
	
	private static int status;
	private static File fname;
	private static String rename;
	private static String name;
	public static void main(String[] a)
	{
		fname=new File(a[0]);
		name=fname.toString();
		if(name.contains(" "))
		{
			rename=name.replaceAll(" ","_");
			fname=new File(rename);
			if (!new File(rename).exists())
			{
			ShellExecuter.commandnoapp("mv '"+name +"' "+rename);
			System.out.println("renaming file name to "+rename);
			}
			name=rename;
		}
		
		
		try {
			System.out.println("Building tree from: "+name);
			if (fname.exists())
			{
					new Thread(new RunCode(name)).start();

			}else {
				System.out.println("File not found");
				System.exit(1);
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("please enter file name");
			System.exit(status);
		}		

	}	
}
