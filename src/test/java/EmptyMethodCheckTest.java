import Checks.EmptyMethodCheck;
import Reporting.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmptyMethodCheckTest {

    private EmptyMethodCheck check;
    private MockReporter reporter;

    @BeforeEach
    void setUp() {
        check = new EmptyMethodCheck();
        reporter = new MockReporter();
    }

    @Test
    void testEmptyMethod_withNoInstructions() {
        // Create a class with an empty method (no instructions at all)
        ClassNode classNode = new ClassNode();
        classNode.name = "test/EmptyClass";
        classNode.version = Opcodes.V11;
        classNode.access = Opcodes.ACC_PUBLIC;

        // Add a method with null instructions
        classNode.methods = new ArrayList<>();
        org.objectweb.asm.tree.MethodNode method = new org.objectweb.asm.tree.MethodNode();
        method.name = "emptyMethod";
        method.desc = "()V";
        method.access = Opcodes.ACC_PUBLIC;
        method.instructions = null;
        classNode.methods.add(method);

        boolean result = check.apply(classNode, reporter);

        assertTrue(result);
        assertEquals(1, reporter.getReports().size());
        assertTrue(reporter.getReports().get(0).contains("emptyMethod"));
        assertTrue(reporter.getReports().get(0).contains("empty body"));
    }

    @Test
    void testEmptyMethod_withOnlyReturn() {
        ClassNode classNode = new ClassNode();
        classNode.name = "test/EmptyClass";
        classNode.version = Opcodes.V11;
        classNode.access = Opcodes.ACC_PUBLIC;

        classNode.methods = new ArrayList<>();
        org.objectweb.asm.tree.MethodNode method = new org.objectweb.asm.tree.MethodNode();
        method.name = "onlyReturnMethod";
        method.desc = "()V";
        method.access = Opcodes.ACC_PUBLIC;
        method.instructions = new org.objectweb.asm.tree.InsnList();
        method.instructions.add(new org.objectweb.asm.tree.InsnNode(Opcodes.RETURN));
        classNode.methods.add(method);

        boolean result = check.apply(classNode, reporter);

        assertTrue(result);
        assertEquals(1, reporter.getReports().size());
        assertTrue(reporter.getReports().get(0).contains("onlyReturnMethod"));
        assertTrue(reporter.getReports().get(0).contains("only contains a return"));
    }

    @Test
    void testNonEmptyMethod() {
        ClassNode classNode = new ClassNode();
        classNode.name = "test/NonEmptyClass";
        classNode.version = Opcodes.V11;
        classNode.access = Opcodes.ACC_PUBLIC;

        classNode.methods = new ArrayList<>();
        org.objectweb.asm.tree.MethodNode method = new org.objectweb.asm.tree.MethodNode();
        method.name = "normalMethod";
        method.desc = "()V";
        method.access = Opcodes.ACC_PUBLIC;
        method.instructions = new org.objectweb.asm.tree.InsnList();

        // Add some actual instructions (load constant, store, return)
        method.instructions.add(new org.objectweb.asm.tree.InsnNode(Opcodes.ICONST_1));
        method.instructions.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ISTORE, 0));
        method.instructions.add(new org.objectweb.asm.tree.InsnNode(Opcodes.RETURN));
        classNode.methods.add(method);

        boolean result = check.apply(classNode, reporter);

        assertTrue(result);
        assertEquals(0, reporter.getReports().size(), "Non-empty method should not be flagged");
    }

    @Test
    void testAbstractMethod_notFlagged() {
        ClassNode classNode = new ClassNode();
        classNode.name = "test/AbstractClass";
        classNode.version = Opcodes.V11;
        classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT;

        classNode.methods = new ArrayList<>();
        org.objectweb.asm.tree.MethodNode method = new org.objectweb.asm.tree.MethodNode();
        method.name = "abstractMethod";
        method.desc = "()V";
        method.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT;
        method.instructions = null;
        classNode.methods.add(method);

        boolean result = check.apply(classNode, reporter);

        assertTrue(result);
        assertEquals(0, reporter.getReports().size(), "Abstract methods should not be flagged");
    }

    @Test
    void testNativeMethod_notFlagged() {
        ClassNode classNode = new ClassNode();
        classNode.name = "test/NativeClass";
        classNode.version = Opcodes.V11;
        classNode.access = Opcodes.ACC_PUBLIC;

        classNode.methods = new ArrayList<>();
        org.objectweb.asm.tree.MethodNode method = new org.objectweb.asm.tree.MethodNode();
        method.name = "nativeMethod";
        method.desc = "()V";
        method.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_NATIVE;
        method.instructions = null;
        classNode.methods.add(method);

        boolean result = check.apply(classNode, reporter);

        assertTrue(result);
        assertEquals(0, reporter.getReports().size(), "Native methods should not be flagged");
    }

    @Test
    void testMultipleEmptyMethods() {
        ClassNode classNode = new ClassNode();
        classNode.name = "test/MultipleEmptyClass";
        classNode.version = Opcodes.V11;
        classNode.access = Opcodes.ACC_PUBLIC;

        classNode.methods = new ArrayList<>();

        // Add first empty method
        org.objectweb.asm.tree.MethodNode method1 = new org.objectweb.asm.tree.MethodNode();
        method1.name = "empty1";
        method1.desc = "()V";
        method1.access = Opcodes.ACC_PUBLIC;
        method1.instructions = new org.objectweb.asm.tree.InsnList();
        method1.instructions.add(new org.objectweb.asm.tree.InsnNode(Opcodes.RETURN));
        classNode.methods.add(method1);

        // Add second empty method
        org.objectweb.asm.tree.MethodNode method2 = new org.objectweb.asm.tree.MethodNode();
        method2.name = "empty2";
        method2.desc = "()V";
        method2.access = Opcodes.ACC_PUBLIC;
        method2.instructions = null;
        classNode.methods.add(method2);

        boolean result = check.apply(classNode, reporter);

        assertTrue(result);
        assertEquals(2, reporter.getReports().size());
        assertTrue(reporter.getReports().get(0).contains("empty1"));
        assertTrue(reporter.getReports().get(1).contains("empty2"));
    }

    @Test
    void testMethodWithLabelBeforeReturn() {
        ClassNode classNode = new ClassNode();
        classNode.name = "test/LabelClass";
        classNode.version = Opcodes.V11;
        classNode.access = Opcodes.ACC_PUBLIC;

        classNode.methods = new ArrayList<>();
        org.objectweb.asm.tree.MethodNode method = new org.objectweb.asm.tree.MethodNode();
        method.name = "labelMethod";
        method.desc = "()V";
        method.access = Opcodes.ACC_PUBLIC;
        method.instructions = new org.objectweb.asm.tree.InsnList();

        // Add label and return (still considered "only return")
        method.instructions.add(new org.objectweb.asm.tree.LabelNode());
        method.instructions.add(new org.objectweb.asm.tree.InsnNode(Opcodes.RETURN));
        classNode.methods.add(method);

        boolean result = check.apply(classNode, reporter);

        assertTrue(result);
        assertEquals(1, reporter.getReports().size());
        assertTrue(reporter.getReports().get(0).contains("only contains a return"));
    }

    // Mock reporter for testing
    private static class MockReporter implements Reporter {
        private final List<String> reports = new ArrayList<>();

        @Override
        public void report(String className, String message) {
            reports.add(className + ": " + message);
        }

        public List<String> getReports() {
            return reports;
        }
    }
}
