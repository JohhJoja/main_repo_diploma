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

            if (triggerMatcher.find()) {
                Pattern regexPattern = Pattern.compile(model.regex, Pattern.DOTALL);
                Matcher m = regexPattern.matcher(text);

                if (m.find()) {
                    Map<String, String> namedGroups = getNamedGroups(m);

                    switch (model.actionType) {
                        case "report":
                            String report = model.reportStructure;
                            for (Map.Entry<String, String> entry : namedGroups.entrySet()) {
                                report = report.replace("{" + entry.getKey() + "}", entry.getValue());
                            }
                            return report;

                        case "replace":
                            String replacement = model.replacementValue != null ? model.replacementValue : "*****";
                            String modifiedText = text;

                            m.reset();
                            while (m.find()) {
                                for (String key : namedGroups.keySet()) {
                                    String value = namedGroups.get(key);
                                    if (value != null && !value.isEmpty()) {
                                        modifiedText = modifiedText.replace(value, replacement);
                                    }
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

    // Вытаскивает именованные группы
    private static Map<String, String> getNamedGroups(Matcher matcher) {
        Map<String, String> namedGroups = new LinkedHashMap<>();
        Pattern namedGroupPattern = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");
        Matcher m = namedGroupPattern.matcher(matcher.pattern().toString());

        while (m.find()) {
            String groupName = m.group(1);
            try {
                namedGroups.put(groupName, matcher.group(groupName));
            } catch (IllegalArgumentException ignored) {
                // Игнорируем если не найдена такая группа
            }
        }

        return namedGroups;
    }
}
