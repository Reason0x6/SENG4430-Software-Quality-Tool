package seng4430_softwarequalitytool.CyclomaticComplexity;


import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.Util.Module;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Module to compute cyclomatic complexity of Java source code.
 * Provides a method to calculate cyclomatic complexity and evaluate risk levels
 * based on defined ranges given in risk_ranges.properties.
 *
 * @author G Austin
 * @studentID 3279166
 * @lastModified: 22/03/2024
 */

public class CyclomaticComplexity implements Module {

    /** Properties object to hold risk ranges for cyclomatic complexity. */
    private Properties properties;
    private int complexity = 0;

    /**
     * Constructs a CyclomaticComplexity object using default risk ranges.
     */
    public CyclomaticComplexity() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/risk_ranges.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a CyclomaticComplexity object using risk ranges from a specified location.
     *
     * @param location The file location of the properties file containing risk ranges.
     */
    public CyclomaticComplexity(String location) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(location));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String compute(List<CompilationUnit> compilationUnits) {
        try{
            printModuleHeader();
            this.complexity = calculateCyclomaticComplexity(compilationUnits);
            printInformation();
            saveResult();
            return "Cyclomatic Complexity Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating Cyclomatic Complexity.";
        }

    }



    @Override
    public  void printModuleHeader(){
        System.out.println("---- Cyclomatic Complexity Module ----");
        System.out.format("%25s %s", "Function Name", "Cyclomatic Complexity\n");
    }


    @Override
    public void printInformation(){
        System.out.println("---- Definitions Used ----");
        System.out.println(properties.toString());
        System.out.println("---- Results ----");
    }

    @Override
    public void saveResult(){
        System.out.println("Complexity Score: " + this.complexity + "\nRisk: " + evaluateRisk( this.complexity));
    }


    public void printRowInformation(String methodName, int complexity){
        System.out.format("%25s %s\n", methodName + "()", complexity);
    }


    /**
     * Computes the complexity for compilation units provided in App.java.
     * receives the complexity score for each compilation unit and returns adds to the total complexity.
     *
     * @param compilationUnits List of CompilationUnit objects representing Java source files.
     * @return A string containing the computed complexity score and corresponding risk level.
     */
    public  int calculateCyclomaticComplexity(List<CompilationUnit> compilationUnits) {
        AtomicInteger totalComplexity = new AtomicInteger();
        for (CompilationUnit cu : compilationUnits) {
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                totalComplexity.addAndGet(calculateMethodComplexity(method));
            });
        }
        return totalComplexity.get();
    }

    /**
     * Calculates the cyclomatic complexity for a single method.
     *
     * @param method MethodDeclaration object representing the method.
     * @return The cyclomatic complexity score of the method.
     */
    private  int calculateMethodComplexity(MethodDeclaration method) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        visitor.visit(method, null);
        int complexity = visitor.getComplexity();
        printRowInformation(method.getNameAsString(), complexity);

        return complexity;
    }


    public  String evaluateRisk(int complexity) {
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




}


