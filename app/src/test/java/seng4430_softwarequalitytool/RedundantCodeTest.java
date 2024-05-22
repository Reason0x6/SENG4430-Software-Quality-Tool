package seng4430_softwarequalitytool;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.*;
import seng4430_softwarequalitytool.RedundantCode.RedundantCode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedundantCodeTest {

    private RedundantCode redundantCode;
    private static final String TEST_FILE_PATH = "testfile.json";

    @BeforeAll
    void createTestFile() {
        try (FileWriter fileWriter = new FileWriter(TEST_FILE_PATH)) {
            fileWriter.write("@@Redundant code response here@@");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void deleteTestFile() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @BeforeEach
    void setUp() {
        redundantCode = new RedundantCode();
    }

    /**
     * {
     *   "description": "Test single method with unreachable code",
     *   "expectedResult": "Method 'myMethod' should have 1 line of unreachable code.",
     *   "unreachableCodeCounts": {
     *     "myMethod": 1
     *   }
     * }
     */
    @Test
    public void testCheckUnreachableCode() {
        // Arrange
        String testCode1 = "public class MyClass {\n" +
                "    public void myMethod() {\n" +
                "        return;\n" +
                "        System.out.println(\"Unreachable\");\n" +
                "    }\n" +
                "}";
        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(testCode1).getResult().get();
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);
        // Act
        redundantCode.compute(compilationUnits, TEST_FILE_PATH);
        // Assert
        assertEquals(1, (int) redundantCode.unreachableCodeCounts.get("myMethod"));
    }

    /**
     * {
     *   "description": "Test single method with duplicated code",
     *   "expectedResult": "Method 'myMethod' should have duplicated code.",
     *   "duplicatedCodeCounts": {
     *     "myMethod()": 1
     *   }
     * }
     */
    @Test
    public void testCheckDuplicatedCode() {
        // Arrange
        String testCode2 = "public class MyClass {\n" +
                "    public void myMethod() {\n" +
                "        System.out.println(\"Duplicate\");\n" +
                "        System.out.println(\"Duplicate\");\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(testCode2).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);
        // Act
        redundantCode.compute(compilationUnits, TEST_FILE_PATH);
        // Assert
        assertEquals(1, (int) redundantCode.duplicatedCodeCounts.get("myMethod()"));
    }

    /**
     * {
     *   "description": "Test single method with unused variable",
     *   "expectedResult": "Method 'myMethod' should have one unused variable 'x'.",
     *   "currentUnusedVariables": {
     *     "myMethod": "Unused Variable: x"
     *   }
     * }
     */
    @Test
    public void testCheckUnusedVariables() {
        // Arrange
        String testCode3 = "public class MyClass {\n" +
                "    public void myMethod() {\n" +
                "        int x = 0;\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(testCode3).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);
        // Act
        redundantCode.compute(compilationUnits, TEST_FILE_PATH);
        // Assert
        assertEquals("Unused Variable: x", redundantCode.currentUnusedVariables.get("myMethod"));
    }

    /**
     * {
     *   "description": "Test single method with used variable",
     *   "expectedResult": "Method 'myMethod' should have all variables used.",
     *   "currentUnusedVariables": {
     *     "myMethod": "All used"
     *   }
     * }
     */
    @Test
    public void testCheckAllUsedVariables() {
       // Arrange
        String sourceCode = "public class MyClass {\n" +
                "    public void myMethod() {\n" +
                "        int x = 0;\n" +
                "        System.out.println(x);\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(sourceCode).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);
        // Act
        redundantCode.compute(compilationUnits, TEST_FILE_PATH);
        // Assert
        assertEquals("All used", redundantCode.currentUnusedVariables.get("myMethod"));
    }

    /**
     * {
     *   "description": "Test truly unused function",
     *   "expectedResult": "Function 'unusedMethod' should be detected as unused.",
     *   "uniqueMethodNames": [
     *     "unusedMethod"
     *   ]
     * }
     */
    @Test
    public void testUnusedFunction() {
        // Arrange
        String sourceCode = "public class MyClass {\n" +
                "    public void unusedMethod() {}\n" +
                "    public void anotherMethod() {\n" +
                "        System.out.println(\"Hello\");\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(sourceCode).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);

        // Act
        redundantCode.compute(compilationUnits, TEST_FILE_PATH);

        // Assert
        assertTrue(redundantCode.uniqueMethodNames.contains("unusedMethod"));
    }

    /**
     * {
     *   "description": "Test all functions used",
     *   "expectedResult": "All functions should be used.",
     *   "uniqueMethodNames": []
     * }
     */
    @Test
    public void testAllFunctionsUsed() {
        // Arrange
        String sourceCode = "public class MyClass {\n" +
                "    public void method1() {\n" +
                "        method2();\n" +
                "    }\n" +
                "    public void method2() {\n" +
                "        method1();\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(sourceCode).getResult().get();
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);
        // Act
        redundantCode.compute(compilationUnits, TEST_FILE_PATH);
        // Assert
        assertFalse(redundantCode.uniqueMethodNames.contains("method1"), "method1 should be used.");
        assertFalse(redundantCode.uniqueMethodNames.contains("method2"), "method2 should be used.");
        assertTrue(redundantCode.uniqueMethodNames.isEmpty(), "All functions should be used.");
    }
}
