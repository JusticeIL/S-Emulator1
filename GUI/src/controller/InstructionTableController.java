package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import model.InstructionTableEntry;

public class InstructionTableController {

    @FXML
    private TableColumn<InstructionTableEntry, String> LabelColumn;

    @FXML
    private TableColumn<InstructionTableEntry, String> TypeColumn;

    @FXML
    private TableColumn<InstructionTableEntry, String> cyclesColumn;

    @FXML
    private TableColumn<InstructionTableEntry, Number> idColumn;

    @FXML
    private TableColumn<InstructionTableEntry, String> instructionColumn;

    @FXML
    public void initialize() {
        LabelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        LabelColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TypeColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        cyclesColumn.setCellValueFactory(new PropertyValueFactory<>("cycles"));
        cyclesColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        instructionColumn.setCellValueFactory(new PropertyValueFactory<>("instruction"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
    }

    public TableColumn<InstructionTableEntry, Number> getIdColumn() {
        return idColumn;
    }
}