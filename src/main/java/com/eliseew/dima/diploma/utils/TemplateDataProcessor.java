package com.eliseew.dima.diploma.utils;

import com.eliseew.dima.diploma.utils.excel.ExcelPatternModel;
import com.eliseew.dima.diploma.utils.text.KeywordEntry;
import com.eliseew.dima.diploma.utils.text.TemplateJson;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TemplateDataProcessor {
    String name, type, description, action, reportText;
    List<String> docIds;
    List<KeywordEntry> keywords;
    Boolean isLocal;
    private List<ExcelPatternModel.CellCoordinate> coordinates;

    public TemplateDataProcessor(String name, String type, String description, String action, String reportText, List<String> docIds, List<KeywordEntry> keywords, Boolean isLocal) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.action = action;
        this.reportText = reportText;
        this.docIds = docIds;
        this.keywords = keywords;
        this.isLocal = isLocal;
        process();
    }

    public TemplateDataProcessor(String name, String type, String description, List<ExcelPatternModel.CellCoordinate> coordinates, Boolean isLocal) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.coordinates = coordinates;
        this.isLocal = isLocal;
        process();
    }

    public void process() {
        switch (type.toLowerCase()) {
            case "text":
                String regex = generateRegex();
                System.out.println("Сгенерированная регулярка: " + regex);
                exportTextTemplate(regex);
                break;
            case "excel":
                exportExcelTemplate();
                break;
            default:
                System.err.println("Неизвестный тип шаблона: " + type);
        }
    }

    private String generateWordPattern(int wordCount) {
        return wordCount <= 1 ? "\\S+" : "(?:\\S+\\s+){" + (wordCount - 1) + "}\\S+";
    }

    private void exportTextTemplate(String regex) {
        String triggerString = "(" + String.join("|", docIds) + ")";

        if ("отчет".equals(action)) {
            for (int i = 1; i <= keywords.size(); i++) {
                reportText = reportText.replaceAll("(?<!\\{)k" + i + "(?!\\})", "\\{k" + i + "\\}");
            }
        }

        TemplateJson json = new TemplateJson(
                description,
                triggerString,
                regex,
                action,
                "замена".equals(action) ? reportText : null,
                "отчет".equals(action) ? reportText : null
        );

        saveJson(Collections.singletonList(json), "doc");
    }

    private void exportExcelTemplate() {
        System.out.println("Сработал exportExcelTemplate в TDP");
        ExcelPatternModel excelJson = new ExcelPatternModel(description, coordinates, null);
        saveJson(Collections.singletonList(excelJson), "excel");
    }

    private <T> void saveJson(List<T> jsonList, String subtype) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File localDir = new File("templates", subtype);
            File externalDir = new File("E:\\Java\\deeplomka\\intronet\\patterns", subtype);

            if (!localDir.exists()) localDir.mkdirs();
            if (!isLocal && !externalDir.exists()) externalDir.mkdirs();

            File outFile1 = new File(localDir, name + ".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(outFile1, jsonList);
            System.out.println("Шаблон сохранён в: " + outFile1.getAbsolutePath());

            if (!isLocal) {
                File outFile2 = new File(externalDir, name + ".json");
                mapper.writerWithDefaultPrettyPrinter().writeValue(outFile2, jsonList);
                System.out.println("Шаблон также сохранён в: " + outFile2.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении JSON: " + e.getMessage());
        }
    }

    public String generateRegex() {
        StringBuilder regexBuilder = new StringBuilder("(?i)");
        int groupIndex = 1;

        for (int i = 0; i < keywords.size(); i++) {
            KeywordEntry keyword = keywords.get(i);
            String key1 = keyword.getKey1();
            String key2 = keyword.getKey2();
            int wordCount = keyword.getWordCount();
            String wordPattern = generateWordPattern(wordCount);

            if (i > 0) regexBuilder.append(".*?");

            switch (keyword.getPosition()) {
                case "after":
                    regexBuilder
                            .append("(?:\\Q").append(key1).append("\\E)[:\\s\\r\\n]*")
                            .append("(?<k").append(groupIndex++).append(">")
                            .append(wordPattern).append(")");
                    break;
                case "before":
                    regexBuilder
                            .append("(?<k").append(groupIndex++).append(">")
                            .append(wordPattern).append(")")
                            .append("[:\\s\\r\\n]*")
                            .append("(?:\\Q").append(key1).append("\\E)");
                    break;
                case "between":
                    regexBuilder
                            .append("(?:\\Q").append(key1).append("\\E)[:\\s\\r\\n]*")
                            .append("(?<k").append(groupIndex++).append(">[\\p{L}\\p{N}\\s]+?)")
                            .append("[:\\s\\r\\n]*")
                            .append("(?:\\Q").append(key2).append("\\E)");
                    break;
            }
        }

        return regexBuilder.toString();
    }

    @Override
    public String toString() {
        return "TemplateDataProcessor{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", action='" + action + '\'' +
                ", reportText='" + reportText + '\'' +
                ", docIds=" + docIds +
                ", keywords=" + keywords +
                '}';
    }
}
