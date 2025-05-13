package com.eliseew.dima.diploma.utils;

import java.util.List;

public class TemplateDataProcessor {
    public static void process(String name, String type, String description, List<String> docIds, String action, String reportText, List<String> keywords) {
        System.out.println("=== Получены данные шаблона ===");
        System.out.println("Название: " + name);
        System.out.println("Тип: " + type);
        System.out.println("Описание: " + description);
        System.out.println("Идентификаторы документа:");
        for (String id : docIds) {
            System.out.println("  - " + id);
        }
        System.out.println("Действие: " + action);
        System.out.println("Текст/Замена: " + reportText);
        System.out.println("Ключевые слова:");
        for (int i = 0; i < keywords.size(); i++) {
            System.out.println("  k" + (i + 1) + ": " + keywords.get(i));
        }
        System.out.println("================================");
    }
}
