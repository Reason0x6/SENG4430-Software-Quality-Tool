package seng4430_softwarequalitytool.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTMLTableBuilder {
    private String name;
    private List<String> columns;
    private List<List<String>> rows;

    public HTMLTableBuilder(String name, String... columns) {
        if (columns.length <= 0) {
            throw new IllegalArgumentException("Cannot create a table with 0 columns.");
        }
        this.name = name;
        this.columns = Arrays.asList(columns);
        this.rows = new ArrayList<>();
    }

    /**
     * Adds a row of items into the table. The number of items passed into this
     * method must match the number of columns in this table.
     * 
     * @param items row to be added to the table.
     * @throws IllegalArgumentException if number of items does not match the number
     *                                  of columns.
     */
    public void addRow(String... items) {
        if (getColumnNum() != items.length) {
            throw new IllegalArgumentException(
                    "A row must have " + getColumnNum() +
                            " items. Number of items passed: " + items.length);
        }

        List<String> sanitisedItems = new ArrayList<>();

        // sanitise items (make them compatible for html)
        for (String item : items) {
            String sanitisedItem = item.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            sanitisedItems.add(sanitisedItem);
        }

        rows.add(sanitisedItems);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Add table name
        if (!name.isEmpty()) {
            builder.append(String.format("<p><b>%s</b></p>", name));
        }

        // Add table header
        builder.append("<table class=\"table\">");
        builder.append("<thead class=\"thead-light\">");
        builder.append("<tr>");
        for (String col : columns) {
            builder.append(String.format("<th scope=\"col\">%s</th>", col));
        }
        builder.append("</tr>");
        builder.append("</thead>");

        // Add contents
        builder.append("<tbody>");
        for (List<String> row : rows) {
            builder.append("<tr>");
            for (String item : row) {
                builder.append(String.format("<td>%s</td>", item));
            }
            builder.append("</tr>");
        }
        builder.append("</tbody>");

        // Add table footer
        builder.append("</table>");

        return builder.toString();
    }

    /* Getters */

    public String getName() {
        return name;
    }

    /**
     * Get number of columns for this table.
     * 
     * @return number of columns.
     */
    public int getColumnNum() {
        return columns.size();
    }

    /**
     * Returns {@code true} if table is empty. Returns {@code false} otherwise.
     * 
     * @return {@code true} if table is empty. {@code false} otherwise.
     */
    public boolean isEmpty() {
        return rows.size() == 0;
    }
}
