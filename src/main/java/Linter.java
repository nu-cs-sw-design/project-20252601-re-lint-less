import ASMParser.Parser;
import Checks.Check;
import Checks.PrintClassNameCheck;

import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Linter {

    private final List<Check> checks = new ArrayList<>();
    private final Parser parser = new Parser();

    public void addCheck(Check check) {
        checks.add(check);
    }

    public void run(String className) throws IOException {
        ClassNode classNode = parser.parse(className);
        for (Check check : checks) {
            check.apply(classNode);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Linter <fully-qualified-class-name>");
            System.exit(1);
        }

        String className = args[0];

        Linter linter = new Linter();
        linter.addCheck(new PrintClassNameCheck());

        linter.run(className);
    }
}
