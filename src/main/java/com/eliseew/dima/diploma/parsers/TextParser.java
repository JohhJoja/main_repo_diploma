package com.eliseew.dima.diploma.parsers;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
    public static String parse(String text, File file) {
        List<TextPatternModel> patterns = PatternLoader.loadAllPatterns("src/main/resources/com/eliseew/dima/diploma/patterns");

        for (TextPatternModel model : patterns) {
            Pattern triggerPattern = Pattern.compile(model.trigger, Pattern.DOTALL);
            Matcher triggerMatcher = triggerPattern.matcher(text);

            System.out.println("Проверяем триггер: " + model.trigger);
            System.out.println("Текст документа:\n" + text);

            if (triggerMatcher.find()) {
                Pattern regexPattern = Pattern.compile(model.regex, Pattern.DOTALL);
                Matcher m = regexPattern.matcher(text);

                if (m.find()) {
                    // Создаём мапу для хранения найденных значений
                    Map<String, String> extractedData = new LinkedHashMap<>();
                    for (int i = 1; i <= m.groupCount(); i++) {
                        String value = m.group(i);
                        System.out.println("Группа #" + i + ": " + value);
                        extractedData.put("group" + i, value);
                    }

                    switch (model.actionType) {
                        case "report":
                            String report = model.reportStructure;
                            for (Map.Entry<String, String> entry : extractedData.entrySet()) {
                                report = report.replace("{" + entry.getKey() + "}", entry.getValue());
                            }
                            return report;

                        case "replace":
                            String replacement = model.replacementValue != null ? model.replacementValue : "*****";
                            String modifiedText = text;

                            // Обрабатываем все совпадения, а не только первое
                            m.reset();
                            while (m.find()) {
                                for (int i = 1; i <= m.groupCount(); i++) {
                                    modifiedText = modifiedText.replace(m.group(i), replacement);
                                }
                            }

                            return modifiedText;


                        default:
                            return "Неизвестное действие: " + model.actionType;
                    }
                }
            }
        }

        return "Тип документа не определен";
    }

    private static String repeatStars(int length) {
        return "*".repeat(Math.max(0, length));
    }
}
