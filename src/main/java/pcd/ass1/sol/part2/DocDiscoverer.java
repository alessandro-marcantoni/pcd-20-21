package pcd.ass1.sol.part2;

import java.io.File;

import pcd.ass1.sol.part1.BasicAgent;
import pcd.ass1.sol.part1.BoundedBuffer;

public class DocDiscoverer extends BasicAgent {

	private File startDir;
	private BoundedBuffer<File> docFiles;
	private int nDocsFound;
	private Flag stopFlag;
	
	public DocDiscoverer(File dir, BoundedBuffer<File> docFiles, Flag stopFlag) {
		super("doc-discoverer");
		this.startDir = dir;	
		this.docFiles = docFiles;
		this.stopFlag = stopFlag;
	}
	
	public void run() {
		log("started.");
		nDocsFound = 0;
		if (startDir.isDirectory()) {
			explore(startDir);
			docFiles.close();
			if (stopFlag.isSet()) {
				log("job done - " + nDocsFound + " docs found.");
			} else {
				log("stopped.");
			}
		}
	}
	
	private void explore(File dir) {
		if (!stopFlag.isSet()) {
			for (File f: dir.listFiles()) {
				if (f.isDirectory()) {
					explore(f);
				} else if (f.getName().endsWith(".pdf")) {
					try {
						logDebug("find a new doc: " + f.getName());
						docFiles.put(f);
						nDocsFound++;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
