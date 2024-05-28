package seng4430_softwarequalitytool;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import seng4430_softwarequalitytool.CodeCommentsAndFormatting.CodeCommentsAndFormatting;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CodeCommentsAndFormattingTest {

    private CodeCommentsAndFormatting codeCommentsAndFormatting;
    private CompilationUnit mockCompilationUnit;

    @BeforeEach
    public void setup() {
        codeCommentsAndFormatting = new CodeCommentsAndFormatting();
        mockCompilationUnit = Mockito.mock(CompilationUnit.class);
    }

    @Test
    public void computeShouldReturnSuccessMessageWhenNoExceptions() {
        when(mockCompilationUnit.getPrimaryTypeName()).thenReturn(java.util.Optional.of("Test"));
        String result = codeCommentsAndFormatting.compute(Collections.singletonList(mockCompilationUnit), "filePath");
        assertEquals("Comments and Formatting Successfully Calculated.", result);
    }

    @Test
    public void computeShouldReturnErrorMessageWhenExceptionOccurs() {
        when(mockCompilationUnit.getPrimaryTypeName()).thenThrow(new RuntimeException("Test Exception"));
        String result = codeCommentsAndFormatting.compute(Collections.singletonList(mockCompilationUnit), "filePath");
        assertEquals("Error Calculating Comments and Formatting.", result);
    }

    @Test
    public void calculateResultShouldReturnAverageScore() {
        when(mockCompilationUnit.getPrimaryTypeName()).thenReturn(java.util.Optional.of("Test"));
        codeCommentsAndFormatting.compute(Arrays.asList(mockCompilationUnit, mockCompilationUnit), "filePath");
        assertEquals(55, codeCommentsAndFormatting.getResult());
    }

    @Test
    public void calculateResultShouldReturnZeroWhenNoCompilationUnits() {
        codeCommentsAndFormatting.compute(Collections.emptyList(), "filePath");
        assertEquals(0, codeCommentsAndFormatting.getResult());
    }
}