package pcd.lab03.monitors.latch;

import java.util.concurrent.locks.*;

/*
 * Latch - to be implemented
 */
public class LatchImpl2 implements Latch {

	private int count;
	private Lock mutex;
	private Condition gateOpened;
	
	public LatchImpl2(int count) {
		this.count = count;
		mutex = new ReentrantLock();
		gateOpened = mutex.newCondition();
	}
	
	@Override
	public void await() throws InterruptedException {	
		try {
			mutex.lock();
			while (count > 0) {
				gateOpened.await();
			}
		} finally {
			mutex.unlock();
		}
	}

	@Override
	public void countDown() {	
		try {
			mutex.lock();
			if (count > 0) {
				count--;
				if (count == 0) {
					gateOpened.signalAll();
				}
			}
		} finally {
			mutex.unlock();
		}
	}

	
}
