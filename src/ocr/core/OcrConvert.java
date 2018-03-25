/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import java.awt.image.BufferedImage;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 *
 * @author benehiko
 * Conversion class for OpenCV/JavaCV to Java usable formats
 */
public class OcrConvert {
    /**
     * Convert Java BufferedImage to Javacpp.opencv_core.IplImage
     * @param bi Accepts a java BufferedImage for conversion to javacpp.opencv_core.IplImage
     * @return javacpp.opencv_core.IplImage
     */
    public static IplImage convertBufferedToIpl(BufferedImage bi){
        //Convert BufferedImage to IplImage
        //Source: https://stackoverflow.com/questions/8368078/java-bufferedimage-to-iplimage
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
        IplImage temp_img = iplConverter.convert(java2dConverter.convert(bi));
                
        return temp_img; 
    }
    
    /**
     * Convert Javacpp.opencv_core.IplImage to Java BufferedImage 
     * @param img accepts javacpp.opencv_core.IplImage
     * @return java BufferedImage
     */
    public static BufferedImage convertIplToBuffered(IplImage img){
        //Source: https://stackoverflow.com/questions/31873704/javacv-how-to-convert-iplimage-tobufferedimage
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(img);
        BufferedImage img_result = paintConverter.getBufferedImage(frame,1);
        
        return img_result;
    }
}
