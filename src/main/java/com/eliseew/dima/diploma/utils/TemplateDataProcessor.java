package com.eliseew.dima.diploma.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateDataProcessor {
    String name, type, description, action, reportText;
    List<String> docIds;
    List<KeywordEntry> keywords;

    public TemplateDataProcessor(String name, String type, String description, String action, String reportText, List<String> docIds, List<KeywordEntry> keywords) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.action = action;
        this.reportText = reportText;
        this.docIds = docIds;
        this.keywords = keywords;
        process();
    }

    public void process() {
        String regex = generateRegex();
        System.out.println("Сгенерированная регулярка: " + regex);
        exportToJsonFile();
    }

    private String generateWordPattern(int wordCount) {
        return wordCount <= 1 ? "\\S+" : "(?:\\S+\\s+){" + (wordCount - 1) + "}\\S+";
    }

    public void exportToJsonFile() {
        String regex = generateRegex();
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

        ObjectMapper mapper = new ObjectMapper();
        try {
            File dir = new File("templates");
            if (!dir.exists()) dir.mkdir();
            File outFile = new File(dir, name + ".json");

            List<TemplateJson> jsonList = new ArrayList<>();
            jsonList.add(json);

            mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, jsonList);
            System.out.println("Шаблон сохранён в: " + outFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении JSON: " + e.getMessage());
        }
    }

    public String generateRegex() {
        StringBuilder regexBuilder = new StringBuilder("(?i)"); // флаг игнорирования регистра
        int groupIndex = 1;

        for (int i = 0; i < keywords.size(); i++) {
            KeywordEntry keyword = keywords.get(i);
            String key1 = keyword.getKey1();
            String key2 = keyword.getKey2();
            int wordCount = keyword.getWordCount();
            String wordPattern = generateWordPattern(wordCount);

            if (i > 0) {
                // Добавим возможность наличия чего угодно между шаблонами (в разумных пределах)
                regexBuilder.append(".*?");
            }

            switch (keyword.getPosition()) {
                case "after":
                    regexBuilder
                            .append("(?:\\Q").append(key1).append("\\E)[:\\s\\r\\n]*")
                            .append("(?<k").append(groupIndex++).append(">") // именованная группа
                            .append(wordPattern).append(")");
                    break;

                case "before":
                    regexBuilder
                            .append("(?<k").append(groupIndex++).append(">") // именованная группа
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
