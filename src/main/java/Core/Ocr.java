/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import Detection.ImageMatch;
import Detection.OcrShapes;
import Display.ImageDisplay;
import ImageBase.Image;
import ImageProcessing.ImageProcess;
import Standard.NumberPlate;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 *
 * @author benehiko
 */
public class Ocr {

    public String[] process(BufferedImage bi) throws IOException, TesseractException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OcrShapes ocrShape;

        /* Convert our image to a workable type */
        Image img = new Image(bi);

        /* Do Template Matching on our Image */
        ImageMatch im = new ImageMatch();
        //Rectangle[] template_rec = im.templateMatch(img.getMat());

        ocrShape = new OcrShapes(img);
        Rectangle[] shapePeucker = ocrShape.getRectArray(ocrShape.findContours());
        img.setMat(ocrShape.drawSquares(shapePeucker));
        new ImageDisplay("PeuckerTest", ImageProcess.mat2BufferedImage(img.getMat())).display();
        
        System.out.println("How many rectangles are found Peucker:" + shapePeucker.length);

        ArrayList<String> extract = new ArrayList<>();
        NumberPlate plate;
        
        for (Rectangle r : shapePeucker) {
            ByteBuffer imgpeucker = ImageProcess.toByteBuffer(ImageProcess.mat2BufferedImage(ImageProcess.denoise(img.getMat(), 20)));
            String tmp = Tess.TesseractHandler.extact(imgpeucker, img.getWidth(), img.getHeight(), r);
            plate = new NumberPlate(tmp);
            if (plate.isPlate())
                extract.add(plate.getPlate());

        }
        

//        for (Rectangle r : shapeHough) {
//            Mat tmp = ocrShape.drawSquares(img.getMat(), r);
//            new ImageDisplay("HoughTest", ImageProcess.mat2BufferedImage(tmp)).display();
//        }

        /* Send the Rectangles to Tesseract with ByteBuffer */
        /* Return the extracted Text */
        return extract.toArray(new String[extract.size()]);
    }
}
