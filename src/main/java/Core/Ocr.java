/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import Core.ThreadProcess.OcrProcess;
import ImageBase.Converter.ImageConvert;
import NumberPlateStandard.NumberPlate;
import Tess.TessThreader;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.opencv.core.Mat;

/**
 *
 * @author benehiko
 */
public class Ocr {

    private List<byte[]> imgData;
    private final List<OcrProcess> ocrObserver;
    private final List<TessThreader> tessObserver;
    private volatile List<String> output;
    private ExecutorService es;
    private boolean canReturn = false;

    public Ocr() {
        this.imgData = new ArrayList<>();
        this.ocrObserver = new ArrayList<>();
        this.tessObserver = new ArrayList<>();
        this.output = new ArrayList<>();
        this.es = Executors.newCachedThreadPool();
    }
    
    /**
     * 
     * @param imgbytes 
     */
    public Ocr(List<byte[]> imgbytes){
        this();
        for (byte[] img : imgbytes){
            this.imgData.add(img);
        }
    }

    /**
     * 
     * @throws InterruptedException 
     */
    public void start() throws InterruptedException {
        imgData.forEach((byte[] entry) -> {
            try {
                OcrProcess p = new OcrProcess(this, entry);
                ocrObserver.add(p);
                p.start();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        });

    }

    /**
     * 
     * @param p
     * @param l 
     */
    public void cvCallback(OcrProcess p, List l) {
        ocrObserver.remove(p);
        TessThreader t = null;
        t = new TessThreader(this, ImageConvert.mat2BufferedImage(l));
        tessObserver.add(t);
        t.start();

    }

    /**
     * 
     * @param p
     * @param r
     * @param img
     * @throws IOException 
     */
    public void cvCallback(OcrProcess p, List<Rectangle> r, Mat img) throws IOException {
        ocrObserver.remove(p);
        TessThreader t = new TessThreader(this, ImageConvert.mat2BufferedImage(img), r);
        tessObserver.add(t);
        t.start();
        check();
    }

    /**
     * 
     * @param t
     * @param extract 
     */
    public void tessCallback(TessThreader t, List<String> extract) {
        tessObserver.remove(t);
        NumberPlate plate = null;
        for (String tmp : extract) {
            if (!tmp.isEmpty()) {
                plate = new NumberPlate(tmp);
                if (plate.isPlate()) {
                    this.output.add(plate.getPlate());
                }
            }
        }
        if (!output.isEmpty()){
            output.sort((s1,s2)-> Integer.compare(s1.length(),s2.length()));
            System.out.println("Sorted: "+Arrays.toString(this.output.toArray()));
            String tmp = output.get(output.size()-1);
            output.clear();
            output.add(tmp);
        }
        check();
    }

    /**
     * Check if the observer is empty
     */
    private void check() {
        if (this.ocrObserver.isEmpty() && this.tessObserver.isEmpty()) {
            this.canReturn = true;
        }
    }

    /**
     * 
     * @return
     * @throws InterruptedException 
     */
    public List<String> getString() throws InterruptedException {
        return this.output;

    }

    /**
     * 
     * @return 
     */
    public boolean isFinished() {
        return this.canReturn;
    }

}
