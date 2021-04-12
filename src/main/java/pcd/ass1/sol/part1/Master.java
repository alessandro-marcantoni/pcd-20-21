package pcd.ass1.sol.part1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Master extends BasicAgent {

	private File configFile;
	private File startDir;
	private int numMostFreqWords;
	
	private WordFreqMap wordsFreqMap;
	private HashMap<String,String> wordsToDiscard;
	
	/* performance tuning params */
	
	private int nDocLoaderAgents;
	private int nTextAnalyzerAgents;
	private static final int docFilesBufferSize = 100;
	private static final int chunksBufferSize = 100;

	public Master(File configFile, File dir, int numMostFreqWords) {
		super("master");
		this.configFile = configFile;
		this.startDir = dir;
		this.numMostFreqWords = numMostFreqWords;
	}
	
	public void run() {
		log("started.");
		try {			
			nDocLoaderAgents = Runtime.getRuntime().availableProcessors();
			nTextAnalyzerAgents = Runtime.getRuntime().availableProcessors();
			
			long t0 = System.currentTimeMillis();
			
			wordsFreqMap = new WordFreqMap();

			loadWordsToDiscard(configFile);

			/* spawn discoverer */
			
			BoundedBuffer<File> docFiles = new BoundedBuffer<File>(docFilesBufferSize);
			DocDiscoverer docDiscoverer = new DocDiscoverer(startDir, docFiles);
			docDiscoverer.start();

			/* spawn doc loaders */
			
			BoundedBuffer<String> chunks = new BoundedBuffer<String>(chunksBufferSize);
			Latch allDocsLoaded = new Latch(nDocLoaderAgents);
			for (int i = 0; i < nDocLoaderAgents; i++) {
				new DocLoader("" + i, docFiles, chunks, allDocsLoaded).start();
			}
			
			/* spawn doc analyzers */
			
			Latch allDocsAnalyzed = new Latch(nTextAnalyzerAgents);
			for (int i = 0; i < nTextAnalyzerAgents; i++) {
				new TextAnalyzer("" + i, wordsToDiscard, chunks, wordsFreqMap, allDocsAnalyzed).start();
			}
			
			/* wait for loaders to complete */
			allDocsLoaded.await();
			log("loaders done.");

			/* no more chunks will be added */
			chunks.close();

			/* wait for analyzers to complete */
			allDocsAnalyzed.await();
			log("analyzers done.");

			long t1 = System.currentTimeMillis();

			/* elab the results */
			
			elabMostFreqWords();
			
			long t2 = System.currentTimeMillis();
			
			log("done in " + (t2-t0) + " - sorting took: " + (t2-t1));

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
		Set<Map.Entry<String, Integer>> set = wordsFreqMap.getWords().entrySet();
		log("dictionary size: " + set.size());
		log("sorting...");
		
		ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();
		list.addAll(set);
		
		list.sort((Map.Entry<String,Integer> e1, Map.Entry<String,Integer> e2) -> {
			return Integer.compare(e2.getValue().intValue(), e1.getValue().intValue());
		});
		
		for (int i = 0; i < numMostFreqWords && i < list.size(); i++) {
			String key = list.get(i).getKey();
			System.out.println(" " + (i+1) + " - " +  key + " " + list.get(i).getValue());
		}		
	}

}
