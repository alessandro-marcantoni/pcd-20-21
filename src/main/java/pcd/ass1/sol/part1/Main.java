package pcd.ass1.sol.part1;

import java.io.File;

/**
 * 
 * Assignment #01 - Part 1
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
			File dir = new File("test/ass01/data-ita");
			File configFile = new File("test/ass01/data-ita/config.txt");
			int numMostFreqWords = 10;
			
			/*
			File dir = new File(args[0]);
			int numMostFreqWords = Integer.parseInt(args[1]);
			File configFile = new File(args[2]);
			*/
			Master coord = new Master(configFile, dir, numMostFreqWords);
			coord.start();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
