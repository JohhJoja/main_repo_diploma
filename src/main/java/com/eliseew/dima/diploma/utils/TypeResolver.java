package com.eliseew.dima.diploma.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.HWPFDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class TypeResolver {
    private static final List<String> TEXT_EXT = List.of(".txt", ".csv", ".xml");
    private static final List<String> DOC_EXT = List.of(".docx", ".doc");
    private static final List<String> EXCEL_EXT = List.of(".xlsx", ".xls");

    public static List<String> resolve(File file) throws IOException {
        String fileName = file.getName().toLowerCase();

        if (TEXT_EXT.stream().anyMatch(fileName::endsWith)) {
            return List.of("doc", readTextFile(file));
        }

        if (DOC_EXT.stream().anyMatch(fileName::endsWith)) {
            return List.of("doc", extractDocText(file));
        }

        if (EXCEL_EXT.stream().anyMatch(fileName::endsWith)) {
            return List.of(fileName.endsWith(".xlsx") ? "xlsx" : "xls",
                    extractExcelText(file));
        }

        if (fileName.endsWith(".gz")) {
            return List.of("gzip", readGzipFile(file));
        }

        return null;
    }

    private static String readTextFile(File file) throws IOException {
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logFileBytes(file);
            throw e;
        }
    }

    private static void logFileBytes(File file) throws IOException {
        byte[] raw = Files.readAllBytes(file.toPath());
        for (int i = 0; i < Math.min(raw.length, 100); i++) {
            System.out.print((raw[i] & 0xFF) + " ");
        }
    }

    private static String extractDocText(File file) throws IOException {
        try (InputStream fis = new FileInputStream(file)) {
            if (file.getName().toLowerCase().endsWith(".docx")) {
                return new XWPFWordExtractor(new XWPFDocument(fis)).getText();
            } else {
                return new WordExtractor(new HWPFDocument(fis)).getText();
            }
        }
    }

    private static String extractExcelText(File file) throws IOException {
        try (InputStream fis = new FileInputStream(file);
             Workbook workbook = file.getName().toLowerCase().endsWith(".xlsx")
                     ? new XSSFWorkbook(fis)
                     : new HSSFWorkbook(fis)) {

            StringBuilder sb = new StringBuilder();
            workbook.forEach(sheet -> sheet.forEach(row -> {
                row.forEach(cell -> sb.append(cell.toString()).append("\t"));
                sb.append("\n");
            }));
            return sb.toString();
        }
    }

    private static String readGzipFile(File file) throws IOException {
        try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             BufferedReader reader = new BufferedReader(new InputStreamReader(gzip))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        }
    }
}