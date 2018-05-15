## OCR-Core v2
This branch is a divergence from the previous successful build of master branche.

## Reason
The previous project crashes on IplImage conversion to BufferedImage. The shape detection algorithm isn't very effective and it is the only method used to finding License Plates. 

## The Fix
The fix was to convert to MAT image types and using the OpenCV java libraries through Maven. This project also uses the Linux only OpenCV library ".so". 

## Current Methods
The project makes use of template matching and shape detections (improved) with the intent of extracting only a licence plate's text. 

## Shape Detection (Improved)
- Duplicates rectangles are removed
- Uses the [Douglas–Peucker algorithm](https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm) to find rectangles
