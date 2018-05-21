/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core.ThreadProcess;

import Core.Ocr;
import Detection.Shape.OpenCvShapeDetect;
import ImageBase.Converter.ImageConvert;
import OpenCVHandler.OpencvHandler;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;

/**
 *
 * @author benehiko
 */
public final class OcrProcess extends Thread {

    private final BufferedImage bi;
    private final Ocr ocr;
    private Thread t;
    private Mat m;
    private Rectangle[] shapes;

    public OcrProcess(Ocr o, byte[] b) throws IOException {
        InputStream in = new ByteArrayInputStream(b);
        bi = ImageIO.read(in);
        ocr = o;
    }

    @Override
    public void run() {

        OpenCvShapeDetect ocrShape;
        try {
            Mat img = ImageConvert.bufferedImage2Mat(bi);
            //Image img = new Image(bi);
            img = OpencvHandler.toBinary(img);
            img = OpencvHandler.denoise(img, 20f);
            ocrShape = new OpenCvShapeDetect(img);
            shapes = ocrShape.getRectArray(ocrShape.findContours());
            m = img;
        } catch (IOException ex) {
            System.out.println("Could not do processing, bufferedImage empty\n" + ex.getMessage());
        }

    }

    @Override
    public void start() {
        t = new Thread(this);
        t.start();
        try {
            t.join();
            ocr.cvCallback(this, Arrays.asList(shapes), m);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(OcrProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
