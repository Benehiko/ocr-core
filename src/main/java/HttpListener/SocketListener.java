/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HttpListener;

import Core.Ocr;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author benehiko
 */
public class SocketListener extends Thread {

    private Thread t;
    private Socket socket;

    public SocketListener(Socket socket) throws IOException {
        this.socket = socket;
        log("New Connection with client...");
    }

    private void log(String message) {
        System.out.println(message);
    }

    @Override
    public void run() {
            try {
                log("Processing Request...");
                DataInputStream in = new DataInputStream(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                //Send connected message
                out.println("Connected");

                int length = in.readInt();
                byte[] bytes = null;
                if (length > 0) {
                    bytes = new byte[length];
                    in.readFully(bytes, 0, bytes.length);
                    InputStream stream = new ByteArrayInputStream(bytes);
                    BufferedImage img = ImageIO.read(stream);
                    List<BufferedImage> bi = new ArrayList<>();
                    bi.add(img);
                    Ocr ocr = new Ocr(bi);
                    ocr.start();
                    while (true) {
                        if (ocr.isFinished()) {
                            log("OCR task completed. Output: " + Arrays.toString(ocr.getString().toArray()));
                            out.println(Arrays.toString(ocr.getString().toArray()));
                            out.close();
                            in.close();
                            break;
                        }
                    }
                }
            } catch (IOException | InterruptedException ex) {
                log("Some error occured " + ex.getMessage());
            }
        
    }

    @Override
    public void start() {
        t = new Thread(this);
        t.start();
        try {
            t.join();
            System.err.println("Server Thread closed");
        } catch (InterruptedException ex) {
            Logger.getLogger(SocketListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
