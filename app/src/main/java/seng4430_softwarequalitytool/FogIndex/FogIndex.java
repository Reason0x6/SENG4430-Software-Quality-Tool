package seng4430_softwarequalitytool.FogIndex;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import seng4430_softwarequalitytool.Util.Module;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Module to compute fog index of Java source code.
 * Provides a method to calculate fog index and evaluate ....
 * based on defined ranges given in ....
 *
 * @author I Andrews
 * @studentID c3204936
 * @lastModified: 24/03/2024
 */

public class FogIndex implements Module {

    private List<CompilationUnit> CompilationUnits;
    private int wordCount = 0;
    private int commentCount = 0;
    private int commentSentences = 0;
    private int commentComplexWords = 0;
    private int codeCount = 0;
    private int codeSentences = 0;
    private int codeSylables = 0;
    SyllableCounter syllableCounter = new SyllableCounter();
    private Properties properties;


    public FogIndex(String location) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(location));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FogIndex() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/fogindex_ranges.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        try{
            printModuleHeader();

            this.wordCount = computeFogIndex(compilationUnits);
            System.out.println("Comment words: " + commentCount);
            System.out.println("Comment sentences: " + commentSentences);
            System.out.println("Comment sentences: " + commentComplexWords);
            int commentRange = calculateFogIndex(commentCount, commentSentences, commentComplexWords);
            System.out.println("Gunning Fog Index for Code Comments: " + commentRange);
            System.out.println("Reading Level: " + evaluateRange(commentRange));

            printInformation();
            saveResult();
            return "Fog Index Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating Fog Index.";
        }
    }

    public int calculateFogIndex(int wordCount, int sentenceCount, int complexWordCount) {
        return (int) Math.round(0.4 * ((wordCount / sentenceCount) + 100 * (complexWordCount / wordCount)));
    }

    public  int computeFogIndex(List<CompilationUnit> compilationUnits) {

        for (CompilationUnit cu : compilationUnits) {
            java.util.List<Comment> comments = cu.getAllComments();
            for (Comment comment : comments) {
               commentCount += countWords(comment.asString());
               commentSentences += countSentences(comment.asString());
               commentComplexWords += syllableCounter.countWordsWithThreeOrMoreSyllables(comment.asString());
            }

        }
        return wordCount;
    }

    public static int countWords(String str) {
        int tempWordCount = 0;
        str = str.replaceAll("[^a-zA-Z 0-9,.+\\-=()]", "");
        if (str != null) {
            String[] words = str.split("\\s+");
            words = Arrays.stream(words).filter(word -> !word.isEmpty()).toArray(String[]::new);
            System.out.println(Arrays.toString(words));
            tempWordCount += words.length;
        }

        return tempWordCount;
    }

    public static int countSentences(String str) {
        int tempSentencesCount = 0;
        str = str.replaceAll("[^a-zA-Z 0-9,.+\\-=()]", "");
        if (str != null) {
            String[] sentences = str.split("/.");
            sentences = Arrays.stream(sentences).filter(sentence -> !sentence.isEmpty()).toArray(String[]::new);
            System.out.println(Arrays.toString(sentences));
            tempSentencesCount += sentences.length;
        }

        return tempSentencesCount;
    }


    public  String evaluateRange(int complexity) {
        String riskLevel = "Unknown";
        for (String range : properties.stringPropertyNames()) {
            String[] limits = range.split("_");
            int lowerLimit = Integer.parseInt(limits[0]);
            int upperLimit = limits.length == 1 ? Integer.MAX_VALUE : Integer.parseInt(limits[1]);
            if (complexity >= lowerLimit && complexity <= upperLimit) {
                riskLevel = properties.getProperty(range);
                break;
            }
        }

        return riskLevel;
    }

    @Override
    public void printModuleHeader() {
        System.out.println("\n");
        System.out.println("---- Fog Index Module ----");
        System.out.format("%25s %s", "Function Name", "Fog Index\n");
    }

    @Override
    public void printInformation() {
        System.out.println("---- Definitions Used ----");
        // System.out.println(properties.toString());
        System.out.println("---- Results ----");
    }

    @Override
    public void saveResult() {
        System.out.println("Fog Index Score: 500");
    }
}
