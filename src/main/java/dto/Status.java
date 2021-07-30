package dto;

public class Status {
    public String status;
    public int errorCode;
    public String message;

    public Status() {
    }

    public Status(String status, int errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public Status(String status, int errorCode) {
        this.status = status;
        this.errorCode = errorCode;
    }

    public Status(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
