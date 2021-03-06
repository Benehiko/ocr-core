
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
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
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
        final List<Rect> r = new ArrayList<>();
        List<MatOfPoint> contour_cache = new ArrayList<>();

        contours.forEach((contour) -> {

            MatOfPoint2f approx = getApprox(contour);
            RotatedRect rect = getRotatedRect(approx);

            if (inScopePercentage(rect, approx)) {

                contour_cache.add(contour);
            }
        });

        contour_cache.removeIf(x -> polygontest(contour_cache, getRotatedRect(getApprox(x))));
       
        contour_cache.forEach((cnt)->{
            MatOfPoint2f approx = getApprox(cnt);
            RotatedRect rect = getRotatedRect(approx);
            r.add(rect.boundingRect());
        });

        List<Rectangle> jrectanlges = rect2rectangle(r);
        System.out.println("Length of original rectangles: "+ jrectanlges.size());
        jrectanlges.removeIf(x-> rectangleInImage(x));
        
        return jrectanlges.toArray(new Rectangle[jrectanlges.size()]);
    }

    private List<Rectangle> rect2rectangle(List<Rect> rects) {
        List<Rectangle> jRects = new ArrayList<>();
        if (rects == null || rects.isEmpty()) {
            return jRects;
        }

        rects.forEach((r) -> {
            Rectangle t = new Rectangle(new java.awt.Point(r.x, r.y), new Dimension(r.width, r.height));
            jRects.add(t);
        });
        return jRects;
    }

    private boolean rectangleInImage(Rectangle jrect){
        double width = this.img.width();
        double height = this.img.height();
        return jrect.contains(width, height);
    }
    
    private List<Rect> joinIntersects(List<Rect> arrRect) {
        if (arrRect.isEmpty()) {
            return null;
        }

        List<Rect> tmp = new ArrayList<>(arrRect);
        List<Rect> result = new ArrayList<>();

        int initialSize = arrRect.size();
        int counter = 0;
        int contained = 0;

        while (true) {
            int tmpSize = tmp.size();
            int i = tmpSize - 1;
            int y = i - 1;

            if (y < 0 || i < 0) {
                break;
            }
            Rect t = this.intersection(tmp.get(i), tmp.get(y));

            if (t != null) {
                tmp.remove(i);
                tmp.remove(y);

                tmp.add(t);

                counter = 0;
                contained++;
            } else {
                result.add(tmp.get(i));
                result.add(tmp.get(y));
                tmp.remove(i);
                tmp.remove(y);
                counter++;
            }

            if (counter > initialSize) {
                break;
            }

        }
        System.out.println("Image contained: " + contained);

        return result;

    }

    /**
     *
     * @param contour
     * @return
     * @throws java.io.IOException
     */
    public Rect getRectFromContour(MatOfPoint contour) throws IOException {
        if (Imgproc.contourArea(contour) > 0) {
            Rect box = getContourSquare(contour);
            if (box != null) {
                double percentage = recMarginPercentage(box);
                if (percentage > 0.2 && percentage < 10) {
                    return box;
                }

            }
        }
        return null;
    }

    private Rect intersection(Rect r1, Rect r2) {

        boolean inters = false;

        int x_min = Math.max(r1.x, r2.x);
        int r1_x = r1.x + r1.width;
        int r2_x = r2.x + r2.width;
        int x_max = Math.min(r1_x, r2_x);

        int y_min = Math.max(r1.y, r2.y);
        int r1_y = r1.y + r1.height;
        int r2_y = r2.y + r2.height;
        int y_max = Math.min(r1_y, r2_y);

        if ((r2_x >= r1.x || r2.x <= r1.x) && (r2_y >= r1.y || r2.y <= r1.y)) {
            inters = true;
        } else if ((r1_x >= r2.x || r1.x <= r2.x) && (r1_y >= r2.y || r1.y <= r2.y)) {
            inters = true;
        }

        if (inters) {
            int x = x_min;
            int y = y_min;
            int width = x_max - x_min;
            int height = y_max - y_min;
            Rect tmp = new Rect(x, y, width, height);
            return tmp;

        }
        return null;
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
    private boolean recAlmostSame(List<Rect> r, Rect r2) {
        if (r.isEmpty() || r2 == null) {
            return false;
        }

        for (int i = 0; i < r.size(); i++) {
            Rect r1 = r.get(i);
            int xdiff = (int) Math.abs(r1.x - r2.x);
            int ydiff = (int) Math.abs(r1.y - r2.y);
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
        RotatedRect rotatedrect = null;
        Mat points = new Mat();
        Rect rect = null;

        if (Imgproc.contourArea(contour) > 0) {
            MatOfPoint2f contour2f = new MatOfPoint2f();
            MatOfPoint approxContour = new MatOfPoint();
            MatOfPoint2f approxcontour2f = new MatOfPoint2f();

            contour.convertTo(contour2f, CvType.CV_32FC2);
            double epsilon = 0.01 * Imgproc.arcLength(contour2f, false);
            Imgproc.approxPolyDP(contour2f, approxcontour2f, epsilon, false);
            approxcontour2f.convertTo(approxContour, CvType.CV_32S);

            rotatedrect = Imgproc.minAreaRect(approxcontour2f);//boundingRect(approxContour);
            Point[] vertices = new Point[4];

            rotatedrect.points(vertices);
            MatOfPoint matPoint = new MatOfPoint(vertices);

            // Imgproc.boxPoints(rotatedrect, points);
            rect = Imgproc.boundingRect(matPoint);

            if (approxContour.size().height == 4) {

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
        Mat tmp = OpencvHandler.toGray(input);
        tmp = OpencvHandler.toCanny(tmp, 50.0);
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

    private MatOfPoint2f getApprox(MatOfPoint contour) {
        MatOfPoint2f contour2f = new MatOfPoint2f();
        //MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxcontour2f = new MatOfPoint2f();

        contour.convertTo(contour2f, CvType.CV_32FC2);
        double epsilon = 0.01 * Imgproc.arcLength(contour2f, false);
        Imgproc.approxPolyDP(contour2f, approxcontour2f, epsilon, false);
        return approxcontour2f;
    }

    private RotatedRect getRotatedRect(MatOfPoint2f approx) {
        RotatedRect rotatedrect = Imgproc.minAreaRect(approx);//boundingRect(approxContour);

        Point[] vertices = new Point[4];

        rotatedrect.points(vertices);
        MatOfPoint matPoint = new MatOfPoint(vertices);

        // Imgproc.boxPoints(rotatedrect, points);
        //rect = Imgproc.boundingRect(matPoint);
        return rotatedrect;
    }

    private boolean inScopePercentage(RotatedRect rect, MatOfPoint2f approx) {
        double img_area = this.img.width() * this.img.height();
        double approx_area = Imgproc.contourArea(approx);

        double angle = rect.angle;
        double perc_area = (approx_area * 100) / img_area;
        double perc_width = (rect.size.height * 100) / this.img.width();
        double perc_height = (rect.size.width * 100) / this.img.height();

        if ((perc_area > 0.15) && (perc_area <= 1) && (perc_height <= 30) && (perc_width <= 30)) {
            if (((angle <= 0) && (angle >= -30)) || ((angle >= -150) && (angle >= -180)) || ((angle >= 0) && (angle <= 30)) || ((angle >= 150) && (angle <= 180))) {
                return true;
            }
        }
        return false;
    }

    private boolean polygontest(List<MatOfPoint> contours, RotatedRect rect) {
        double height = rect.size.height;
        double width = rect.size.width;

        Point[] points = new Point[4];
        rect.points(points);

        for (MatOfPoint cnt : contours) {
            int count = 0;
            for (Point p : points) {
                MatOfPoint2f contour2f = new MatOfPoint2f();
                cnt.convertTo(contour2f, CvType.CV_32FC2);
                if (Imgproc.pointPolygonTest(contour2f, p, false) > -1) {
                    count++;
                }
            }
            if (count == 4) {
                double rect_area = width * height;
                double cnt_area = Imgproc.contourArea(cnt);
                if (cnt_area > rect_area) {
                    return true;
                }
            }
        }
        return false;

    }
}
