/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author benehiko
 */
public class TestDriver {
    public static void main(String[] args) throws IOException, CvHandler{
       
        OcrCore ocr = new OcrCore();
        BufferedImage img_source = ImageIO.read(new File("./images/low_res_test.png"));
        System.out.println(Arrays.toString(ocr.process_image_array(img_source)));
        //System.out.println((ocr.process_image(new File("./images/low_res_test.png"))));
    }
}
