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
 * where:
 * 'I' is the coupling index between module / classes
 * I = 1 - 1/(1+R+P+M)
 * R = return type usage
 * P = parameter usage
 * M = member usage
 * dependence / coupling from 0 to 1 (0 = lowest level of coupling).
 *
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
        html.append("<button class=\"btn btn-light\" onclick=\"toggleViewById('CBC')\">Toggle greater detail</button>");
        html.append("<table class=\"table\" id=\"CBC\" style=\"display:none;\">");
        html.append("<thead class=\"thead-light\"><tr><th scope=\"col\">Class relationship</th>" +
                "<th scope=\"col\">Total usage</th><th scope=\"col\">Return type usage</th>" +
                "<th scope=\"col\">Parameter usage</th><th scope=\"col\">member usage</th></tr></thead>");
        html.append("<tbody>");
        StringBuilder comments = new StringBuilder();
        comments.append("<table class=\"table\">");
        comments.append("<thead class=\"thead-light\"><tr>" +
                "<th scope=\"col\">Comments on Usage</th>" +
                "<th scope=\"col\">Index</th>" +
                "<th scope=\"col\">Return Type</th>" +
                "<th scope=\"col\">Parameter</th>" +
                "<th scope=\"col\">Member</th>" +
                "</tr></thead>");
        comments.append("<tbody>");
        boolean issueFound = false;
        for (int i = 0; i < classes.size(); i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                ClassModel a = classes.get(i);
                ClassModel b = classes.get(j);
                double bUsingAIndex = couplingIndex(a.returnTypeDictionary.getOrDefault(b.name,0),
                        a.parameterDictionary.getOrDefault(b.name,0),
                        a.memberDictionary.getOrDefault(b.name,0));
                double aUsingBIndex = couplingIndex(b.returnTypeDictionary.getOrDefault(a.name,0),
                        b.parameterDictionary.getOrDefault(a.name,0),
                        b.memberDictionary.getOrDefault(a.name,0));
                html.append("<tr>");
                html.append("<td><span style=\"font-weight:bold;\">" + b.name + "</span> - uses - <span style=\"font-weight:bold;\">" + a.name + "</span></td>");
                html.append("<td>" +
                        couplingIndexString(a.returnTypeDictionary.getOrDefault(b.name,0),
                                 a.parameterDictionary.getOrDefault(b.name,0),
                                 a.memberDictionary.getOrDefault(b.name,0))
                        + "</td>");
                html.append("<td>" + a.returnTypeDictionary.getOrDefault(b.name,0) + "</td>");
                html.append("<td>" + a.parameterDictionary.getOrDefault(b.name,0) + "</td>");
                html.append("<td>" + a.memberDictionary.getOrDefault(b.name,0) + "</td>");
                html.append("</tr>");
                html.append("<tr>");
                html.append("<td><span style=\"font-weight:bold;\">" + a.name + "</span> - uses - <span style=\"font-weight:bold;\">" + b.name + "</span></td>");
                html.append("<td>" +
                        couplingIndexString(b.returnTypeDictionary.getOrDefault(a.name,0),
                                b.parameterDictionary.getOrDefault(a.name,0),
                                b.memberDictionary.getOrDefault(a.name,0))
                        + "</td>");
                html.append("<td>" + b.returnTypeDictionary.getOrDefault(a.name,0) + "</td>");
                html.append("<td>" + b.parameterDictionary.getOrDefault(a.name,0) + "</td>");
                html.append("<td>" + b.memberDictionary.getOrDefault(a.name,0) + "</td>");
                html.append("</tr>");
                if (bUsingAIndex >= 0.8 && bUsingAIndex < 0.9) {
                    issueFound = true;
                    comments.append("<tr bgcolor=\"#ECD55E\">");
                    comments.append("<td><span style=\"font-weight:bold;\">" + b.name + "</span> - uses - <span style=\"font-weight:bold;\">" + a.name + "</span></td>");
                    comments.append("<td>" +
                            couplingIndexString(a.returnTypeDictionary.getOrDefault(b.name,0),
                            a.parameterDictionary.getOrDefault(b.name,0),
                            a.memberDictionary.getOrDefault(b.name,0))
                            + "</td>");
                    comments.append("<td>" + a.returnTypeDictionary.getOrDefault(b.name,0) + "</td>");
                    comments.append("<td>" + a.parameterDictionary.getOrDefault(b.name,0) + "</td>");
                    comments.append("<td>" + a.memberDictionary.getOrDefault(b.name,0) + "</td>");
                    comments.append("</tr>");
                } else if (bUsingAIndex >= 0.9) {
                    issueFound = true;
                    comments.append("<tr bgcolor=\"#F29461\">");
                    comments.append("<td><span style=\"font-weight:bold;\">" + b.name + "</span> - uses - <span style=\"font-weight:bold;\">" + a.name + "</span></td>");
                    comments.append("<td>" +
                            couplingIndexString(a.returnTypeDictionary.getOrDefault(b.name,0),
                                    a.parameterDictionary.getOrDefault(b.name,0),
                                    a.memberDictionary.getOrDefault(b.name,0))
                            + "</td>");
                    comments.append("<td>" + a.returnTypeDictionary.getOrDefault(b.name,0) + "</td>");
                    comments.append("<td>" + a.parameterDictionary.getOrDefault(b.name,0) + "</td>");
                    comments.append("<td>" + a.memberDictionary.getOrDefault(b.name,0) + "</td>");
                    comments.append("</tr>");
                }
                if (aUsingBIndex >= 0.8 && aUsingBIndex < 0.9) {
                    issueFound = true;
                    comments.append("<tr bgcolor=\"#ECD55E\">");
                    comments.append("<td><span style=\"font-weight:bold;\">" + a.name + "</span> - uses - <span style=\"font-weight:bold;\">" + b.name + "</span></td>");
                    comments.append("<td>" +
                            couplingIndexString(b.returnTypeDictionary.getOrDefault(a.name,0),
                                    b.parameterDictionary.getOrDefault(a.name,0),
                                    b.memberDictionary.getOrDefault(a.name,0))
                            + "</td>");
                    comments.append("<td>" + b.returnTypeDictionary.getOrDefault(a.name,0) + "</td>");
                    comments.append("<td>" + b.parameterDictionary.getOrDefault(a.name,0) + "</td>");
                    comments.append("<td>" + b.memberDictionary.getOrDefault(a.name,0) + "</td>");
                    comments.append("</tr>");
                } else if (aUsingBIndex >= 0.9) {
                    issueFound = true;
                    comments.append("<tr bgcolor=\"#F29461\">");
                    comments.append("<td><span style=\"font-weight:bold;\">" + a.name + "</span> - uses - <span style=\"font-weight:bold;\">" + b.name + "</span></td>");
                    comments.append("<td>" +
                            couplingIndexString(b.returnTypeDictionary.getOrDefault(a.name,0),
                                    b.parameterDictionary.getOrDefault(a.name,0),
                                    b.memberDictionary.getOrDefault(a.name,0))
                            + "</td>");
                    comments.append("<td>" + b.returnTypeDictionary.getOrDefault(a.name,0) + "</td>");
                    comments.append("<td>" + b.parameterDictionary.getOrDefault(a.name,0) + "</td>");
                    comments.append("<td>" + b.memberDictionary.getOrDefault(a.name,0) + "</td>");
                    comments.append("</tr>");
                }

            }
        }
        html.append("</tbody>");
        html.append("</table>");
        comments.append("</tbody>");
        comments.append("</table>");
        if (issueFound) {
            html.append(comments);
        } else {
            html.append("<table class=\"table\">");
            html.append("<thead class=\"thead-light\"><tr><th scope=\"col\">No coupling issues detected in this application</th></tr></thead>");
            html.append("<tbody>");
            html.append("</tbody>");
            html.append("</table>");
        }
    }

    private String couplingIndexString(int returnUsage, int parameterUsage, int memberUsage) {
        double result = 1 - (1 / (double)(1 + returnUsage + parameterUsage + memberUsage));
        return String.format("%.2f",result);
    }
    private double couplingIndex(int returnUsage, int parameterUsage, int memberUsage) {
        return 1 - (1 / (double)(1 + returnUsage + parameterUsage + memberUsage));

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
