/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.image.BufferedImage;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.THRESH_BINARY_INV;

/**
 *
 * @author benehiko
 * OcrPreprocessing will take an RGB image and try get rid of all the unnecessary
 * In the end an image will be returned where text will be visible to Tesseract for processing
 */
public class OcrPreProcessing implements ImageProcessing{
    
    /**
     * Preprocessing an image from BufferedImage to BufferedImage for text extraction
     * @param bi BufferedImage source image
     * @return BufferedImage 
     * @throws error.cvhandler.CvHandler 
    */
    public static BufferedImage refine_image(BufferedImage bi) throws CvHandler{
        BufferedImage img_result = null;
        //get opencv iplimage
        IplImage img_src = OcrConvert.convertBufferedToIpl(bi);
        
        //IplImage img_src = IplImage.create(i.getWidth(),i.getHeight(),IPL_DEPTH_8U,1);
        if (img_src != null) {
                     
            //Convert Source image to Gray image -> Black and White -> Threshold -> Convert to BufferedImage
            IplImage img_gray = ImageProcessing.grayscale(img_src);
            IplImage img_binary = ImageProcessing.binary(img_gray, THRESH_BINARY_INV);
            //IplImage img_threshold = ImageProcessing.threshold(img_binary, CV_THRESH_OTSU);
            img_result = OcrConvert.convertIplToBuffered(img_binary);
                        
        }else throw new CvHandler("Empty Image Passed");
        
        //return result image for tesseract
        return img_result;
    }
    

    /**
     * Refine image for shape detection
     * Source: https://stackoverflow.com/questions/6044119/opencv-cvfindcontours-how-do-i-separate-components-of-a-contour
     * Source: https://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/canny_detector/canny_detector.html
     * @param bi BufferedImage
     * @return 
     */
    public static IplImage refine_shape_finder(BufferedImage bi){
        IplImage img_ipl = OcrConvert.convertBufferedToIpl(bi);
        
        IplImage img_gray = ImageProcessing.grayscale(img_ipl);
        IplImage img_canny = ImageProcessing.canny(img_gray,1,3,5);
        IplImage img_threshold = ImageProcessing.threshold(img_canny, CV_THRESH_OTSU);
        
        //ImageDisplay.display(OcrConvert.convertIplToBuffered(img_threshold));
        /*
        IplImage img_gray = cvCreateImage(img_temp.cvSize(), IPL_DEPTH_8U, 1);
        IplImage img_edges = null;
        cvCvtColor(img_temp, img_gray, opencv_imgproc.CV_BGR2GRAY);
        cvSmooth(img_gray, img_gray);
        cvCanny(img_gray, img_gray, lowThreshold, lowThreshold*ratio, kernal_size);
        //cvDilate(img_gray, img_gray, null, 1);
        //ImageDisplay.display(OcrConvert.convertIplToBuffered(img_gray));
        //cvThreshold(img_gray, img_gray, 230.0, 255.0, opencv_imgproc.THRESH_BINARY);
        */
        return img_threshold;
    }
}