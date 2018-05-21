/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageBase.Template;

import ImageBase.CustomImage;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author benehiko
 */
public final class ImageTemplate extends CustomImage{
    
    private String extracted, province;
    
    public ImageTemplate(BufferedImage bi, String extracted, String province) throws IOException {
        super(bi);
        this.extracted = extracted;
        this.province = province;
    }

    /**
     * @return the extracted
     */
    public String getExtracted() {
        return extracted;
    }

    /**
     * @param extracted the extracted to set
     */
    public void setExtracted(String extracted) {
        this.extracted = extracted;
    }

    /**
     * @return the province
     */
    public String getProvince() {
        return province;
    }

    /**
     * @param province the province to set
     */
    public void setProvince(String province) {
        this.province = province;
    }
    
   
    
}
