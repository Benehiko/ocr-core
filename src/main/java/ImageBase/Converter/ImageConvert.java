/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageBase.Converter;

import com.recognition.software.jdeskew.ImageDeskew;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.coobird.thumbnailator.Thumbnails;
import net.sourceforge.tess4j.util.ImageHelper;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author benehiko
 */
public class ImageConvert {

    /**
     * Source: https://stackoverflow.com/a/29341586/9313679
     *
     * @param bi
     * @return
     */
    public static ByteBuffer toByteBuffer(BufferedImage bi) {

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

    /**
     * Source: https://stackoverflow.com/a/25652283
     *
     * @param m
     * @return
     */
    public static ByteBuffer toByteBuffer(Mat m) {
        byte[] bytes = new byte[m.rows() * m.cols() * m.channels()];
        m.get(0, 0, bytes);
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

    public static List<BufferedImage> mat2BufferedImage(List<Mat> img){
        List<BufferedImage> tmp = new ArrayList<>();
        
        img.forEach((entry)->{
            try {
                tmp.add(mat2BufferedImage(entry));
            } catch (IOException ex) {
                Logger.getLogger(ImageConvert.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return tmp;
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

    public static BufferedImage deskew(BufferedImage img) {

        double imageSkewAngle = new ImageDeskew(img).getSkewAngle();
        img = ImageHelper.rotateImage(img, -imageSkewAngle);

        return img;
    }
    
     public static boolean isSkew(BufferedImage img) {
        int MINIMUM_DESKEW_THRESHOLD = 1;
        double imageSkewAngle = new ImageDeskew(img).getSkewAngle();
        return (imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD));
    }
}
