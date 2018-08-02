/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NumberPlateStandard;

import java.util.Comparator;

/**
 *
 * @author benehiko
 */
public class MyComparator implements Comparator<String>{

    private final int referenceLength;

    public MyComparator(String reference) {
        super();
        this.referenceLength = reference.length();
    }

    @Override
    public int compare(String t, String t1) {
        int dist1 = Math.abs(t.length() - referenceLength);
        int dist2 = Math.abs(t.length() - referenceLength);

        return dist1 - dist2;
    }
    
}
