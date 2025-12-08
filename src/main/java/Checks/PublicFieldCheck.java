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
                if (field.isPublic()) {
                    reporter.report(
                            clazz.getClassName(),
                            "Field '" + field.getName() + "' is public; consider using private/protected with accessors."
                    );
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
}
