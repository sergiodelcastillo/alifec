package alifec.core.exception;

/**
 * Created by Sergio Del Castillo on 14/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CreateContestFolderException extends Exception {
    private static final long serialVersionUID = 0L;

    public CreateContestFolderException(String s) {
        super(s);

    }
    public CreateContestFolderException(String s, Throwable ex) {
        super(s, ex);

    }
}
