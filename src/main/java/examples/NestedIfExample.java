package examples;

public class NestedIfExample {

    public void deep(int a, int b, int c, int d) {
        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    if (d > 0) {
                        System.out.println("deep!");
                    }
                }
            }
        }
    }

    public void shallow(int x) {
        if (x > 0) {
            System.out.println("ok");
        }
    }
}
