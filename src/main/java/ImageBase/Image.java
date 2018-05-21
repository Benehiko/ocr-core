/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageBase;

import ImageBase.Converter.ImageConvert;
import OpenCVHandler.OpencvHandler;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.opencv.core.Mat;

/**
 *
 * @author benehiko
 */
public final class Image extends CustomImage {

    private Rectangle[] arrRect;

    public Image(BufferedImage bi) throws IOException {
        super(bi);
    }

    /**
     * @return the bb
     */
    public ByteBuffer getBb() {
        return bb;
    }

    /**
     * @param bb the bb to set
     */
    public void setBb(ByteBuffer bb) {
        if (bb == null) {
            bb = ImageConvert.toByteBuffer(bi);
        }
        this.bb = bb;
    }

    /**
     * @return the bi
     */
    @Override
    public BufferedImage getBi() {
        return bi; 
    }

    /**
     * @return the width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the arrRect
     */
    public Rectangle[] getArrRect() {
        return arrRect;
    }

    /**
     * @param arrRect the arrRect to set
     */
    public void setArrRect(Rectangle[] arrRect) {
        this.arrRect = arrRect;
    }

    /**
     * @return the mat
     */
    @Override
    public Mat getMat() {
        return matImg;
    }

    /**
     * @param mat the mat to set
     */
    @Override
    public void setMat(Mat mat) {
        this.matImg = mat;
    }
    
    public void crop(Rectangle roi) throws IOException{
        setMat(OpencvHandler.crop(matImg, roi.x, roi.y, roi.width, roi.height));
        setBi(ImageConvert.mat2BufferedImage(matImg));
    }
    
    public void updateBiUsingMat() throws IOException{
        setBi(ImageConvert.mat2BufferedImage(matImg));
    }
    
    public void updateMatUsingBi() throws IOException{
        setMat(ImageConvert.bufferedImage2Mat(bi));
    }

}
