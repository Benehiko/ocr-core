/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;



/**
 *
 * @author benehiko
 */

import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY_INV;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
public class OcrCore {
  
    private static String config_home = "./configuration/";
    private static File img_src;
    
    public OcrCore(){
        img_src = null;
    }
    
    public String process_image(File f) throws IOException, CvHandler{
        String extracted_text = "";
        try{
            BufferedImage img_src = ImageIO.read(f);
            //display_out(img_src);
            extracted_text = tesseract(openCv(img_src));
        }catch(IOException e){
            throw new CvHandler(e.getMessage());
        }
        
        return extracted_text;
    }
    
    private BufferedImage openCv(BufferedImage i) throws CvHandler{
        BufferedImage img_result = null;
        
        //Convert BufferedImage to IplImage
        //Source: https://stackoverflow.com/questions/8368078/java-bufferedimage-to-iplimage
        ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
        IplImage img_src = iplConverter.convert(java2dConverter.convert(i));
        
        
        //IplImage img_src = IplImage.create(i.getWidth(),i.getHeight(),IPL_DEPTH_8U,1);
        if (img_src != null) {
                     
            //Convert Source image to Gray image
            opencv_core.IplImage img_gray = cvCreateImage(cvGetSize(img_src), IPL_DEPTH_8U, 1);
            cvCvtColor(img_src, img_gray, CV_RGB2GRAY);
            
            //get rectangles
            OcrShapes ocrShapes = new OcrShapes();
            IplImage shaped_img = ocrShapes.recognise_shapes(img_src);
            display_out(convertIpl_bi(shaped_img));
            
            //Convert Gray image to Black and White
            opencv_core.IplImage img_bw = cvCreateImage(cvGetSize(img_gray),IPL_DEPTH_8U,1);
            cvThreshold(img_gray, img_bw, 128, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);
            
            //Return Image for Tesseract
            img_result = convertIpl_bi(img_bw);
              
        }else throw new CvHandler("File does");
        
        return img_result;
    }
    
    //convert opencv iplimage to java bufferedimage
    private BufferedImage convertIpl_bi(opencv_core.IplImage img){
        //Source: https://stackoverflow.com/questions/31873704/javacv-how-to-convert-iplimage-tobufferedimage
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(img);
        BufferedImage img_result = paintConverter.getBufferedImage(frame,1);
        
        return img_result;
    }
    
    private String tesseract(BufferedImage i) throws CvHandler{
            //File imageFile = new File("./images/test_clean_copy.jpg");
            ITesseract tess = new Tesseract();
            tess.setDatapath("./tessdata/");
            tess.setTessVariable("psm", "13");
            String result = "";

            try{
                result = tess.doOCR(i);
            }catch(TesseractException e){
                throw new CvHandler(e.getMessage());
            }
            return result;
    }
    
    private void display_out(BufferedImage i){
        //CreaetJFrame
            JFrame jframe = new JFrame("Window");
            jframe.setVisible(true);
            jframe.add(new JLabel(new ImageIcon(i)));
            jframe.pack();
            jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
