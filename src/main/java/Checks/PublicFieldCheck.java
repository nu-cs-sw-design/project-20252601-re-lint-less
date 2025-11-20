package Checks;

import Reporting.Reporter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

/**
 * Flags fields that are declared public.
 */
public class PublicFieldCheck implements Check {

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            List<FieldNode> fields = (List<FieldNode>) classNode.fields;
            if (fields == null || fields.isEmpty()) {
                return true;
            }

            for (FieldNode field : fields) {
                boolean isPublic = (field.access & Opcodes.ACC_PUBLIC) != 0;

                if (isPublic) {
                    reporter.report(
                            classNode.name,
                            "Field '" + field.name + "' is public; consider using private/protected with accessors."
                    );
                }
            }

            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "PublicFieldCheck failed: " + e.getMessage()
            );
            return false;
        }
    }
}
