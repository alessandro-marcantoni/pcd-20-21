package pcd.lab03.sem.ex.sol;

import java.util.concurrent.Semaphore;

public class Pinger extends Thread {

	private Semaphore pongTurn;
	private Semaphore pingTurn;

	public Pinger(Semaphore pingTurn, Semaphore pongTurn) {
		this.pongTurn = pongTurn;
		this.pingTurn = pingTurn;
	}	
	
	public void run() {
		while (true) {
			try {
				pingTurn.acquire(); /* wait event */
				System.out.println("ping!");
				pongTurn.release(); /* signal */
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}