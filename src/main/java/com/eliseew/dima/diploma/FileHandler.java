package com.eliseew.dima.diploma;

import com.eliseew.dima.diploma.parsers.*;
import com.eliseew.dima.diploma.utils.TypeResolver;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHandler {

    private String type;
    private String text;
    private String result;
    private final File file;
    private String selectedTemplateName = null;

    public FileHandler(File file, String selectedTemplateName, String selectedTemplateType) {
        this.file = file;
        this.selectedTemplateName = selectedTemplateName;
    }

    public void handle() throws IOException {
        List<String> typeAndText = TypeResolver.resolve(file);
        if (typeAndText == null || typeAndText.size() < 2) {
            throw new IOException("Не удалось определить тип или текст файла: " + file.getName());
        }

        type = typeAndText.get(0);
        text = typeAndText.get(1);

        result = parseByType(type, text, file, selectedTemplateName);

        System.out.println("Файл: " + file.getName());
        System.out.println("Тип: " + type);
        System.out.println("РезультатЪ: " + result);
        System.out.println("-----------------------------------");
    }

    private String parseByType(String type, String text, File file, String selectedTemplateName) throws IOException {
        switch (type) {
            case "doc":
                return TextParser.parse(text, file, selectedTemplateName);
            case "xml":
                return XMLParser.parse(text, file, selectedTemplateName);
            case "xls":
                return XLSParser.parse(text, file, selectedTemplateName);
            case "xlsx":
                return XLSXParser.parse(text, file, selectedTemplateName);
            case "gz":
                return GZParser.parse(text, file, selectedTemplateName);
            default:
                return "Формат не поддерживается: " + type;
        }
    }

    public String getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public String getParsedData() {
        return result;
    }
}
