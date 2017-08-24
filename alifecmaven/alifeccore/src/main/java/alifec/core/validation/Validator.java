package alifec.core.validation;

/**
 * Created by Sergio Del Castillo on 15/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public interface Validator<T> {

    boolean validate(T object);
}
