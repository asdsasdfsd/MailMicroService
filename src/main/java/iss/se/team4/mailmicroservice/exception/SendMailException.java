package iss.se.team4.mailmicroservice.exception;

public class SendMailException extends Exception {
    public SendMailException() {

    }

    public SendMailException(String message) {
        super(message);
    }


    public SendMailException(Throwable cause) {
        super(cause);
    }
}
