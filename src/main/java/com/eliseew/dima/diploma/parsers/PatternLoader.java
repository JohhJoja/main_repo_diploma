package com.eliseew.dima.diploma.parsers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PatternLoader {

    public static List<PatternModel> loadAllPatterns(String folderPath, String selectedTemplateName) {
        List<PatternModel> allPatterns = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        File folder = new File(folderPath);

        if (selectedTemplateName != null) {
            File selectedFile = new File(folder, selectedTemplateName + ".json");
            if (selectedFile.exists()) {
                System.out.println("📄 Читаем файл шаблона: " + selectedFile.getName());
                try {
                    List<PatternModel> patterns = mapper.readValue(
                            selectedFile,
                            new TypeReference<List<PatternModel>>() {}
                    );
                    allPatterns.addAll(patterns);
                    if (patterns !=null){
                        System.out.println("YES!!!!");
                    }
                } catch (IOException e) {
                    System.out.println("❌ Ошибка чтения файла " + selectedFile.getName());
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Файл шаблона не найден: " + selectedFile.getName());
            }
        } else {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null) return allPatterns;

            for (File file : files) {
                System.out.println("📄 Читаем файл шаблона: " + file.getName());
                try {
                    List<PatternModel> patterns = mapper.readValue(
                            file,
                            new TypeReference<List<PatternModel>>() {}
                    );
                    allPatterns.addAll(patterns);
                } catch (IOException e) {
                    System.out.println("❌ Ошибка чтения файла " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return allPatterns;
    }
}
