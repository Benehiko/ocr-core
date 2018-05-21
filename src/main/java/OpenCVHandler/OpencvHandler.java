/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenCVHandler;

import com.recognition.software.jdeskew.ImageDeskew;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

/**
 *
 * @author benehiko
 */
public final class OpencvHandler {

    

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
     * @return
     * @throws IOException
     */
    public static Mat toCanny(Mat img) throws IOException {
        Mat imgCanny = img.clone();
        double lowThreshold = 50;
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
        Mat imgDenoise = img.clone();
        Photo.fastNlMeansDenoising(img, imgDenoise, intensity, 7, 21);
        return imgDenoise;
    }

    /**
     * Blur image with kernal size 3x3
     *
     * @param img
     * @return
     */
    public static Mat gaussianBlur(Mat img) {
        Mat imgBlur = img.clone();
        Size kSize = new Size(3, 3);
        Imgproc.GaussianBlur(img, imgBlur, kSize, 5);
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
        Imgproc.cvtColor(img, imgBin, Imgproc.THRESH_BINARY_INV);
        return imgBin;
    }

    /**
     * Resize the image
     *
     * @param img
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Mat resize(Mat img, int newWidth, int newHeight) {
        Mat imgResized = new Mat();
        Imgproc.resize(img, imgResized, new Size(newWidth, newHeight));
        return imgResized;
    }

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
            int percentageWidth =  (int) Math.floor(tmp.width * 100 / imgWidth);
            int percentageHeight = (int) Math.floor(tmp.height * 100 / imgHeight);
            int newWidth =  imgWidth * (percentageWidth+10)/100;
            int newHeight = imgHeight * (percentageHeight+10)/100;
            System.out.println("New Crop: "+newWidth+"x"+newHeight);
            
            
            retMat[counter] = crop(img, tmp.x-50, tmp.y-50, newWidth, newHeight);
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
