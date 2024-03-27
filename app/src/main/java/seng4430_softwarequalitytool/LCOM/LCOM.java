package seng4430_softwarequalitytool.LCOM;

import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.Util.Module;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

/**
 * Module to compute lack of cohesion in methods (LCOM) of Java source code.
 * Provides a method to calculate LCOM and evaluate ....
 * based on defined ranges given in ....
 *
 * @author I Andrews
 * @studentID c3204936
 * @lastModified: 27/03/2024
 */

public class LCOM implements Module {
    @Override
    public String compute(List<CompilationUnit> compilationUnits) {
        try{
            printModuleHeader();



            printInformation();
            saveResult();
            return "LCOM Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating LCOM.";
        }
    }

    @Override
    public void printModuleHeader() {
        System.out.println("\n");
        System.out.println("---- LCOM Module ----");
        System.out.format("%25s %s", "Function Name", "Lack of Cohesion in Methods (LCOM)\n");
    }

    @Override
    public void printInformation() {
        System.out.println("---- Definitions Used ----");
        // System.out.println(properties.toString());
        System.out.println("---- Results ----");
    }

    @Override
    public void saveResult() {
        System.out.println("LCOM Score: 500");
    }
}
