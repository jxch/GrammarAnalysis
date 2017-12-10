package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class Controller {
    @FXML
    private TextField nonTerminator, terminator, starter, sentence;
    @FXML
    private TextArea precept;
    @FXML
    private Button analyzeSentences;
    @FXML
    private TableView<ObservableList<StringProperty>> table, tableS;

    private LR0Analysis lr0Analysis;

    public void analysis() {
        LR0Analysis lr0Analysis = new LR0Analysis(nonTerminator.getText(), terminator.getText(), starter.getText(), precept.getText());
        this.lr0Analysis = lr0Analysis;
        String text = lr0Analysis.analysis(table);
        if (lr0Analysis.isLR0()) {
            analyzeSentences.setOnAction(event -> {
                try {
                    analyzeSentences();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            analyzeSentences.setStyle("-fx-background-color: lawngreen");
            precept.setText(precept.getText() + "\n" + text);
        } else {
            VBox pane = new VBox();
            pane.setAlignment(Pos.CENTER);
            pane.setSpacing(10);
            pane.setPadding(new Insets(5, 5, 5, 5));
            Button button = new Button("确定");
            pane.getChildren().addAll(new Label(text), button);
            button.setOnAction(event -> button.getScene().getWindow().hide());
            Stage stage = new Stage();
            stage.setScene(new Scene(pane, 200, 75));
            stage.setResizable(false);
            stage.show();
        }
    }

    public void saveTo() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("html files (*.txt)", "*.txt"));
//        File file = fileChooser.showSaveDialog(new Stage());
//        try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
//            outputStream.writeUTF(analysisResults.getText());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void clear() {
        table.getColumns().clear();
        terminator.clear();
        nonTerminator.clear();
        starter.clear();
        precept.clear();
        analyzeSentences.setStyle("-fx-background-color: darkgray");
        analyzeSentences.setOnAction(event -> {
        });
    }

    private void analyzeSentences() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("analyzeSentences.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("LR0GrammarAnalysis");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public void go() {
        lr0Analysis.analyzeSentences(sentence.getText(), tableS);
    }

    public void sentenceClear(){
        sentence.clear();
        tableS.getColumns().clear();
    }


    static TableColumn<ObservableList<StringProperty>, String> createColumn(final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        String title;
        if (columnTitle == null || columnTitle.trim().length() == 0) {
            title = "Column " + (columnIndex + 1);
        } else {
            title = columnTitle;
        }
        column.setText(title);
//                new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>()
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>
        column.setCellValueFactory(cellDataFeatures -> {
            ObservableList<StringProperty> values = cellDataFeatures.getValue();
            if (columnIndex >= values.size()) {
                return new SimpleStringProperty("");
            } else {
                return cellDataFeatures.getValue().get(columnIndex);
            }
        });
        return column;
    }
}
