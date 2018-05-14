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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 *
 * @author benehiko
 */
public class Ocr {

    public String[] process(BufferedImage bi) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String[] extractedText = null;
        OcrShapes ocrShape = new OcrShapes();

        /* Convert our image to a workable type */
        Image img = new Image(bi);

        /* Do Template Matching on our Image */
        ImageMatch im = new ImageMatch();
        //Rectangle[] template_rec = im.templateMatch(img.getMat());

        Rectangle[] shape_rec = null;
        
        shape_rec = ocrShape.getRectArray(ocrShape.findContours(img.getMat(), true));
        System.out.println("How many rectangles are found:"+shape_rec.length);
        for (Rectangle r : shape_rec) {
            Mat tmp = ocrShape.drawSquares(img.getMat(), r);
            new ImageDisplay("Test", ImageProcess.mat2BufferedImage(tmp)).display();
        }

        /* Send the Rectangles to Tesseract with ByteBuffer */
 /* Return the extracted Text */
        return extractedText;
    }
}
