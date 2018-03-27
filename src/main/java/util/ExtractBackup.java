package util;

public class ExtractBackup {

	private String rooted;
	private String not_rooted;
	private ShellExecutor shell;
	public ExtractBackup(String name) {
		shell=new ShellExecutor();
		rooted= shell.commandnoapp("file --mime-type "+name+" | grep -w 'gzip'  | cut -d / -f 2 | cut -d \"-\" -f 2");
		not_rooted= shell.commandnoapp("file --mime-type "+name+" | grep -w 'zip'  | cut -d / -f 2 | cut -d \"-\" -f 2");
		if(rooted.equals("gzip"))
		{
			extractGzip(name);
			System.out.println("Archive type gzip");
		}
		else if(not_rooted.equals("zip"))
		{
			extractZip(name);
			System.out.println("Archive type zip");
		}
	
		}

	private void extractGzip(String file) {
		shell.command("tar -xvf " + file);
	}
	
	private void extractZip(String file) {
		shell.command("unzip -o "+ file);
		shell.command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' "+GetBuildInfo.propFile()+"  | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
		new GetBuildInfo();
	}
}
