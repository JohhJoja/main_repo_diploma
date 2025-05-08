package com.eliseew.dima.diploma.parsers;

import com.eliseew.dima.diploma.parsers.PatternLoader;

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
            System.out.println("Проверяем шаблон: " + model.trigger);
            System.out.println("Trigger подходит? " + Pattern.compile(model.trigger, Pattern.DOTALL).matcher(text).find());

            if (Pattern.compile(model.trigger, Pattern.DOTALL).matcher(text).find()) {
                Pattern p = Pattern.compile(model.regex, Pattern.DOTALL);
                Matcher m = p.matcher(text);
                if (m.find()) {
                    System.out.println("Matcher matches? Уес" );

                    switch (model.actionType) {
                        case "report":
                            return model.reportStructure
                                    .replace("{name}", m.group(1))
                                    .replace("{surname}", m.group(2));
                        default:
                            return "Неизвестное действие: " + model.actionType;
                    }
                }
            }
        }

        return "Тип документа не определен";
    }
}
