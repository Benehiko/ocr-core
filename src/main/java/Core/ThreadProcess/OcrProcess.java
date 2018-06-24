/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core.ThreadProcess;

import Core.Ocr;
import Detection.Shape.OpenCvShapeDetect;
import Display.ImageDisplay;
import Enum.Colour;
import ImageBase.Converter.ImageConvert;
import OpenCVHandler.OpencvHandler;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author benehiko
 */
public final class OcrProcess extends Thread {

    //private final BufferedImage bi;
    private final Ocr ocr;
    private Thread t;
    private Mat m;
    private Mat tessImg;
    private Rectangle[] shapes;

    public OcrProcess(Ocr o, byte[] b) throws IOException {
//        InputStream in = new ByteArrayInputStream(b);
//        bi = ImageIO.read(in);
        m = ImageConvert.byte2Mat(b);
        ocr = o;
    }

    @Override
    public void run() {

        OpenCvShapeDetect ocrShape;
        try {
            //Clone the original image to prevent it from being overwritten
            Mat img = m.clone();
            
            System.out.println("Original Image Resolution: "+m.cols()+"x"+m.rows());
            
            Dimension size = OpencvHandler.getAspectRatio(img, new Dimension(1920, 1080));
            img = OpencvHandler.resize(img, size.width, size.height);
            System.out.println("New Image Resolution: "+img.cols()+"x"+img.rows());
            
            Mat imgBlur = OpencvHandler.gaussianBlur(img);
            Mat imgGrey = OpencvHandler.toGrey(imgBlur);
            Mat imgDenoise = OpencvHandler.denoise(imgGrey, 2f);
            Mat imgBinary = OpencvHandler.toBinary(imgGrey);
            
            
            //Get Shapes
            ocrShape = new OpenCvShapeDetect(imgBinary);
            shapes = ocrShape.getRectArray(ocrShape.findContours());
            
            System.out.println("Amount of Rectangles Found: "+shapes.length);
            
            //Resize Image back to original
            tessImg = imgDenoise;
            
        } catch (IOException ex) {
            System.out.println("Could not do processing:\n" + ex.getMessage());
        }

    }

    @Override
    public void start() {
        t = new Thread(this);
        t.start();
        try {
            t.join();
            ocr.cvCallback(this, Arrays.asList(shapes), this.tessImg);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(OcrProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
