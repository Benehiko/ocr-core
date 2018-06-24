
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Detection.Shape;

import Enum.Colour;
import OpenCVHandler.OpencvHandler;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author benehiko This is a helper class for recognising shapes from an image
 *
 * This is version 2 of OpenCvShapeDetect and is currently working Built from
 * source code created by alias saudet source:
 * https://github.com/bytedeco/javacv/blob/master/samples/Square.java
 */
public class OpenCvShapeDetect {

    private Mat img;
    private Rectangle[] rectangles;

    public OpenCvShapeDetect(Mat img) {
        this.img = img;
    }

    /**
     *
     * @return
     */
    public Mat getImgMat() {
        return img;
    }

    /**
     *
     * @param img
     * @param p
     * @param colour
     * @param lineThickness
     * @return
     */
    public Mat drawSquares(Mat img, Point[] p, Colour colour, int lineThickness) {
        Mat imgtmp = img.clone();
        Scalar scal;
        Point startLoc = p[0];
        Point endLoc = p[1];

        switch (colour) {
            case Black:
                scal = new Scalar(0, 0, 0);
                break;
            case Green:
                scal = new Scalar(0, 255, 0);
                break;
            case Blue:
                scal = new Scalar(0, 0, 255);
                break;
            case White:
                scal = new Scalar(255, 255, 255);
                break;
            case Red:
                scal = new Scalar(255, 0, 0);
                break;
            default:
                scal = new Scalar(0, 0, 0);
                break;
        }

        Imgproc.rectangle(imgtmp, startLoc, endLoc, scal, lineThickness);
        return imgtmp;
    }

    /**
     *
     * @param img
     * @param r
     * @param colour
     * @param lineThickness
     * @return
     */
    public Mat drawSquares(Mat img, Rectangle r, Colour colour, int lineThickness) {
        Point[] p = getPointFromRec(r);
        return drawSquares(img, p, colour, lineThickness);
    }

    /**
     *
     * @param img
     * @param r
     * @param colour
     * @param lineThickness
     * @return
     */
    public Mat drawSquares(Mat img, Rectangle[] r, Colour colour, int lineThickness) {
        Mat mTemp = img.clone();

        for (Rectangle tmp : r) {
            mTemp = this.drawSquares(mTemp, tmp, colour, lineThickness);
        }
        return mTemp;
    }

    /**
     *
     * @param r
     * @return
     */
    private Point[] getPointFromRec(Rectangle r) {
        Point startLoc = new Point(r.x, r.y);
        Point endLoc = new Point(r.x + r.width, r.y + r.height);
        Point[] p = new Point[2];
        p[0] = startLoc;
        p[1] = endLoc;
        return p;
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
            Rectangle tmprec = null;
            try {
                tmprec = this.getRectArray(contour);
                if (tmprec != null) {
                    if (!recAlmostSame(r, tmprec)) {
                        r.add(tmprec);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OpenCvShapeDetect.class.getName()).log(Level.SEVERE, null, ex);
            }
            //r.addAll(Arrays.asList(tmprec));
        });

        return r.toArray(new Rectangle[r.size()]);
    }

    /**
     *
     * @param contour
     * @return
     * @throws java.io.IOException
     */
    public Rectangle getRectArray(MatOfPoint contour) throws IOException {
        Rectangle r = null;
        if (Imgproc.contourArea(contour) > 0) {
            Rect box = getContourSquare(contour);
            if (box != null) {
                if (recMarginPercentage(box) > 0.3) {
                    Rect imgrec = new Rect(0, 0, img.cols(), img.rows());

                    if (intersection(imgrec, box)) {
                        Rectangle jRect = new Rectangle(new java.awt.Point(box.x, box.y), new Dimension(box.width, box.height));
                        r = jRect;

                    }
                }

            }
        }
        return r;
    }

    private boolean intersection(Rect img, Rect bounding) {
        double newX = Math.max(img.x, bounding.x);
        double newY = Math.max(img.y, bounding.y);

        double newWidth = Math.min(img.x + img.width, bounding.x + bounding.width) - newX;
        double newHeight = Math.min(img.y + img.height, bounding.y + bounding.height) - newY;

        return !(newWidth <= 0d || newHeight <= 0d);

    }

    /**
     *
     * @param imgWidth
     * @param imgHeight
     * @param r
     * @return
     */
    private double recMarginPercentage(Rect r) {
        double recArea = r.width * r.height;
        double picArea = this.img.height() * this.img.width();
        return (recArea * 100) / picArea;
    }

    /**
     *
     * @param r
     * @param r2
     * @return
     */
    private boolean recAlmostSame(ArrayList<Rectangle> r, Rectangle r2) {
        if (r.isEmpty() || r2 == null) {
            return false;
        }

        for (int i = 0; i < r.size(); i++) {
            Rectangle r1 = r.get(i);
            int xdiff = (int) Math.abs(r1.getX() - r2.getX());
            int ydiff = (int) Math.abs(r1.getY() - r2.getY());
            int widthdiff = (int) Math.abs(r1.width - r2.width);
            int heightdiff = (int) Math.abs(r1.height - r2.height);

            if ((xdiff < 20 && ydiff < 20) && (widthdiff < 20 && heightdiff < 20)) {
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
    private Rect getContourSquare(MatOfPoint contour) throws IOException {
        Rect rect = null;
        if (Imgproc.contourArea(contour) > 0) {
            MatOfPoint2f contour2f = new MatOfPoint2f();
            MatOfPoint approxContour = new MatOfPoint();
            MatOfPoint2f approxcontour2f = new MatOfPoint2f();

            contour.convertTo(contour2f, CvType.CV_32FC2);
            double peri = Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approxcontour2f, 0.04 * peri, true);
            approxcontour2f.convertTo(approxContour, CvType.CV_32S);

            if (approxContour.size().height == 4) {
                rect = Imgproc.boundingRect(approxContour);
            }
        }

        return rect;
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
        this.img = input;
        return findContours();
    }

    public List<MatOfPoint> findContours() throws IOException {
        Mat tmp = this.img.clone();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        contours.clear();
        Imgproc.findContours(tmp, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.printf("Amount of Contours: %d", contours.size());
        return contours;
    }

    /**
     * @deprecated @param input
     * @return
     * @throws IOException
     */
    public MatOfPoint findHoughLines(Mat input) throws IOException {
        Mat tmp = OpencvHandler.toGrey(input);
        tmp = OpencvHandler.toCanny(tmp);
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
