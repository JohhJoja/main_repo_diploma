package com.eliseew.dima.diploma.parsers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatternLoader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<DocPatternModel> loadDocPatterns(String basePath, String templateName) {
        return loadPatterns(basePath + "/doc", templateName, new TypeReference<>() {});
    }

    public static List<ExcelPatternModel> loadExcelPatterns(String basePath, String templateName) {
        return loadPatterns(basePath + "/excel", templateName, new TypeReference<>() {});
    }

    private static <T> List<T> loadPatterns(String fullPath, String templateName, TypeReference<List<T>> typeRef) {
        try {
            File folder = new File(fullPath);
            if (!folder.exists()) {
                System.err.println("Папка не существует: " + folder.getAbsolutePath());
                return new ArrayList<>();
            }

            if (templateName != null) {
                File file = new File(folder, templateName + ".json");
                if (file.exists()) {
                    return mapper.readValue(file, typeRef);
                }
                System.err.println("Файл шаблона не найден: " + file.getAbsolutePath());
                return new ArrayList<>();
            }

            List<T> allPatterns = new ArrayList<>();
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        allPatterns.addAll(mapper.readValue(file, typeRef));
                    } catch (IOException e) {
                        System.err.println("Ошибка чтения файла " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
            return allPatterns;
        } catch (Exception e) {
            System.err.println("Критическая ошибка при загрузке шаблонов: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}