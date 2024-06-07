package seng4430_softwarequalitytool;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.LineComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng4430_softwarequalitytool.CouplingBetweenClasses.CouplingBetweenClasses;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CouplingBetweenClassesTest {
    private CouplingBetweenClasses couplingBetweenClassesTest;

    @BeforeEach
    void setUp() {
        couplingBetweenClassesTest = new CouplingBetweenClasses();
    }

    @Test
    void testCalculateFogIndex() {
        assertEquals(0.75, couplingBetweenClassesTest.couplingIndex(1,1,1), 0.00000001);
        assertEquals(0.8, couplingBetweenClassesTest.couplingIndex(2,1,1), 0.00000001);
        assertEquals(0.8, couplingBetweenClassesTest.couplingIndex(1,2,1), 0.00000001);
        assertEquals(0.8, couplingBetweenClassesTest.couplingIndex(1,1,2), 0.00000001);
    }

}
