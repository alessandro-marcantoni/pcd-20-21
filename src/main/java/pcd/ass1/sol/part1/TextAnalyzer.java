package pcd.ass1.sol.part1;

import java.util.HashMap;

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
		
		log("started.");
		try {		    
		    String del = "[\\x{201D}\\x{201C}\\s'\", ?.@;:!-]+";
	
		    long nChunks = 0;
		    boolean noMoreWork = false;
		    
			while (!noMoreWork) {
				try {
					String chunk = chunks.get();	
					logDebug("got a chunk.");
					nChunks++;
					String[] words = chunk.split(del);
					for (String w: words) {
						String w1 = w.trim().toLowerCase();
						if (!w1.equals("") && !wordsToDiscard.containsKey(w1)) {
							map.add(w1);
						}
					}
					logDebug("chunk " + nChunks + " processed.");
				} catch (ClosedException ex) {
					logDebug("no more work to do.");
					noMoreWork = true;
				}
			}
			analyzersLatch.countDown();
			log("done.");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
