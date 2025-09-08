package controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class RightSideController{


    private TopComponentController topController;
    private LeftSideController leftController;
    private SingleProgramController model;

    public void setModel(SingleProgramController model) {
        this.model = model;
    }


    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }

    @FXML
    private Button AddArgumentBtn;

    @FXML
    private TableView<?> ExecutionArgumentInput;

    @FXML
    private Button RerunBtn;

    @FXML
    private Button ResumeDebugBtn;

    @FXML
    private Button RunProgramBtn;

    @FXML
    private Button ShowStatisticsBtn;

    @FXML
    private Button StartDebugBtn;

    @FXML
    private TableView<?> StatisticsTable;

    @FXML
    private Button StepOverDebugBtn;

    @FXML
    private Button StopDebugBtn;

    @FXML
    private TableView<?> VariableTable;

    @FXML
    void AddArgumentPressed(ActionEvent event) {

    }

    @FXML
    void ExecutionArgumentUpdated(ActionEvent event) {

    }

    @FXML
    void RerunPressed(ActionEvent event) {

    }

    @FXML
    void ResumeDebugPressed(ActionEvent event) {

    }

    @FXML
    void RunProgramPressed(ActionEvent event) {

    }

    @FXML
    void ShowStatisticsPressed(ActionEvent event) {

    }

    @FXML
    void StartDebugPressed(ActionEvent event) {

    }

    @FXML
    void StepOverDebugPressed(ActionEvent event) {

    }

    @FXML
    void StopDebugPressed(ActionEvent event) {

    }

}
