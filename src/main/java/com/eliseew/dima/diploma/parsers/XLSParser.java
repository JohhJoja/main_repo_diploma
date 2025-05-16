package com.eliseew.dima.diploma.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XLSParser {
    public static String parse(String text, File file, String selectedTemplateName) {
        // 1. Разбиваем текст на строки и ячейки
        List<List<String>> table = parseExcelTextToTable(text);

        // 2. Заглушка: координаты для поиска (строка, столбец)
        List<int[]> coordinates = new ArrayList<>();
        coordinates.add(new int[]{2, 2});  // "Ананас" (2-я строка, 2-я колонка)
        coordinates.add(new int[]{4, 3});  // "глист" (4-я строка, 3-я колонка)

        // 3. Извлекаем данные по координатам
        StringBuilder result = new StringBuilder();
        for (int[] coord : coordinates) {
            int row = coord[0] - 1;  // Переводим в 0-based индекс
            int col = coord[1] - 1;

            if (row < table.size() && col < table.get(row).size()) {
                String value = table.get(row).get(col);
                result.append("Значение в [")
                        .append(coord[0]).append("-").append(coord[1])
                        .append("]: ").append(value).append("\n");
            } else {
                result.append("Ошибка: ячейка [")
                        .append(coord[0]).append("-").append(coord[1])
                        .append("] не существует!\n");
            }
        }

        return result.toString();
    }

    static List<List<String>> parseExcelTextToTable(String text) {
        List<List<String>> table = new ArrayList<>();
        String[] rows = text.split("\n");  // Разделяем строки

        for (String row : rows) {
            String[] cells = row.split("\t");  // Разделяем ячейки
            table.add(List.of(cells));
        }

        return table;
    }
}