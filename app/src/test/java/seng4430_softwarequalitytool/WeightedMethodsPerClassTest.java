package seng4430_softwarequalitytool;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.LineComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng4430_softwarequalitytool.WeightedMethodsPerClass.WeightedMethodsPerClass;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class WeightedMethodsPerClassTest {
    private WeightedMethodsPerClass weightedMethodsPerClass;

    @BeforeEach
    void setUp() {
        weightedMethodsPerClass = new WeightedMethodsPerClass();
    }

}
