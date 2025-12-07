import Checks.*;
import java.util.List;

public class CheckFactory {

    public static List<Check> createChecks() {
        return List.of(
                new PrintClassNameCheck(),
                new TooManyNestedIfsCheck(),
                new RedundantInterfacesCheck(),
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
