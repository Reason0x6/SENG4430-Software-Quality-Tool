package seng4430_softwarequalitytool;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.*;
import seng4430_softwarequalitytool.NestedIfs.NestedIfs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NestedIfsTest {

    private NestedIfs nestedIfs;
    private static final String TEST_FILE_PATH = "testfile.json";

    @BeforeAll
    void createTestFile() {
        try (FileWriter fileWriter = new FileWriter(TEST_FILE_PATH)) {
            fileWriter.write("@@nested if code response here@@");
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
        nestedIfs = new NestedIfs();
    }

    /**
     * {
     *   "description": "Test single method with one nested if statement",
     *   "expectedResult": "Nested If's Successfully Calculated.",
     *   "nestedIfScore": {
     *     "SingleNestedIf": 1
     *   }
     * }
     */
    @Test
    public void testComputeSingleMethod() {
        String test1Code = "public class MyClass {\n" +
                "    public void SingleNestedIf() {\n" +
                "        if (true) {\n" +
                "            if (true) {\n" +
                "                System.out.println(\"Nested\");\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(test1Code).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);

        String result = nestedIfs.compute(compilationUnits, TEST_FILE_PATH);
        assertEquals("Nested If's Successfully Calculated.", result);
    }

    /**
     * {
     *   "description": "Test multiple methods with different nested if counts",
     *   "expectedResult": "Nested If's Successfully Calculated.",
     *   "nestedIfScore": {
     *     "method1With1NestedIf": 1,
     *     "method2WithZeroNestedIf": 0
     *   }
     * }
     */
    @Test
    public void testComputeMultipleMethods() {
        String test2Code = "public class MyClass {\n" +
                "    public void method1With1NestedIf() {\n" +
                "        if (true) {\n" +
                "            if (true) {\n" +
                "                System.out.println(\"Nested\");\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    public void method2WithZeroNestedIf() {\n" +
                "        if (true) {\n" +
                "            System.out.println(\"Not Nested\");\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(test2Code).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);

        String result = nestedIfs.compute(compilationUnits, TEST_FILE_PATH);
        assertEquals("Nested If's Successfully Calculated.", result);
    }


    /**
     * {
     *   "description": "Test empty class",
     *   "expectedResult": "Nested If's Successfully Calculated.",
     *   "nestedIfScore": {}
     * }
     */
    @Test
    public void testComputeEmptyClass() {
        String testCode3 = "public class TestClass {}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(testCode3).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);

        String result = nestedIfs.compute(compilationUnits, TEST_FILE_PATH);
        assertEquals("Nested If's Successfully Calculated.", result);
    }

    /**
     * {
     *   "description": "Test class with empty method",
     *   "expectedResult": "Nested If's Successfully Calculated.",
     *   "nestedIfScore": {
     *     "myMethod": 0
     *   }
     * }
     */
    @Test
    public void testComputeClassWithEmptyMethod() {
        String testCode4 = "public class MyClass {\n" +
                "    public void myMethod() {}\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(testCode4).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);

        String result = nestedIfs.compute(compilationUnits, TEST_FILE_PATH);
        assertEquals("Nested If's Successfully Calculated.", result);
    }

    /**
     * {
     *   "description": "Test single method with one if statement",
     *   "expectedResult": "Nested If's Successfully Calculated.",
     *   "nestedIfScore": {
     *     "myMethod": 0
     *   }
     * }
     */
    @Test
    public void testCalculateMaxDepthSingleIf() {
        String testCode5 = "public class MyClass {\n" +
                "    public void TestMaxDepthSingleIf() {\n" +
                "        if (true) {\n" +
                "            System.out.println(\"True\");\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(testCode5).getResult().get();
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);
        nestedIfs.compute(compilationUnits, TEST_FILE_PATH);
    }

    /**
     * {
     *   "description": "Test single method with nested if statements",
     *   "expectedResult": "Nested If's Successfully Calculated.",
     *   "nestedIfScore": {
     *     "myMethod": 2
     *   }
     * }
     */
    @Test
    public void testCalculateMaxDepthNestedIf() {
        String TestCode6 = "public class MyClass {\n" +
                "    public void TestMaxDepthNestedIf() {\n" +
                "        if (true) {\n" +
                "            if (true) {\n" +
                "                if (true) {\n" +
                "                    System.out.println(\"Nested\");\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(TestCode6).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);
        nestedIfs.compute(compilationUnits, TEST_FILE_PATH);
    }

    /**
     * {
     *   "description": "Test single method with nested if-else statements",
     *   "expectedResult": "Nested If's Successfully Calculated.",
     *   "nestedIfScore": {
     *     "myMethod": 1
     *   }
     * }
     */
    @Test
    public void testCalculateMaxDepthNestedIfElse() {
        String testCode7 = "public class MyClass {\n" +
                "    public void testCalculateMaxDepthNestedIfElse() {\n" +
                "        if (true) {\n" +
                "            if (true) {\n" +
                "                System.out.println(\"Nested\");\n" +
                "            }\n" +
                "        } else {\n" +
                "            if (true) {\n" +
                "                System.out.println(\"Nested\");\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(testCode7).getResult().get();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnit);

        nestedIfs.compute(compilationUnits, TEST_FILE_PATH);
    }
}
