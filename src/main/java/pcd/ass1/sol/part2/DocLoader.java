package pcd.ass1.sol.part2;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import pcd.ass1.sol.part1.BasicAgent;
import pcd.ass1.sol.part1.BoundedBuffer;
import pcd.ass1.sol.part1.ClosedException;
import pcd.ass1.sol.part1.Latch;

public class DocLoader extends BasicAgent {

	private BoundedBuffer<File> docFiles;
	private BoundedBuffer<String> chunks;
	private PDFTextStripper stripper;
	private Latch loadersLatch;
	private Flag stopFlag;
	
	public DocLoader(String id, BoundedBuffer<File> docFiles, BoundedBuffer<String> chunks, Latch loadersLatch, Flag stopFlag) throws Exception  {
		super("doc-loader-" + id);
		this.stopFlag = stopFlag;
		this.docFiles = docFiles;
		this.chunks = chunks;
        stripper = new PDFTextStripper();
        this.loadersLatch = loadersLatch;
	}
	
	public void run() {
		log("started");
		int nJobs = 0;
		boolean noMoreDocs = false;
		while (!noMoreDocs) {
			try {
				File doc = docFiles.get();
				if (!stopFlag.isSet()) {
					nJobs++;
					logDebug("got a doc to load: " + doc.getName() + " - job: " + nJobs);
					try {
						loadDoc(doc);
						logDebug("job " + nJobs + " done.");
					} catch (Exception ex) {
						log("error in processing the " + nJobs + " doc.");
					}
				} else {
					log("stopped");
				}
			} catch (ClosedException ex) {
				log("no more docs.");
				noMoreDocs = true;
			}
		}
		loadersLatch.countDown();
		log("done.");
	}
	
	private void loadDoc(File doc) throws Exception {
        PDDocument document = PDDocument.load(doc);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent())
        {
            throw new IOException("You do not have permission to extract text");
        }
        logDebug("doc loaded.");

        int nPages = document.getNumberOfPages();
        logDebug("Chunks to be processed: " + nPages);
        for (int i = 0; i < nPages; i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String chunk =  stripper.getText(document);
			chunks.put(chunk);	
			logDebug("chunk added (" + i + ")");
        }
  	}
}
