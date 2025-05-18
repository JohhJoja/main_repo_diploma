package com.eliseew.dima.diploma.windows.main;

import com.eliseew.dima.diploma.utils.FileHandler;
import com.eliseew.dima.diploma.utils.excel.ExcelPatternModel;
import com.eliseew.dima.diploma.windows.template.TemplateCreationWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.eliseew.dima.diploma.utils.text.TemplateJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HelloApplication extends Application {

    private final Label templateDescriptionLabel = new Label("Описание шаблона будет здесь...");
    private List<File> selectedFiles;
    private FileHandler handler;
    private String selectedTemplateName = null;
    private String selectedTemplateType = null;
    private TreeView<String> templateTree;
    private TreeItem<String> docTemplates;
    private TreeItem<String> excelTemplates;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Парсер шаблонов");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/eliseew/dima/diploma/image.jpg")));

        // Меню
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Файл");
        MenuItem importTemplateItem = new MenuItem("Импорт шаблона");
        MenuItem exportTemplateItem = new MenuItem("Экспорт шаблона");
        MenuItem exitItem = new MenuItem("Выход");
        fileMenu.getItems().addAll(importTemplateItem, exportTemplateItem, new SeparatorMenuItem(), exitItem);

        Menu helpMenu = new Menu("Помощь");
        MenuItem aboutItem = new MenuItem("О программе");
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        // Левая панель - шаблоны
        templateTree = new TreeView<>();
        TreeItem<String> rootItem = new TreeItem<>("Шаблоны");
        templateTree.setRoot(rootItem);
        templateTree.setShowRoot(false);

        // Подкатегории
        docTemplates = new TreeItem<>("Документы");
        excelTemplates = new TreeItem<>("Excel");
        rootItem.getChildren().addAll(docTemplates, excelTemplates);

        Button createButton = new Button("Создать");
        Button deleteButton = new Button("Удалить");
        Button refreshButton = new Button("Обновить список");
        Button clearSelectionButton = new Button("Отменить выбор");

        VBox templateBox = new VBox(10, new Label("Список шаблонов:"), templateTree, refreshButton, createButton, deleteButton, clearSelectionButton);
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

        refreshButton.setOnAction(event -> refreshTemplateTree(templateTree, docTemplates, excelTemplates));

        importTemplateItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите шаблоны для импорта");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
            fileChooser.setInitialDirectory(new File("E:\\Java\\deeplomka\\intronet\\patterns"));

            List<File> filesToImport = fileChooser.showOpenMultipleDialog(null);

            if (filesToImport != null && !filesToImport.isEmpty()) {
                for (File file : filesToImport) {
                    String subfolder = file.getParentFile().getName(); // определим тип (doc или excel)
                    File destinationDir = new File("templates\\" + subfolder);
                    if (!destinationDir.exists()) {
                        destinationDir.mkdirs();
                    }

                    File destFile = new File(destinationDir, file.getName());
                    try {
                        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        refreshTemplateTree(templateTree, docTemplates, excelTemplates);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            templateTree.refresh();
        });

        exportTemplateItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите шаблоны для экспорта");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
            fileChooser.setInitialDirectory(new File("templates"));

            List<File> filesToExport = fileChooser.showOpenMultipleDialog(null);

            if (filesToExport != null && !filesToExport.isEmpty()) {
                for (File file : filesToExport) {
                    String subfolder = file.getParentFile().getName(); // определим тип (doc или excel)
                    File exportTargetDir = new File("E:\\Java\\deeplomka\\intronet\\patterns\\" + subfolder);
                    if (!exportTargetDir.exists()) {
                        exportTargetDir.mkdirs();
                    }

                    File destFile = new File(exportTargetDir, file.getName());
                    try {
                        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            templateTree.refresh();
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

        deleteButton.setOnAction(e -> {
            if (selectedTemplateName != null && selectedTemplateType != null) {
                File fileToDelete = new File("templates/" + selectedTemplateType, selectedTemplateName + ".json");
                if (fileToDelete.exists()) {
                    boolean deleted = fileToDelete.delete();
                    if (deleted) {
                        templateDescriptionLabel.setText("Шаблон удалён: " + selectedTemplateName);
                        selectedTemplateName = null;
                        selectedTemplateType = null;
                        refreshTemplateTree(templateTree, docTemplates, excelTemplates);
                    } else {
                        templateDescriptionLabel.setText("Не удалось удалить шаблон.");
                    }
                } else {
                    templateDescriptionLabel.setText("Файл шаблона не найден.");
                }
            } else {
                templateDescriptionLabel.setText("Сначала выберите шаблон для удаления.");
            }
        });


        clearSelectionButton.setOnAction(e -> {
            templateTree.getSelectionModel().clearSelection();
            selectedTemplateName = null;
            selectedTemplateType = null;
            templateDescriptionLabel.setText("Описание шаблона будет здесь...");
        });

        createButton.setOnAction(e -> {
            TemplateCreationWindow creationWindow = new TemplateCreationWindow();
            creationWindow.show();
//            refreshTemplateTree(templateTree, docTemplates, excelTemplates);
        });

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

        parseButton.setOnAction(e -> {
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (selectedTemplateName == null) {
                    resultArea.setText("Сначала выберите шаблон");
                    return;
                }

                StringBuilder resultBuilder = new StringBuilder();

                for (File file : selectedFiles) {
                    try {
                        handler = new FileHandler(file, selectedTemplateName, selectedTemplateType);
                        handler.handle();
                        String parsedData = handler.getParsedData();
                        resultBuilder.append("Файл: ").append(file.getName()).append("\n");
                        resultBuilder.append(parsedData).append("\n\n");
                    } catch (IOException ex) {
                        resultBuilder.append("Ошибка обработки файла ").append(file.getName()).append(": ").append(ex.getMessage()).append("\n\n");
                    }
                }

                String resultText = resultBuilder.toString();
                resultArea.setText(resultText);
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

        // Загрузка шаблонов
        loadTemplates(docTemplates, excelTemplates);

        templateTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(rootItem) && !newVal.equals(docTemplates) && !newVal.equals(excelTemplates)) {
                selectedTemplateName = newVal.getValue();
                selectedTemplateType = newVal.getParent().equals(docTemplates) ? "doc" : "excel";

                String description = getTemplateDescription(selectedTemplateName, selectedTemplateType);
                templateDescriptionLabel.setText(description);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadTemplates(TreeItem<String> docTemplates, TreeItem<String> excelTemplates) {
        // Очищаем существующие элементы
        docTemplates.getChildren().clear();
        excelTemplates.getChildren().clear();

        // Загружаем шаблоны документов
        File docDir = new File("templates/doc");
        if (docDir.exists() && docDir.isDirectory()) {
            File[] docFiles = docDir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
            if (docFiles != null) {
                for (File file : docFiles) {
                    String fileName = file.getName();
                    if (fileName.endsWith(".json")) {
                        docTemplates.getChildren().add(new TreeItem<>(fileName.substring(0, fileName.length() - 5)));
                    }
                }
            }
        }

        // Загружаем Excel шаблоны
        File excelDir = new File("templates/excel");
        if (excelDir.exists() && excelDir.isDirectory()) {
            File[] excelFiles = excelDir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
            if (excelFiles != null) {
                for (File file : excelFiles) {
                    String fileName = file.getName();
                    if (fileName.endsWith(".json")) {
                        excelTemplates.getChildren().add(new TreeItem<>(fileName.substring(0, fileName.length() - 5)));
                    }
                }
            }
        }
    }

    private String getTemplateDescription(String templateName, String templateType) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("templates/" + templateType, templateName + ".json");
        if (!file.exists()) return "Описание не найдено";

        try {
            if ("doc".equals(templateType)) {
                List<TemplateJson> templates = mapper.readValue(file, new TypeReference<List<TemplateJson>>() {});
                if (!templates.isEmpty()) {
                    return templates.get(0).getDescription();
                }
            } else {
                // Аналогичная логика для Excel, возможно с другим классом вместо TemplateJson
                List<ExcelPatternModel> templates = mapper.readValue(file, new TypeReference<List<ExcelPatternModel>>() {});
                if (!templates.isEmpty()) {
                    return templates.get(0).getDescription();
                }
            }
        } catch (IOException e) {
            return "Ошибка чтения шаблона: " + e.getMessage();
        }
        return "Описание отсутствует";
    }

    private void saveResultToFile(String result) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setTitle("Сохранить результат");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void refreshTemplateTree(TreeView<String> templateTree, TreeItem<String> docTemplates, TreeItem<String> excelTemplates) {
        loadTemplates(docTemplates, excelTemplates);
        templateTree.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }

}