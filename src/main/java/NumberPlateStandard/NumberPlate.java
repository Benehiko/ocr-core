/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NumberPlateStandard;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author benehiko
 */
public final class NumberPlate {
    
    private final String plate;
    private final int plateLen;
    private boolean isCustom = false;
    
    public NumberPlate(String plate){
        this.plate = sanitise(plate).toUpperCase(Locale.UK);
        this.plateLen = this.plate.length();
        System.out.println("Plate Length: "+plateLen);
    }

    public NumberPlate() {
        this.plate = "";
        this.isCustom = false;
        this.plateLen =0;
    }
    
    public boolean isPlate(){
        /* Plate standards are quite variable, Since there is such variety we first need to test for "letters" only.*/
        /* Ensure that it's not garbage text */
        if (isDigit(plate))
            return false;
        
        /* Check if it is a number plate */
        if ( plateLen > 2 && plateLen < 10){
            String plate_province;
            List<String> provinces = getProvinces();
            for (String p : provinces){
                int pLen = p.length();
                if (p.equals("ca"))
                    plate_province= plate.substring(1, pLen);
                else
                    plate_province = plate.substring(plateLen-pLen, plateLen);
                
                System.out.println("Possible province: "+plate_province);
                 if (p.equalsIgnoreCase(plate_province) && !isDigit(plate_province)){
                     return true;
                 }
            }

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
    
    public List<String> getProvinces(){
        String[] provinces = {"gp", "mp", "l", "ca", "zn", "ec", "nw", "nc", "fs", "d", "g", "b", "m", "wp"};
        return Arrays.asList(provinces);
    }
}
