package com.eliseew.dima.diploma.parsers;

import com.eliseew.dima.diploma.utils.text.DocPatternModel;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {

    public static String parse(String text, File file, String selectedTemplateName) throws IOException {
        Objects.requireNonNull(text, "Текст не может быть null");
        System.out.println("Выбран шаблон: " + selectedTemplateName);

        // Загружаем только DOC шаблоны из папки templates/doc
        List<DocPatternModel> patterns = PatternLoader.loadDocPatterns("templates", selectedTemplateName);

        if (patterns.isEmpty()) {
            return "Не найдено подходящих шаблонов для документа";
        }

        for (DocPatternModel model : patterns) {
            System.out.println("Обрабатываем шаблон: " + model.description);
            System.out.println("Триггер: " + model.trigger);
            System.out.println("Regex: " + model.regex);

            Pattern triggerPattern = Pattern.compile(model.trigger, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher triggerMatcher = triggerPattern.matcher(text);

            if (triggerMatcher.find()) {
                System.out.println("🔎 Найден триггер: " + model.trigger);
                Pattern regexPattern = Pattern.compile(model.regex,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
                Matcher m = regexPattern.matcher(text);

                if (m.find()) {
                    Map<String, String> namedGroups = getNamedGroups(m);
                    logFoundGroups(namedGroups);

                    switch (model.actionType) {
                        case "report":
                            return generateReport(model.reportStructure, namedGroups);

                        case "replace":
                            return replaceText(text, model.replacementValue, namedGroups, m);

                        default:
                            return "Неизвестный тип действия: " + model.actionType;
                    }
                }
            }
        }
        return "Тип документа не определен";
    }

    private static String generateReport(String template, Map<String, String> namedGroups) {
        String report = template;
        for (Map.Entry<String, String> entry : namedGroups.entrySet()) {
            report = report.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return report;
    }

    private static String replaceText(String originalText, String replacement,
                                      Map<String, String> namedGroups, Matcher matcher) {
        if (replacement == null) {
            replacement = "*****";
        }

        String modifiedText = originalText;
        matcher.reset();

        while (matcher.find()) {
            for (String key : namedGroups.keySet()) {
                String value = namedGroups.get(key);
                if (value != null && !value.isEmpty()) {
                    modifiedText = modifiedText.replace(value, replacement);
                }
            }
        }
        return modifiedText;
    }

    private static void logFoundGroups(Map<String, String> namedGroups) {
        System.out.println("📌 Найденные группы и значения:");
        namedGroups.forEach((key, value) ->
                System.out.println("    " + key + " = " + value));
    }

    private static Map<String, String> getNamedGroups(Matcher matcher) {
        Map<String, String> namedGroups = new LinkedHashMap<>();
        Pattern namedGroupPattern = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");
        Matcher m = namedGroupPattern.matcher(matcher.pattern().toString());

        while (m.find()) {
            String groupName = m.group(1);
            try {
                String groupValue = matcher.group(groupName);
                if (groupValue != null) {
                    namedGroups.put(groupName, groupValue);
                }
            } catch (IllegalArgumentException ignored) {
                // Группа не найдена - пропускаем
            }
        }
        return namedGroups;
    }
}