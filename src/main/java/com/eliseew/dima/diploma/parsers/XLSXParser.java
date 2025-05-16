package com.eliseew.dima.diploma.parsers;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class XLSXParser {
    public static String parse(String text, File file, String selectedTemplateName) {
        Objects.requireNonNull(text, "Текст не может быть null");

        List<List<String>> table = parseExcelTextToTable(text);
        List<ExcelPatternModel> patterns = PatternLoader.loadExcelPatterns(
                "templates",
                selectedTemplateName
        );

        if (patterns.isEmpty()) {
            return "Не найдено подходящих шаблонов для Excel";
        }

        StringBuilder result = new StringBuilder();

        for (ExcelPatternModel pattern : patterns) {
            List<String> values = extractValues(table, pattern.coordinates());
            result.append(generateReport(pattern, values)).append("\n\n");
        }

        return result.toString().trim();
    }

    private static List<String> extractValues(List<List<String>> table,
                                              List<ExcelPatternModel.CellCoordinate> coords) {
        List<String> values = new ArrayList<>();

        for (ExcelPatternModel.CellCoordinate coord : coords) {
            int row = coord.row() - 1;
            int col = coord.col() - 1;

            String value = (row < table.size() && col < table.get(row).size())
                    ? table.get(row).get(col)
                    : "N/A";

            values.add(value);
        }
        return values;
    }

    private static String generateReport(ExcelPatternModel pattern, List<String> values) {
        String report = pattern.reportStructure();

        for (int i = 0; i < values.size(); i++) {
            report = report.replace("{" + (i + 1) + "}", values.get(i));
        }

        return pattern.description() + ":\n" + report;
    }

    public static List<List<String>> parseExcelTextToTable(String text) {
        List<List<String>> table = new ArrayList<>();
        String[] rows = text.split("\n");

        for (String row : rows) {
            table.add(List.of(row.split("\t")));
        }

        return table;
    }
}