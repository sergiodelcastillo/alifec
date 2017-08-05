/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.exception;


import alifec.core.simulation.Microorganism;

public class DeadMicroorganismException extends Exception {
    private static final long serialVersionUID = 0L;
    private Microorganism mo;
	 
    public DeadMicroorganismException(String s, Microorganism mo){
        super(s);
		  	 this.mo = mo;
    }
	 public Microorganism getMO(){
		 return mo;
	 }
           
}
