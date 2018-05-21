/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageBase;

/**
 *
 * @author benehiko
 */
public abstract class Imagee {
    
    protected byte[] data;
    protected int width;
    protected int height;
    
    public Imagee(byte[] b, int width, int height){
        this.data = b;
        this.width = width;
        this.height = height;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
    
    public byte[] getBytes(){
        return data;
    }
}
