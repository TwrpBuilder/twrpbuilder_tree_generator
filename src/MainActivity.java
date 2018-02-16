
import java.io.File;
import util.RunCode;

public class MainActivity {
	
	private static int status;
	private static File fname;
	public static void main(String[] a)
	{
		try {
			fname=new File(a[0]);
			if (fname.exists())
			{
				if(!fname.getName().toString().contains(" "))
				{
					System.out.println("Building tree using: "+fname.getName());
					new Thread(new RunCode(fname.getName())).start();
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
