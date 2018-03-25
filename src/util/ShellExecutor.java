package util;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class ShellExecutor {
	public static String command(String command) {
		Process process;
		String o=null;
		StringBuilder sb=null;
	    sb=new StringBuilder();
	    String[] commands = new String[]{"/bin/bash", "-c", command};

	    try {
			process = Runtime.getRuntime().exec(commands);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(        
			        process.getInputStream()));                
			    while ((o = reader.readLine()) != null) {                                
			    	sb.append(o+"\n");
			    }  
				return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.exit(1);
			return null;
		}                    
	}

	public static String commandnoapp(String command) {
		Process process;
		String o=null;
		StringBuilder sb=null;
	    sb=new StringBuilder();
	    String[] commands = new String[]{"/bin/bash", "-c", command};

	    try {
			process = Runtime.getRuntime().exec(commands);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(        
			        process.getInputStream()));                
			    while ((o = reader.readLine()) != null) {                                
			    	sb.append(o);
			    }  
				return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.exit(1);
			return null;
		}                    
	}

	
	public static String CopyRight() {
		String copy="#\n" + 
				"# Copyright (C) 2018 The TwrpBuilder Open-Source Project\n" + 
				"#\n" + 
				"# Licensed under the Apache License, Version 2.0 (the \"License\");\n" + 
				"# you may not use this file except in compliance with the License.\n" + 
				"# You may obtain a copy of the License at\n" + 
				"#\n" + 
				"# http://www.apache.org/licenses/LICENSE-2.0\n" + 
				"#\n" + 
				"# Unless required by applicable law or agreed to in writing, software\n" + 
				"# distributed under the License is distributed on an \"AS IS\" BASIS,\n" + 
				"# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" + 
				"# See the License for the specific language governing permissions and\n" + 
				"# limitations under the License.\n" + 
				"#\n" + 
				"\n";
		return copy;
	}
	
	public static boolean mkdir(String name) {
		File theDir = new File(name).getAbsoluteFile();

		// if the directory does not exist, create it
		if (!theDir.isDirectory()) {
		    System.out.println("creating directory: " + theDir.getAbsolutePath());
		    boolean result = false;

		    try{
		        theDir.mkdirs();
		        result = true;
		    } 
		    catch(SecurityException se){
		    	System.out.println("Failed to make dir");
		    	System.exit(0);
		    }        
		    if(result) {    
		        System.out.println("Dir created: "+name);  
		    }
		}else
		{
	        System.out.println("Dir: "+name+" already exist");  
		}
		return theDir.isDirectory();

	}

	public static boolean rm(String filename){
		File file=new File(filename);
		if (file.exists())
		{
			if (file.isDirectory())
			{
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				file.delete();
			}
		}
		return false;
	}

	public static void cp( String from, String to ) {
		File f=new File(from);
		File t=new File(to); 
		if(t.exists())
		{
			command("rm "+ t.getAbsolutePath());
		}
	    try {
			Files.copy( f.toPath(), t.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	
}
