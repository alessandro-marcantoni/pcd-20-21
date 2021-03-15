package pcd.lab03.sem.ex.sol;

import java.util.concurrent.Semaphore;

public class Ponger extends Thread {
	
	private Semaphore pongTurn;
	private Semaphore pingTurn;
	
	public Ponger(Semaphore pongTurn, Semaphore pingTurn) {
		this.pongTurn = pongTurn;
		this.pingTurn = pingTurn;
	}	
	
	public void run() {
		while (true) {
			try {
				pongTurn.acquire(); /* wait */
				System.out.println("pong!");
				pingTurn.release(); /* signal */
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}