package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class InstructionTableController {

    @FXML
    private TableColumn<?, ?> LabelColumn;

    @FXML
    private TableColumn<?, ?> TypeColumn;

    @FXML
    private TableColumn<?, ?> cyclesColumn;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> instructionColumn;

    @FXML
    public void initialize() {
        LabelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        cyclesColumn.setCellValueFactory(new PropertyValueFactory<>("cycles"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        instructionColumn.setCellValueFactory(new PropertyValueFactory<>("instruction"));
    }
}