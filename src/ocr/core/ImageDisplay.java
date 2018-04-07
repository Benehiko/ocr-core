/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author benehiko
 */
public class ImageDisplay {
    
    /**
     * Display BufferedImage in JFrame
     * @param i Java BufferedImage
     */
    public static void display(BufferedImage i){
        //CreaetJFrame
            JFrame jframe = new JFrame("Window");
            jframe.setVisible(true);
            jframe.add(new JLabel(new ImageIcon(i)));
            jframe.pack();
            jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
