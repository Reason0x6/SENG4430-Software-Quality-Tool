package seng4430_softwarequalitytool.WeightedMethodsPerClass;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import seng4430_softwarequalitytool.Util.Module;

import java.io.*;
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

    private StringBuilder html = new StringBuilder();
    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        //build out models of classes and methods

        getClassData(compilationUnits, classes);

        String result = "\n***********************\n" + toString() + "***********************\n";
        System.out.println(result);

        try{
            printModuleHeader();
            printInformation();
            saveResult();
            printToFile(filePath);
            return "Weighted Methods per Class Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating Weighted Methods per Class.";
        }

    }

    private void printToFile(String filePath) throws IOException {
        //pattern to find and replace
        String find = " <!------ @@WMC Output@@  ---->";
        // Read the content of the file
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            contentBuilder.append(line).append("\n");
        }
        reader.close();
        String content = contentBuilder.toString();

        // Perform find and replace operation
        content = content.replaceAll(find, this.html.toString());

        // Write modified content back to the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }

    @Override
    public void printModuleHeader() {

    }

    @Override
    public void printInformation() {

        int i = 0;
        for (ClassModel classModel :
                classes) {
            i++;
            html.append("<h3>Class: " + classModel.name + "</h3>");
            html.append("<button class=\"btn btn-light\" onclick=\"toggleViewById(\'WMPC"+i+"-"+"\')\">Toggle greater detail</button>");
            html.append("<table class=\"table\" id=\"WMPC"+i+"-"+"\" style=\"display:none;\">");
            html.append("<thead class=\"thead-light\"><tr><th scope=\"col\">" + classModel.name + " | Methods: " + classModel.numberOfMethods + "</th><th scope=\"col\">LOC per Method</th></tr></thead>");
            html.append("<tbody>");
            for (MethodModel methodModel :
                    classModel.methods) {
                html.append("<tr><td>" + methodModel.id + "</td><td>" + methodModel.linesOfCode + "</td></tr>");
            }
            html.append("</tbody>");
            html.append("</table>");
            StringBuilder comments = new StringBuilder();
            comments.append("<table class=\"table\">");
            comments.append("<thead class=\"thead-light\"><tr><th scope=\"col\">Comments</th></tr></thead>");
            comments.append("<tbody>");
            boolean commentPresent = false;
            if (classModel.numberOfMethods > CAUTION_NOM_PER_CLASS && classModel.numberOfMethods < WARNING_NOM_PER_CLASS) {
                commentPresent = true;
                comments.append("<tr bgcolor=\"#ECD55E\"><td>Class ")
                        .append(classModel.name)
                        .append(" has ")
                        .append(classModel.numberOfMethods)
                        .append(" methods. This is over the ")
                        .append(CAUTION_NOM_PER_CLASS)
                        .append(" method threshold giving this Class a caution status.</td></tr>");
            }
            if (classModel.numberOfMethods >= WARNING_NOM_PER_CLASS) {
                commentPresent = true;
                comments.append("<tr bgcolor=\"#F29461\"><td>Class ")
                        .append(classModel.name)
                        .append(" has ")
                        .append(classModel.numberOfMethods)
                        .append(" methods. This is over the ")
                        .append(WARNING_NOM_PER_CLASS)
                        .append(" method threshold giving this Class a warning status.</td></tr>");
            }
            for (MethodModel methodModel :
                    classModel.methods) {
                if (methodModel.linesOfCode > CAUTION_LOC_PER_METHOD && methodModel.linesOfCode < WARNING_LOC_PER_METHOD) {
                    commentPresent = true;
                    comments.append("<tr bgcolor=\"#ECD55E\"><td>Method ")
                            .append(methodModel.name)
                            .append(" of class ")
                            .append(classModel.name)
                            .append(" has ")
                            .append(methodModel.linesOfCode)
                            .append(" LOC. This is over the ")
                            .append(CAUTION_LOC_PER_METHOD)
                            .append(" LOC threshold giving this Method a caution status.</td></tr>");
                }
                if (methodModel.linesOfCode >= WARNING_LOC_PER_METHOD) {
                    commentPresent = true;
                    comments.append("<tr bgcolor=\"#F29461\"><td>Method ")
                            .append(methodModel.name)
                            .append(" of class ")
                            .append(classModel.name)
                            .append(" has ")
                            .append(methodModel.linesOfCode)
                            .append(" LOC. This is over the ")
                            .append(WARNING_LOC_PER_METHOD)
                            .append(" LOC threshold giving this Method a warning status.</td></tr>");
                }
            }
            comments.append("</tbody>");
            comments.append("</table>");
            if(commentPresent){
                html.append(comments);
            }else{
                html.append("<table class=\"table\">");
                html.append("<thead class=\"thead-light\"><tr><th scope=\"col\">No issues detected in this class</th></tr></thead>");
                html.append("<tbody>");
                html.append("</tbody>");
                html.append("</table>");
            }
        }
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
