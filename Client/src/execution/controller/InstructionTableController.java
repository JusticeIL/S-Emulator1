package execution.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import execution.model.InstructionTableEntry;

public class InstructionTableController {

    @FXML
    private TableColumn<InstructionTableEntry, String> TypeColumn;

    @FXML
    private TableColumn<InstructionTableEntry, Number> idColumn;

    @FXML
    private TableColumn<InstructionTableEntry, String> LabelColumn;

    @FXML
    private TableColumn<InstructionTableEntry, String> instructionColumn;

    @FXML
    private TableColumn<InstructionTableEntry, String> cyclesColumn;

    @FXML
    private TableColumn<InstructionTableEntry, String> architectureColumn;

    @FXML
    public void initialize() {
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TypeColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        LabelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        LabelColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        instructionColumn.setCellValueFactory(new PropertyValueFactory<>("instruction"));
        cyclesColumn.setCellValueFactory(new PropertyValueFactory<>("cycles"));
        cyclesColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        architectureColumn.setCellValueFactory(new PropertyValueFactory<>("architecture"));
        architectureColumn.setCellFactory(col -> new TableCell<>() {
            { setStyle("-fx-alignment: center;"); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
    }

    public TableColumn<InstructionTableEntry, Number> getIdColumn() {
        return idColumn;
    }
}