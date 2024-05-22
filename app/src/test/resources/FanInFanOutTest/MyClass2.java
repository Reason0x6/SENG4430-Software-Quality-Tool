package FanInFanOutTest;

public class MyClass2 {
    public void method1() {
        MyClass1 c1 = new MyClass1();
        c1.method1();
    }

    public void method2() {
        MyClass1 c1 = new MyClass1();
        c1.method2();
    }
}
