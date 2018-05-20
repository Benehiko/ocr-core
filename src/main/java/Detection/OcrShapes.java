
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Detection;

import Display.ImageDisplay;
import ImageBase.Image;
import ImageProcessing.ImageProcess;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private Image img;
    private Rectangle[] rectangles;

    public OcrShapes(Image img) {
        this.img = img;
    }

    /**
     *
     * @param img
     * @param startLoc
     * @param endLoc
     * @return
     */
    public Mat drawSquares(Mat img, Point startLoc, Point endLoc) {
        Mat imgtmp = img.clone();
        Imgproc.rectangle(imgtmp, startLoc, endLoc, new Scalar(255, 255, 255), 14);

        return imgtmp;
    }

    /**
     *
     * @param img
     * @param r
     * @return
     */
    public Mat drawSquares(Mat img, Rectangle r) {
        this.img.setMat(img);
        return drawSquares(r);
    }

    /**
     *
     * @param img
     * @param r
     * @return
     */
    public Mat drawSquares(Mat img, Rectangle[] r) {
        this.img.setMat(img);
        return this.drawSquares(r);
    }

    /**
     *
     * @param r
     * @return
     */
    public Mat drawSquares(Rectangle r) {
        Point startLoc = new Point(r.x, r.y);
        Point endLoc = new Point(r.x + r.width, r.y + r.height);
        return this.drawSquares(this.img.getMat().clone(), startLoc, endLoc);
    }

    /**
     *
     * @param rec
     * @return
     */
    public Mat drawSquares(Rectangle[] rec) {
        for (Rectangle r : rec) {
            this.img.setMat(this.drawSquares(r));
        }
        return this.img.getMat();
    }

    /**
     * Get the rectangle as an array
     *
     * @param contours
     * @return
     */
    public Rectangle[] getRectArray(List<MatOfPoint> contours) {
        ArrayList<Rectangle> r = new ArrayList<>();
        contours.forEach((contour) -> {
            Rectangle[] tmprec = this.getRectArray(contour);
            for (Rectangle re : tmprec) {
                r.add(re);
            }
        });

        return r.toArray(new Rectangle[r.size()]);
    }

    /**
     * 
     * @param contour
     * @return 
     */
    public Rectangle[] getRectArray(MatOfPoint contour) {
        ArrayList<Rectangle> r = new ArrayList<>();

        int minArea = 1000;
        for (int approxCurve = 2; approxCurve < 20; approxCurve++) {
            if (isContourSquare(contour, approxCurve)) {

                if (Imgproc.contourArea(contour) > minArea) {
                    Rect rectTmp = Imgproc.boundingRect(contour);
                    Rectangle jRect = new Rectangle(new java.awt.Point(rectTmp.x, rectTmp.y), new Dimension(rectTmp.width, rectTmp.height));
                    if (!recAlmostSame(r, jRect)) {
                        if (recMarginPercentage(img.getWidth(), img.getHeight(), jRect) > 0.3) {
                            r.add(jRect);
                            System.out.println("Found a rectangle");
                        }
                    }

                }

            }
        }
        return r.toArray(new Rectangle[r.size()]);
    }

    /**
     *
     * @param imgWidth
     * @param imgHeight
     * @param r
     * @return
     */
    private double recMarginPercentage(int imgWidth, int imgHeight, Rectangle r) {
        double recArea = r.width * r.height;
        double picArea = imgHeight * imgWidth;
        double percentage = (recArea * 100) / picArea;
        return percentage;
    }

    /**
     *
     * @param r
     * @param r2
     * @return
     */
    private boolean recAlmostSame(ArrayList<Rectangle> r, Rectangle r2) {
        for (int i = 0; i < r.size(); i++) {
            Rectangle r1 = r.get(i);
            int xdiff = (int) Math.abs(r1.getX() - r2.getX());
            int ydiff = (int) Math.abs(r1.getY() - r2.getY());
            System.out.println("Rectangle test:\nx:" + xdiff + "\ny:" + ydiff);
            if (xdiff < 20 && ydiff < 20) {
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
     * nearest black pixel. Source:
     * http://opencvexamples.blogspot.com/2013/09/find-contour.html
     *
     * @param input The image on which to perform the Distance Transform.
     * @param externalOnly
     * @return
     * @throws java.io.IOException
     */
    public List<MatOfPoint> findContours(Mat input) throws IOException {
        this.img.setMat(input);
        return findContours();
    }

    public List<MatOfPoint> findContours() throws IOException {
        Mat tmp = this.img.getMat().clone();
        tmp = ImageProcess.toBinary(tmp);
        tmp = ImageProcess.denoise(tmp, 20f);
        tmp = ImageProcess.gaussianBlur(tmp);
        tmp = ImageProcess.toCanny(tmp);

        new ImageDisplay("Inside Contour", ImageProcess.mat2BufferedImage(tmp)).display();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        contours.clear();
        Imgproc.findContours(tmp, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        return contours;
    }

    public MatOfPoint findHoughLines(Mat input) throws IOException {
        Mat tmp = ImageProcess.toGray(input);
        tmp = ImageProcess.toCanny(tmp);
        int threshold = 70;
        int minLineSize = 30;
        int lineGap = 20;

        List<MatOfPoint> lines = new ArrayList<>();
        Mat vector = new Mat();

        Imgproc.HoughLinesP(tmp, vector, 1, Math.PI / 180, threshold, minLineSize, lineGap);
        Point p1 = null, p2 = null;

        for (int i = 0; i < vector.cols(); i++) {
            double[] val = vector.get(0, i);

            p1 = new Point(val[0], val[1]);
            p2 = new Point(val[2], val[3]);

        }
        MatOfPoint mop = new MatOfPoint(p1, p2);
        return mop;
    }

}
