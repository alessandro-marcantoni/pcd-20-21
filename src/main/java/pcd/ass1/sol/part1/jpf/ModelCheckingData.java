package pcd.ass1.sol.part1.jpf;

import java.util.ArrayList;
import java.util.Map;

public class ModelCheckingData {

	private static ModelCheckingData instance;
	
	public static ModelCheckingData getInstance() {
		synchronized(ModelCheckingData.class) {
			if (instance == null) {
				instance = new ModelCheckingData();
			}
			return instance;
		}
	}
	
	private ModelCheckingData() {}

	public File genMockDirData() {
		File[] files = new File[] {
				new  FileMockImpl("a.pdf"),
				new  FileMockImpl("b.pdf"),
				new  FileMockImpl("c.pdf")
		};
		File dir = new DirMockImpl(files);
		return dir;
	}

	public String[] genMockChunks() {
		return new String[] {
		        "alfa beta alfa gamma",
		        "alfa beta alfa gamma"
		};
	}

	public void checkResults(ArrayList<Map.Entry<String, Integer>> list) {
		Map.Entry<String, Integer> e0 = list.get(0);
		Map.Entry<String, Integer> e1 = list.get(1);
		Map.Entry<String, Integer> e2 = list.get(2);
		
		assert ((e0.getKey().equals("alfa") && e0.getValue() == 12) &&
			(e1.getKey().equals("beta") && e1.getValue() == 6) &&
			(e2.getKey().equals("gamma") && e2.getValue() == 6));
	}
}
