package com.eliseew.dima.diploma.windows.template;

import com.eliseew.dima.diploma.utils.text.KeywordEntry;
import com.eliseew.dima.diploma.utils.TemplateDataProcessor;
import com.eliseew.dima.diploma.utils.excel.ExcelPatternModel;
import com.eliseew.dima.diploma.windows.main.HelloApplication;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateCreationWindow {

    Map<String, String> positionMap = new HashMap<>();
    private ArrayList<ExcelPatternModel.CellCoordinate> excelCoordinates = null;
    private File selectedExcelFile = null;
    private boolean isExcelTemplateSelected = false;
    private ArrayList<ExcelPatternModel.CellCoordinate> coords;
//    private HelloApplication mainApp;

//    public TemplateCreationWindow(HelloApplication mainApp) {
//        this.mainApp = mainApp;
//    }


    public TemplateCreationWindow() {
        positionMap.put("до", "before");
        positionMap.put("после", "after");
        positionMap.put("между", "between");
    }

    public void show() {

        Stage window = new Stage();
        window.setTitle("Создание шаблона");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #e6f4ec;");

        Label typeLabel = new Label("Тип документа:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("text", "excel", "gz");
        typeCombo.setValue("text");

        Label nameLabel = new Label("Название шаблона (латиница):");
        TextField nameField = new TextField();
        nameField.setPromptText("Например: pattern1");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z0-9_-]*")) {
                nameField.setText(newVal.replaceAll("[^a-zA-Z0-9_-]", ""));
            }
        });

        HBox topRow = new HBox(10, typeLabel, typeCombo, nameLabel, nameField);

        Label descriptionLabel = new Label("Описание шаблона:");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Введите описание...");
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setWrapText(true);

        Label identifierLabel = new Label("Идентификатор документа:");
        VBox identifierBox = new VBox(5);
        TextField idField = new TextField();
        Button addIdButton = new Button("+");
        HBox idInputBox = new HBox(5, idField, addIdButton);
        identifierBox.getChildren().add(idInputBox);
        addIdButton.setOnAction(e -> {
            TextField newIdField = new TextField();
            Button removeBtn = new Button("-");
            HBox row = new HBox(5, newIdField, removeBtn);
            identifierBox.getChildren().add(row);
            removeBtn.setOnAction(ev -> identifierBox.getChildren().remove(row));
        });

        Label keywordLabel = new Label("Ключевые слова:");
        VBox keywordBox = new VBox(5);
        HBox keywordInputBox = createKeywordRow(keywordBox);
        keywordBox.getChildren().add(keywordInputBox);

        Button addKeywordButton = new Button("+");
        addKeywordButton.setOnAction(e -> {
            HBox newKeywordRow = createKeywordRow(keywordBox);
            keywordBox.getChildren().add(newKeywordRow);
        });

        VBox keywordWrapper = new VBox(5, keywordBox, addKeywordButton);

        Label actionLabel = new Label("Действие:");
        ComboBox<String> actionCombo = new ComboBox<>();
        actionCombo.getItems().addAll("отчет", "замена");

        Label instructionLabel = new Label();
        instructionLabel.setVisible(false);

        TextArea actionArea = new TextArea();
        actionArea.setWrapText(true);
        actionArea.setVisible(false);
        actionArea.setDisable(true);


        // Кнопка загрузки шаблона для Excel
        Button downloadTemplateButton = new Button("Загрузить шаблон");
        downloadTemplateButton.setVisible(false); // Скрыта по умолчанию

        downloadTemplateButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите шаблон Excel (xlsx)");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showOpenDialog(window);

            if (file != null) {
                try (org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
                    coords = new ArrayList<>();

                    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(i);
                        for (org.apache.poi.ss.usermodel.Row row : sheet) {
                            for (org.apache.poi.ss.usermodel.Cell cell : row) {
                                if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                                    if ("*".equals(cell.getStringCellValue().trim())) {
                                        coords.add(new ExcelPatternModel.CellCoordinate(row.getRowNum(), cell.getColumnIndex()));
                                    }
                                }
                            }
                        }
                    }

                    // Сохраняем во временные переменные
                    selectedExcelFile = file;
                    excelCoordinates = coords;
                    isExcelTemplateSelected = true;

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Шаблон загружен");
                    alert.setHeaderText("Excel шаблон успешно загружен");
                    alert.setContentText("Нажмите 'Сохранить шаблон', чтобы завершить сохранение.");
                    alert.showAndWait();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Ошибка при обработке Excel файла");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            } else {
                System.out.println("Файл не выбран.");
            }
        });


// Обработчик изменения выбора типа документа
        typeCombo.setOnAction(e -> {
            String selectedType = typeCombo.getValue();
            boolean isText = "text".equals(selectedType);

            // Показываем или скрываем поля в зависимости от типа
            nameLabel.setVisible(true);
            nameField.setVisible(true);

            descriptionLabel.setVisible(true);
            descriptionArea.setVisible(true);

            if (isText) {
                // При text показываем все поля (твой текущий интерфейс)
                identifierLabel.setVisible(true);
                identifierBox.setVisible(true);

                keywordLabel.setVisible(true);
                keywordWrapper.setVisible(true);

                actionLabel.setVisible(true);
                actionCombo.setVisible(true);

                instructionLabel.setVisible(actionCombo.getValue() != null);
                actionArea.setVisible(actionCombo.getValue() != null);
                actionArea.setDisable(false);

                downloadTemplateButton.setVisible(false);
            } else if ("excel".equals(selectedType)) {
                // При excel скрываем ненужные поля
                identifierLabel.setVisible(false);
                identifierBox.setVisible(false);

                keywordLabel.setVisible(false);
                keywordWrapper.setVisible(false);

                actionLabel.setVisible(false);
                actionCombo.setVisible(false);

                instructionLabel.setVisible(false);
                actionArea.setVisible(false);
                actionArea.setDisable(true);

                downloadTemplateButton.setVisible(true);
            }
        });

// Добавляем кнопку загрузки в layout, например внизу рядом с кнопкой сохранения
// Если у тебя есть контейнер bottomButtons, добавь туда



        actionCombo.setOnAction(e -> {
            String selected = actionCombo.getValue();
            if ("отчет".equals(selected)) {
                instructionLabel.setText("Используйте k1, k2 и т.д. в тексте ниже:");
                instructionLabel.setVisible(true);
                actionArea.setPromptText("Текст отчета...");
                actionArea.setVisible(true);
                actionArea.setDisable(false);
            } else if ("замена".equals(selected)) {
                instructionLabel.setVisible(false);
                actionArea.setPromptText("заменить на...");
                actionArea.setVisible(true);
                actionArea.setDisable(false);
            } else {
                instructionLabel.setVisible(false);
                actionArea.setVisible(false);
                actionArea.setDisable(true);
            }
        });

        Button saveButton = new Button("Сохранить шаблон");

        layout.getChildren().addAll(
                topRow,
                descriptionLabel, descriptionArea,
                identifierLabel, identifierBox,
                keywordLabel, keywordWrapper,
                actionLabel, actionCombo,
                instructionLabel, actionArea,
                saveButton
        );

        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String type = typeCombo.getValue();
            String description = descriptionArea.getText();
            String action = actionCombo.getValue();
            String reportText = actionArea.getText();

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Общий доступ");
            confirmAlert.setHeaderText("Сделать шаблон общедоступным?");
            confirmAlert.setContentText("Если выбрать 'Нет', шаблон будет сохранён только локально.");

            ButtonType yesButton = new ButtonType("Да");
            ButtonType noButton = new ButtonType("Нет");
            ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmAlert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == yesButton || response == noButton) {
                    Boolean isLocal = !(response == yesButton);
                    if (isExcelTemplateSelected){
                        TemplateDataProcessor TDP = new TemplateDataProcessor(name,
                                type,
                                description,
                                coords,
                                isLocal);
                    } else {

            if (name.isEmpty() || description.isEmpty() || action == null || action.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Поля не могут быть пустыми");
                alert.setContentText("Пожалуйста, заполните все обязательные поля.");
                alert.showAndWait();
                return;
            }

            List<String> docIds = new ArrayList<>();
            for (javafx.scene.Node node : identifierBox.getChildren()) {
                if (node instanceof HBox hbox) {
                    for (javafx.scene.Node child : hbox.getChildren()) {
                        if (child instanceof TextField tf && !tf.getText().isEmpty()) {
                            docIds.add(tf.getText());
                        }
                    }
                }
            }

            List<KeywordEntry> keywords = new ArrayList<>();
            for (javafx.scene.Node node : keywordBox.getChildren()) {
                if (node instanceof HBox hbox) {
                    TextField key1Field = null;
                    TextField key2Field = null;
                    ComboBox<String> positionCombo = null;
                    ComboBox wordCountField = null;

                    for (javafx.scene.Node child : hbox.getChildren()) {
                        if (child instanceof TextField tf) {
                            if (tf.isVisible() && key1Field == null) {
                                key1Field = tf;
                            } else if (tf.isVisible() && key2Field == null) {
                                key2Field = tf;
                            }
                        } else if (child instanceof ComboBox<?> cb && cb.getValue() instanceof Integer) {
                            wordCountField = (ComboBox<Integer>) cb;
                        }

                        if (child instanceof ComboBox<?> cb && cb.getValue() instanceof String) {
                            positionCombo = (ComboBox<String>) cb;
                        }
                    }

                    if (positionCombo != null && key1Field != null && !key1Field.getText().isEmpty()) {
                        String selectedPosition = positionMap.get(positionCombo.getValue()); // Получаем "before"/"after"/"between"
                        if ("between".equals(selectedPosition) && key2Field != null && !key2Field.getText().isEmpty()) {
                            // Обработка "between"
                            keywords.add(new KeywordEntry(key1Field.getText(), key2Field.getText(), selectedPosition));
                        } else {
                            // Обработка для "before" и "after"
                            int wordCount = wordCountField != null ? (int) wordCountField.getValue() : 1;

                            keywords.add(new KeywordEntry(key1Field.getText(), selectedPosition, wordCount));
                        }
                    }
                }
            }
                        TemplateDataProcessor TDP = new TemplateDataProcessor(name, type, description, action, reportText, docIds, keywords, isLocal);
                    }
                } else {
                    System.out.println("Сохранение отменено.");
                }
                //mainApp.refreshTemplateTree(mainApp.getTemplateTree(), mainApp.getDocTemplates(), mainApp.getExcelTemplates());

            });

        });













        // Кнопка информации
        Button infoButton = new Button("i");
        infoButton.setStyle("-fx-font-size: 16px; -fx-background-radius: 50%; -fx-min-width: 30px; -fx-min-height: 30px;");
        Tooltip tooltip = new Tooltip("Скачать инструкцию по применению");
        Tooltip.install(infoButton, tooltip);

// Действие при нажатии
        infoButton.setOnAction(event -> {
            try {
                String sourcePath = "E:\\Java\\deeplomka\\intronet\\instruction\\Инструкция по созданию шаблонов.docx";
                String downloadsPath = System.getProperty("user.home") + "/Downloads/Инструкция по созданию шаблонов.docx";

                java.nio.file.Files.copy(
                        java.nio.file.Paths.get(sourcePath),
                        java.nio.file.Paths.get(downloadsPath),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                // Открыть папку Downloads
                java.awt.Desktop.getDesktop().open(new java.io.File(System.getProperty("user.home") + "/Downloads"));

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Не удалось скачать инструкцию");
                alert.setContentText("Проверьте путь к файлу и повторите попытку.");
                alert.showAndWait();
            }
        });

// Обертка для кнопок "Сохранить шаблон" и "i" на одном уровне
        BorderPane bottomButtons = new BorderPane();
        bottomButtons.setPadding(new Insets(10, 0, 0, 0));

        bottomButtons.setLeft(saveButton);
        bottomButtons.setRight(infoButton);

        layout.getChildren().add(bottomButtons);
        bottomButtons.setCenter(downloadTemplateButton);


        Scene scene = new Scene(layout, 750, 700);
        window.setScene(scene);
        window.show();
    }

    private HBox createKeywordRow(VBox parentBox) {
        Label searchLabel = new Label("Искать");
        TextField firstField = new TextField();
        TextField secondField = new TextField();
        secondField.setVisible(false);

        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll("до", "после", "между");
        positionCombo.setValue("после");

        Label countLabel = new Label("Кол-во слов:");
        ComboBox<Integer> wordCountCombo = new ComboBox<>();
        wordCountCombo.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        wordCountCombo.setValue(1);
        wordCountCombo.setPrefWidth(50);

        Button removeButton = new Button("-");

        // Создание строки с полями
        HBox row = new HBox(5, searchLabel, firstField, positionCombo, countLabel, wordCountCombo, removeButton);

        // Обработка изменения позиции в ComboBox
        positionCombo.setOnAction(e -> {
            String selectedPosition = positionMap.get(positionCombo.getValue()); // Получаем "before"/"after"/"between"

            System.out.println(selectedPosition);
            if ("between".equals(selectedPosition)) {
                if (!row.getChildren().contains(secondField)) {
                    row.getChildren().add(2, secondField);
                    secondField.setVisible(true);
                }
                countLabel.setVisible(false);
                wordCountCombo.setVisible(false);
            } else {
                row.getChildren().remove(secondField);
                secondField.setVisible(false);
                countLabel.setVisible(true);
                wordCountCombo.setVisible(true);
            }
        });

        // Удаление строки
        removeButton.setOnAction(e -> parentBox.getChildren().remove(row));

        return row;
    }
}
