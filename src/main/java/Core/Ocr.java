/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import Core.ThreadProcess.OcrProcess;
import ImageBase.Converter.ImageConvert;
import NumberPlateStandard.NumberPlate;
import Tess.TessThreader;
import cz.adamh.utils.NativeUtils;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.scijava.nativelib.NativeLoader;

/**
 *
 * @author benehiko
 */
public class Ocr {

    private List<byte[]> imgData;
    private final List<OcrProcess> ocrObserver;
    private final List<TessThreader> tessObserver;
    private volatile List<String> output;
    private ExecutorService es;
    private boolean canReturn = false;

    public Ocr() {
        this.imgData = new ArrayList<>();
        this.ocrObserver = new ArrayList<>();
        this.tessObserver = new ArrayList<>();
        this.output = new ArrayList<>();
        this.es = Executors.newCachedThreadPool();
    }

    public Ocr(List<BufferedImage> bi) throws IOException {
        this();
        for (BufferedImage b : bi) {
            byte[] imageInByte;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(b, "jpg", baos);
                baos.flush();
                imageInByte = baos.toByteArray();
            }
            this.imgData.add(imageInByte);
        }
        //init();

    }

    public void init() throws IOException {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //NativeUtils.loadLibraryFromJar(Core.NATIVE_LIBRARY_NAME);
//        String osName = System.getProperty("os.name");
//        File classpathRoot = new File(Main.class.getClassLoader().getResource("").getPath());
//        System.out.println(classpathRoot);
//        InputStream in = Main.class.getResourceAsStream(classpathRoot+"/"+Core.NATIVE_LIBRARY_NAME);
//        File fileOut = File.createTempFile("openCvLib",".so");
//        OutputStream out = FileUtils.openOutputStream(fileOut);
//        IOUtils.copy(in, out);
//        in.close();
//        out.close();
//        System.loadLibrary(fileOut.toString());
      //  NativeUtils.loadLibraryFromJar(Core.NATIVE_LIBRARY_NAME);
        
    }

    public void start() throws InterruptedException {
        imgData.forEach((byte[] entry) -> {
            try {
                OcrProcess p = new OcrProcess(this, entry);
                ocrObserver.add(p);
                p.start();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        });

    }

    public void cvCallback(OcrProcess p, List l) {
        ocrObserver.remove(p);
        TessThreader t = null;
        t = new TessThreader(this, ImageConvert.mat2BufferedImage(l));
        tessObserver.add(t);
        t.start();

    }

    public void cvCallback(OcrProcess p, List<Rectangle> r, Mat img) throws IOException {
        ocrObserver.remove(p);
        TessThreader t = new TessThreader(this, ImageConvert.mat2BufferedImage(img), r);
        tessObserver.add(t);
        t.start();
        check();
    }

    public void tessCallback(TessThreader t, List<String> extract) {
        tessObserver.remove(t);
        NumberPlate plate = null;
        for (String tmp : extract) {
            if (!tmp.isEmpty()) {
                plate = new NumberPlate(tmp);
                if (plate.isPlate()) {
                    this.output.add(plate.getPlate());
                }
            }
        }
        check();
    }

    private void check() {
        if (this.ocrObserver.isEmpty() && this.tessObserver.isEmpty()) {
            this.canReturn = true;
        }
    }

    public List<String> getString() throws InterruptedException {
        return this.output;

    }

    public boolean isFinished() {
        return this.canReturn;
    }

}
