package Checks;

import BytecodeParser.IClassParser;
import java.util.List;

import BytecodeParser.Parser;
import BytecodeParser.ASM.ASMParser;

public class CheckFactory {

    private static final IClassParser asmParser = new ASMParser();
    private static final Parser parserWrapper = new Parser(asmParser);

    public static List<Check> createChecks() {
        return List.of(
                new TooManyNestedIfsCheck(),
                new RedundantInterfacesCheck(parserWrapper),
                new PublicFieldCheck(),
                new MagicNumberCheck(),
                new TooManyParametersCheck(),
                new EmptyMethodCheck(),
                new UnusedVariablesCheck(),
                new GodClassCheck(),
                new NamingConventionCheck()
        );
    }
}
