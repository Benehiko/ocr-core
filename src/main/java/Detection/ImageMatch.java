/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Detection;

import Display.ImageDisplay;
import ImageProcessing.ImageProcess;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author benehiko
 */
public class ImageMatch {

    private Mat[] templateImg = new Mat[2];

    public ImageMatch() {
        this.preLoad();
    }

    private void preLoad() {
        String[] location = {"./template/tmp1.jpg", "./template/tmp2.jpg"};

        int i = 0;
        for (String loc : location) {
            try {
                templateImg[i] = new Mat();
                templateImg[i] = ImageProcess.bufferedImage2Mat(ImageIO.read(new FileInputStream(new File(loc))));
                i++;
            } catch (IOException e) {
                System.out.println("Could not read image:\n" + loc);
            }
        }
    }

    public Rectangle[] templateMatch(Mat src) throws IOException {
        Mat result = new Mat();
        int[] arrModes = {Imgproc.TM_SQDIFF, Imgproc.TM_SQDIFF_NORMED, Imgproc.TM_CCORR, Imgproc.TM_CCORR_NORMED, Imgproc.TM_CCOEFF, Imgproc.TM_CCOEFF_NORMED};
        String[] arrModesStr = {"TM_SQDIFF", "TM_SQDIFF_NORMED", "TM_CCORR", "TM_CCORR_NORMED", "TM_CCOEFF", "TM_CCOEFF_NORMED"};
        Rectangle[] r = new Rectangle[arrModes.length];
        
        for (int i = 0; i < arrModes.length; i++) {
            for (Mat m : templateImg) {
                Imgproc.matchTemplate(src, m, result, arrModes[i]);
                Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
                MinMaxLocResult mmr = Core.minMaxLoc(result);
                Point matchLoc;
                if (arrModes[i] == Imgproc.TM_SQDIFF || arrModes[i] == Imgproc.TM_SQDIFF_NORMED){
                    matchLoc = mmr.minLoc;
                }else {
                    matchLoc = mmr.maxLoc;
                }
                int x = (int)Math.round(matchLoc.x);
                int y = (int)Math.round(matchLoc.y);
                
                r[i] = new Rectangle(new java.awt.Point(x, y), new Dimension(m.cols(), m.rows()));
                
            }
        }
        return r;
    }
}
