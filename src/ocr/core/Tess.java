/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author benehiko
 */
public class Tess {
    
    public static String extract(BufferedImage bi) throws CvHandler{
        //File imageFile = new File("./images/test_clean_copy.jpg");
        ITesseract tess = new net.sourceforge.tess4j.Tesseract();
        tess.setDatapath("./tessdata/");
        tess.setTessVariable("tessedit_create_hocr", "1");
        tess.setTessVariable("tessedit_char_whitelist","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        //tess.setTessVariable("tessedit_char_whitelist","ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        String result = "";

        try{
            result = tess.doOCR(bi);
        }catch(TesseractException e){
            throw new CvHandler(e.getMessage());
        }
        return result;
    }
}
