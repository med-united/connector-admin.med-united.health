package health.medunited.architecture.jaxrs.resource.crypto;

public class IdpCryptoException extends Exception {

    private static final long serialVersionUID = 6861433495462078391L;

    public IdpCryptoException() {

    }

    public IdpCryptoException(final Exception e) {
        super(e);
    }

    public IdpCryptoException(final String s) {
        super(s);
    }

    public IdpCryptoException(final String s, final Exception ex) {
        super(s, ex);
    }
}
