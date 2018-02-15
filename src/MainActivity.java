
import java.io.File;

public class MainActivity {
	
	private static int status;
	public static void main(String[] a)
	{
		try {
			System.out.println("Building tree from: "+a[0]);
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
