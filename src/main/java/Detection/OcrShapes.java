
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Detection;

import ImageProcessing.ImageProcess;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author benehiko This is a helper class for recognising shapes from an image
 *
 * This is version 2 of OcrShapes and is currently working Built from source
 * code created by alias saudet source:
 * https://github.com/bytedeco/javacv/blob/master/samples/Square.java
 */
public class OcrShapes {

    public OcrShapes() {
    }

    public Mat drawSquares(Mat img, Point startLoc, Point endLoc) {
        Mat imgtmp = img.clone();
        Imgproc.rectangle(imgtmp, startLoc, endLoc, new Scalar(0, 255, 0), 14);

        return imgtmp;
    }

    public Mat drawSquares(Mat img, Rectangle r) {
        Point startLoc = new Point(r.x, r.y);
        Point endLoc = new Point(r.x + r.width, r.y + r.height);
        return this.drawSquares(img, startLoc, endLoc);
    }

    /**
     * Get the rectangle as an array
     *
     * @param contours
     * @return
     */
    public Rectangle[] getRectArray(List<MatOfPoint> contours) {
        ArrayList<Rectangle> r = new ArrayList<>();

        int minArea = 800;
        contours.forEach((contour) -> {
            for (int approxCurve = 2; approxCurve < 6; approxCurve++) {
                if (isContourSquare(contour, approxCurve)) {

                    if (Imgproc.contourArea(contour) > minArea) {
                        Rect rectTmp = Imgproc.boundingRect(contour);
                        Rectangle jRect = new Rectangle(new java.awt.Point(rectTmp.x, rectTmp.y), new Dimension(rectTmp.width, rectTmp.height));
                        if (!recAlmostSame(r, jRect)) {
                            r.add(jRect);
                            System.out.println("Found a rectangle");
                        }

                    }

                }
            }
        });

        return r.toArray(new Rectangle[r.size()]);
    }

    private boolean recAlmostSame(ArrayList<Rectangle> r, Rectangle r2) {
        for (int i = 0; i < r.size(); i++) {
            Rectangle r1 = r.get(i);
            int xdiff = (int) Math.abs(r1.getX() - r2.getX());
            int ydiff = (int) Math.abs(r1.getY() - r2.getY());
            System.out.println("Rectangle test:\nx:" + xdiff + "\ny:" + ydiff);
            if (xdiff < 100 && ydiff < 100) {
                System.out.println("Found same Rectangle");
                return true;
            }

        }
        return false;
    }

    /**
     * Checks if contour is square or not
     *
     * @param contour
     * @return
     */
    private boolean isContourSquare(MatOfPoint contour, int approxCurve) {
        Rect ret = null;

        MatOfPoint2f contour2f = new MatOfPoint2f();
        MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxcontour2f = new MatOfPoint2f();

        contour.convertTo(contour2f, CvType.CV_32FC2);

        Imgproc.approxPolyDP(contour2f, approxcontour2f, approxCurve, true);

        approxcontour2f.convertTo(approxContour, CvType.CV_32S);

        if (approxContour.size().height == 4) {
            ret = Imgproc.boundingRect(approxContour);
        }

        return (ret != null);
    }

    /**
     * Sets the values of pixels in a binary image to their distance to the
     * nearest black pixel.
     *
     * @param input The image on which to perform the Distance Transform.
     * @param externalOnly
     * @return
     */
    public List<MatOfPoint> findContours(Mat input, boolean externalOnly) {
        Mat tmp = ImageProcess.toGray(input);
        tmp = ImageProcess.toCanny(ImageProcess.gaussianBlur(tmp));

        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        contours.clear();

        int mode;
        if (externalOnly) {
            mode = Imgproc.RETR_EXTERNAL;
        } else {
            mode = Imgproc.RETR_LIST;
        }
        int method = Imgproc.CHAIN_APPROX_SIMPLE;
        Imgproc.findContours(tmp, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

}
