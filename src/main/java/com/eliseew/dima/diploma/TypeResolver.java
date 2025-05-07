package com.eliseew.dima.diploma;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class TypeResolver {
    public static List<String> resolve(File file) throws IOException {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".txt") || fileName.endsWith(".csv") || fileName.endsWith(".xml")) {
            String content;
            try {
                content = Files.readString(file.toPath());
            } catch (MalformedInputException e) {
              //  System.out.println("Не удалось прочитать файл. Пробую показать байты:");
                byte[] raw = Files.readAllBytes(file.toPath());
                for (int i = 0; i < Math.min(raw.length, 100); i++) {
                    System.out.print((raw[i] & 0xFF) + " ");
                }
                throw e;
            }
            return List.of("text", content);
        }

        if (fileName.endsWith(".docx")) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument doc = new XWPFDocument(fis);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                return List.of("doc", extractor.getText());
            }
        }

        if (fileName.endsWith(".doc")) {
            try (FileInputStream fis = new FileInputStream(file);
                 HWPFDocument doc = new HWPFDocument(fis);
                 WordExtractor extractor = new WordExtractor(doc)) {
                return List.of("doc", extractor.getText());
            }
        }

        if (fileName.endsWith(".xlsx")) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                return List.of("excel", extractExcelText(workbook));
            }
        }

        if (fileName.endsWith(".xls")) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new HSSFWorkbook(fis)) {
                return List.of("excel", extractExcelText(workbook));
            }
        }

        if (fileName.endsWith(".gz")) {
            try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                 InputStreamReader reader = new InputStreamReader(gzip);
                 BufferedReader buffered = new BufferedReader(reader)) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = buffered.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                return List.of("gzip", builder.toString());
            }
        }
        return null;
    }

    private static String extractExcelText(Workbook workbook) {
        StringBuilder sb = new StringBuilder();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    sb.append(cell.toString()).append("\t");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
