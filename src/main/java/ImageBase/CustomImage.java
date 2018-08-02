/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageBase;

import ImageBase.Converter.ImageConvert;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.opencv.core.Mat;

/**
 *
 * @author benehiko
 */
public abstract class CustomImage {
    
    protected BufferedImage bi;
    protected int width, height;
    protected ByteBuffer bb;
    protected Mat matImg;
    
    public CustomImage(BufferedImage bi) throws IOException{
        this.setBi(bi);
    }
    
    protected final void setBi(BufferedImage bi) throws IOException{
        this.bi = bi;
        setWidth(bi.getWidth());
        setHeight(bi.getHeight());
        setBB(ImageConvert.toByteBuffer(bi));
        setMat(ImageConvert.bufferedImage2Mat(bi)); 
        
    }
    
    protected void setWidth(int width){
        this.width = width;
    }
    
    protected void setHeight(int height){
        this.height = height;
    }
    
    protected void setBB(ByteBuffer bb){
        this.bb = bb;
    }
    
    protected void setMat(Mat img){
        this.matImg = img;
    }
    
    protected BufferedImage getBi(){
        return this.bi;
    }
    
    protected Mat getMat(){
        return this.matImg;
    }
    
    protected int getWidth(){
        return this.width;
    }
    
    protected int getHeight(){
        return this.height;
    }
    
    protected ByteBuffer getByteBuffer(){
        return this.bb;
    }
    
}
