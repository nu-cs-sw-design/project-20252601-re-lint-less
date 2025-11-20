package examples;

interface A {}
interface B extends A {}

public class RedundantExample implements A, B {
}
