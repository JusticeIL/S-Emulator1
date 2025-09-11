package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class InstructionTableController {

    @FXML void initialize() { // TODO: add access modifier
        LabelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        TypeCollumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        cyclesCollumn.setCellValueFactory(new PropertyValueFactory<>("cycles"));
        idCollumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        instructionCollumn.setCellValueFactory(new PropertyValueFactory<>("instruction"));
    }

    @FXML
    private TableColumn<?, ?> LabelColumn;

    @FXML
    private TableColumn<?, ?> TypeCollumn; // TODO: typo

    @FXML
    private TableColumn<?, ?> cyclesCollumn; // TODO: typo

    @FXML
    private TableColumn<?, ?> idCollumn; // TODO: typo

    @FXML
    private TableColumn<?, ?> instructionCollumn; // TODO: typo

}