package com.eliseew.dima.diploma.utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaver {

    // Метод для записи текста в файл
    public static void saveReportToFile(String reportContent, String filePath) {
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Запись содержимого в файл
            writer.write(reportContent);
            writer.newLine(); // Добавляем новую строку для разделения отчетов
            System.out.println("Отчет успешно записан в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }
}
