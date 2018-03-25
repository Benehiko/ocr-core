/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_core.cvarrToMat;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import org.opencv.imgproc.Imgproc;

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
            img_result = OcrConvert.convertIplToBuffered(ImageProcessing.binary(ImageProcessing.grayscale(img_src), ADAPTIVE_THRESH_GAUSSIAN_C));
            //img_result = OcrConvert.convertIplToBuffered(ImageProcessing.threshold(ImageProcessing.binary(ImageProcessing.grayscale(img_src)), ADAPTIVE_THRESH_GAUSSIAN_C));
                        
            //cvThreshold(img_gray, img_bw, 128, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);
                        
        }else throw new CvHandler("File does");
        
        //return result image for tesseract
        return img_result;
    }
    
    /**
     * Preprocessing an image from BufferedImage to BufferedImage for text extraction (BETA)
     * @param bi Java BufferedImage
     * @return Java BufferedImage
     * @throws CvHandler 
     */
    public static BufferedImage refine_image_beta(BufferedImage bi) throws CvHandler{
        BufferedImage temp = null;
        
        //work with ipl instead of buffered
        IplImage img_src = OcrConvert.convertBufferedToIpl(bi);
        
        //Get mat
        opencv_core.Mat mat_alter = cvarrToMat(img_src);
        
        //Create mat containers
        opencv_core.Mat mat_blur = new opencv_core.Mat();
        opencv_core.Mat mat_threshold = new opencv_core.Mat();
        opencv_core.Mat mat_output = new opencv_core.Mat();
        
        //Get Size
        opencv_core.Size size = new opencv_core.Size();
        
        //-- test output
        //display_out(convertIpl_bi(new IplImage(mat_alter)));
       
        //Do some funky stuff
        opencv_imgproc.cvtColor(mat_alter, mat_blur, Imgproc.COLOR_RGB2GRAY);
        
        opencv_imgproc.GaussianBlur(mat_blur,mat_threshold,size, 2.5);
        //display_out(convertIpl_bi(new IplImage(mat_threshold)));
        opencv_imgproc.threshold(mat_threshold, mat_threshold, 127, 255, Imgproc.THRESH_BINARY_INV);
        //opencv_imgproc.threshold(mat_threshold, mat_output, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
        //display_out(convertIpl_bi(new IplImage(mat_threshold)));
        //get Iplimage back
        //Source: http://bytedeco.org/javacpp-presets/opencv/apidocs/org/bytedeco/javacpp/opencv_core.IplImage.html
        IplImage img_dest = new IplImage(mat_threshold);
        
        //get Buffered Image for return
        temp = OcrConvert.convertIplToBuffered(img_dest);
        return temp;        
    }
 
    /**
     * Beta OpenCv - in testing for better text extraction
     * Source: https://stackoverflow.com/questions/20277954/crop-image-into-pieces-and-then-join-is-that-possible-using-opencv
     * @param bi Java BufferedImage
     * @return ArrayList<BufferedImage> sliced using shapes extracted from source image
     * @throws CvHandler 
     */
    public static ArrayList<BufferedImage> batch_openCv(BufferedImage bi) throws CvHandler{
        ArrayList<BufferedImage> arrImages = new ArrayList();
        
        //get opencv iplimage
        IplImage img_src = OcrConvert.convertBufferedToIpl(bi);
        
        //get rectangles
        OcrShapes ocrShapes = new OcrShapes();
        IplImage shaped_img = ocrShapes.recognise_shapes(img_src);
        ImageDisplay.display(OcrConvert.convertIplToBuffered(shaped_img));
        
        //get images from shapes
        ArrayList<IplImage> arrExtractedImg = ocrShapes.get_images(img_src);
        for (IplImage img_extract : arrExtractedImg){
            //display_out(convertIpl_bi(img_extract));
            if (img_extract != null) {
                     
            //Convert Source image to Gray image
            IplImage img_gray = cvCreateImage(cvGetSize(img_extract), IPL_DEPTH_8U, 1);
            cvCvtColor(img_extract, img_gray, CV_RGB2GRAY);
                        
            //Convert Gray image to Black and White
            IplImage img_bw = cvCreateImage(cvGetSize(img_gray),IPL_DEPTH_8U,1);
            cvThreshold(img_gray, img_bw, 128, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);
            
            //Resize image
            //Source: https://stackoverflow.com/questions/15839316/cvresize-function-with-javacv
            int resize_amount = 1;            
            opencv_core.CvSize img_size = cvSize(img_bw.width()*(resize_amount), img_bw.height()*(resize_amount));
            IplImage resizeImage = IplImage.create(img_size.width(),img_size.height(),img_bw.depth(),img_bw.nChannels());
            cvResize(img_bw, resizeImage);
            
            //Return Image for Tesseract
            arrImages.add(OcrConvert.convertIplToBuffered(resizeImage));
              
        }else throw new CvHandler("File does");
        }
        return arrImages;
    }
}
