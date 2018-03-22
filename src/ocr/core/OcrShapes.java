/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.core;

import java.util.ArrayList;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.CV_WHOLE_SEQ;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvSlice;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvCloneImage;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCreateSeq;
import static org.bytedeco.javacpp.opencv_core.cvCvtSeqToArray;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRect;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvSeqPush;
import static org.bytedeco.javacpp.opencv_core.cvSetImageCOI;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvCheckContourConvexity;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourArea;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourPerimeter;
import static org.bytedeco.javacpp.opencv_imgproc.cvDilate;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvPolyLine;
import static org.bytedeco.javacpp.opencv_imgproc.cvPyrDown;
import static org.bytedeco.javacpp.opencv_imgproc.cvPyrUp;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

/**
 *
 * @author benehiko
 * This is a helper class for recognising shapes from an image
 * 
 * Built from source code created by alias saudet 
 * source: https://github.com/bytedeco/javacv/blob/master/samples/Square.java
 */
public class OcrShapes {
    
    private CvMemStorage storage;
    
    public OcrShapes(){
       storage = cvCreateMemStorage(0);
    }
    
    //Public method used to find shapes
    public IplImage recognise_shapes(IplImage img){
        IplImage img_return = null;
        
        //Convert 
        img_return = drawSquares(img, find_squares(img));
        
        return img_return;
    }
    
    public ArrayList<IplImage> get_images(IplImage img){
        ArrayList<IplImage> arrImg = extract_images(img, find_squares(img));
        return arrImg;
    }
    
    
    private double angle(opencv_core.CvPoint pt1, opencv_core.CvPoint pt2, opencv_core.CvPoint pt0) {
        double dx1 = pt1.x() - pt0.x();
        double dy1 = pt1.y() - pt0.y();
        double dx2 = pt2.x() - pt0.x();
        double dy2 = pt2.y() - pt0.y();

        return (dx1*dx2 + dy1*dy2) / Math.sqrt((dx1*dx1 + dy1*dy1) * (dx2*dx2 + dy2*dy2) + 1e-10);
    }
     
    //Method to find all the squares in the image
    private CvSeq find_squares(IplImage img){
        //set some variables
        int thresh = 50;
        int N = 11;
        //get size of image
        opencv_core.CvSize sz = cvSize(img.width() & -2, img.height() & -2);
        IplImage timg = cvCloneImage(img); // make a copy of input image
        
        //create new iplimage from input image size
        IplImage gray = cvCreateImage(sz, 8, 1);
        IplImage pyr = cvCreateImage(cvSize(sz.width()/2, sz.height()/2), 8, 3);
        IplImage tgray = null;
        
        //create the squares vertices
        CvSeq squares = cvCreateSeq(0, Loader.sizeof(CvSeq.class), Loader.sizeof(opencv_core.CvPoint.class), storage);
        
        // select the maximum ROI in the image
        // with the width and height divisible by 2
        cvSetImageROI(timg, cvRect(0, 0, sz.width(), sz.height()));
        
        // down-scale and upscale the image to filter out the noise
        cvPyrDown(timg, pyr, 7);
        cvPyrUp(pyr, timg, 7);
        tgray = cvCreateImage(sz, 8, 1);
        
        // find squares in every color plane of the image
        for (int c = 0; c < 3; c++) {
            // extract the c-th color plane
            cvSetImageCOI(timg, c+1);
            cvCopy(timg, tgray);

            // try several threshold levels
            for (int l = 0; l < N; l++) {
                // hack: use Canny instead of zero threshold level.
                // Canny helps to catch squares with gradient shading
                if (l == 0) {
                    // apply Canny. Take the upper threshold from slider
                    // and set the lower to 0 (which forces edges merging)
                    cvCanny(tgray, gray, 0, thresh, 5);
                    // dilate canny output to remove potential
                    // holes between edge segments
                    cvDilate(gray, gray, null, 1);
                } else {
                    // apply threshold if l!=0:
                    //     tgray(x,y) = gray(x,y) < (l+1)*255/N ? 255 : 0
                    cvThreshold(tgray, gray, (l+1)*255/N, 255, CV_THRESH_BINARY);
                }

                // find contours and store them all as a list
                // Java translation: moved into the loop
                CvSeq contours = new CvSeq();
                cvFindContours(gray, storage, contours, Loader.sizeof(opencv_core.CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

                // test each contour
                while (contours != null && !contours.isNull()) {
                    // approximate contour with accuracy proportional
                    // to the contour perimeter
                    // Java translation: moved into the loop
                    CvSeq result = cvApproxPoly(contours, Loader.sizeof(opencv_core.CvContour.class), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0);
                    // square contours should have 4 vertices after approximation
                    // relatively large area (to filter out noisy contours)
                    // and be convex.
                    // Note: absolute value of an area is used because
                    // area may be positive or negative - in accordance with the
                    // contour orientation
                    if(result.total() == 4 && Math.abs(cvContourArea(result, CV_WHOLE_SEQ, 0)) > 1000 && cvCheckContourConvexity(result) != 0) {

                        // Java translation: moved into loop
                        double s = 0.0, t = 0.0;

                        for(int i = 0; i < 5; i++ ) {
                            // find minimum angle between joint
                            // edges (maximum of cosine)
                            if( i >= 2 ) {
                                t = Math.abs(angle(new opencv_core.CvPoint(cvGetSeqElem(result, i)),
                                        new opencv_core.CvPoint(cvGetSeqElem(result, i-2)),
                                        new opencv_core.CvPoint(cvGetSeqElem(result, i-1))));
                                s = s > t ? s : t;
                            }
                        }

                        // if cosines of all angles are small
                        // (all angles are ~90 degree) then write quandrange
                        // vertices to resultant sequence
                        if (s < 0.3){
                            for(int i = 0; i < 4; i++ ) {
                                cvSeqPush(squares, cvGetSeqElem(result, i));
                            }
                        }
                    }

                    // take the next contour
                    contours = contours.h_next();
                }
            }
        }

        // release all the temporary images
        cvReleaseImage(gray);
        cvReleaseImage(pyr);
        cvReleaseImage(tgray);
        cvReleaseImage(timg);

        return squares;
    }
    
    //Draw Squares on current image
    private IplImage drawSquares(IplImage img, CvSeq squares){
        IplImage cpy = cvCloneImage(img);
        
        
        CvSlice slice = new CvSlice(squares);
        
        for(int i=0; i < squares.total(); i +=4){
            CvPoint rect = new CvPoint(4);
            IntPointer count = new IntPointer(1).put(4);
            // get the 4 corner slice from the "super"-slice
             cvCvtSeqToArray(squares, rect, slice.start_index(i).end_index(i + 4));
             
             
             // draw the square as a closed polyline
             // Java translation: gotcha (re-)setting the opening "position" of the CvPoint sequence thing
             cvPolyLine(cpy, rect.position(0), count, 1, 1, CV_RGB(0,255,0), 3, CV_AA, 0);
            
        }
        return cpy;
    }
    
    //Extract "mini" images from squares
    private ArrayList<IplImage> extract_images(IplImage img, CvSeq squares){
        //source: https://gist.github.com/zudov/4967792
        ArrayList<IplImage> arrImg = new ArrayList();
        
        CvSlice slice = new CvSlice(squares);
        
        for(int i=0; i < squares.total(); i +=4){
            CvPoint rect = new CvPoint(4);
            IntPointer count = new IntPointer(1).put(4);
            // get the 4 corner slice from the "super"-slice
            cvCvtSeqToArray(squares, rect, slice.start_index(i).end_index(i + 4));
             
            // Creating rectangle by which bounds image will be cropped
            CvRect r = new CvRect(rect.position(0));
            // After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
            cvSetImageROI(img, r);
            IplImage cropped = cvCreateImage(cvGetSize(img), img.depth(), img.nChannels());
            // Copy original image (only ROI) to the cropped image
            cvCopy(img, cropped);
            arrImg.add(cropped);
        }
        return arrImg;
    }
}
