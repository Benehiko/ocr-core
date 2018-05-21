/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NumberPlateStandard;

/**
 *
 * @author benehiko
 */
public final class NumberPlate {
    
    private final String plate;
    private final int plateLen;
    private boolean isCustom = false;
    
    public NumberPlate(String plate){
        this.plate = sanitise(plate);
        this.plateLen = this.plate.length();
        System.out.println("Plate Length: "+plateLen);
    }
    
    public boolean isPlate(){
        /* Plate standards are quite variable, Since there is such variety we first need to test for "letters" only.*/
        /* Ensure that it's not garbage text */
        if (isDigit(plate))
            return false;
        
        /* Check if it is a number plate */
        if ( plateLen > 3 && plateLen < 9){

            /* Check if number plate is custom or not */
            if (isAlphabet(plate.substring(1,3)) && isDigit(plate.substring(4, 6))){
                isCustom = false;
            }else{
                isCustom = true;
            }
            return true;
        }
            return false;
    }
    
    /**
     * Lambda expression for only character matching 
     * Source: https://stackoverflow.com/a/29836318       
     * @return 
     */
    private boolean isDigit(String tmp){
        return tmp.chars().allMatch(Character::isDigit);
    }
    
    private boolean isAlphabet(String tmp){
        return tmp.chars().allMatch(Character::isLetter);
    }
    
    private String sanitise(String tmp){
        return tmp.replaceAll("[^A-Za-z0-9]", "");
    }

    
    public boolean isCustom(){
        return isCustom;
    }
    
    public String getPlate(){
        return this.plate;
    }
}
