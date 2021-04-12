package pcd.ass1.sol.part1;

public abstract class BasicAgent extends Thread {

	protected BasicAgent(String name) {
		super(name);
	}
	
	protected void logDebug(String msg) {
		// System.out.println("[ " + getName() +"] " + msg);
	}

	protected void log(String msg) {
		System.out.println("[ " + getName() +"] " + msg);
	}
}
