package seng4430_softwarequalitytool.DataValidation;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import seng4430_softwarequalitytool.Util.Module;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class BillOfMaterials implements Module {

    static List<String> standardLibraries = new ArrayList<>();
    List<String> nonStandardLibraries = new ArrayList<>();

    private StringBuilder htmlOutput = new StringBuilder();


    // Function to check for vulnerabilities in import statements
    public void checkForVulnerabilitiesInImports(List<CompilationUnit> compilationUnits) {

        // Loop through each compilation unit
        for (CompilationUnit cu : compilationUnits) {
            // Get all import declarations in the compilation unit
            List<ImportDeclaration> importDeclarations = cu.findAll(ImportDeclaration.class);

            // Check each import declaration for potential vulnerabilities
            for (ImportDeclaration importDeclaration : importDeclarations) {
                if (isStandard(importDeclaration)) {
                    // Add vulnerability information to the list
                    standardLibraries.add(importDeclaration.getNameAsString());
                }else{
                    nonStandardLibraries.add(importDeclaration.getNameAsString());
                }
            }
        }

    }

    private  boolean isStandard(ImportDeclaration importDeclaration) {
        // Check if the import starts with "java." or "javax."
        String importPath = importDeclaration.getNameAsString();
        return importPath.startsWith("java.") || importPath.startsWith("javax.");
    }


    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        checkForVulnerabilitiesInImports(compilationUnits);

        System.out.println("Standard Libraries:");
        this.htmlOutput.append("<b>Standard Libraries</b><hr />");
        this.htmlOutput.append("<table class=\"table\">")
                .append("<thead class=\"thead-light\">")
                .append("<tr>")
                .append("<th scope=\"col\">Import</th>")
                .append("<th scope=\"col\">Usages</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");
        if(standardLibraries.size() == 0){
            System.out.println("<em>No Standard Libraries</em>");
            this.htmlOutput.append("<tr>")
                    .append("<td>").append("No Standard Libraries Used").append("</td>")
                    .append("<td>").append("</td>")
                    .append("</tr>");
        }else{
            for (String s : standardLibraries) {
                System.out.println(s);
                this.htmlOutput.append("<tr>")
                        .append("<td>").append(s).append("</td>")
                        .append("<td>").append("1").append("</td>")
                        .append("</tr>");
            }
        }
        this.htmlOutput.append("</tbody>")
                .append("</table>");

        System.out.println("Non-Standard Libraries:");
        this.htmlOutput.append("<b>Non-Standard Libraries:</b><hr />");
        this.htmlOutput.append("<table class=\"table\">")
                .append("<thead class=\"thead-light\">")
                .append("<tr>")
                .append("<th scope=\"col\">Import</th>")
                .append("<th scope=\"col\">Usages</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");
        if(nonStandardLibraries.size() == 0){
            System.out.println("No Non-Standard Libraries Used");
            this.htmlOutput.append("<tr>")
                    .append("<td>").append("<em>No Non-Standard Libraries</em>").append("</td>")
                    .append("<td>").append("</td>")
                    .append("</tr>");
        }else{
            for (String s : nonStandardLibraries) {
                System.out.println(s);
                this.htmlOutput.append("<tr>")
                        .append("<td>").append(s).append("</td>")
                        .append("<td>").append("1").append("</td>")
                        .append("</tr>");
            }
        }
        this.htmlOutput.append("</tbody>")
                .append("</table>");

        printToFile(filePath);
        return "";
    }


    private void printToFile(String filePath) {
        String find = "<!------ @@Bill Of Materials@@  ---->";

        try {
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
            content = content.replaceAll(find, this.htmlOutput.toString());

            // Write modified content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content);
            writer.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
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
}
