package pcd.ass1.seq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

public class DocAnalyzer {

	private PDFTextStripper stripper;
	private int nChunks;
	private WordFreqMap map;
	private HashMap<String, String> wordsToDiscMap;
	private int numMostFreqWords;
	
	public static void main(String[] args) {
		
		try {
			
			File dir = new File("test/ass01/data-ita");
			File configFile = new File("test/ass01/data-ita/config.txt");
			int numMostFreqWords = 10;
			
			// File dir = new File(args[0]);
			// int numMostFreqWords = Integer.parseInt(args[1]);
			// File configFile = new File(args[2]);
			
			new DocAnalyzer().main(dir, configFile, numMostFreqWords);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void main(File dir, File configFile, int numMostFreqWords) {
		try {
			long t0 = System.currentTimeMillis();
			
	        stripper = new PDFTextStripper();
			wordsToDiscMap = new HashMap<String,String>();

			nChunks = 0;
			
			map = new WordFreqMap();

			setup(configFile);

			explore(dir);
			
			long t1 = System.currentTimeMillis();
			Set<Map.Entry<String, Integer>> set = map.getWords().entrySet();
			log("dictionary size: " + set.size());
			log("sorting...");
			
			ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();
			list.addAll(set);
			
			list.sort((Map.Entry<String,Integer> e1, Map.Entry<String,Integer> e2) -> {
				return Integer.compare(e2.getValue().intValue(), e1.getValue().intValue());
			});
			
			for (int i = 0; i < numMostFreqWords; i++) {
				String key = list.get(i).getKey();
				System.out.println(" " + (i+1) + " - " +  key + " " + list.get(i).getValue());
			}
			
			long t2 = System.currentTimeMillis();
			log2("done in " + (t2-t0) + " - sorting took: " + (t2-t1));
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	 	
	private void setup(File configFile)  {
		try {
			log("setup.");
			FileReader fr = new FileReader(configFile);
			BufferedReader br = new BufferedReader(fr);
			br.lines().forEach(w -> {
		    	wordsToDiscMap.put(w, w);
			});			
			fr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			log("setup done.");
		}
	}	
		
	private void explore(File dir) {
		for (File f: dir.listFiles()) {
			if (f.isDirectory()) {
				explore(f);
			} else if (f.getName().endsWith(".pdf")) {
					log("find a new doc: " + f.getName());
					File doc = f;
					try {
						elabDoc(doc);
					} catch (Exception ex) {
						ex.printStackTrace();
						log("error in processing the doc.");
					}
			}
		}
	}

	private void elabDoc(File doc) throws Exception {
        PDDocument document = PDDocument.load(doc);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent())
        {
            throw new IOException("You do not have permission to extract text");
        }
        log("doc loaded.");

        int nPages = document.getNumberOfPages();
        log("Chunks to be processed: " + nPages);
        for (int i = 0; i < nPages; i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String chunk =  stripper.getText(document);
			elabChunk(chunk);	
			log("chunk added (" + i + ")");
        }
  	}
	
	private void elabChunk(String chunk) {
		log("got a chunk.");
		nChunks++;
	    String del = "[\\x{201D}\\x{201C}\\s'\", ?.@;:!-]+";
		String[] words = chunk.split(del);
		for (String w: words) {
			String w1 = w.trim().toLowerCase();
			if (!w1.equals("") && !wordsToDiscMap.containsKey(w1)) {
				map.add(w1);
			}
		}
		
	}
	
	protected void log(String msg) {
		// System.out.println("[ analyzer] " + msg);
	}

	protected void log2(String msg) {
		System.out.println("[ analyzer] " + msg);
	}
}
