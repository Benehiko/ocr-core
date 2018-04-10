
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvDilate;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

/**
 * Purpose of ImageProcessing is for a complete list of Image processing within Imageproc
 * This interface is using certain values for optimum output for TEXT extraction. 
 * Since this software package is specified towards the extraction of Vehicle License Plates,
 * this interface has been written for that purpose
 * @author benehiko
 */
public interface ImageProcessing {

    /**
     *
     * @param img_source IplImage source image
     * @return IplImage gray
     */
    public static IplImage grayscale(IplImage img_source){
        IplImage img_gray = cvCreateImage(img_source.cvSize(), IPL_DEPTH_8U, 1);
        //IplImage img_gray = cvCreateImage(img_source.cvSize(), IPL_DEPTH_8U, 1);
        //img_gray.dataOrder(img_source.dataOrder());
        cvCvtColor(img_source, img_gray, CV_RGB2GRAY);
        return img_gray;
    }
    
    /**
     * Convert to binary
     * Source: https://stackoverflow.com/questions/1585535/convert-rgb-to-black-white-in-opencv
     * @param img_gray Gray IplImage
     * @param threshold
     * @return Black and White IplImage
     */
    public static IplImage binary(IplImage img_gray, int threshold){
        IplImage img_bw = cvCreateImage(cvGetSize(img_gray),img_gray.depth(),img_gray.nChannels());
        cvThreshold(img_gray, img_bw, 127, 255, threshold);
        return img_bw;
    }
    
    /**
     * 
     * @param img_source Gray IplImage
     * @param threshold Integer for the threshold type
     * @return IplImage resultant
     */
    public static IplImage threshold(IplImage img_source, int threshold){
        IplImage img_output = cvCreateImage(img_source.cvSize(), IPL_DEPTH_8U, 1);
        cvThreshold(img_source, img_output, 127, 255, threshold);
        return img_output;
    }
    
    /**
     * 
     * @param img_source
     * @param lowThreshold
     * @param ratio
     * @param kernal_size
     * @return 
     */
    public static IplImage canny(IplImage img_source, int lowThreshold, int ratio, int kernal_size){
        IplImage temp = IplImage.create(img_source.cvSize(), IPL_DEPTH_8U, 1);
        //temp = cvCloneImage(img_source);
        cvCanny(img_source, temp, lowThreshold, lowThreshold*ratio, kernal_size);
        return temp;
    }
    
    public static IplImage dilate(IplImage img_source){
        IplImage temp = IplImage.create(img_source.cvSize(), IPL_DEPTH_8U, 1);
        //cvCloneImage(img_source);
        cvDilate(temp, temp, null, 1);
        return temp;
    }
    
    public static IplImage resize(IplImage img_source){
        IplImage resized = IplImage.create(600, 480, img_source.depth(), img_source.nChannels());
        cvResize(img_source,resized);
        return resized;
    }
}
