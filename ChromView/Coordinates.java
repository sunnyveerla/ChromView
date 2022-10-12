/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChromView;

/**
 * SNVTF: Single Nucleotide Varation Transcription Factor.
 * Author:Srinivas Veerla (Sunny).
 * Description: 
 * 
 * 
 * 
 * Constructor: 
 * 
 * 
 *
 * Methods:
 * 
 * 
 * 
 *
 */
public class Coordinates implements Comparable<Coordinates> {


    String chr;
    int start;
    int end;
    String arm;
    String g;
    

    public Coordinates(
            String chr,
            int start,
            int end,
            String arm,
            String g
            ) {
      
        this.chr = chr;
        this.start = start;
        this.end = end;
        this.arm = arm;
        this.g = g;
    }

    @Override
    public int compareTo(Coordinates o) {
        if (this.start > o.start) {
            return 1;
        } else if (this.start < o.start) {
            return -1;
        } else {
            return 0;
        }
    }

    

}
