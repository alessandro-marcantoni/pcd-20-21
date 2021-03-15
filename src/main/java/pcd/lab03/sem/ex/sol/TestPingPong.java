package pcd.lab03.sem.ex.sol;

import java.util.concurrent.Semaphore;

/**
 * Unsynchronized version
 * 
 * @TODO make it sync 
 * @author aricci
 *
 */
public class TestPingPong {

	public static void main(String[] args) throws Exception {
		
		Semaphore pongTurn = new Semaphore(0);
		Semaphore pingTurn = new Semaphore(0);
		
		new Pinger(pingTurn, pongTurn).start();
		new Ponger(pongTurn, pingTurn).start();
	
		System.out.println("Ready....");
		Thread.sleep(2000);
		System.out.println("Go!");

		pingTurn.release();
	}

}
