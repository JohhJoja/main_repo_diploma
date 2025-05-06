package com.eliseew.dima.diploma;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Processor {

    public static void process(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".txt") || fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".csv")) {
            processTextFile(file);
        } else if (fileName.endsWith(".xml")) {
            processXmlFile(file);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            processExcelFile(file);
        } else if (fileName.endsWith(".gz")) {
            processGzArchive(file);
        } else {
            System.out.println("Неизвестный формат. Попытка обработки как текстового файла: " + fileName);
            processTextFile(file);
        }
    }

    private static void processTextFile(File file) {
        System.out.println("Обработка текстового файла: " + file.getName());
        detectTemplateFromTextFile(file);
        // TODO: сюда будет добавлен парсинг текста
    }

    private static void processXmlFile(File file) {
        System.out.println("Обработка XML-файла: " + file.getName());
        // TODO: сюда будет добавлен парсинг XML
    }

    private static void processExcelFile(File file) {
        System.out.println("Обработка Excel-файла: " + file.getName());
        // TODO: сюда будет добавлен парсинг Excel
    }

    private static void processGzArchive(File file) {
        System.out.println("Обработка GZ-архива: " + file.getName());
        // TODO: сюда будет добавлен разархиватор и дальнейшая обработка
    }

    public String detectFileType(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".txt") || fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".csv")) {
            return "Текстовый файл";
        } else if (fileName.endsWith(".xml")) {
            return "XML";
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return "Excel";
        } else if (fileName.endsWith(".gz")) {
            return "GZip архив";
        } else {
            return "Неизвестный (обрабатывается как текст)";
        }
    }
    private static void detectTemplateFromTextFile(File file) {
        try {
            System.out.printf("AAAAAAAA");
            String content = Files.readString(file.toPath()).toLowerCase();

            System.out.printf(content);

            if (content.contains("документ1")) System.out.printf("док1");
            else if (content.contains("документ2")) System.out.printf("док2");
            else if (content.contains("теаоаост")) System.out.printf("тоаоаоа");
            else if (content.contains("1120412321")) System.out.printf("11235");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
