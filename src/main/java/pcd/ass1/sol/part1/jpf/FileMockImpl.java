package pcd.ass1.sol.part1.jpf;

public class FileMockImpl implements File {

	private String name;
	
	public FileMockImpl(String name) {
		this.name = name;
	}
	
	public boolean isDirectory() {
		return false;
	}

	public File[] listFiles() {
		return new File[0];
	}

	public String getName() {
		return name;
	}

}
