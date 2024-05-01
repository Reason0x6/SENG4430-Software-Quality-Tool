package seng4430_softwarequalitytool.CouplingBetweenClasses;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import seng4430_softwarequalitytool.Util.ClassModel;
import seng4430_softwarequalitytool.Util.Module;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static seng4430_softwarequalitytool.Util.ClassModel.getClassData;

/**
 * Fenton and Melton metric C(a,b) = i + n/(n+1)
 * where:
 * C(a,b) is the coupling index between module / classes
 * a and b, n is the number of dependencies and i is a score
 * of the tightest dependency from 0 to 5 (0 = lowest tightness).
 *
 * currently only n is considered, that is C(a,b) = n/(n+1)
 * to be added to later
 */
public class CouplingBetweenClasses implements Module {
    private List<ClassModel> classes = new ArrayList<>();
    private StringBuilder html = new StringBuilder();
    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        //build out models of classes and methods
        getClassData(compilationUnits, classes);
        List<String> classNames = new ArrayList<>();
        for (ClassModel c :
                classes) {
            classNames.add(c.name);
        }
        for (ClassModel c :
                classes) {
            c.findDependencies(compilationUnits, classNames);
        }

        String result = "\n***********************\n" + toString() + "\n***********************\n";
        System.out.println(result);
        try{
            printModuleHeader();
            printInformation();
            saveResult();
            printToFile(filePath);
            return "Coupling Between Classes Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating Coupling Between Classes.";
        }
    }

    private void printToFile(String filePath) throws IOException {
        //pattern to find and replace
        String find = " <!------ @@CBO Output@@  ---->";
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
        html.append("<table class=\"table\">");
        html.append("<thead class=\"thead-light\"><tr><th scope=\"col\">Class relationship</th>" +
                "<th scope=\"col\">Total usage</th><th scope=\"col\">Return type usage</th>" +
                "<th scope=\"col\">Parameter usage</th><th scope=\"col\">member usage</th></tr></thead>");
        html.append("<tbody>");
        for (int i = 0; i < classes.size(); i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                ClassModel a = classes.get(i);
                ClassModel b = classes.get(j);
                int bUsingA = a.returnTypeDictionary.getOrDefault(b.name,0)
                        + a.parameterDictionary.getOrDefault(b.name,0)
                        + a.memberDictionary.getOrDefault(b.name,0);
                int aUsingB = b.returnTypeDictionary.getOrDefault(a.name,0)
                        + b.parameterDictionary.getOrDefault(a.name,0)
                        + b.memberDictionary.getOrDefault(a.name,0);
                if (bUsingA > 4) {
                    html.append("<tr>");
                    html.append("<td>" + b.name + " uses " + a.name + "</td>");
                    html.append("<td>" +
                            (a.returnTypeDictionary.getOrDefault(b.name,0)
                            + a.parameterDictionary.getOrDefault(b.name,0)
                            + a.memberDictionary.getOrDefault(b.name,0))
                            + "</td>");
                    html.append("<td>" + a.returnTypeDictionary.getOrDefault(b.name,0) + "</td>");
                    html.append("<td>" + a.parameterDictionary.getOrDefault(b.name,0) + "</td>");
                    html.append("<td>" + a.memberDictionary.getOrDefault(b.name,0) + "</td>");
                    html.append("</tr>");
                }
                if (aUsingB > 4) {
                    html.append("<tr>");
                    html.append("<td>" + a.name + " uses " + b.name + "</td>");
                    html.append("<td>" +
                            (b.returnTypeDictionary.getOrDefault(a.name,0)
                                    + b.parameterDictionary.getOrDefault(a.name,0)
                                    + b.memberDictionary.getOrDefault(a.name,0))
                            + "</td>");
                    html.append("<td>" + b.returnTypeDictionary.getOrDefault(a.name,0) + "</td>");
                    html.append("<td>" + b.parameterDictionary.getOrDefault(a.name,0) + "</td>");
                    html.append("<td>" + b.memberDictionary.getOrDefault(a.name,0) + "</td>");
                    html.append("</tr>");
                }

            }
        }
        html.append("</tbody>");
        html.append("</table>");
    }

    @Override
    public void saveResult() {

    }

    @Override
    public String toString() {
        String result = "";

        for (int i = 0; i < classes.size(); i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                ClassModel a = classes.get(i);
                ClassModel b = classes.get(j);
                int bUsingA = a.returnTypeDictionary.getOrDefault(b.name,0)
                        + a.parameterDictionary.getOrDefault(b.name,0)
                        + a.memberDictionary.getOrDefault(b.name,0);
                int aUsingB = b.returnTypeDictionary.getOrDefault(a.name,0)
                        + b.parameterDictionary.getOrDefault(a.name,0)
                        + b.memberDictionary.getOrDefault(a.name,0);
                if (bUsingA > 4) {
                    result += b.name + " uses " + a.name + ":\n";
                    result += a.returnTypeDictionary.getOrDefault(b.name,0) + " times as a return type\n";
                    result += a.parameterDictionary.getOrDefault(b.name,0) + " times as a parameter\n";
                    result += a.memberDictionary.getOrDefault(b.name,0) + " times as a instance member\n";
                }
                if (aUsingB > 4) {
                    result += a.name + " uses " + b.name + ":\n";
                    result += b.returnTypeDictionary.getOrDefault(a.name,0) + " times as a return type\n";
                    result += b.parameterDictionary.getOrDefault(a.name,0) + " times as a parameter\n";
                    result += b.memberDictionary.getOrDefault(a.name,0) + " times as a instance member\n";
                }
            }
        }
        return result;
    }
}
