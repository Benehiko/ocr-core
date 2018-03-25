/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author benehiko
 */

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.helper.opencv_core.CvArr;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvSize;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_core.cvarrToMat;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_LINEAR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.opencv.imgproc.Imgproc;

public class OcrCore {
  
    private static String config_home = "./configuration/";
    private static File img_src;
    
    public OcrCore(){
        img_src = null;
    }
    /*
    *@param f java file type (image) should be given
    */
    public String process_image(File f) throws IOException, CvHandler{
        String extracted_text = "";
        String[] arrExtracted_text;
        
        try{
            //get buffered image from file object f
            BufferedImage img_src = ImageIO.read(f);
            
            //Display source image
            ImageDisplay.display(img_src);
      
            //Get opencv
            
            //Batch OpenCV is bugged and will not be used right now.
            /*ArrayList<BufferedImage> arrImg_src = batch_openCv(img_src);
            
            arrExtracted_text = new String[arrImg_src.size()];
            for (int i=0; i < arrImg_src.size(); i++){
                display_out(arrImg_src.get(i));
                arrExtracted_text[i] = tesseract(arrImg_src.get(i));
            }
            */
            BufferedImage temp_opencv = OcrPreProcessing.refine_image(img_src);
            OcrShapes ocr_shapes = new OcrShapes();
            IplImage temp_shaped = ocr_shapes.recognise_shapes(OcrConvert.convertBufferedToIpl(img_src));
            ImageDisplay.display(OcrConvert.convertIplToBuffered(temp_shaped));
            
            extracted_text = Tess.extract(temp_opencv);
        }catch(IOException e){
            throw new CvHandler(e.getMessage());
        }
        
        return extracted_text;
    }
    
    public String process_image(BufferedImage bi) throws CvHandler{
        BufferedImage refined_image = OcrPreProcessing.refine_image(bi);
        ImageDisplay.display(refined_image);
        String extracted_text = Tess.extract(refined_image);
                
        return extracted_text;
    } 
  
    
    
}
