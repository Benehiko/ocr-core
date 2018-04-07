/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author benehiko
 */
public class Tess {
        
    public static String extract(BufferedImage bi) throws CvHandler, IOException{
        //File imageFile = new File("./images/test_clean_copy.jpg");
        ITesseract tess = new Tesseract();
        tess.setDatapath(getTess());
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
    
    private static String getTess() throws MalformedURLException, IOException{
        File f = null;
        if (!new File("/home/conf/tessdata/eng.traineddata").exists()){
            try{
                new File("/home/conf/tessdata").mkdir();
                URL url = new URL("https://github.com/tesseract-ocr/tessdata/raw/4.00/eng.traineddata");
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream("/home/conf/tessdata/eng.trainneddata");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }catch (IOException e){
                System.out.println(e.toString());
            }            
        }
        try{
            f = new File("/home/conf/tessdata");
        }catch (Exception e){
                System.out.println(e.toString());
        }
        return f.getAbsolutePath();
    }
}
