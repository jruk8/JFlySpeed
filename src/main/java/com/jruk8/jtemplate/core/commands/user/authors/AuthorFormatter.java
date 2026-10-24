package com.jruk8.jtemplate.core.commands.user.authors;

import java.util.List;

/**
 * Business logic class for formatting author lists.
 * Follows clean architecture principles - pure formatting logic with no dependencies on framework types.
 */
public class AuthorFormatter {

    /**
     * Formats a single author name.
     *
     * @param author the author name
     * @return the formatted author name (unchanged)
     */
    public String formatSingleAuthor(String author) {
        return author;
    }

    /**
     * Formats two authors with "and" in gray between them.
     *
     * @param firstAuthor  the first author
     * @param secondAuthor the second author
     * @return formatted string like "MyAuthor <gray>and</gray> MyOther"
     */
    public String formatTwoAuthors(String firstAuthor, String secondAuthor) {
        return firstAuthor + " <gray>and</gray> " + secondAuthor;
    }

    /**
     * Formats three or more authors with commas and "and" in gray.
     * Format: "MyAuthor<gray>,</gray> MyOther<gray>, and</gray> MyOtherOther"
     *
     * @param authors list of author names (must have at least 3 elements)
     * @return formatted string with gray commas and "and"
     */
    public String formatThreePlusAuthors(List<String> authors) {
        if (authors.size() < 3) {
            return formatTwoAuthors(authors.get(0), authors.get(1));
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < authors.size(); i++) {
            String author = authors.get(i);

            if (i == 0) {
                result.append(author);
            } else if (i == authors.size() - 1) {
                // Last author with "and" in gray
                result.append("<gray>, and</gray> ").append(author);
            } else {
                // Middle authors with comma in gray
                result.append("<gray>,</gray> ").append(author);
            }
        }

        return result.toString();
    }

    /**
     * Formats authors list based on the number of authors.
     *
     * @param authors list of author names
     * @return properly formatted author string
     */
    public String formatAuthors(List<String> authors) {
        if (authors.isEmpty()) {
            return "";
        }

        int count = authors.size();
        if (count == 1) {
            return authors.getFirst();
        }
        else if (count == 2) {
            return formatTwoAuthors(authors.get(0), authors.get(1));
        }
        return formatThreePlusAuthors(authors);
    }
}