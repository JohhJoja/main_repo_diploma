package com.eliseew.dima.diploma.windows.main;

import com.eliseew.dima.diploma.FileHandler;
import com.eliseew.dima.diploma.windows.TemplateCreationWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.eliseew.dima.diploma.utils.TemplateJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HelloApplication extends Application {

    private Label templateDescriptionLabel = new Label("Описание шаблона будет здесь...");
    private List<File> selectedFiles;
    private FileHandler handler;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Парсер шаблонов");

        // Меню
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Файл");
        //MenuItem openFileItem = new MenuItem("Открыть файл");
        //MenuItem saveResultItem = new MenuItem("Сохранить результат");
        MenuItem importTemplateItem = new MenuItem("Импорт шаблона");
        MenuItem exportTemplateItem = new MenuItem("Экспорт шаблона");
        MenuItem exitItem = new MenuItem("Выход");
        fileMenu.getItems().addAll(
               // openFileItem, saveResultItem,
                new SeparatorMenuItem(), importTemplateItem, exportTemplateItem, new SeparatorMenuItem(), exitItem);

        Menu helpMenu = new Menu("Помощь");
        MenuItem aboutItem = new MenuItem("О программе");
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        // Левая панель - шаблоны
        ListView<String> templateList = new ListView<>();
        templateList.getItems().addAll("Шаблон 1", "Шаблон 2");

        Button createButton = new Button("Создать");
        Button deleteButton = new Button("Удалить");
        Button editButton = new Button("Редактировать");

        VBox templateBox = new VBox(10, new Label("Список шаблонов:"), templateList, createButton, deleteButton, editButton);
        templateBox.setPadding(new Insets(10));
        templateBox.setPrefWidth(180);
        templateBox.setStyle("-fx-background-color: #e6f4ec;");

        // Центр
        templateDescriptionLabel.setWrapText(true);
        VBox centerBox = new VBox(10, new Label("Описание шаблона:"), templateDescriptionLabel);
        centerBox.setPadding(new Insets(10));
        centerBox.setStyle("-fx-background-color: #ffffff;");

        // Правая панель
        VBox parseBox = new VBox(10);
        parseBox.setPadding(new Insets(10));
        parseBox.setStyle("-fx-background-color: #e6f4ec;");

        Button loadButton = new Button("Загрузить документы");
        Button parseButton = new Button("Применить шаблон");
        TextArea resultArea = new TextArea();
        resultArea.setPromptText("Результаты будут здесь...");
        resultArea.setPrefHeight(300);

        importTemplateItem.setOnAction(e -> {
            File destinationDir = new File("E:\\Java\\deeplomka\\intronet\\patterns");
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите шаблоны для импорта");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
            fileChooser.setInitialDirectory(destinationDir); // Устанавливаем нужную папку

            List<File> filesToImport = fileChooser.showOpenMultipleDialog(null);

            if (filesToImport != null && !filesToImport.isEmpty()) {
                for (File file : filesToImport) {
                    File destFile = new File(destinationDir, file.getName());
                    try {
                        java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        exportTemplateItem.setOnAction(e -> {
            File sourceDir = new File("templates");
            if (!sourceDir.exists()) {
                sourceDir.mkdirs();
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите шаблоны для экспорта");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
            fileChooser.setInitialDirectory(sourceDir);
            List<File> filesToExport = fileChooser.showOpenMultipleDialog(null);

            if (filesToExport != null && !filesToExport.isEmpty()) {
                File exportTargetDir = new File("E:\\Java\\deeplomka\\intronet\\patterns");
                if (!exportTargetDir.exists()) {
                    exportTargetDir.mkdirs();
                }

                for (File file : filesToExport) {
                    File destFile = new File(exportTargetDir, file.getName());
                    try {
                        java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        aboutItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("О программе");
            alert.setHeaderText("Система обработки шаблонов");
            alert.setContentText("заглушка");
            alert.showAndWait();
        });


        exitItem.setOnAction(e -> {
            Platform.exit();
        });

        parseBox.getChildren().addAll(loadButton, new Label("Выберите шаблон и нажмите 'Применить шаблон'"), parseButton, resultArea);

        // Layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setLeft(templateBox);
        root.setCenter(centerBox);
        root.setRight(parseBox);

        Scene scene = new Scene(root, 900, 600);



        //Открытие окна создания
        createButton.setOnAction(e -> {
            TemplateCreationWindow creationWindow = new TemplateCreationWindow();
            creationWindow.show();  // Отображаем новое окно
        });


        // Загрузка файлов
        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите документы");
            selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                templateDescriptionLabel.setText("Выбрано файлов: " + selectedFiles.size());
                resultArea.setText("Выбрано файлов: " + selectedFiles.size());
            } else {
                resultArea.setText("Файлы не выбраны");
            }
        });

        // Применение шаблона и сохранение результата в файл
        parseButton.setOnAction(e -> {
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                StringBuilder resultBuilder = new StringBuilder();

                for (File file : selectedFiles) {
                    try {
                        handler = new FileHandler(file);
                        handler.handle();
                        String parsedData = handler.getParsedData();  // Здесь ты получаешь реальный результат парсинга
                        resultBuilder.append(parsedData).append("\n\n");
                    } catch (IOException ex) {
                        resultBuilder.append("Ошибка файла ").append(file.getName()).append(": ").append(ex.getMessage()).append("\n\n");
                    }
                }

                String resultText = resultBuilder.toString();
                resultArea.setText(resultText);  // Вывод результата в TextArea

                // Сохранение результата в файл
                saveResultToFile(resultText);
            } else {
                resultArea.setText("Сначала выберите хотя бы один файл");
            }
        });

        // Drag and Drop
        root.setOnDragOver(event -> {
            if (event.getGestureSource() != root && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        root.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                selectedFiles = db.getFiles();
                templateDescriptionLabel.setText("Выброшено файлов: " + selectedFiles.size());
                resultArea.setText("Выброшено файлов: " + selectedFiles.size());
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

        loadTemplates(templateList);

        templateList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String description = getTemplateDescription(newVal);
                templateDescriptionLabel.setText(description);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveResultToFile(String result) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setTitle("Сохранить результат");

        // Открытие диалогового окна для выбора места сохранения
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(result); // Записываем результат в файл
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadTemplates(ListView<String> listView) {
        File dir = new File("templates");
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) return;

        listView.getItems().clear();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".json")) {
                listView.getItems().add(fileName.substring(0, fileName.length() - 5));
            }
        }
    }

    private String getTemplateDescription(String templateName) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("templates", templateName + ".json");
        if (!file.exists()) return "Описание не найдено";

        try {
            List<TemplateJson> templates = mapper.readValue(file, new TypeReference<List<TemplateJson>>() {});
            if (!templates.isEmpty()) {
                return templates.get(0).getDescription();
            }
        } catch (IOException e) {
            return "Ошибка чтения шаблона: " + e.getMessage();
        }

        return "Описание отсутствует";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
