package pcd.ass1.sol.part1.jpf;


/**
 * 
 * Assignment #01 - JPF version
 * 
 * doc:
 * https://docs.google.com/document/d/1v0XlJIEjCPZr87PIBaqROFcco8oNO-se_HO3GbM0gqU/edit?usp=sharing
 * 
 * @author aricci
 *
 */
public class Main {

	public static void main(String[] args) {
		
		try {
			int numMostFreqWords = 10;
			File configFile = new FileMockImpl("");

			File dir = ModelCheckingData.getInstance().genMockDirData();
			
			Master coord = new Master(configFile, dir, numMostFreqWords);
			coord.start();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
