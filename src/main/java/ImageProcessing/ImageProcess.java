/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageProcessing;

import Display.ImageDisplay;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import net.coobird.thumbnailator.Thumbnails;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

/**
 *
 * @author benehiko
 */
public final class ImageProcess {

    /**
     *
     * @param bi
     * @return
     */
    public static ByteBuffer toByteBuffer(BufferedImage bi) {
        //source: https://stackoverflow.com/a/29341586/9313679
        ByteBuffer byteBuffer;
        DataBuffer dataBuffer = bi.getRaster().getDataBuffer();

        if (dataBuffer instanceof DataBufferByte) {
            byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
            byteBuffer = ByteBuffer.wrap(pixelData);
        } else if (dataBuffer instanceof DataBufferUShort) {
            short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        } else if (dataBuffer instanceof DataBufferShort) {
            short[] pixelData = ((DataBufferShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        } else if (dataBuffer instanceof DataBufferInt) {
            int[] pixelData = ((DataBufferInt) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 4);
            byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
        } else {
            throw new IllegalArgumentException("Not implemented for data buffer type: " + dataBuffer.getClass());
        }
        return byteBuffer;
    }
    
    public static ByteBuffer toByteBuffer(Mat m){
        byte[] bytes = new byte[m.rows() * m.cols() * m.channels()];
        m.get(0,0,bytes);
        return ByteBuffer.wrap(bytes);
    }

    /**
     * Source:
     * https://stackoverflow.com/questions/46492440/opencv-converting-mat-to-grayscale-image
     *
     * @param img
     * @return
     * @throws IOException
     */
    public static BufferedImage mat2BufferedImage(Mat img) throws IOException {
//        MatOfByte mob = new MatOfByte();
//        Imgcodecs.imencode(".jpg", img, mob);
//        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (img.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = img.channels() * img.cols() * img.rows();
        byte[] b = new byte[bufferSize];
        img.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(img.cols(), img.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    /**
     * Source:
     * https://stackoverflow.com/questions/46492440/opencv-converting-mat-to-grayscale-image
     *
     * @param image
     * @return
     * @throws IOException
     */
    public static Mat bufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Thumbnails.of(image).outputFormat("jpg").scale(1).toOutputStream(byteArrayOutputStream);
        //ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
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
     * Denoise removes any unnecessary particles. The higher the intensity the slower the process. 
     * Set intensity between 0 and 100. No more than 100.
     * Source:
     * https://docs.opencv.org/3.3.1/d1/d79/group__photo__denoise.html#ga76abf348c234cecd0faf3c42ef3dc715
     *
     * @param img
     * @return
     */
    public static Mat denoise(Mat img, float intensity) throws IOException {
        Mat imgDenoise = img.clone();
        Photo.fastNlMeansDenoising(img, imgDenoise, intensity, 7, 21);
        //new ImageDisplay("Inside Contour", ImageProcess.mat2BufferedImage(imgDenoise)).display();
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

        //new ImageDisplay("Saturation Test", ImageProcess.mat2BufferedImage(tmp)).display();
        return tmp;
    }

}
