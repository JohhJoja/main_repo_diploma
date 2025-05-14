package com.eliseew.dima.diploma.windows;

import com.eliseew.dima.diploma.utils.KeywordEntry;
import com.eliseew.dima.diploma.utils.TemplateDataProcessor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TemplateCreationWindow {

    public void show() {
        Stage window = new Stage();
        window.setTitle("Создание шаблона");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #e6f4ec;");

        Label typeLabel = new Label("Тип документа:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("text (txt)", "word", "excel", "gz");
        typeCombo.setValue("text (txt)");

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

            // Проверка на пустые поля
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
                        String position = positionCombo.getValue();
                        if ("between".equals(position) && key2Field != null && !key2Field.getText().isEmpty()) {
                            // Обработка "between"
                            keywords.add(new KeywordEntry(key1Field.getText(), key2Field.getText(), position));
                        } else {
                            // Обработка для "before" и "after"
                            int wordCount = wordCountField != null ? (int) wordCountField.getValue() : 1;

                            keywords.add(new KeywordEntry(key1Field.getText(), position, wordCount));
                        }
                    }
                }
            }

            // Печать полученных данных
            System.out.println("== Получены данные шаблона ===");
            System.out.println("Название: " + name);
            System.out.println("Тип: " + type);
            System.out.println("Описание: " + description);
            System.out.println("Идентификаторы документа:");
            for (String docId : docIds) {
                System.out.println("  - " + docId);
            }
            System.out.println("Действие: " + action);
            System.out.println("Текст/Замена: " + reportText);
            System.out.println("Ключевые слова:");
            for (int i = 0; i < keywords.size(); i++) {
                KeywordEntry keyword = keywords.get(i);
                System.out.println("  k" + (i + 1) + ": " + keyword);
            }
            System.out.println("================================");

            TemplateDataProcessor TDP = new TemplateDataProcessor(name, type, description, action, reportText, docIds, keywords);
        });

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
        positionCombo.getItems().addAll("before", "after", "between");
        positionCombo.setValue("before");

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
            String selected = positionCombo.getValue();
            if ("between".equals(selected)) {
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
