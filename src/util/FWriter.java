package util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FWriter {

	public FWriter(String name,String data)
	{
		run(name,data);
	}

	private void run(String name,String data) {
		  PrintWriter writer;
			try {
				writer = new PrintWriter(GetBuildInfo.getPathS()+name, "UTF-8");
				writer.println(data);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
}