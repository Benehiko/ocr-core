/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.benehiko.ocr;

import Core.Ocr;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author benehiko
 */
public class Driver {
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        File f = new File("./images/personalised.jpg");
        Ocr ocr = new Ocr();
        BufferedImage bi = Thumbnails.of(new FileInputStream(f)).scale(1).asBufferedImage();
        ocr.process(bi);
    }
}
