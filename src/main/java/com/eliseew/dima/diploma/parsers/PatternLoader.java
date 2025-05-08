package com.eliseew.dima.diploma.parsers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PatternLoader {

    public static List<TextPatternModel> loadAllPatterns(String folderPath) {
        List<TextPatternModel> allPatterns = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null) return allPatterns;

        for (File file : files) {
            System.out.println("Читаем файл: " + file.getName());
            List<TextPatternModel> patterns = null;
//            System.out.println("Загружено шаблонов: " + patterns.size());

            try {
                patterns = mapper.readValue(
                        file,
                        new TypeReference<List<TextPatternModel>>() {
                        }
                );
                allPatterns.addAll(patterns);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return allPatterns;
    }
}
