/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.Rectangle;
import java.io.IOException;


import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class OcrCore {
  
  
    private BufferedImage drawn_image;
    public OcrCore(){
        drawn_image = null;
       
    }
    
    
    public String process_image(BufferedImage bi) throws CvHandler, IOException{
        BufferedImage refined_image = OcrPreProcessing.refine_image(bi);
        //ImageDisplay.display(refined_image);
        String extracted_text = "";//Tess.extract(refined_image);
                
        return extracted_text;
    } 
    
    public String[] process_image_array(BufferedImage bi) throws CvHandler, IOException, TesseractException{
        //Get pre-processing shape
        IplImage shape_image = OcrPreProcessing.refine_shape_finder(bi);
        
        //Get slices
        OcrShapes ocr_shapes = new OcrShapes();
        IplImage original_image = OcrConvert.convertBufferedToIpl(bi);
        Rectangle[] rect = ocr_shapes.getRectArray(ocr_shapes.find_contours(shape_image));
        //IplImage drawn_image = ocr_shapes.drawSquares(original_image, ocr_shapes.find_contours(shape_image));
        //this.drawn_image = OcrConvert.convertIplToBuffered(drawn_image);
        
        //Display the drawn image
        //ImageDisplay.display(OcrConvert.convertIplToBuffered(drawn_image));
        //ArrayList<BufferedImage> arrSlices = ocr_shapes.get_images(shape_image, original_image);
        
        //Process each slice for tesseract
        BufferedImage refined = OcrPreProcessing.refine_image(OcrConvert.convertIplToBuffered(original_image));
        String[] extracted_text = new String[rect.length];
        int counter = 0;
        for (Rectangle r : rect){
            //ImageDisplay.display(refined);
            extracted_text[counter] = Tess.extract(refined, r);
            counter++;
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