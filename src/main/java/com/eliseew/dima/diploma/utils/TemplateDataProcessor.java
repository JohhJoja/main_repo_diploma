package com.eliseew.dima.diploma.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;

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
        if (wordCount <= 1) return "\\S+";
        return "(\\S+\\s+){" + (wordCount - 1) + "}\\S+";
    }

    public void exportToJsonFile() {
        // Генерация регулярного выражения
        String regex = generateRegex();

        // Преобразуем trigger (список строк) в строку в формате "(k1|k2|k3)"
        String triggerString = "(" + String.join("|", docIds) + ")";  // Используем | для соединения элементов

        // Создаем объект JSON для записи
        TemplateJson json = new TemplateJson(

                description,
                triggerString,  // Здесь заменяем список на строку
                regex,
                action,
                "замена".equals(action) ? reportText : null,
                "отчет".equals(action) ? reportText : null
        );

        // Пишем в JSON файл
        ObjectMapper mapper = new ObjectMapper();
        try {
            File dir = new File("templates");
            if (!dir.exists()) dir.mkdir();
            File outFile = new File(dir, name + ".json");

            // Оборачиваем в список
            List<TemplateJson> jsonList = new ArrayList<>();
            jsonList.add(json);

            // Запись в файл
            mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, jsonList);
            System.out.println("Шаблон сохранён в: " + outFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении JSON: " + e.getMessage());
        }
    }


    public String generateRegex() {
        StringBuilder regexBuilder = new StringBuilder("(?si).*");

        for (KeywordEntry keyword : keywords) {
            String key1 = keyword.getKey1();
            String key2 = keyword.getKey2();
            int wordCount = keyword.getWordCount();

            switch (keyword.getPosition()) {
                case "before":
                    regexBuilder.append("((?:[\\p{L}\\p{N}]+\\s+){")
                            .append(wordCount)
                            .append("})")
                            .append("\\W*")
                            .append(Pattern.quote(key1));
                    break;
                case "after":
                    regexBuilder.append(Pattern.quote(key1))
                            .append("\\W*")
                            .append("((?:[\\p{L}\\p{N}]+\\s+){")
                            .append(wordCount)
                            .append("})");
                    break;
                case "between":
                    regexBuilder.append(Pattern.quote(key1))
                            .append("\\W*")
                            .append("([\\p{L}\\p{N}\\s]+?)")
                            .append("\\W*")
                            .append(Pattern.quote(key2));
                    break;
            }
        }

        regexBuilder.append(".*");
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
