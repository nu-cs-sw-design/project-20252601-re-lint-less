import ASMParser.Parser;
import Checks.Check;
import Reporting.Reporter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Linter {

    private final Parser parser;
    private final List<Check> checks = new ArrayList<>();

    public Linter(Parser parser, List<Check> initialChecks) {
        this.parser = parser;
        addChecks(initialChecks);
    }

    private void addChecks(List<Check> checks) {
        if (checks != null) {
            this.checks.addAll(checks);
        }
    }

    /**
     * Lint a single class by name.
     */
    private void run(String className, Reporter reporter) {
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

    /**
     * Run linter on multiple class names.
     * @param classNames list of fully qualified class names
     * @param reporter   the reporter to use for output
     */
     void runMultiple(List<String> classNames, Reporter reporter) {
        if (classNames == null) {
            return;
        }
        for (String className : classNames) {
            run(className, reporter);
        }
    }
}
