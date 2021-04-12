package pcd.ass1.sol.part2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.HashMap;

import pcd.ass1.sol.part1.BasicAgent;
import pcd.ass1.sol.part1.BoundedBuffer;
import pcd.ass1.sol.part1.Latch;

public class Master extends BasicAgent {

	private File configFile;
	private File dir;
	private int numMostFreqWords;
	
	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	
	private Flag stopFlag;
	
	private View view;
	
	/* performance tuning params */
	
	private int nDocLoaderAgents;
	private int nTextAnalyzerAgents;
	private static final int docFilesBufferSize = 100;
	private static final int chunksBufferSize = 100;
	
	public Master(File configFile, File dir, int numMostFreqWords, Flag stopFlag, View view) {
		super("master");
		this.configFile = configFile;
		this.dir = dir;
		this.numMostFreqWords = numMostFreqWords;
		this.view = view;
		this.stopFlag = stopFlag;
	}
	
	public void run() {
		log("started.");
		try {
			nDocLoaderAgents = Runtime.getRuntime().availableProcessors();
			nTextAnalyzerAgents = Runtime.getRuntime().availableProcessors();
			
			long t0 = System.currentTimeMillis();
			
			map = new WordFreqMap(numMostFreqWords);

			Flag done = new Flag();
			Viewer viewer = new Viewer(map,view,done);
			viewer.start();
			
			loadWordsToDiscard(configFile);

			/* spawn discoverer */
			
			BoundedBuffer<File> docFiles = new BoundedBuffer<File>(docFilesBufferSize);
			DocDiscoverer docDiscoverer = new DocDiscoverer(dir, docFiles, stopFlag);
			docDiscoverer.start();

			/* spawn doc loaders */
			
			BoundedBuffer<String> chunks = new BoundedBuffer<String>(chunksBufferSize);
			Latch allDocsLoaded = new Latch(nDocLoaderAgents);
			for (int i = 0; i < nDocLoaderAgents; i++) {
				new DocLoader("" + i, docFiles, chunks, allDocsLoaded, stopFlag).start();
			}
			
			/* spawn doc analyzers */
			
			Latch allChunksAnalyzed = new Latch(nTextAnalyzerAgents);
			for (int i = 0; i < nTextAnalyzerAgents; i++) {
				new TextAnalyzer("" + i, wordsToDiscard, chunks, map, allChunksAnalyzed, stopFlag).start();
			}
			
			/* wait for loaders to complete */
			allDocsLoaded.await();
			log("loaders done.");

			/* no more chunks will be added */
			chunks.close();

			/* wait for analyzers to complete */
			allChunksAnalyzed.await();
			log("analyzers done.");
						
			long t2 = System.currentTimeMillis();
			done.set();
			view.done();
			
			elabMostFreqWords();
			
			log("done in " + (t2-t0));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	private void loadWordsToDiscard(File configFile) {
		try {
			wordsToDiscard = new HashMap<String,String>();
			FileReader fr = new FileReader(configFile);
			BufferedReader br = new BufferedReader(fr);
			br.lines().forEach(w -> {
				wordsToDiscard.put(w, w);
			});			
			fr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}	
	
	private void elabMostFreqWords() {
		Object[] freqs = map.getCurrentMostFreq();
		for (int i = numMostFreqWords-1; i >=0; i--) {
			AbstractMap.SimpleEntry<String, Integer> el = (AbstractMap.SimpleEntry<String, Integer>) freqs[i];
			String key = el.getKey();
			System.out.println(" " + (i+1) + " - " +  key + " " + el.getValue());
		}		
	}

}
