package pcd.ass1.sol.part1.jpf;

public interface File {

	boolean isDirectory();
	File[] listFiles();
	String getName();
	
}
