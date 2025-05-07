package com.eliseew.dima.diploma.parsers;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
    public static String parse(String text, File file) {
        // Ключ — шаблон для определения типа документа
        // Значение — шаблон для извлечения нужных данных
        Map<String, String> patternMap = new LinkedHashMap<>();

        String

        patternMap.put("Заявление1", "(?s).*?имя:\\s*(\\S+).*?фамилия:\\s*(\\S+).*");

        for (Map.Entry<String, String> entry : patternMap.entrySet()) {
            String keyRegex = entry.getKey();
            String valueRegex = entry.getValue();

            Pattern keyPattern = Pattern.compile(keyRegex);
            Matcher keyMatcher = keyPattern.matcher(text);

            if (keyMatcher.find()) {
                Pattern valuePattern = Pattern.compile(valueRegex);
                Matcher valueMatcher = valuePattern.matcher(text);

                if (valueMatcher.find()) {
                    String name = valueMatcher.group(1);
                    String surname = valueMatcher.group(2);

                    System.out.println("Тип документа: " + keyMatcher.group());
                    System.out.println("Имя: " + name);
                    System.out.println("Фамилия: " + surname);

                    return "Тип: " + keyMatcher.group() + "\nИмя: " + name + "\nФамилия: " + surname;
                } else {
                    System.out.println("Ключ найден, но данные не удалось извлечь.");
                    return "Ключ найден, но данные не найдены.";
                }
            }
        }

        System.out.println("Тип документа не определён.");
        return "Неизвестный тип документа";
    }
}
