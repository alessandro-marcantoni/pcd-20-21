package pcd.ass1.sol.part1.jpf;

import java.util.HashMap;

import gov.nasa.jpf.vm.Verify;

public class TextAnalyzer extends BasicAgent {

	private BoundedBuffer<String> chunks;
	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	private Latch analyzersLatch;
	
	public TextAnalyzer(String id, HashMap<String,String> wordsToDiscard, BoundedBuffer<String> chunks, WordFreqMap map, Latch analyzersLatch) throws Exception  {
		super("text-analyzer-" + id);
		this.chunks = chunks;
		this.wordsToDiscard = wordsToDiscard;
		this.map = map;
		this.analyzersLatch = analyzersLatch;
	}
	
	public void run() {
		
		// log("started.");
		try {		    
		    String del = "[\\x{201D}\\x{201C}\\s'\", ?.@;:!-]+";
	
		    long nChunks = 0;
		    boolean noMoreWork = false;
		    
			while (!noMoreWork) {
				try {
					String chunk = chunks.get();	
					// logDebug("got a chunk.");
					Verify.beginAtomic();
					nChunks++;					
					String[] words = chunk.split(del);
					Verify.endAtomic();
					for (String w: words) {
						// String w1 = w.trim().toLowerCase();
						// if (!w1.equals("") && !wordsToDiscard.containsKey(w1)) {
							map.add(w);
						// }
					}
					// logDebug("chunk " + nChunks + " processed.");
				} catch (ClosedException ex) {
					// logDebug("no more work to do.");
					noMoreWork = true;
				}
			}
			analyzersLatch.countDown();
			// log("done.");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
