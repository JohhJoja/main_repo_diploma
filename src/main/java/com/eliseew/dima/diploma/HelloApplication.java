package com.eliseew.dima.diploma;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {

    private Label templateDescriptionLabel = new Label("Описание шаблона будет здесь...");
    private File selectedFile;

    ///
    FileHandler handler;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Парсер шаблонов");

        // Меню
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("Файл");
        MenuItem openFileItem = new MenuItem("Открыть файл");
        MenuItem saveResultItem = new MenuItem("Сохранить результат");
        MenuItem importTemplateItem = new MenuItem("Импорт шаблона");
        MenuItem exportTemplateItem = new MenuItem("Экспорт шаблона");
        MenuItem exitItem = new MenuItem("Выход");

        fileMenu.getItems().addAll(openFileItem, saveResultItem, new SeparatorMenuItem(), importTemplateItem, exportTemplateItem, new SeparatorMenuItem(), exitItem);

        Menu helpMenu = new Menu("Помощь");
        MenuItem aboutItem = new MenuItem("О программе");
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        // Левая панель - список шаблонов
        ListView<String> templateList = new ListView<>();
        templateList.getItems().addAll("Шаблон 1", "Шаблон 2");

        Button createButton = new Button("Создать");
        Button deleteButton = new Button("Удалить");
        Button editButton = new Button("Редактировать");

        VBox templateBox = new VBox(10, new Label("Список шаблонов:"), templateList, createButton, deleteButton, editButton);
        templateBox.setPadding(new Insets(10));
        templateBox.setPrefWidth(180);
        templateBox.setStyle("-fx-background-color: #e6f4ec;");

        // Центральная панель - описание шаблона
        templateDescriptionLabel.setWrapText(true);
        VBox centerBox = new VBox(10, new Label("Описание шаблона:"), templateDescriptionLabel);
        centerBox.setPadding(new Insets(10));
        centerBox.setStyle("-fx-background-color: #ffffff;");

        // Правая панель - загрузка и парсинг
        VBox parseBox = new VBox(10);
        parseBox.setPadding(new Insets(10));
        parseBox.setStyle("-fx-background-color: #e6f4ec;");

        Button loadButton = new Button("Загрузить документ");
        Button parseButton = new Button("Применить шаблон");
        TextArea resultArea = new TextArea();
        resultArea.setPromptText("Результаты будут здесь...");
        resultArea.setPrefHeight(300);

        parseBox.getChildren().addAll(loadButton, new Label("Выберите шаблон и нажмите 'Применить шаблон'"), parseButton, resultArea);

        // Основной layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setLeft(templateBox);
        root.setCenter(centerBox);
        root.setRight(parseBox);

        Scene scene = new Scene(root, 900, 600);

        parseButton.setOnAction(e -> {
            if (selectedFile != null) {
                try {
                    handler = new FileHandler(selectedFile);
                    handler.handle();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String type = handler.getType();
                resultArea.setText("Тип определённого файла: " + type);
            } else {
                resultArea.setText("Сначала выберите файл");
            }
        });

        // Drag & Drop
        root.setOnDragOver(event -> {
            if (event.getGestureSource() != root && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        root.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                selectedFile = db.getFiles().get(0);
                templateDescriptionLabel.setText("Файл выбран: " + selectedFile.getName());
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Логика кнопок
        deleteButton.setOnAction(e -> {
            String selected = templateList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Подтверждение удаления");
                confirm.setHeaderText("Удалить шаблон " + selected + "?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        templateList.getItems().remove(selected);
                    }
                });
            }
        });

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile = file;
                templateDescriptionLabel.setText("Файл выбран: " + file.getName());
                ///
                ///Processor.process(file); // пока что просто логика определения типа
                resultArea.setText("Файл выбран: " + file.getName());
            }
        });

        aboutItem.setOnAction(e -> {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("О программе");
            info.setHeaderText("Парсер шаблонов");
            info.setContentText("Программа позволяет загружать шаблоны для парсинга отчетов и применять их к различным документам. Вы можете создавать, редактировать, импортировать и экспортировать шаблоны.");
            info.showAndWait();
        });

        importTemplateItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Импорт шаблона");
            fileChooser.showOpenDialog(primaryStage);
        });

        exportTemplateItem.setOnAction(e -> {
            List<String> templates = templateList.getItems();
            ChoiceDialog<String> dialog = new ChoiceDialog<>(templates.isEmpty() ? null : templates.get(0), templates);
            dialog.setTitle("Экспорт шаблона");
            dialog.setHeaderText("Выберите шаблон для экспорта");
            dialog.setContentText("Шаблон:");
            dialog.showAndWait();
        });

        templateList.setOnMouseClicked(event -> {
            String selected = templateList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                templateDescriptionLabel.setText("Описание для шаблона: " + selected);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
