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
import org.bytedeco.javacpp.opencv_core.IplImage;

public class OcrCore {
  
  
    private BufferedImage drawn_image;
    public OcrCore(){
        drawn_image = null;
       
    }
    
    /*
    *@param f java file type (image) should be given
    */
    @Deprecated
    public String process_image(File f) throws IOException, CvHandler{
        String extracted_text = "";
        String[] arrExtracted_text;
        
        try{
            //get buffered image from file object f
            BufferedImage img_src = ImageIO.read(f);
            
            //Display source image
            //ImageDisplay.display(img_src);
      
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
            //ImageDisplay.display(OcrConvert.convertIplToBuffered(temp_shaped));
            
            extracted_text = Tess.extract(temp_opencv);
        }catch(IOException e){
            throw new CvHandler(e.getMessage());
        }
        
        return extracted_text;
    }
    
    public String process_image(BufferedImage bi) throws CvHandler, IOException{
        BufferedImage refined_image = OcrPreProcessing.refine_image(bi);
        //ImageDisplay.display(refined_image);
        String extracted_text = Tess.extract(refined_image);
                
        return extracted_text;
    } 
    
    public String[] process_image_array(BufferedImage bi) throws CvHandler, IOException{
        //Get pre-processing shape
        IplImage shape_image = OcrPreProcessing.refine_shape_finder(bi);
        
        //Get slices
        OcrShapes ocr_shapes = new OcrShapes();
        IplImage original_image = OcrConvert.convertBufferedToIpl(bi);
        IplImage drawn_image = ocr_shapes.drawSquares(original_image, ocr_shapes.find_contours(shape_image));
        this.drawn_image = OcrConvert.convertIplToBuffered(drawn_image);
        
        //Display the drawn image
        //ImageDisplay.display(OcrConvert.convertIplToBuffered(drawn_image));
        ArrayList<BufferedImage> arrSlices = ocr_shapes.get_images(shape_image, original_image);
        
        //Process each slice for tesseract
        String[] extracted_text = new String[arrSlices.size()];
        for (BufferedImage slice : arrSlices){
            BufferedImage refined = OcrPreProcessing.refine_image(slice);
            //ImageDisplay.display(refined);
            extracted_text[arrSlices.indexOf(slice)] = Tess.extract(refined);
        }
    
        return extracted_text;
    }
  
    /**
     * 
     * @return
     * @throws CvHandler 
     */
    public BufferedImage get_drawn() throws CvHandler{
        if (this.drawn_image != null){
            return this.drawn_image;
        }else throw new CvHandler("There is no drawn image. First process an image");
    }
    
    
}