/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author benehiko
 */
public class Tess {
        
    public static String extract(BufferedImage bi, Rectangle r) throws CvHandler, IOException, TesseractException{
        ITesseract tess = new Tesseract();
        String tessPath = getTess();
        tess.setPageSegMode(1);
        tess.setLanguage("eng");
        tess.setDatapath(tessPath);
        tess.setOcrEngineMode(TessOcrEngineMode.OEM_DEFAULT);
        tess.setTessVariable("load_system_dawg", "false");
        tess.setTessVariable("load_freq_dawg", "false");
        tess.setTessVariable("tessedit_create_hocr", "0");
        tess.setTessVariable("tessedit_char_whitelist","ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        //tess.setTessVariable("tessedit_char_whitelist","ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        String result = "";
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
        try{
            f = new File(tessdata);
        }catch (Exception e){
                System.out.println(e.toString());
        }
        return f.getAbsolutePath();
    }
}
