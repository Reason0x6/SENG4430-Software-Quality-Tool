package seng4430_softwarequalitytool.ErrorHandling;


import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.Util.Module;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ErrorHandling implements Module {

    private int result;

    private List<ErrorHandlingMetrics> errorHandlingMetricsList = new ArrayList<>();
    private StringBuilder htmlOutput = new StringBuilder();


    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        try {
            for (CompilationUnit compilationUnit : compilationUnits) {
                // Analyze error handling metrics for each CompilationUnit
                errorHandlingMetricsList.add(setMetricValues(compilationUnit));
            }

            printInformation();
            saveResult();
            printToFile(filePath);


            return "Error Handling Successfully Calculated.";
        } catch(Exception e){
            return "Error Calculating Error Handling.";
        }
    }

    @Override
    public void printModuleHeader() {
        // console
        System.out.println("\n");
        System.out.println("---- Error Handling ----");
        System.out.format(
                "%-35s %-20s %-20s %-25s %-25s %s\n",
                "File Name",
                "Score",
                "Try Catch Count",
                "Throw Count",
                "Unique Exception Count",
                "Generic Exception Count"
        );

        // HTML
        this.htmlOutput.append("<div class=\"col-sm-12\">");
        this.htmlOutput.append("<table class=\"table\">")
                .append("<thead class=\"thead-light\">")
                .append("<tr>")
                .append("<th scope=\"col\">File Name</th>")
                .append("<th scope=\"col\">Try Catch Count</th>")
                .append("<th scope=\"col\">Throw Count</th>")
                .append("<th scope=\"col\">Unique Exception Count</th>")
                .append("<th scope=\"col\">Generic Exception Count</th>")
                .append("<th scope=\"col\">Score</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");
    }

    @Override
    public void printInformation() {
        printModuleHeader();
        // each row
        for (ErrorHandlingMetrics errorHandlingMetrics : errorHandlingMetricsList) {
            // console
            System.out.format("%-35s %-20s %-20s %-25s %-25s %s\n",
                    errorHandlingMetrics.getClassName(),
                    errorHandlingMetrics.getScore(),
                    errorHandlingMetrics.getTryCatchCount(),
                    errorHandlingMetrics.getThrowCount(),
                    errorHandlingMetrics.getUnequeExceptionTypes().size(),
                    errorHandlingMetrics.getGenericExceptionsCount()
            );

            // HTML
            this.htmlOutput.append("<tr>")
                    .append("<td>"+errorHandlingMetrics.getClassName()+"()</td>")
                    .append("<td>"+errorHandlingMetrics.getTryCatchCount()+"</td>")
                    .append("<td>"+errorHandlingMetrics.getThrowCount()+"</td>")
                    .append("<td>"+errorHandlingMetrics.getUnequeExceptionTypes().size()+"</td>")
                    .append("<td>"+errorHandlingMetrics.getGenericExceptionsCount()+"</td>")
                    .append("<td>"+errorHandlingMetrics.getScore()+"</td>")
                    .append("</tr>");


        }
    }

    @Override
    public void saveResult() {

        // get total result
        calcluateResult();

        // console
        System.out.println("---- Result ----");
        System.out.println("Error Handling Score: " + result + "/100");

        // html
        this.htmlOutput.append("</tbody>")
                .append("</table>")
                .append("<b>Error Handling Score: </b>" + result + "/100")
                .append("</div>");

    }

    private void calcluateResult()
    {
        int score = 0;

        // average
        for (ErrorHandlingMetrics errorHandlingMetrics : errorHandlingMetricsList) {
            score += errorHandlingMetrics.getScore();
        }

        result = (int)(score/errorHandlingMetricsList.size());
    }

    private ErrorHandlingMetrics setMetricValues(CompilationUnit compilationUnit) {

        // create visitor and calculate
        ErrorHandlingVisitor errorHandlingVisitor = new ErrorHandlingVisitor();

        // start visiting
        compilationUnit.accept(errorHandlingVisitor, null);

        // get calculation from visitor and store
        ErrorHandlingMetrics errorHandlingMetrics = errorHandlingVisitor.getMetrics();

        // get the name for output
        errorHandlingMetrics.setClassName(compilationUnit.getPrimaryTypeName().get());

        return errorHandlingMetrics;
    }

    private void printToFile(String filePath) {
        String find = "<!------ @@Error Output@@  ---->";

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

}
