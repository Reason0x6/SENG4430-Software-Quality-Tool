package seng4430_softwarequalitytool;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.LineComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng4430_softwarequalitytool.CouplingBetweenClasses.CouplingBetweenClasses;
import seng4430_softwarequalitytool.Util.ClassModel;
import seng4430_softwarequalitytool.Util.MethodModel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seng4430_softwarequalitytool.Util.ClassModel.getClassData;

/*
* the main calculation of this module involves the aggregation of
* class and method data into data model objects
* testing of these objects are the major focus for this module
*
*/

public class WeightedMethodsPerClassTest {
    private List<ClassModel> classes;


    @BeforeEach
    void setUp() {
        classes = new ArrayList<>();
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        CompilationUnit cu =  StaticJavaParser.parse("class ClassA {\n" +
                "public void methodA(){\n" +
                "System.out.println(\"1\");\n" +
                "System.out.println(\"2\");\n" +
                "System.out.println(\"3\");\n" +
                "System.out.println(\"4\");\n" +
                "System.out.println(\"5\");\n" +
                "System.out.println(\"6\");\n" +
                "System.out.println(\"7\");\n" +
                "}\n" +
                "public void methodB(){\n" +
                "System.out.println(\"1\");\n" +
                "System.out.println(\"2\");\n" +
                "System.out.println(\"3\");\n" +
                "System.out.println(\"4\");\n" +
                "System.out.println(\"5\");\n" +
                "}\n" +
                "}");
        compilationUnits.add(cu);
        cu = StaticJavaParser.parse("class ClassB {\n" +
                "public void methodA(){\n" +
                "System.out.println(\"1\");\n" +
                "System.out.println(\"2\");\n" +
                "System.out.println(\"3\");\n" +
                "System.out.println(\"4\");\n" +
                "}\n" +
                "}");
        compilationUnits.add(cu);
        cu =  StaticJavaParser.parse("class ClassC {\n" +
                "public void methodA(){\n" +
                "System.out.println(\"1\");\n" +
                "System.out.println(\"2\");\n" +
                "System.out.println(\"3\");\n" +
                "System.out.println(\"4\");\n" +
                "System.out.println(\"5\");\n" +
                "System.out.println(\"6\");\n" +
                "}\n" +
                "public void methodB(){\n" +
                "System.out.println(\"1\");\n" +
                "System.out.println(\"2\");\n" +
                "}\n" +
                "public void methodC(){\n" +
                "System.out.println(\"1\");\n" +
                "System.out.println(\"2\");\n" +
                "System.out.println(\"3\");\n" +
                "System.out.println(\"4\");\n" +
                "}\n" +
                "}");
        compilationUnits.add(cu);

        getClassData(compilationUnits, classes);

    }
    @Test
    void testNumberOfClasses() {
        //classes list is filled with contrived classes aggregated from Compilation units
        assertEquals(3,classes.size());
    }
    @Test
    void testNumberOfMethodsPerClass() {
        //classes list is filled with contrived classes aggregated from Compilation units

        assertEquals(2,classes.get(0).numberOfMethods);
        assertEquals(1,classes.get(1).numberOfMethods);
        assertEquals(3,classes.get(2).numberOfMethods);
    }
    @Test
    void testNumberOfLOCPerMethod() {
        //classes list is filled with contrived classes aggregated from Compilation units
        int[] LOCSequence = {7,5,4,6,2,4};
        int index = 0;
        for (ClassModel c :
                classes) {
            for (MethodModel m :
                    c.methods) {
                assertEquals(LOCSequence[index++],m.linesOfCode);

            }
        }
    }


}
