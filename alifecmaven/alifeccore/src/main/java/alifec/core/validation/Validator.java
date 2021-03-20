package alifec.core.validation;

import alifec.core.exception.ValidationException;

/**
 * Created by Sergio Del Castillo on 15/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public interface Validator<T> {

    T validate(T object) throws ValidationException;
}
