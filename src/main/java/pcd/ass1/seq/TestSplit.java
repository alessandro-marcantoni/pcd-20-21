package pcd.ass1.seq;

import java.util.regex.Pattern;

public class TestSplit {

	public static void main(String[] args) {
	    String delimiters = "[\", ?.@;:!-]+";
	    String test = "This is a test; I said: - test - tough test, here. Hello again!!! Last \"done\" Yes? No.";
		String[] words = test.split(delimiters);
		for (String w: words) {
			System.out.println(w);
		}
	}

}
