package controller;

public class ControllerResponse {
    boolean isSuccess;
    String message;

    public ControllerResponse(String message) {
        this.message = message;
        isSuccess = false;
    }

    public ControllerResponse() {
        isSuccess = true;
        message = "";
    }
}
