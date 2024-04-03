package seng4430_softwarequalitytool.WeightedMethodsPerClass;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import seng4430_softwarequalitytool.Util.Module;

import java.util.ArrayList;
import java.util.List;
import seng4430_softwarequalitytool.Util.ClassModel;
import seng4430_softwarequalitytool.Util.MethodModel;

import static seng4430_softwarequalitytool.Util.ClassModel.getClassData;

/**
 * Class WeightedMethodsPerClass
 *
 * this module will measure the complexity per method and methods per class to arrive
 * at a general index for class complexity.
 *
 * 20 LOC in a Method will be the maximum before the complexity of a method
 * will be brought into question (negatively impact the score). 40 LOC is considered a
 * full red-flag situation requiring attention.
 *
 * 15 Methods in a Class will be the maximum before the complexity of a method
 * will be brought into question (negatively impact the score). 20 methods is considered a
 * full red-flag situation requiring attention.
 *
 * Should class also involve No. of variables per class / per method (RESOLVE THIS)
 */
public class WeightedMethodsPerClass  implements Module {
    //class models potentially moving out of this class for broader use
    private static int CAUTION_NOM_PER_CLASS = 15;
    private static int WARNING_NOM_PER_CLASS = 20;
    private static int CAUTION_LOC_PER_METHOD = 20;
    private static int WARNING_LOC_PER_METHOD = 40;
    private List<ClassModel> classes = new ArrayList<>();
    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        //build out models of classes and methods

        getClassData(compilationUnits, classes);

        return "\n***********************\n" + this + "***********************\n";
    }

    @Override
    public void printModuleHeader() {

    }

    @Override
    public void printInformation() {

    }

    @Override
    public void saveResult() {

    }


    public String toStringOld() {
        StringBuilder result = new StringBuilder();
        result.append("\nweighted methods per class:\n");
        int i = 0;
        for (ClassModel classModel :
                classes) {
            result.append("class ").append(++i).append(":").append("\n");
            result.append("  ").append("class name - ").append(classModel.name).append("\n");
            result.append("  ").append("Number of methods - ").append(classModel.numberOfMethods).append("\n");
            int j = 0;
            for (MethodModel methodModel :
                    classModel.methods) {
                result.append("  ").append("method ").append(++j).append(":").append("\n");
                result.append("  ").append("  ").append("method name - ").append(methodModel.name).append("\n");
                result.append("  ").append("  ").append("Number of LOC - ").append(methodModel.linesOfCode).append("\n");
            }
        }
        return result.toString();
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("weighted methods per class:\n\n");
        for (ClassModel classModel :
                classes) {
            if (classModel.numberOfMethods > CAUTION_NOM_PER_CLASS && classModel.numberOfMethods < WARNING_NOM_PER_CLASS) {
                result.append("Class ")
                        .append(classModel.name)
                        .append(" has ")
                        .append(classModel.numberOfMethods)
                        .append(" methods. This is over the ")
                        .append(CAUTION_NOM_PER_CLASS)
                        .append(" method threshold giving this Class a caution status.\n");
            }
            if (classModel.numberOfMethods >= WARNING_NOM_PER_CLASS) {
                result.append("Class ")
                        .append(classModel.name)
                        .append(" has ")
                        .append(classModel.numberOfMethods)
                        .append(" methods. This is over the ")
                        .append(WARNING_NOM_PER_CLASS)
                        .append(" method threshold giving this Class a warning status.\n");
            }
            for (MethodModel methodModel :
                    classModel.methods) {
                if (methodModel.linesOfCode > CAUTION_LOC_PER_METHOD && methodModel.linesOfCode < WARNING_LOC_PER_METHOD) {
                    result.append("Method ")
                            .append(methodModel.name)
                            .append(" of class ")
                            .append(classModel.name)
                            .append(" has ")
                            .append(methodModel.linesOfCode)
                            .append(" LOC. This is over the ")
                            .append(CAUTION_LOC_PER_METHOD)
                            .append(" LOC threshold giving this Method a caution status.\n");
                }
                if (methodModel.linesOfCode >= WARNING_LOC_PER_METHOD) {
                    result.append("Method ")
                            .append(classModel.name)
                            .append(" of class ")
                            .append(classModel.name)
                            .append(" has ")
                            .append(methodModel.linesOfCode)
                            .append(" LOC. This is over the ")
                            .append(WARNING_LOC_PER_METHOD)
                            .append(" LOC threshold giving this Method a warning status.\n");
                }
            }
        }
        return result.toString();
    }
}
