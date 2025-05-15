package com.eliseew.dima.diploma.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextParser {
    //String selectedTemplateName = null;
    public static String parse(String text, File file, String selectedTemplateName) throws IOException {
//        this.selectedTemplateName = selectedTemplateName;
        System.out.println(selectedTemplateName);
       // System.out.println("Текст в виде кодов: " + text.codePoints().mapToObj(c -> (int)c).collect(Collectors.toList()));
//        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
//        System.out.println(content);


        List<PatternModel> patterns = PatternLoader.loadAllPatterns("templates", selectedTemplateName);

        for (PatternModel model : patterns) {
            Pattern triggerPattern = Pattern.compile(model.trigger, Pattern.CASE_INSENSITIVE);
            Matcher triggerMatcher = triggerPattern.matcher(text);
            System.out.println(model.trigger);
            System.out.println("Используемый regex: " + model.regex);



            if (triggerMatcher.find()) {
                System.out.println("🔎 Найден триггер: " + model.trigger); // ← вывод 1
                Pattern regexPattern = Pattern.compile(model.regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                Matcher m = regexPattern.matcher(text);

                System.out.println(m + " эмочка");
                if (m.find()) {
                    System.out.println("Yes");
                    Map<String, String> namedGroups = getNamedGroups(m);

                    System.out.println("📌 Найденные группы и значения:"); // ← вывод 2
                    for (Map.Entry<String, String> entry : namedGroups.entrySet()) {
                        System.out.println("    " + entry.getKey() + " = " + entry.getValue());
                    }

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
