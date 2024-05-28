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
//
//    @Test
//    void testComputeFogIndex() {
//        List<CompilationUnit> compilationUnits = new ArrayList<>();
//        CompilationUnit compilationUnit1 = new CompilationUnit();
//        CompilationUnit compilationUnit2 = new CompilationUnit();
//        compilationUnits.add(compilationUnit1);
//        compilationUnits.add(compilationUnit2);
//
//        LineComment comment1 = new LineComment();
//        comment1.setContent("In the realm of quantum mechanics, the entanglement of particles within a multi-dimensional spacetime manifold presents profound implications for the conceptualization of causality, challenging our fundamental understanding of the deterministic nature of physical phenomena and necessitating a paradigmatic shift towards a more holistic and probabilistic interpretation of reality.");
//        LineComment comment2 = new LineComment();
//        comment2.setContent("This is another test comment. Despite the rain, the determined hiker trudged up the steep, muddy path, eager to reach the summit before nightfall.");
//
//        compilationUnit1.addOrphanComment(comment1);
//        compilationUnit2.addOrphanComment(comment2);
//        compilationUnit1.addEnum("Tables");
//        compilationUnit2.addClass("Test Class");
//
//        assertArrayEquals(new int[]{21, 72, 2, 21, 83, 6}, fogIndex.computeFogIndex(compilationUnits));
//
//    }
//
//    @Test
//    void testCountWords() {
//        assertEquals(4, fogIndex.countWords("This is a test.", true));
//    }
//
//    @Test
//    void testCountSentences() {
//        assertEquals(2, fogIndex.countSentences("This is a test; This is another test.", true));
//    }
//
//    @Test
//    void testEvaluateRange() {
//        assertEquals("Senior High School", fogIndex.evaluateRange(10));
//    }
//
//
//    @Test
//    public void countWordsWithThreeOrMoreSyllables_returnsZeroForEmptyString() {
//        // Given
//        String sentence = "";
//
//        // When
//        int count = SyllableCounter.countWordsWithThreeOrMoreSyllables(sentence);
//
//        // Then
//        assertEquals(0, count);
//    }
//
//    @Test
//    public void countWordsWithThreeOrMoreSyllables_returnsZeroForStringWithNoWordsOfThreeOrMoreSyllables() {
//        // Given
//        String sentence = "This is a test.";
//
//        // When
//        int count = SyllableCounter.countWordsWithThreeOrMoreSyllables(sentence);
//
//        // Then
//        assertEquals(0, count);
//    }
}
