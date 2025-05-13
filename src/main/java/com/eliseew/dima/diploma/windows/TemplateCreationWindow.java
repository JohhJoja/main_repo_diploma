package com.eliseew.dima.diploma.windows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TemplateCreationWindow {

    public void show() {
        Stage window = new Stage();
        window.setTitle("Создание шаблона");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #e6f4ec;");

        // Тип документа + Название шаблона
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

        // Описание шаблона
        Label descriptionLabel = new Label("Описание шаблона:");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Введите описание...");
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setWrapText(true);

        // Идентификатор документа
        Label identifierLabel = new Label("Идентификатор документа:");
        VBox identifierBox = new VBox(5);
        TextField idField = new TextField();
        Button addIdButton = new Button("+");
        Tooltip.install(addIdButton, new Tooltip("Добавить варианты"));
        HBox idInputBox = new HBox(5, idField, addIdButton);
        identifierBox.getChildren().add(idInputBox);
        addIdButton.setOnAction(e -> {
            TextField newIdField = new TextField();
            Button removeBtn = new Button("-");
            HBox row = new HBox(5, newIdField, removeBtn);
            identifierBox.getChildren().add(row);
            removeBtn.setOnAction(ev -> identifierBox.getChildren().remove(row));
        });

        // Ключевые слова
        Label keywordLabel = new Label("Ключевые слова:");
        VBox keywordBox = new VBox(5);
        Label regexPreview = new Label("Регулярка: ");

        HBox keywordInputBox = createKeywordRow(keywordBox, regexPreview);
        keywordBox.getChildren().add(keywordInputBox);

        Button addKeywordButton = new Button("+");
        Tooltip.install(addKeywordButton, new Tooltip("Добавить ключевое слово"));
        addKeywordButton.setOnAction(e -> {
            HBox newKeywordRow = createKeywordRow(keywordBox, regexPreview);
            keywordBox.getChildren().add(newKeywordRow);
        });

        VBox keywordWrapper = new VBox(5, keywordBox, addKeywordButton, regexPreview);

        // Действие
        Label actionLabel = new Label("Действие:");
        ComboBox<String> actionCombo = new ComboBox<>();
        actionCombo.getItems().addAll("отчет", "замена");

        Label instructionLabel = new Label("Используйте k1, k2 и т.д. в тексте ниже:");
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

        // Кнопка сохранить
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

        Scene scene = new Scene(layout, 750, 700);
        window.setScene(scene);
        window.show();
    }

    private HBox createKeywordRow(VBox parentBox, Label regexPreview) {
        Label searchLabel = new Label("Искать");
        TextField firstField = new TextField();
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll("до", "после", "между");
        positionCombo.setValue("до");

        TextField secondField = new TextField();
        secondField.setVisible(false);

        HBox row = new HBox(5);
        Button removeButton = new Button("-");
        row.getChildren().addAll(searchLabel, firstField, positionCombo, removeButton);

        positionCombo.setOnAction(e -> {
            if (positionCombo.getValue().equals("между")) {
                if (!row.getChildren().contains(secondField)) {
                    row.getChildren().add(1, secondField); // вставить перед firstField
                    secondField.setVisible(true);
                }
            } else {
                row.getChildren().remove(secondField);
                secondField.setVisible(false);
            }
            updateRegex(parentBox, regexPreview);
        });

        // Обновлять регулярку при вводе текста
        firstField.textProperty().addListener((obs, oldVal, newVal) -> updateRegex(parentBox, regexPreview));
        secondField.textProperty().addListener((obs, oldVal, newVal) -> updateRegex(parentBox, regexPreview));
        positionCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateRegex(parentBox, regexPreview));

        removeButton.setOnAction(e -> {
            parentBox.getChildren().remove(row);
            updateRegex(parentBox, regexPreview);
        });

        return row;
    }

    private void updateRegex(VBox keywordBox, Label previewLabel) {
        StringBuilder regex = new StringBuilder();
        for (javafx.scene.Node node : keywordBox.getChildren()) {
            if (node instanceof HBox hbox) {
                TextField word1 = null;
                TextField word2 = null;
                ComboBox<?> combo = null;

                for (javafx.scene.Node child : hbox.getChildren()) {
                    if (child instanceof TextField field) {
                        if (word1 == null) word1 = field;
                        else word2 = field;
                    } else if (child instanceof ComboBox<?>) {
                        combo = (ComboBox<?>) child;
                    }
                }

                if (combo != null && word1 != null) {
                    String position = combo.getValue().toString();
                    switch (position) {
                        case "до" -> regex.append(".*?(?=").append(word1.getText()).append(")");
                        case "после" -> regex.append("(?<=").append(word1.getText()).append(").*?");
                        case "между" -> {
                            if (word2 != null) {
                                regex.append("(?<=").append(word2.getText()).append(").*?(?=").append(word1.getText()).append(")");
                            }
                        }
                    }
                    regex.append(" | ");
                }
            }
        }
        if (regex.length() >= 3) {
            regex.setLength(regex.length() - 3); // убрать последний " | "
        }
        previewLabel.setText("Регулярка: " + regex);
    }
}
