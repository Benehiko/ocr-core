/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tess;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author benehiko
 */
public class TesseractHandler {

    //source: http://tess4j.sourceforge.net/docs/docs-0.4/net/sourceforge/tess4j/Tesseract.html#doOCR(int,%20int,%20java.nio.ByteBuffer,%20java.awt.Rectangle,%20int)
    private static final int BBP = 24;
    private static ITesseract tess;

    public TesseractHandler() throws IOException {
        setEnvironment();
    }

    private void setEnvironment() throws IOException {
        //source: http://www.sk-spell.sk.cx/tesseract-ocr-parameters-in-302-version
        tess = new Tesseract();
        String tessPath = getTessDataPath();
        /*
        source:https://stackoverflow.com/a/8148874
        0 = Orientation and script detection (OSD) only.
        1 = Automatic page segmentation with OSD.
        2 = Automatic page segmentation, but no OSD, or OCR
        3 = Fully automatic page segmentation, but no OSD. (Default)
        4 = Assume a single column of text of variable sizes.
        5 = Assume a single uniform block of vertically aligned text.
        6 = Assume a single uniform block of text.
        7 = Treat the image as a single text line.
        8 = Treat the image as a single word.
        9 = Treat the image as a single word in a circle.
        10 = Treat the image as a single character.
         */
        tess.setPageSegMode(7);
        tess.setLanguage("eng");
        tess.setDatapath(tessPath);
        tess.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED);

        //Turn off dictionaries:
        tess.setTessVariable("load_system_dawg", "false");
        tess.setTessVariable("load_freq_dawg", "false");
        tess.setTessVariable("load_punc_dawg", "false");
        tess.setTessVariable("load_number_dawg", "false");
        tess.setTessVariable("load_unambig_dawg", "false");
        tess.setTessVariable("load_bigram_dawg", "false");
        tess.setTessVariable("load_fixed_length_dawgs", "false");

        //Text settings:
        tess.setTessVariable("tessedit_create_hocr", "0");
        tess.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    public String[] extract(List<BufferedImage> bi) {

        String[] retStr = new String[bi.size()];

        bi.forEach((bi1) -> {
            try {
                retStr[bi.indexOf(bi1)] = tess.doOCR(bi1);
            } catch (TesseractException ex) {
                System.out.println("Tesseract error\n"+ex.getMessage());
            }
        });
        return retStr;
    }

    /**
     *
     * @param bi
     * @return
     * @throws IOException
     * @throws TesseractException
     */
    public String[] extract(BufferedImage[] bi) throws IOException, TesseractException {
        String[] retStr = new String[bi.length];

        int counter = 0;
        for (BufferedImage tmp : bi) {
            retStr[counter] = tess.doOCR(tmp);
        }
        return retStr;
    }

    /**
     *
     * @param bi
     * @return
     * @throws IOException
     * @throws TesseractException
     */
    public String extract(BufferedImage bi) throws IOException, TesseractException {
        return tess.doOCR(bi);
    }

    /**
     *
     * @param bb
     * @param width
     * @param height
     * @param r
     * @return
     * @throws TesseractException
     * @throws IOException
     */
    public String extact(ByteBuffer bb, int width, int height, Rectangle r) throws TesseractException, IOException {
        return tess.doOCR(width, height, bb, r, BBP);
    }

    /**
     *
     * @param bi
     * @param r
     * @return
     * @throws IOException
     * @throws TesseractException
     */
    public String extract(BufferedImage bi, Rectangle r) throws IOException, TesseractException {
        return tess.doOCR(bi, r);
    }

    /**
     *
     * @return @throws MalformedURLException
     * @throws IOException
     */
    private String getTessDataPath() throws MalformedURLException, IOException {
        File f = null;
        //URL classPath = Tess.class.getResource("Tess.class");

        String tessdata = "tessdata";
        //tessdata = tessdata.replace("Tess.class", "/tessdata");
        if (!new File(tessdata + "/eng.traineddata").exists() || !new File(tessdata + "/osd.traineddata").exists()) {

            try {
                new File(tessdata).mkdirs();
                System.out.println("Downloading eng.traineddata...");
                URL url = new URL("https://github.com/tesseract-ocr/tessdata/raw/master/eng.traineddata");
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(tessdata + "/eng.traineddata");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                System.out.println("Downloading osd.traineddata...");
                url = new URL("https://github.com/tesseract-ocr/tessdata/raw/master/osd.traineddata");
                rbc = Channels.newChannel(url.openStream());
                fos = new FileOutputStream(tessdata + "/osd.traineddata");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        try {
            f = new File(tessdata);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return f.getAbsolutePath();
    }
}
