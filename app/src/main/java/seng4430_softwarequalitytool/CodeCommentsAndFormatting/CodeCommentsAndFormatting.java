package seng4430_softwarequalitytool.CodeCommentsAndFormatting;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import seng4430_softwarequalitytool.Util.Module;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CodeCommentsAndFormatting implements Module {

    private int result;
    private List<CommentsAndFormattingEvaluationMetrics> commentsAndFormattingEvaluationMetricsList = new ArrayList<>();

    private StringBuilder htmlOutput = new StringBuilder();


    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        try {
            for (CompilationUnit compilationUnit : compilationUnits) {
                commentsAndFormattingEvaluationMetricsList.add(setMetricValues(compilationUnit));
            }


            printInformation();
            saveResult();
            printToFile(filePath);

            return "Comments and Formatting Successfully Calculated.";
        } catch(Exception e){
            return "Error Calculating Comments and Formatting.";
        }
    }

    @Override
    public void printModuleHeader() {
        System.out.println("\n");
        System.out.println("---- Code Commenting and Formatting ----");
        System.out.format(
                "%-35s %-20s %-20s %-20s %-20s %-20s %s\n",
                "File Name",
                "Overall Score",
                "Formatting Score",
                "Comment Score",
                "Lines Too Long",
                "Comments",
                "Block Comments"
        );

        this.htmlOutput.append("<div class=\"col-sm-12\">");
        this.htmlOutput.append("<table class=\"table\">")
                .append("<thead class=\"thead-light\">")
                .append("<tr>")
                .append("<th scope=\"col\">File Name</th>")
                .append("<th scope=\"col\">Formatting Score</th>")
                .append("<th scope=\"col\">Comment Score</th>")
                .append("<th scope=\"col\">Lines Too Long</th>")
                .append("<th scope=\"col\">Comments</th>")
                .append("<th scope=\"col\">Block Comments</th>")
                .append("<th scope=\"col\">Score</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");
    }

    @Override
    public void printInformation() {
        printModuleHeader();
        for (CommentsAndFormattingEvaluationMetrics commentsAndFormattingEvaluationMetrics : commentsAndFormattingEvaluationMetricsList) {
            System.out.format("%-35s %-20s %-20s %-20s %-20s %-20s %s\n",
                    commentsAndFormattingEvaluationMetrics.getClassName(),
                    commentsAndFormattingEvaluationMetrics.getScore(),
                    commentsAndFormattingEvaluationMetrics.getLinesTooLongScore(),
                    commentsAndFormattingEvaluationMetrics.getCommentScore(),
                    commentsAndFormattingEvaluationMetrics.getLinesTooLong(),
                    commentsAndFormattingEvaluationMetrics.getNumberCommentsInUnit(),
                    commentsAndFormattingEvaluationMetrics.getNumberBlockCommentsInUnit());

            this.htmlOutput.append("<tr>")
                    .append("<td>"+commentsAndFormattingEvaluationMetrics.getClassName()+"()</td>")
                    .append("<td>"+commentsAndFormattingEvaluationMetrics.getLinesTooLongScore()+"</td>")
                    .append("<td>"+commentsAndFormattingEvaluationMetrics.getCommentScore()+"</td>")
                    .append("<td>"+commentsAndFormattingEvaluationMetrics.getLinesTooLong()+"</td>")
                    .append("<td>"+commentsAndFormattingEvaluationMetrics.getNumberCommentsInUnit()+"</td>")
                    .append("<td>"+commentsAndFormattingEvaluationMetrics.getNumberCommentsInUnit()+"</td>")
                    .append("<td>"+commentsAndFormattingEvaluationMetrics.getScore()+"</td>")
                    .append("</tr>");
        }
    }

    @Override
    public void saveResult() {
        calcluateResult();
        System.out.println("---- Result ----");
        System.out.println("Code Comments and Formatting Score: " + result + "/100");

        this.htmlOutput.append("</tbody>")
                .append("</table>")
                .append("<b>Code Comments and Formatting Score: </b>" + result + "/100")
                .append("</div>");
    }

    private void calcluateResult()
    {
        int score = 0;

        for (CommentsAndFormattingEvaluationMetrics commentsAndFormattingEvaluationMetrics : commentsAndFormattingEvaluationMetricsList) {
            score += commentsAndFormattingEvaluationMetrics.getScore();
        }

        result = (int)(score/commentsAndFormattingEvaluationMetricsList.size());
    }

    private CommentsAndFormattingEvaluationMetrics setMetricValues(CompilationUnit compilationUnit) {

        // COMMENTS ///////////////////////////////////////////////

        // this is for ammount of comments
        List<Comment> comments = compilationUnit.getAllComments();

        // used to calculate percentage of methods that have a block comment
        int blockCommentCount = 0;
        for (Comment comment : comments) {
            if (comment.isBlockComment())
            {
                blockCommentCount++;
            }
        }

        CommentsAndFormattingEvaluationMetrics commentsAndFormattingEvaluationMetrics = new CommentsAndFormattingEvaluationMetrics();

        // name for output
        commentsAndFormattingEvaluationMetrics.setClassName(compilationUnit.getPrimaryTypeName().get());

        // gets all the comments then the size of the list returned
        commentsAndFormattingEvaluationMetrics.setNumberCommentsInUnit(comments.size());

        // gets the unit into a string then counts the lines
        commentsAndFormattingEvaluationMetrics.setNumberLinesInUnit(compilationUnit.toString().lines().count());

        commentsAndFormattingEvaluationMetrics.setNumberCommentsInUnit(comments.size());

        commentsAndFormattingEvaluationMetrics.setNumberBlockCommentsInUnit(blockCommentCount);

        // finds all methods in unit
        commentsAndFormattingEvaluationMetrics.setNumberMethodsInUnit(compilationUnit.findAll(MethodDeclaration.class).size());

        // FORMATTING (Line Length) /////////////////////////////

        // This will store largest line length, average line length

        String[] lines = compilationUnit.toString().split("\n");

        commentsAndFormattingEvaluationMetrics.setLinesTooLong(countLinesTooLong(lines));

        return commentsAndFormattingEvaluationMetrics;
    }

    private long countLinesTooLong(String[] lines) {
        long linesTooLong = 0;

        for (String line : lines) {
            if (line.length() > 120)
            {
                linesTooLong++;
            }
        }
        return linesTooLong;
    }

    private void printToFile(String filePath) {
        String find = "<!------ @@Code Comments & Formatting Output@@  ---->";

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
