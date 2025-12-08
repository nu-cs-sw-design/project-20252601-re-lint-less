package Checks;

import BytecodeParser.IClassParser;
import java.util.List;

import BytecodeParser.ASM.ASMParser;

public class CheckFactory {

    private static final IClassParser asmParser = new ASMParser();

    public static List<Check> createChecks() {
        return List.of(
                new TooManyNestedIfsCheck(),
                new RedundantInterfacesCheck(asmParser),
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
