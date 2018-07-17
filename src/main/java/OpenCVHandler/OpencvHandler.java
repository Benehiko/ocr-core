/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenCVHandler;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import org.opencv.photo.Photo;

/**
 *
 * @author benehiko
 */
public final class OpencvHandler {

    
    /**
     * 
     * @param img
     * @return 
     */
    public static Mat morph(Mat img){
        Mat morph = img.clone();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(img, morph, Imgproc.MORPH_OPEN, kernel);
        return morph;
    }
    
    /**
     * 
     * @param img
     * @return 
     */
    public static Mat dilate(Mat img){
        Mat dilate = img.clone();
        Mat kernel = Imgproc.getStructuringElement(1, new Size(5,5));
        Imgproc.dilate(img, dilate, kernel, new Point(0,0), 1);
        return dilate;
    }
    
    /**
     * 
     * @param img
     * @return 
     */
    public static Mat otsuBinary(Mat img){
        Mat blur = gaussianBlur(img);
        Mat thresh = img.clone();
        Imgproc.threshold(blur, thresh, 240, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        return thresh;
    }
    /**
     * 
     * @param img
     * @return 
     */
    public static Mat adaptiveBinary(Mat img){
        Mat bin = img.clone();
        Imgproc.adaptiveThreshold(img, bin, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
        return bin;
    }
    
    /**
     * 
     * @param img
     * @return 
     */
    public static Mat equaHist(Mat img){
        Mat equalised = img.clone();
        CLAHE clahe = Imgproc.createCLAHE(3.0, new Size(15, 15));
        clahe.apply(img, equalised);
        return equalised;
    }
    
    /**
     * Convert to gray
     *
     * @param img
     * @return
     */
    public static Mat toGray(Mat img) {
        Mat imgGray = img.clone();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        return imgGray;
    }

    /**
     * Canny edges
     *
     * @param img
     * @param lowThreshold
     * @return
     * @throws IOException
     */
    public static Mat toCanny(Mat img, double lowThreshold) throws IOException {
        Mat imgCanny = img.clone();
        double highThreshold = lowThreshold * 3;
        Imgproc.Canny(img, imgCanny, lowThreshold, highThreshold);
        return imgCanny;
    }

    /**
     * Denoise removes any unnecessary particles. The higher the intensity the
     * slower the process. Set intensity between 0 and 100. No more than 100.
     * Source:
     * https://docs.opencv.org/3.3.1/d1/d79/group__photo__denoise.html#ga76abf348c234cecd0faf3c42ef3dc715
     *
     * @param img
     * @param intensity
     * @return
     * @throws java.io.IOException
     */
    public static Mat denoise(Mat img, float intensity) throws IOException {
        Mat clone = img.clone();
        Photo.fastNlMeansDenoising(clone, clone, intensity, 7, 12); //7, 21
        return clone;
    }

    /**
     * Blur image with kernal size 3x3
     *
     * @param img
     * @return
     */
    public static Mat gaussianBlur(Mat img) {
        Mat imgBlur = img.clone();
        Size kSize = new Size(5, 5);
        Imgproc.GaussianBlur(img, imgBlur, kSize, 0);
        return imgBlur;
    }

    /**
     * Convert Gray to Binary
     *
     * @param img
     * @return
     */
    public static Mat toBinary(Mat img) {
        Mat imgBin = img.clone();
        //Imgproc.cvtColor(img, imgBin, Imgproc.THRESH_BINARY_INV);
        Imgproc.threshold(img, imgBin, 127, 255, 0);
        return imgBin;
    }

    /**
     * Resize the image
     *
     * @param img
     * @param boundWidth
     * @param boundHeight
     * @return
     */
    public static Mat resize(Mat img, int boundWidth, int boundHeight) {
        Mat imgResized = new Mat();
        //Dimension size = getAspectRatio(img, new Dimension(boundWidth, boundHeight));
        Imgproc.resize(img, imgResized, new Size(boundWidth, boundHeight), 0, 0, INTER_CUBIC);
        return imgResized;
    }
    
    public static Dimension getAspectRatio(Mat imgsrc, Dimension bound){
        
        int imgWidth = imgsrc.width();
        int imgHeight = imgsrc.height();
        int newWidth = imgWidth;
        int newHeight = imgHeight;

        if (imgWidth > bound.width) {
            //scale width to fit
            newWidth = bound.width;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * imgHeight) / imgWidth;

        }
        // then check if we need to scale even with the new height
        if (imgHeight > bound.height) {
            //scale height to fit instead
            newHeight = bound.height;
            //scale width to maintain aspect ratio
            newWidth = (newHeight * imgWidth) / imgHeight;
        }
        return new Dimension(newWidth, newHeight);
    }

    /**
     *
     * @param img
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static Mat crop(Mat img, int x, int y, int width, int height) {
        Rect roi = new Rect(x, y, width, height);
        Mat imgCropped = new Mat(img, roi);
        return imgCropped;
    }

    public static Mat[] crop(Mat img, Rectangle[] r) {
        Mat[] retMat = new Mat[r.length];
        int imgWidth = img.width();
        int imgHeight = img.height();

        int counter = 0;
        for (Rectangle tmp : r) {
            int percentageWidth = (int) Math.floor(tmp.width * 100 / imgWidth);
            int percentageHeight = (int) Math.floor(tmp.height * 100 / imgHeight);
            int newWidth = imgWidth * (percentageWidth + 10) / 100;
            int newHeight = imgHeight * (percentageHeight + 10) / 100;
            System.out.println("New Crop: " + newWidth + "x" + newHeight);

            retMat[counter] = crop(img, tmp.x - 50, tmp.y - 50, newWidth, newHeight);
            counter++;
        }
        return retMat;
    }

    public static Mat saturate(Mat img) throws IOException {
        double alpha = 1.0;
        int beta = 50;
        Mat tmp = Mat.zeros(img.size(), img.type());

        for (int y = 0; y < img.rows(); y++) {
            for (int x = 0; x < img.cols(); x++) {
                double r = alpha * (img.get(y, x)[0]) + beta;
                double g = alpha * (img.get(y, x)[1]) + beta;
                double b = alpha * (img.get(y, x)[2]) + beta;
                tmp.put(y, x, new double[]{r, g, b});
            }
        }

        //new ImageDisplay("Saturation Test", OpencvHandler.mat2BufferedImage(tmp)).display();
        return tmp;
    }

}
