/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChromView;

import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFGraphics;
import com.qoppa.pdfWriter.PDFPage;
import java.awt.Color;
import java.awt.Font;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 *
 * @author klin-sve
 */
public class CreateConsistencyPlot {

    public CreateConsistencyPlot(Map<String, List<int[]>> cs, int cc, int max, String title, String fileName) {
        int x = 10; //distance between the columns
        int tx = cc * 35; //X length
//        System.out.println(tx);
        int ty = 450; //Y length
        int by = 250;
        int size = by / max;
        int py = 850;
        //Prepare PDF Document
        PDFDocument pdfDoc = new PDFDocument();
        Paper pp = new Paper();
        pp.setSize(tx, py);
        pp.setImageableArea(0, 0, tx, py);
        PageFormat pf = new PageFormat();
        pf.setPaper(pp);
        // create a new page and add it to the PDF (important!)
        PDFPage page = pdfDoc.createPage(pf);
        pdfDoc.addPage(page);
// get graphics from the page
        // this object is a Graphics2D Object and you can draw anything 
        // you would draw on a Graphics2D
        PDFGraphics g2d = (PDFGraphics) page.createGraphics();

        //Drawing starts......
        g2d.setColor(Color.BLACK);
        //g2d.drawString("Sunny", x, 50);
//        by = by-100;
        g2d.translate(x, ty - 150);
        g2d.setFont(new Font("SUNNY", Font.BOLD, 8));
        g2d.drawString(title, (tx / 2) - title.length(), -(by + 10));
        g2d.drawLine(0, 0, 0, -(by + 2 * max));
        // g2d.drawLine(0, 0, tx, 0);
        for (int i = 0; i <= max; i++) {
            g2d.drawString("" + i, -x, -(int) (((float) (i) / max) * by));
            //System.out.println(i);
        }
        //Percentage of samples
        int offset = 80;
        int sty = by - offset;
        g2d.drawLine(0, by, 0, offset);
        title = "Samples involved (%)";
        g2d.drawString(title, (tx / 2) - title.length(), offset - 20);
        //g2d.drawLine(0, by, tx, by);
        for (int i = 0; i <= 100; i += 10) {
            g2d.drawString("" + i, -x, by - ((int) (((float) (i) / 100) * sty)));
            //System.out.println(i);
        }
        //Sihouette Values 
        int kty = by * 2;
        int hty = kty - by - offset;
        g2d.drawLine(0, kty, 0, by + offset);
        title = "Silhouette Score (%)";
        g2d.drawString(title, (tx / 2) - title.length(), by + offset - 20);
        //g2d.drawLine(0, by, tx, by);
        for (int i = 0; i <= 100; i += 10) {
            g2d.drawString("" + i, -x, kty - ((int) (((float) (i) / 100) * hty)));
            //System.out.println(i);
        }
        Object obj[] = cs.keySet().toArray();
        float f[] = new float[obj.length];
        for (int j = 0; j < obj.length; j++) {
            f[j] = Float.parseFloat(obj[j].toString());

        }
        Arrays.sort(f);
        int dy = (size + 2);
        int dx = 21;
        Color c[] = new ToolBox().getDifferentColors(50);
        boolean flag = false;
        for (float o : f) {
//System.out.println(o);
            List v = cs.get("" + o);
            int cl = v.size();
//             System.out.println(cl);
            g2d.setColor(Color.BLACK);
//            int[] p = (int[]) v.get(0);
//            if (p[0] == 0 && !flag) {
//                continue;
//            } else {
//                flag = true;
//            }
            g2d.drawString("" + o, x, 20);
            g2d.drawString("" + o, x, by + 20);
            g2d.drawString("" + o, x, kty + 20);

            for (int i = 0; i < cl; i++) {
                int[] cp = (int[]) v.get(i);
                if (cp[0] != 0) {
                    int h = (int) ((cp[0] / (float) max) * by);
//                        System.out.println(cp[0]);
                    g2d.setColor(c[i]);
                    //g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, -h, dx, h);
                    int fill = (int) ((cp[1] * (float) h / 100));
                    g2d.fillRect(x, -fill, dx, fill);
                    //Samples percentage
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, offset, dx, sty);
                    int pfill = (int) ((cp[2] * (float) sty / 100));
                    g2d.fillRect(x, by - pfill, dx, pfill);

                    g2d.drawString("" + (i + 1), (x + 8), -(fill / 2));
                    //Silhouette percentage
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, by + offset, dx, hty);
                    int hfill = (int) ((cp[3] * (float) hty / 100));
                    g2d.fillRect(x, kty - hfill, dx, hfill);

                    g2d.drawString("" + (i + 1), (x + 8), -(fill / 2));
                    // g2d.drawString("" + cp[1] + "%", x, -h);
                    x += dx + 5;
                } else {
                    //x += 15;
                }
            }
            x += 10;
//            System.out.println(o + "\t" + x);
        }
        g2d.drawLine(0, 0, x, 0);
        g2d.drawLine(0, by, x, by);
        g2d.drawLine(0, kty, x, kty);
        try {
            pdfDoc.saveDocument(new File(fileName + ".pdf").getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
