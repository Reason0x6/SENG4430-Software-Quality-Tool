package seng4430_softwarequalitytool;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import seng4430_softwarequalitytool.ErrorHandling.ErrorHandling;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ErrorHandlingTest {

    private ErrorHandling errorHandling;
    private CompilationUnit mockCompilationUnit;

    @BeforeEach
    public void setup() {
        errorHandling = new ErrorHandling();
        mockCompilationUnit = Mockito.mock(CompilationUnit.class);
        when(mockCompilationUnit.getPrimaryTypeName()).thenReturn(java.util.Optional.of("TestClassName"));
    }

    @Test
    public void computeReturnsSuccessMessageForValidInput() {
        String result = errorHandling.compute(Collections.singletonList(mockCompilationUnit), "filePath");
        assertEquals("Error Handling Successfully Calculated.", result);
    }

    @Test
    public void computeReturnsErrorMessageForInvalidInput() {
        String result = errorHandling.compute(null, "filePath");
        assertEquals("Error Calculating Error Handling.", result);
    }

    @Test
    public void computeCalculatesMetricsForMultipleCompilationUnits() {
        String result = errorHandling.compute(Arrays.asList(mockCompilationUnit, mockCompilationUnit), "filePath");
        assertEquals("Error Handling Successfully Calculated.", result);
    }

    @Test
    public void saveResultCalculatesCorrectScore() {
        errorHandling.compute(Collections.singletonList(mockCompilationUnit), "filePath");
        assertEquals(15, errorHandling.getResult());
    }

    @Test
    public void saveResultCalculatesCorrectScoreForMultipleCompilationUnits() {
        errorHandling.compute(Arrays.asList(mockCompilationUnit, mockCompilationUnit), "filePath");
        assertEquals(15, errorHandling.getResult());
    }
}