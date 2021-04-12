package pcd.ass1.seq;

import java.util.HashMap;

public class WordFreqMap {

	private HashMap<String, Integer> freqs;
	
	public WordFreqMap() {
		freqs = new HashMap<String, Integer>();
	}
	
	public  void add(String word) {
		Integer nf = freqs.get(word);
		if (nf == null) {
			freqs.put(word, 1);
		} else {
			freqs.put(word, nf + 1);
		}
	}
	
	public HashMap<String, Integer> getWords(){
		return freqs;
	}
	
}
