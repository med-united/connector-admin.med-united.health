package health.medunited.architecture.exception.connector;

public class ConnectorCardsException extends Exception {

    public ConnectorCardsException(String msg) {
        super(msg);
    }

    public ConnectorCardsException(String msg, Throwable e) {
        super(msg, e);
    }
}
