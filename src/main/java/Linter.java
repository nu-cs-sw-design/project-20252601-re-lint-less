import ASMParser.Parser;
import Checks.Check;
import org.objectweb.asm.tree.ClassNode;
import Reporting.Reporter;
import Reporting.ConsoleReporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Linter {

    private final Parser parser = new Parser();
    private final List<Check> checks = new ArrayList<>();

    public Linter() {
        // default constructor, no args
    }

    public void addCheck(Check check) {
        checks.add(check);
    }

    public void addChecks(List<Check> checks) {
        this.checks.addAll(checks);
    }

    public void run(String className, Reporter reporter) {
        ClassNode node;

        try {
            node = parser.parse(className);
        } catch (IOException | RuntimeException e) {
            reporter.report(className, "Failed to parse class: " + e.getMessage());
            return;
        }

        for (Check check : checks) {
            try {
                boolean ok = check.apply(node, reporter);
                if (!ok) {
                    reporter.report(className,
                            "Check failed: " + check.getClass().getSimpleName());
                }
            } catch (RuntimeException e) {
                reporter.report(className,
                        "Check crashed: " + check.getClass().getSimpleName() +
                                " — " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Linter <fully-qualified-class-name>");
            System.exit(1);
        }

        String className = args[0];

        Reporter reporter = new ConsoleReporter();

        Linter linter = new Linter();

        linter.addChecks(CheckFactory.createChecks());

        linter.run(className, reporter);
    }
}
