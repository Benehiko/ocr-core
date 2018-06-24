/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tess;

import Core.Ocr;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author benehiko
 */
public class TessThreader extends Thread {
    
    private Thread t;
    private List<BufferedImage> bi;
    private BufferedImage b;
    private final Ocr ocr;
    private List<Rectangle> rect;
    private List<String> extract;

    public TessThreader(Ocr o, List<BufferedImage> b){
        bi = b;
        ocr = o;
        b = null;
    }
    
    public TessThreader(Ocr o, BufferedImage b){
        bi = null;
        this.b = b;
        ocr = o;
    }
    
    public TessThreader(Ocr o, BufferedImage b, List<Rectangle> r){
        this(o,b);
        this.rect = r;
    }
    
    @Override
    public void run(){
        extract = new ArrayList<>();
        
        if (bi == null){
            try {
                extract.addAll(extract(b, rect));
            } catch (IOException | TesseractException ex) {
                System.out.println("Error in TessThreader\n"+ex.getMessage());
            }    
        }else{
            TesseractHandler tess;
            try {
                tess = new TesseractHandler();
                extract = Arrays.asList(tess.extract(bi));
            } catch (IOException ex) {
                System.out.println("Error in TessThreader\n"+ex.getMessage());
            }
            
        }
        
        
       // TessThreader.currentThread().interrupt();
    }
    
    private List extract(BufferedImage b, List<Rectangle> rect) throws IOException, TesseractException{
        TesseractHandler tess = new TesseractHandler();
        List<String> txt = new ArrayList<>();
        
        for (Rectangle r : rect){
            String tmp = tess.extract(b, r);
            if (!txt.contains(tmp))
                txt.add(tess.extract(b,r));
        }
        return txt;
    }
    
    @Override
    public void start(){
        t = new Thread(this);
        t.start();
        try {
            t.join();
            ocr.tessCallback(this, extract);
        } catch (InterruptedException ex) {
            Logger.getLogger(TessThreader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
