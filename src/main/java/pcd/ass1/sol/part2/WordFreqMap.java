package pcd.ass1.sol.part2;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class WordFreqMap {

	private HashMap<String, Integer> freqs;
	private LinkedList<AbstractMap.SimpleEntry<String, Integer>> mostFreq;
	private int numMostFreq;
	
	public WordFreqMap(int nMostFreq) {
		freqs = new HashMap<String, Integer>();
		mostFreq = new LinkedList<AbstractMap.SimpleEntry<String, Integer>>();
		this.numMostFreq = nMostFreq;
		for (int i = 0; i < numMostFreq; i++) {
			mostFreq.add(new AbstractMap.SimpleEntry<String,Integer>("",0));
		}
	}
	
	public synchronized void add(String word) {
		Integer nf = freqs.get(word);
		int freq = 1;
		if (nf == null) {
			freqs.put(word, 1);
		} else {
			freqs.put(word, nf + 1);
			freq = nf + 1;
		}
				
		int index = 0;
		boolean alreadyPresent = false;
		Iterator<AbstractMap.SimpleEntry<String, Integer>> it = mostFreq.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> el = it.next();
			if (el.getKey().equals(word)) {
				it.remove();
				alreadyPresent = true;
			} else if (freq > el.getValue()) {
				index++;
			} else {
			    break;
			}
		}
		
		if (alreadyPresent) {
			AbstractMap.SimpleEntry<String, Integer> el = new AbstractMap.SimpleEntry<String, Integer>(word, freq);
			mostFreq.add(index, el); 	
		} else if (index > 0) {
			AbstractMap.SimpleEntry<String, Integer> el = new AbstractMap.SimpleEntry<String, Integer>(word, freq);
			mostFreq.add(index, el); 	
			mostFreq.removeFirst();
		}		
	}
	
	public synchronized HashMap<String, Integer> getWords(){
		return freqs;
	}
	
	public synchronized Object[] getCurrentMostFreq(){
		return (Object[]) mostFreq.toArray();
	}
	
}
