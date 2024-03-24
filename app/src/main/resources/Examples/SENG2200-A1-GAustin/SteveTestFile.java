public class SteveTestFile {
    public void simpleIf() {
        if (true) {
            System.out.println("Simple If");
        }
    }

    public void nestedIfs() {
        if (true) {
            if (false) {
                if (true) {
                    System.out.println("Nested deep");
                }
            }
        }
    }

    public void nestedIfsWithElse() {
        if (true) {
            if (false) {
                System.out.println("Nested inside");
            } else {
                if (true) {
                    System.out.println("Nested in else");
                }
            }
        }
    }

    public void ifElseChain() {
        if (true) {
            System.out.println("First");
        } else if (false) {
            System.out.println("Second");
        } else if (true) {
            System.out.println("Third");
        } else {
            System.out.println("Last");
        }
    }

    public void mixedControlStructures() {
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                switch (i) {
                    case 2:
                        if (true) {
                            System.out.println("Nested inside switch");
                        }
                        break;
                    default:
                        System.out.println("Default case");
                }
            }
        }
    }

    public void nestedIfsWithLoops() {
        while (true) {
            if (true) {
                for (int i = 0; i < 3; i++) {
                    if (i == 2) {
                        System.out.println("Nested inside loop");
                    }
                }
            }
            break;
        }
    }
}
