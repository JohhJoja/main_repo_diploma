package com.eliseew.dima.diploma;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Парсер шаблонов");

        // Меню
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("Файл");
        fileMenu.getItems().addAll(new MenuItem("Открыть файл"), new MenuItem("Сохранить результат"), new SeparatorMenuItem(), new MenuItem("Выход"));

        Menu templateMenu = new Menu("Шаблоны");
        templateMenu.getItems().addAll(new MenuItem("Создать шаблон"), new MenuItem("Импорт шаблона"), new MenuItem("Экспорт шаблона"));

        menuBar.getMenus().addAll(fileMenu, templateMenu);

        // Левая панель - список шаблонов
        ListView<String> templateList = new ListView<>();
        templateList.getItems().addAll("Шаблон 1", "Шаблон 2");

        Button deleteButton = new Button("Удалить");
        Button editButton = new Button("Редактировать");

        VBox templateBox = new VBox(10, new Label("Список шаблонов:"), templateList, deleteButton, editButton);
        templateBox.setPadding(new Insets(10));
        templateBox.setPrefWidth(200);

        // Правая панель - загрузка и парсинг
        VBox parseBox = new VBox(10);
        parseBox.setPadding(new Insets(10));

        Button loadButton = new Button("Загрузить документ");
        Button parseButton = new Button("Применить шаблон");

        parseBox.getChildren().addAll(loadButton, new Label("Выберите шаблон и нажмите 'Применить шаблон'"), parseButton);

        // Основной layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setLeft(templateBox);
        root.setRight(parseBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Обработчики событий
        templateList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !templateList.getSelectionModel().isEmpty()) {
                String selectedTemplate = templateList.getSelectionModel().getSelectedItem();
                showTemplateEditorWindow(selectedTemplate);
            }
        });

        templateMenu.getItems().get(0).setOnAction(e -> showTemplateEditorWindow(null));

        editButton.setOnAction(e -> {
            String selectedTemplate = templateList.getSelectionModel().getSelectedItem();
            if (selectedTemplate != null) {
                showTemplateEditorWindow(selectedTemplate);
            }
        });

        deleteButton.setOnAction(e -> {
            String selectedTemplate = templateList.getSelectionModel().getSelectedItem();
            if (selectedTemplate != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение удаления");
                alert.setHeaderText("Удалить шаблон?");
                alert.setContentText("Вы уверены, что хотите удалить шаблон: " + selectedTemplate + "?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    templateList.getItems().remove(selectedTemplate);
                }
            }
        });

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите документ для загрузки");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Документы", "*.doc", "*.docx", "*.txt", "*.pdf"),
                    new FileChooser.ExtensionFilter("Все файлы", "*.*")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                // логика обработки выбранного файла (заглушка)
                System.out.println("Файл выбран: " + selectedFile.getAbsolutePath());
            }
        });
    }

    private void showTemplateEditorWindow(String templateName) {
        Stage editorStage = new Stage();
        editorStage.setTitle(templateName == null ? "Создание шаблона" : "Редактирование шаблона: " + templateName);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Название шаблона");
        if (templateName != null) nameField.setText(templateName);

        TextArea patternArea = new TextArea();
        patternArea.setPromptText("Настройки шаблона (ключевые слова, теги и т.д.)");

        Button saveButton = new Button("Сохранить");

        layout.getChildren().addAll(new Label("Название шаблона:"), nameField, new Label("Параметры шаблона:"), patternArea, saveButton);

        Scene scene = new Scene(layout, 400, 300);
        editorStage.setScene(scene);
        editorStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}