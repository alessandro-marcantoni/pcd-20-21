package pcd.ass1.sol.part2;

import java.io.File;

/**
 * Controller part of the application - passive part.
 * 
 * @author aricci
 *
 */
public class Controller implements InputListener {

	private Flag stopFlag;
	private View view;
	
	public Controller(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public synchronized void started(File dir, File wordsFile, int nMostFreqWords){
		stopFlag.reset();
		Master coord = new Master(wordsFile, dir, nMostFreqWords,stopFlag, view);
		coord.start();
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}
