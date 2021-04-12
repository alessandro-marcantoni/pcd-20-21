package pcd.ass1.sol.part1.jpf;


public class DocLoader extends BasicAgent {

	private BoundedBuffer<File> docFiles;
	private BoundedBuffer<String> chunks;
	private Latch loadersLatch;
	
	public DocLoader(String id, BoundedBuffer<File> docFiles, BoundedBuffer<String> chunks, Latch loadersLatch) throws Exception  {
		super("doc-loader-" + id);
		this.docFiles = docFiles;
		this.chunks = chunks;
        this.loadersLatch = loadersLatch;
	}
	
	public void run() {
		// log("started");
		int nJobs = 0;
		boolean noMoreDocs = false;
		while (!noMoreDocs) {
			try {
				File doc = docFiles.get();
				nJobs++;
				// logDebug("got a doc to load: " + doc.getName() + " - job: " + nJobs);
				try {
					loadDoc(doc);
					// logDebug("job " + nJobs + " done.");
				} catch (Exception ex) {
					// log("error in processing the " + nJobs + " doc.");
				}
			} catch (ClosedException ex) {
				// log("no more docs.");
				noMoreDocs = true;
			}
		}
		loadersLatch.countDown();
		// log("done.");
	}
	
	private void loadDoc(File doc) throws Exception {
        String[] mockChunks = ModelCheckingData.getInstance().genMockChunks();
        for (String chunk: mockChunks) {
			chunks.put(chunk);	
        }
  	}
}
