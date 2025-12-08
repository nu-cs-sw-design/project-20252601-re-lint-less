package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IField;
import Reporting.Reporter;
import org.objectweb.asm.Opcodes;

import java.util.List;

/**
 * Flags fields that are declared public.
 */
public class PublicFieldCheck implements Check {

    @Override
    public boolean apply(IClass clazz, Reporter reporter) {
        try {
            List<IField> fields = clazz.getFields();
            if (fields == null || fields.isEmpty()) {
                return true;
            }

            for (IField field : fields) {
                // IField may need a method to get access flags if available in your wrapper
                // Assuming getAccess() exists, otherwise you could extend IField with it
                if (field instanceof AccessAwareField) {
                    int access = ((AccessAwareField) field).getAccess();
                    boolean isPublic = (access & Opcodes.ACC_PUBLIC) != 0;
                    if (isPublic) {
                        reporter.report(
                                clazz.getClassName(),
                                "Field '" + field.getName() + "' is public; consider using private/protected with accessors."
                        );
                    }
                }
            }

            return true;
        } catch (Exception e) {
            reporter.report(
                    clazz.getClassName(),
                    "PublicFieldCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Interface to allow access flag retrieval from fields if your wrapper doesn't already provide it.
     */
    public interface AccessAwareField extends IField {
        int getAccess();
    }
}
