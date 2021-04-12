package pcd.ass1.sol.part1.jpf;

import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class BoundedBuffer<Item> {

	private LinkedList<Item> buffer;
	private Lock mutex;
	private Condition notEmptyOrClosed, notFull;
	private boolean closed;
	private int maxSize;

	public BoundedBuffer(int size) {
		this.maxSize = size;
		buffer = new LinkedList<Item>();
		mutex = new ReentrantLock();
		notEmptyOrClosed = mutex.newCondition();
		notFull = mutex.newCondition();
		closed = false;
	}

	public void put(Item item) throws InterruptedException {
		try {
			mutex.lock();
			while (isFull()) {
				notFull.await();
			}
			buffer.addLast(item);
			notEmptyOrClosed.signalAll();
		} finally {
			mutex.unlock();
		}
	}

	public Item get() throws ClosedException {
		try {
			mutex.lock();
			while (isEmpty() && !closed) {
				try {
					notEmptyOrClosed.await();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (isEmpty()) {
				throw new ClosedException();
			}
			Item item = buffer.removeFirst();
			notFull.signalAll();
			return item;
		} finally {
			mutex.unlock();
		}
	}

	public void close() {
		try {
			mutex.lock();
			closed = true;
			notEmptyOrClosed.signalAll();
		} finally {
			mutex.unlock();
		}
	}
	
	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.size() == 0;
	}

}
