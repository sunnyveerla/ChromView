/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChromView;

import RadarPlot.UtilInterface;
import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFGraphics;
import com.qoppa.pdfWriter.PDFPage;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author klin-sve
 */
public class CreateSNVTFPlot {

    Map<String, LinkedHashMap<String, List<String>>> cMap = new LinkedHashMap<>();
    Map<String, String> cSize = new HashMap<>();

    public CreateSNVTFPlot() {
        String fileName = "E:/SNVTF/testing";
        String dataFile = "E:/SNVTF/testing_data.txt";
        UtilInterface util = new UtilInterface();
        HashMap<String, HashMap<Integer, double[]>> hChr = createChr(dataFile);
        String[] mappers = util.getHeader(dataFile).split("\t");
        int nump = mappers.length - 2;
        System.out.println(maxValue);
//        int total = new UtilInterface().lineCount(dataFile) - 1;
        int max = (int) maxValue;
        int xOffset = 10;
        double yOffPer = 2.0; //Gap in percentage of total py
        int yOffset = 0;
        int px = 500;
        int py = 500;

//        int x1 = 0, y1 = 0, x2 = 0, y2 = 0, x3 = px-20, y3 = 0,x4=0,y4=0;
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        int yLen = py / nump;
        yOffset = (int) (py * yOffPer / 100);
        py = py + (yOffset * nump) + 50;

        //Prepare PDF Document
        PDFDocument pdfDoc = new PDFDocument();
        Paper pp = new Paper();
        pp.setSize(px, py);
        pp.setImageableArea(0, 0, px, py);
        PageFormat pf = new PageFormat();
        pf.setPaper(pp);
        // create a new page and add it to the PDF (important!)
        PDFPage page = pdfDoc.createPage(pf);
        pdfDoc.addPage(page);
// get graphics from the page
        // this object is a Graphics2D Object and you can draw anything 
        // you would draw on a Graphics2D
        PDFGraphics g2d = (PDFGraphics) page.createGraphics();
        g2d.setColor(Color.BLACK);
        //g2d.drawString("Sunny", x, 50);
//        by = by-100;
        g2d.translate(10, 10);
        x2 = px - 25;
        drawBarValues(g2d, x1, y1, x2, y2, yOffset, yLen, hChr);
        g2d.setColor(Color.BLACK);
        for (int p = 0; p < nump; p++) {
            y1 = yOffset * (p + 1) + yLen * p;
            y2 = y1 + yLen;
            drawChrScale(g2d, x1, y1, x2, y2, max, mappers[p + 2]);
        }

        try {
            pdfDoc.saveDocument(new File(fileName + ".pdf").getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawChrScale(PDFGraphics g2d, int x1, int y1, int x2, int y2, int max, String title) {
        //Drawing starts......
        g2d.setFont(new Font("SUNNY", Font.PLAIN, 4));
        g2d.drawString(title, x2 + 2, y1 + ((y2 - y1) / 2));
        g2d.drawLine(x1, y1, x1, y2);
        g2d.drawLine(x1, y2, x2, y2);
        g2d.drawLine(x2, y2, x2, y1);
        for (int i = 0; i <= max; i++) {
            int tmp = (int) (((float) (i) / max) * (y2 - y1));
            g2d.drawString("" + (max - i), x1 - 5, (tmp == 0) ? y1 : y1 + tmp);
//            System.out.println(i + "\t" + y2);
        }
    }

    private void drawBarValues(PDFGraphics g2d, int x1, int y1, int x2, int y2, int yOffset, int yLen, HashMap<String, HashMap<Integer, double[]>> hChr) {
        String chrs[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y"};
        double tmpX1 = 0;
        double tmpX2 = 0;
        g2d.setFont(new Font("SUNNY", Font.PLAIN, 4));
        String ctmp = "";
        HashMap<String, List<Coordinates>> posMap = getChromCoord("E:/SNVTF/cytoBandIdeo.txt");
        Color col[] = new ToolBox().getDifferentColors(50);
//        System.out.println(posMap.size());
        int ci = 0;
        long total = getGenomeLength(posMap);
//        g2d.setColor(Color.GRAY);
        for (String c : chrs) {
            if (hChr.containsKey(c)) {

                HashMap<Integer, double[]> chrArray = hChr.get(c);
                List<Coordinates> cTmp = posMap.get(c);
                int cEnd = cTmp.get(cTmp.size() - 1).end;
                tmpX1 += tmpX2;
                tmpX2 = ((double) cEnd / total) * x2;

                System.out.println(tmpX1 + "\t" + cEnd + "\t" + tmpX2 + "\t" + total);
                g2d.setColor(col[ci++]);
                for (Map.Entry<Integer, double[]> map : chrArray.entrySet()) {
                    double cPos = tmpX1 + ((double) map.getKey() / cEnd) * tmpX2;
//                    System.out.println(c + "\t" + ((float) map.getKey() / cEnd) + "\t" + map.getKey() + "\t" + cEnd + "\t" + tmpX2);
                    double[] values = map.getValue();
                    for (int v = 0; v < values.length; v++) {
                        y1 = yOffset * (v + 1) + yLen * v;
                        y2 = y1 + yLen;
                        g2d.draw(new Line2D.Double(cPos, y2, cPos, y2 - (yLen * (values[v] / maxValue))));
                        if (!c.equals(ctmp)) {
                            g2d.drawString(c, (float) (tmpX1 + tmpX2/2), y2 + 5);
                        }
//                        System.out.println(cPos + "\t" + y2 + "\t" + cPos + "\t" + y2);
                    }
                    ctmp = c;
                }
            }
        }
    }

    private long getGenomeLength(HashMap<String, List<Coordinates>> posMap) {
        long cLen = 0;
        for (Map.Entry<String, List<Coordinates>> map : posMap.entrySet()) {
            List<Coordinates> lcoor = map.getValue();
            cLen += lcoor.get(lcoor.size() - 1).end;
            System.out.println(cLen);
        }
        return cLen;
    }

    private HashMap<String, List<Coordinates>> getChromCoord(String chromFile) {
        HashMap<String, List<Coordinates>> posMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(chromFile))) {
            String line = null;
            String chr = "";
            int cStart = -1;
            int cEnd = -1;
            String arm = "";
            String g = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("_") || line.contains("chrM") || line.contains("chrMT")) {
                    continue;
                }
                String sp[] = line.split("\t");

                chr = sp[0].replace("chr", "");
//                System.out.println(chr);
                cStart = Integer.parseInt(sp[1].trim());
                cEnd = Integer.parseInt(sp[2].trim());
                arm = sp[3].trim();
                g = sp[4].trim();
                Coordinates coor = new Coordinates(chr, cStart, cEnd, arm, g);
                if (!posMap.containsKey(chr)) {
                    List<Coordinates> acoor = new ArrayList<>();
                    acoor.add(coor);
                    posMap.put(chr, acoor);
                } else {
                    List<Coordinates> acoor = posMap.get(chr);
                    acoor.add(coor);
                    posMap.put(chr, acoor);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posMap;
    }

//    private Group drawChrom(PDFGraphics g2d, LinkedHashMap<String, List<String>> posMap, int cEnd, float height, float pWidth,
//            float xPos, float yPos) {
//        int cStart = 0;
//
//        int s = 0;
//        for (Map.Entry<String, List<String>> map : posMap.entrySet()) {
//            List<String> li = map.getValue();
//            int start = Integer.parseInt(li.get(0));
//            int end = Integer.parseInt(li.get(1));
//            float cX = xPos + (pWidth * (start - cStart) / (float) (cEnd - cStart));
//            float cY = xPos + (pWidth * (end - cStart) / (float) (cEnd - cStart));
//            // System.out.println(cX + " " + cY);
//            Rectangle pRectangle = new Rectangle();
//            pRectangle.setX(cX);
//            pRectangle.setY(yPos);
//            pRectangle.setWidth(cY - cX);
//            pRectangle.setHeight(height);
//            pRectangle.setFill(cColors.get(li.get(2)));
//            pRectangle.setStroke(javafx.scene.paint.Color.BLACK);
//            ;
//            if (li.get(2).equals("acen")) {
//                pRectangle.setArcHeight(height);
//                pRectangle.setArcWidth(cY - cX);
//            }
//            root.getChildren().add(pRectangle);
//        }
//
//        return root;
//    }
    private HashMap<String, HashMap<Integer, double[]>> createChr(String dataFile) {
        UtilInterface util = new UtilInterface();
        String[] ann = util.getDataFromFile(dataFile, true);
        HashMap<String, HashMap<Integer, double[]>> hChr = new HashMap<>();
        for (String line : ann) {
            String sp[] = line.split("\t");
            String chr = sp[0].trim().replace("chr", "").trim();
            int start = Integer.parseInt(sp[1].trim());
            int end = 0;
//            int end = Integer.parseInt(sp[2].trim());

            String st[] = Arrays.copyOfRange(sp, 2, sp.length);
            if (!hChr.containsKey(chr)) {
                HashMap<Integer, double[]> hTmp = new HashMap<>();
                hTmp.put(start, stringArrayToDoubleArray(st));
//                ArrayList<Coordinates> arrayCoord = new ArrayList<>();
//                arrayCoord.add(coord);
                hChr.put(chr, hTmp);
            } else {
                HashMap<Integer, double[]> hTmp = hChr.get(chr);
                if (!hTmp.containsKey(start)) {
                    hTmp.put(start, stringArrayToDoubleArray(st));
                } else {
                    hTmp.put(start, addTowDoubleArrays(hTmp.get(start), stringArrayToDoubleArray(st)));
                }
//                ArrayList<Coordinates> arrayCoord = hChr.get(chr);
//                arrayCoord.add(coord);
                hChr.put(chr, hTmp);
            }
        }

//        String[] cKeys = hChr.keySet().toArray(new String[0]);
//        for (String c : cKeys) {
//            if (hChr.containsKey(c)) {
//                ArrayList<Coordinates> arrayCoord = hChr.get(c);
//                Collections.sort(arrayCoord);
//                hChr.put(c, arrayCoord);
//            }
//        }
        return hChr;
    }

    private double[] stringArrayToDoubleArray(String[] arr) {
        double[] dArray = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            dArray[i] = Double.valueOf(arr[i]);
        }
        return dArray;
    }
    double maxValue = 1;

    private double[] addTowDoubleArrays(double[] d1, double[] d2) {
        double[] dArray = new double[d1.length];
        for (int i = 0; i < d1.length; i++) {
            dArray[i] = d1[i] + d2[i];
            if (dArray[i] > maxValue) {
                maxValue = dArray[i];
            }
        }
        return dArray;

    }
}
