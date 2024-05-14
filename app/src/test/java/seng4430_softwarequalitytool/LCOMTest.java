package seng4430_softwarequalitytool;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng4430_softwarequalitytool.LCOM.LCOM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LCOMTest {

    private LCOM lcom;

    @BeforeEach
    void setUp() {
        lcom = new LCOM(){
            @Override
            public void saveResult() {}
        };
    }

    @Test
    void testComputeLCOM() {
        // Mock compilation units
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        CompilationUnit compilationUnit = StaticJavaParser.parse("class MyClass { int x; void method1() { x = 5; } void method2() { x = 10; } }");
        compilationUnits.add(compilationUnit);

        // Compute LCOM
        String result = lcom.compute(compilationUnits, "testFilePath");

        // Check if LCOM was computed successfully
        assertEquals("LCOM Computed", result);

        // Check if LCOMs map contains the expected value
        assertEquals(1, lcom.LCOMs.size());
        assertEquals(0, lcom.LCOMs.get("MyClass"));
    }

    @Test
    void testCalculateLCOM() {
        // Mock method fields map
        List<String> methods = List.of("method1", "method2");
        List<String> fields = List.of("x");
        List<List<String>> methodFields = List.of(fields);

        // Calculate LCOM
        double lcomValue = LCOM.calculateLCOM(new HashSet<>(methods), Collections.singletonMap("MyClass", new HashSet<>(fields)));

        // Check if LCOM value matches expected result
        assertEquals(0.5, lcomValue);
    }

    @Test
    void testComputeLCOMWithEmptyCompilationUnits() {
        // Mock empty compilation units
        List<CompilationUnit> compilationUnits = new ArrayList<>();

        // Compute LCOM
        String result = lcom.compute(compilationUnits, "testFilePath");

        // Check if LCOM was computed successfully
        assertEquals("LCOM Computed", result);

        // Check if LCOMs map is empty
        assertEquals(0, lcom.LCOMs.size());
    }

    @Test
    void testComputeLCOMWithMultipleClasses() {
        // Mock compilation units with multiple classes
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        CompilationUnit compilationUnit1 = StaticJavaParser.parse("class MyClass1 { int x; void method1() { x = 5; } }");
        CompilationUnit compilationUnit2 = StaticJavaParser.parse("class MyClass2 { int y; void method2() { y = 10; } }");
        compilationUnits.add(compilationUnit1);
        compilationUnits.add(compilationUnit2);

        // Compute LCOM
        String result = lcom.compute(compilationUnits, "testFilePath");

        // Check if LCOM was computed successfully
        assertEquals("LCOM Computed", result);

        // Check if LCOMs map contains the expected number of entries
        assertEquals(2, lcom.LCOMs.size());
    }

    @Test
    void testComputeLCOMWithNoMethods() {
        // Mock compilation units with a class having no methods
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        CompilationUnit compilationUnit = StaticJavaParser.parse("class MyClass { int x; }");
        compilationUnits.add(compilationUnit);

        // Compute LCOM
        String result = lcom.compute(compilationUnits, "testFilePath");

        // Check if LCOM was computed successfully
        assertEquals("LCOM Computed", result);


        assertEquals(0, lcom.LCOMs.size());
        assertEquals(null, lcom.LCOMs.get("MyClass"));
    }

    @Test
    void testComputeLCOMWithMultipleReferences() {
        // Mock compilation units with a class having methods referencing multiple fields
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        CompilationUnit compilationUnit = StaticJavaParser.parse("class MyClass { int x, y; void method1() { x = 5; } void method2() { y = 10; } }");
        compilationUnits.add(compilationUnit);

        // Compute LCOM
        String result = lcom.compute(compilationUnits, "testFilePath");

        // Check if LCOM was computed successfully
        assertEquals("LCOM Computed", result);

        assertEquals(1, lcom.LCOMs.size());
        assertEquals(0.5, lcom.LCOMs.get("MyClass"));
    }
}
