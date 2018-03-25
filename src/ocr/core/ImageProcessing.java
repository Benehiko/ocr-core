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
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
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
        //Convert Source image to Gray image
        IplImage img_gray = cvCreateImage(cvGetSize(img_source), IPL_DEPTH_8U, 1);
        cvCvtColor(img_source, img_gray, CV_RGB2GRAY);
        return img_gray;
    }
    
    /**
     * Convert to binary
     * Source: https://stackoverflow.com/questions/1585535/convert-rgb-to-black-white-in-opencv
     * @param img_gray Gray IplImage
     * @return Black and White IplImage
     */
    public static IplImage binary(IplImage img_gray, int threshold){
        
        ImageDisplay.display(OcrConvert.convertIplToBuffered(img_gray));
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
        IplImage img_output = cvCreateImage(cvGetSize(img_source), IPL_DEPTH_8U,1);
        cvThreshold(img_source, img_output, 127, 255, threshold);
        return img_output;
    }
}
