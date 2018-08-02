/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.benehiko.ocr;

import HttpListener.SocketListener;
import cz.adamh.utils.NativeUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;

/**
 *
 * @author benehiko
 */
public class Driver {

    public static void main(String[] args) throws FileNotFoundException, IOException, TesseractException, InterruptedException {
        try{
            String lib = "lib"+Core.NATIVE_LIBRARY_NAME+".so";
            String libname = "/META-INF/lib/"+lib;
            NativeUtils.loadLibraryFromJar(libname);
          
        }catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
        int port = 10000;//Integer.parseInt(args[0]);
        System.err.println("Running server on Port: "+port);
        try(ServerSocket listener = new ServerSocket(10000)) {
            while(true){
                new SocketListener(listener.accept()).start();
            }
        }
    }

}
