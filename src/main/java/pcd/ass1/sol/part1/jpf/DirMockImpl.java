package pcd.ass1.sol.part1.jpf;

public class DirMockImpl implements File {

	private File[] files;
	
	public DirMockImpl(File[] files) {
		this.files = files;
	}
	
	public boolean isDirectory() {
		return true;
	}

	public File[] listFiles() {
		return files;
	}

	public String getName() {
		return null;
	}

	
}
