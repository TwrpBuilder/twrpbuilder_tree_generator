
import java.io.File;
import util.RunCode;

public class MainActivity {
	
	private static int status;
	private static File fname;
	private static String name;
	public static void main(String[] a)
	{
		try {
			fname=new File(a[0]);
			if (fname.exists())
			{
				if(!fname.getName().toString().contains(" "))
				{
					System.out.println("Building tree using: "+name);
					new Thread(new RunCode(name)).start();
				}else
				{
					System.out.println("remove spaces from file name");
					System.exit(0);
				}

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
