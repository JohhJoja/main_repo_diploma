package com.eliseew.dima.diploma.parsers;

import com.eliseew.dima.diploma.parsers.PatternLoader;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
    public static String parse(String text, File file) {
        Map<String, String> patternMap = PatternLoader.loadPatterns(new File("src/main/resources/com/eliseew/dima/diploma/patterns/pattern1.json"));

        for (Map.Entry<String, String> entry : patternMap.entrySet()) {
            String triggerRegex = entry.getKey();
            String extractionRegex = entry.getValue();

            if (Pattern.compile(triggerRegex, Pattern.DOTALL).matcher(text).find()) {
                System.out.println("key yes");
                Pattern p = Pattern.compile(extractionRegex, Pattern.DOTALL);
                Matcher m = p.matcher(text);
                if (m.find()) {
                    System.out.println("Имя: " + m.group(1) + ", Фамилия: " + m.group(2));
                    return "Имя: " + m.group(1) + ", Фамилия: " + m.group(2);
                }
            }
        }

        return "Тип документа не определен";
    }
}
