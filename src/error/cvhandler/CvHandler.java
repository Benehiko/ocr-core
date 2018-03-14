/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package error.cvhandler;

/**
 *
 * @author benehiko
 */
public class CvHandler extends Exception{
    
    public CvHandler(){}
    
    public CvHandler(String message){
        super(message);
    }
    
    
}
