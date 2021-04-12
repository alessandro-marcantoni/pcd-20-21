package pcd.ass1.sol.part2;

import java.io.File;

public interface InputListener {

	void started(File dir, File wordsFile, int nMostFreqWords);
	
	void stopped();
}
