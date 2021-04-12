package pcd.ass1.sol.part2;

/**
 * 
 * Assignment #01 - Parte 2
 * 
 * doc:
 * https://docs.google.com/document/d/1v0XlJIEjCPZr87PIBaqROFcco8oNO-se_HO3GbM0gqU/edit?usp=sharing
 * 
 * @author aricci
 *
 */
public class Main {
	public static void main(String[] args) {
		try {
			
			View view = new View();
			
			Controller controller = new Controller(view);
			view.addListener(controller);
			
			view.display();						
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
