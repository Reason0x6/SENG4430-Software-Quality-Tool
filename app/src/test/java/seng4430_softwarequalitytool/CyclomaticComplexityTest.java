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
