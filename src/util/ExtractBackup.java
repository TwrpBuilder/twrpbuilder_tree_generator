package util;

public class ExtractBackup {

	private String rooted;
	private String not_rooted;
	public ExtractBackup(String name) {
		rooted=ShellExecuter.commandnoapp("file --mime-type "+name+" | grep -w 'gzip'  | cut -d / -f 2 | cut -d \"-\" -f 2");
		not_rooted=ShellExecuter.commandnoapp("file --mime-type "+name+" | grep -w 'zip'  | cut -d / -f 2 | cut -d \"-\" -f 2");
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
		ShellExecuter.command("tar -xvf " + file);
		if(GetBuildInfo.getCodename().isEmpty())
		{
			ShellExecuter.command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' build.prop | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
			if(GetBuildInfo.getCodename().isEmpty())
			{
				System.out.println("Failed to get info");
				System.exit(0);
			}
		}
		new GetBuildInfo();
	}
	
	private void extractZip(String file) {
		ShellExecuter.command("unzip -o "+ file);
		ShellExecuter.command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' build.prop | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
		new GetBuildInfo();
	}
}
