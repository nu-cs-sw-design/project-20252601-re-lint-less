import BytecodeParser.IClassParser;
import Checks.*;
import java.util.List;

import BytecodeParser.Parser;
import BytecodeParser.ASM.ASMParser;
import Checks.RedundantInterfacesCheck;

public class CheckFactory {

    private static final IClassParser asmParser = new ASMParser();
    private static final Parser parserWrapper = new Parser(asmParser);

    public static List<Check> createChecks() {
        return List.of(
                new PrintClassNameCheck(),
                new TooManyNestedIfsCheck(),
                new RedundantInterfacesCheck(parserWrapper),
                new PublicFieldCheck(),
                new MagicNumberCheck(),
                new TooManyParametersCheck(),
                new UnreachableCodeCheck(),
                new UnusedVariablesCheck(),
                new GodClassCheck(),
                new NamingConventionCheck()
        );
    }
}
