/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
 
package exceptions;


public class CreateRankingException extends Exception{
   private static final long serialVersionUID = 0L;
   public CreateRankingException(String what){
      super(what);
   }
}
