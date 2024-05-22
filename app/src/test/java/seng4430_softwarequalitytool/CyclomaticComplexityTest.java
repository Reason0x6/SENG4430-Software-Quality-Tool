package seng4430_softwarequalitytool;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng4430_softwarequalitytool.CyclomaticComplexity.CyclomaticComplexity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CyclomaticComplexityTest {

    private  CyclomaticComplexity cyclomaticComplexity;

    @BeforeEach
    void setUp() {
        cyclomaticComplexity = new CyclomaticComplexity();
    }

    @Test
    public void testCalculateCyclomaticComplexity() {

        // Create a simple compilation unit
        String sourceCode = "public class MyClass {\n" +
                "    public void myMethod() {\n" +
                "        if (true) {\n" +
                "            System.out.println(\"True\");\n" +
                "        } else {\n" +
                "            System.out.println(\"False\");\n" +
                "        }\n" +
                "    }\n" +
                " public void myMethod2() {}" +
                "}";

        JavaParser j = new JavaParser();
        CompilationUnit compilationUnit = j.parse(sourceCode).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);

        int complexity = cyclomaticComplexity.calculateCyclomaticComplexity(compilationUnits);

        assertEquals(2, complexity);
    }

    @Test
public void calculateCyclomaticComplexity_returnsCorrectComplexityForMultipleMethods() {
    // Given
    String sourceCode = "public class MyClass {\n" +
            "    public void method1() {\n" +
            "        if (true) {\n" +
            "            System.out.println(\"True\");\n" +
            "        } else {\n" +
            "            System.out.println(\"False\");\n" +
            "        }\n" +
            "    }\n" +
            "    public void method2() {\n" +
            "        for (int i = 0; i < 10; i++) {\n" +
            "            System.out.println(i);\n" +
            "        }\n" +
            "    }\n" +
            "}";

    JavaParser j = new JavaParser();
    CompilationUnit compilationUnit = j.parse(sourceCode).getResult().get();

    List<CompilationUnit> compilationUnits = new ArrayList<>();
    compilationUnits.add(compilationUnit);

    // When
    int complexity = cyclomaticComplexity.calculateCyclomaticComplexity(compilationUnits);

    // Then
    assertEquals(3, complexity);
}

@Test
public void calculateCyclomaticComplexity_returnsZeroForEmptyClass() {
    // Given
    String sourceCode = "public class MyClass {}";

    JavaParser j = new JavaParser();
    CompilationUnit compilationUnit = j.parse(sourceCode).getResult().get();

    List<CompilationUnit> compilationUnits = new ArrayList<>();
    compilationUnits.add(compilationUnit);

    // When
    int complexity = cyclomaticComplexity.calculateCyclomaticComplexity(compilationUnits);

    // Then
    assertEquals(0, complexity);
}

@Test
public void calculateCyclomaticComplexity_returnsZeroForClassWithEmptyMethod() {
    // Given
    String sourceCode = "public class MyClass {\n" +
            "    public void myMethod() {}\n" +
            "}";

    JavaParser j = new JavaParser();
    CompilationUnit compilationUnit = j.parse(sourceCode).getResult().get();

    List<CompilationUnit> compilationUnits = new ArrayList<>();
    compilationUnits.add(compilationUnit);

    // When
    int complexity = cyclomaticComplexity.calculateCyclomaticComplexity(compilationUnits);

    // Then
    assertEquals(1, complexity);
}

    @Test
    void testEvaluateRisk() {
        assertEquals("Simple procedure, little risk.", cyclomaticComplexity.evaluateRisk(3));
        assertEquals("More complex, moderate risk.", cyclomaticComplexity.evaluateRisk(15));
        assertEquals("Complex, high risk.", cyclomaticComplexity.evaluateRisk(40));
        assertEquals("Very high risk.", cyclomaticComplexity.evaluateRisk(50));
    }

    @Test
    void testExceptionHandling() {
        // Test with a non-existent file location
        CyclomaticComplexity ccWithInvalidLocation = new CyclomaticComplexity("invalid_location.properties");
        assertEquals("Simple procedure, little risk.", ccWithInvalidLocation.evaluateRisk(10)); // Ensure risk level is set to default
    }


    @Test
    void testEdgeCases() {
        assertEquals("Simple procedure, little risk.", cyclomaticComplexity.evaluateRisk(0)); // Lower bound edge case
        assertEquals("Very high risk.", cyclomaticComplexity.evaluateRisk(Integer.MAX_VALUE)); // Upper bound edge case
    }
}
