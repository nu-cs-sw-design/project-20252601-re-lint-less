package examples;

public class PublicFieldExample {

    public int badField;        // should be flagged
    private int okField;        // should not be flagged
    protected String alsoOk;    // should not be flagged
}