package examples;

public class UnreachableCodeExample {
	public void unreachableCodeExample() {
        System.out.println("This is reachable");
		if (false) {
			// unreachable code
			System.out.println("This is unreachable");
		}
    }
}
