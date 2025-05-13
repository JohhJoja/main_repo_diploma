package com.eliseew.dima.diploma.utils;

import java.util.List;
import java.util.regex.Pattern;

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
        StringBuilder regexBuilder = new StringBuilder("(?s).*");

        for (int i = 0; i < keywords.size(); i++) {
            KeywordEntry keyword = keywords.get(i);
            String key1 = keyword.getKey1();
            String key2 = keyword.getKey2();
            int wordCount = keyword.getWordCount();

            switch (keyword.getPosition()) {
                case "before": {
                    // Точное количество слов перед key1
                    regexBuilder.append("(\\S+\\s+){")
                            .append(wordCount)
                            .append("}")
                            .append(Pattern.quote(key1));
                    break;
                }
                case "after": {
                    // Точное количество слов после key1
                    regexBuilder.append(Pattern.quote(key1))
                            .append("\\s+")
                            .append("(\\S+\\s+){")
                            .append(wordCount)
                            .append("}");
                    break;
                }
                case "between": {
                    // Все слова между key1 и key2, без жадности
                    regexBuilder.append(Pattern.quote(key1))
                            .append("((\\S+\\s+)*?)")  // Ненасытный захват между key1 и key2
                            .append(Pattern.quote(key2));
                    break;
                }
            }
        }
        regexBuilder.append(".*");
        System.out.println("Сгенерированная регулярка: " + regexBuilder.toString());
    }

    private String generateWordPattern(int wordCount) {
        if (wordCount <= 1) return "\\S+";
        return "(\\S+\\s+){" + (wordCount - 1) + "}\\S+";
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
