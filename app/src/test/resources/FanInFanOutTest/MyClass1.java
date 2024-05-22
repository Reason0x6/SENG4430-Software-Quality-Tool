package FanInFanOutTest;

public class MyClass1 {
    public void method1() {
        if (true) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
    }

    public void method2() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }
}
