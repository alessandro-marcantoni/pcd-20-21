package pcd.lab03.monitors.resman;

public class ResManagerImpl implements ResManager {

	private int nResourcesAvailable;
	private int nResourcesUsed;
	private boolean[] resourceFreeMap;
	
	public ResManagerImpl(int nResourcesAvailable) {
		this.nResourcesAvailable = nResourcesAvailable;
		nResourcesUsed = 0;
		resourceFreeMap = new boolean[nResourcesAvailable];
		for (int i = 0; i < nResourcesAvailable; i++) {
			resourceFreeMap[i] = true;
		}
	}
	
	@Override
	public synchronized int get() throws InterruptedException {
		while (nResourcesUsed == nResourcesAvailable) {
			wait();
		}
		int availRes = getAvailRes();
		resourceFreeMap[availRes] = false;
		nResourcesUsed++;
		return availRes;
	}

	@Override
	public synchronized void release(int id) {
		resourceFreeMap[id] = true;
		nResourcesUsed--;
		notifyAll();
	}

	private int getAvailRes() {
		for (int i = 0; i < nResourcesAvailable; i++) {
			if (resourceFreeMap[i])
				return i;
		}
		return -1;
	}
}
