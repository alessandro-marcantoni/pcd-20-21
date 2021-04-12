package pcd.ass1.sol.part1.jpf;

public class DocDiscoverer extends BasicAgent {

	private File startDir;
	private BoundedBuffer<File> docFiles;
	private int nDocsFound;
	
	public DocDiscoverer(File dir, BoundedBuffer<File> docFiles) {
		super("doc-discoverer");
		this.startDir = dir;	
		this.docFiles = docFiles;
	}
	
	public void run() {
		// log("started.");
		nDocsFound = 0;
		if (startDir.isDirectory()) {
			explore(startDir);
			docFiles.close();
			// log("job done - " + nDocsFound + " docs found.");
		}
	}
	
	private void explore(File dir) {
		for (File f: dir.listFiles()) {
			if (f.isDirectory()) {
				explore(f);
			} else if (f.getName().endsWith(".pdf")) {
				try {
					// logDebug("find a new doc: " + f.getName());
					docFiles.put(f);
					nDocsFound++;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
