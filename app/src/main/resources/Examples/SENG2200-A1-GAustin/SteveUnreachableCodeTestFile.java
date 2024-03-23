public class SteveUnreachableCodeTestFile {
    public void method1() {
        System.out.println("This is method 1");
        System.out.println("This is method 1");
        return;
        // The following statements will be considered unreachable
        System.out.println("Unreachable statement 1");
        System.out.println("Unreachable statement 2");
    }

    public void method2() {
        System.out.println("This is method 2");
        return;
        // The following statements will be considered unreachable
        System.out.println("Unreachable statement 3");
        System.out.println("Unreachable statement 4");
    }

    public void method3() {
        System.out.println("This is method 3");
        // The following return statement is the last statement, so no unreachable code
        return;
    }

    public void method4() {
        System.out.println("This is method 4");
        // The following statements are not unreachable
        if (true) {
            return;
        }
        System.out.println("Statement after return inside if");
    }
}
