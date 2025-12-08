package BytecodeParser;

import java.util.List;

public interface IClass {
	String getClassName();
    List<IMethod> getMethods();
    List<IField> getFields();
	List<String> getDirectInterfaces();
    String getSuperClassName();
}
