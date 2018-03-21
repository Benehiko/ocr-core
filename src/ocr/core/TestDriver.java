/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import error.cvhandler.CvHandler;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author benehiko
 */
public class TestDriver {
    public static void main(String[] args) throws IOException, CvHandler{
        OcrCore ocr = new OcrCore();
        System.out.println(ocr.process_image(new File("./images/nplates.jpg")));
    }
}
