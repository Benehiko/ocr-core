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
<<<<<<< HEAD
        if (!r.isEmpty()){
            try{
                result = tess.doOCR(bi, r);
                //result = tess.doOCR(bi,r);
            }catch(TesseractException e){
                throw new CvHandler(e.getMessage());
            }
        }else result = tess.doOCR(bi);
        
        return result;
    }
    
    private static String getTess() throws MalformedURLException, IOException{
        File f = null;
        //URL classPath = Tess.class.getResource("Tess.class");
        
        String tessdata = "tessdata";
        //tessdata = tessdata.replace("Tess.class", "/tessdata");
        if (!new File(tessdata+"/eng.traineddata").exists() || !new File(tessdata+"/osd.traineddata").exists()){
            
            try{
                new File(tessdata).mkdirs();
                System.out.println("Downloading eng.traineddata...");
                URL url = new URL("https://github.com/tesseract-ocr/tessdata/raw/master/eng.traineddata");
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(tessdata+"/eng.traineddata");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                
                System.out.println("Downloading osd.traineddata...");
                url = new URL("https://github.com/tesseract-ocr/tessdata/raw/master/osd.traineddata");
                rbc = Channels.newChannel(url.openStream());
                fos = new FileOutputStream(tessdata+"/osd.traineddata");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                
            }catch (IOException e){
                System.out.println(e.toString());
            }            
        }
=======

>>>>>>> parent of 2561de9... Update Fix crash on large images
        try{
            result = tess.doOCR(bi);
        }catch(TesseractException e){
            throw new CvHandler(e.getMessage());
        }
        return result;
    }
}
