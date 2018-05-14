/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author benehiko
 */
public class ImageDisplay extends JFrame {

    Image img;
    String windowName;
    int width, height;

    class CustomPanel extends JPanel{
        
        Image img;
        int x, y;
        
        public CustomPanel(Image img, int x, int y){
            this.img = img;
            this.x = x;
            this.y = y;
        }
        
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this);
        }
        
        @Override
        public Dimension getPreferredSize(){
            return new Dimension(640,480);
        }
    }
    public ImageDisplay(String fName, BufferedImage bi) {
        this.windowName = fName;
        this.img = bi;
        super.setSize(new Dimension(640,480));
        super.setPreferredSize(new Dimension(640, 480));
        width = bi.getWidth();
        height = bi.getHeight();

    }

    public void display() {
        super.setTitle(windowName);
        
        

        double scaleFactor = Math.min(1d, getScaleFactorToFit(new Dimension(width, height), getSize()));

        int scaleWidth = (int) Math.round(width * scaleFactor);
        int scaleHeight = (int) Math.round(height * scaleFactor);

        Image scaled = img.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

        int windowWidth = getWidth() - 1;
        int windowHeight = getHeight() - 1;

        int x = (windowWidth - scaled.getWidth(this) / 2);
        int y = (windowHeight - scaled.getHeight(this) / 2);
        this.add(new CustomPanel(scaled, x, y));
        this.pack();
        this.setVisible(true);
    }

    public double getScaleFactorToFit(Dimension original, Dimension toFit) {

        double dScale = 1d;

        if (original != null && toFit != null) {

            double dScaleWidth = getScaleFactor(original.width, toFit.width);
            double dScaleHeight = getScaleFactor(original.height, toFit.height);

            dScale = Math.min(dScaleHeight, dScaleWidth);

        }

        return dScale;

    }

    public double getScaleFactor(int iMasterSize, int iTargetSize) {

        double dScale = 1;
        if (iMasterSize > iTargetSize) {

            dScale = (double) iTargetSize / (double) iMasterSize;

        } else {

            dScale = (double) iTargetSize / (double) iMasterSize;

        }

        return dScale;

    }
}
