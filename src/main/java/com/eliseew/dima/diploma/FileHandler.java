package com.eliseew.dima.diploma;

import com.eliseew.dima.diploma.parsers.GZParser;
import com.eliseew.dima.diploma.parsers.TextParser;
import com.eliseew.dima.diploma.parsers.XLSParser;
import com.eliseew.dima.diploma.parsers.XMLParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHandler {

    private List<String> type_text; //мб без new
    private String type,text,_result;
    private File file;

    public void handle() throws IOException {
        type_text = TypeResolver.resolve(file); ///get type and text
        assert type_text != null;
        type = type_text.get(0);
        System.out.println(type); ///for debug
        text = type_text.get(1);
        System.out.println(text); ///for debug
        _result = ParserResolver(type, text, file); ///get result
    }

    public FileHandler(File file) {
        this.file = file;
    }

    private String ParserResolver(String type, String text, File file) {
        String res = "парсинг не вернул никакого результата";
        switch (type){
            case "doc": res = TextParser.parse(text, file);
            case "xml": res = XMLParser.parse(text, file);
            case "xls": res = XLSParser.parse(text, file);
            case "gz": res = GZParser.parse(text, file);
            // TODO: тут продумать дефолтное поведение
                // TODO: подумать над родительским классом и интерфейсом, что-то будет
        }
        return res;
    }

    public String getType() {
        return type;
    }
}
