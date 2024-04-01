package seng4430_softwarequalitytool.FogIndex;

import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.Util.Module;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

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
    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        try{
            printModuleHeader();



            printInformation();
            saveResult();
            return "Fog Index Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating Fog Index.";
        }
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
