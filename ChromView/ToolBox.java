/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChromView;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author klin-sve
 */
public class ToolBox {

    public Color[] getDifferentColors() {
        Color[] colorlist = new Color[20];
        colorlist[0] = Color.RED;
        colorlist[1] = Color.BLUE;
        colorlist[2] = Color.GREEN;
        colorlist[3] = Color.ORANGE;
        colorlist[4] = Color.PINK;
        colorlist[5] = Color.MAGENTA;
        colorlist[6] = Color.CYAN;
        colorlist[7] = Color.GRAY;
        colorlist[8] = Color.BLACK;
        colorlist[9] = Color.DARK_GRAY;

        for (int i = 10; i < 20; i++) {
            colorlist[i] = Color.getHSBColor((float) 255 / (i + 1), (float) 255 / ((i + 1) * 2), (float) 255 / ((i + 1) * 3));
        }
        return colorlist;
    }

    public Color[] getDifferentColors(int x) {
        Color[] colorlist = new Color[x];
        colorlist[0] = Color.RED;
        colorlist[1] = Color.BLUE;
        colorlist[2] = Color.GREEN;
        colorlist[3] = Color.ORANGE;
        colorlist[4] = Color.PINK;
        colorlist[5] = Color.MAGENTA;
        colorlist[6] = Color.CYAN;
        colorlist[7] = Color.GRAY;
        colorlist[8] = Color.BLACK;
        colorlist[9] = Color.DARK_GRAY;
        for (int i = 10; i < x; i++) {
            colorlist[i] = Color.getHSBColor((float) 255 / (i + 1), (float) 255 / ((i + 1) * 2), (float) 255 / ((i + 1) * 3));
        }
        return colorlist;
    }

    public String[] getDataFromFile(String inFile) {
        List<String> data = new ArrayList<>();
        try {
            data = Files.readAllLines(Paths.get(inFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toArray(new String[0]);
    }

    public double roundDouble(double d, int places) {
        return Math.round(d * Math.pow(10, (double) places)) / Math.pow(10, (double) places);
    }

    public float[][] substract(float results[][], float value) {
//        SStatUtils sutil = new SStatUtils();
        int len = results.length;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                results[i][j] = (value - results[i][j]);
                results[j][i] = results[i][j];
            }
        }
        return results;
    }
 public float[][] divide(float results[][], float value) {
//        SStatUtils sutil = new SStatUtils();
        int len = results.length;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                results[i][j] = results[i][j]/value;
                results[j][i] = results[i][j];
            }
        }
        return results;
    }
    public float[][] normalize(float results[][]) {
        int len = results.length;
        float maxD = (float) 0.0;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (results[i][j] > maxD) {
                    maxD = results[i][j];
                }
            }
        }
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                //System.out.println(results[i][j]+" DIV "+maxD);
                results[i][j] = (results[i][j] / maxD);
                results[j][i] = results[i][j];
            }
        }
        System.out.println(maxD);
        return results;
    }

    public void writeStringArrayToFile(String data[], String filename) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < data.length; i++) {
                if (i == 0) {
                    bw.write(data[i]);
                } else {
                    bw.write("\n" + data[i]);
                }
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getDataFromStringArray(String[] column, int[] row) {
        String[] cell = new String[row.length];
        int count = 0;
        for (int i = 0; i < column.length; i++) {
            if (count == row.length) {
                break;
            }
            for (int j = 0; j < row.length; j++) {
                if (i == row[j]) {
                    cell[j] = column[i];
                    //System.out.println(line+"**********util"+count);
                    count++;
                }
            }

        }
        //System.out.println("Getting cell data");
        return cell;
    }

    public void mergeFilesToSingleFile(String inPath, String outFile) {

        //String outFile = inPath + study + "_GeneExpData.txt";
        String flist[] = new File(inPath).list(getFilesWithExtension(inPath, ".txt"));

        String sampleFile = inPath + flist[0];
        String genes[] = getColumnDataFromFile(0, sampleFile);
        int lc = genes.length;
        Map<String, String> hGenes = new HashMap(lc);
        int count = 0;
        for (String g : genes) {
            hGenes.put(g, "" + (count++));
        }
        BufferedWriter bw = null;
        try {

            bw = new BufferedWriter(new FileWriter(outFile));
            //bw.write(getHeader(inPath + flist[0]) + "\n");
            for (String f : flist) {
                System.out.println(f);
                String tmpData[] = getDataFromFile(inPath + f, false);
                for (String d : tmpData) {
                    /*bw.write(d + "\n");
                     bw.flush();*/
                    String sp[] = d.split("\t");
                    if (hGenes.containsKey(sp[0].trim())) {
                        int rowNum = Integer.parseInt(hGenes.get(sp[0].trim()).toString());
                        genes[rowNum] += "\t" + d.substring(d.indexOf("\t"), d.length()).trim();
                    }
                }
            }
            for (String gex : genes) {
                bw.write(gex + "\n");
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String[] getColumnDataFromFile(int col, String filename) {
        int tcol[] = new int[1];
        tcol[0] = col;
        
        return (getColumnDataFromFile(tcol, filename));
    }

 public String getColumnDataFromFileAsString(int col[], String filename) {
        Vector vt = new Vector();
        boolean flag = false;
        String cdata = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            String tmp = "";

            int count = 0;
            String ctmp = "\t";
            if (col.length > 1) {
                ctmp = "\t";
            }
            while ((line = br.readLine()) != null && !line.equals("")) {
                String st[] = line.split("\t");
                //System.out.println(col.length);
                if (st.length - 1 < col[0]) {
                    break;
                }
                //System.out.println(st[col[0]]);
                for (int i = 0; i < col.length; i++) {
                    if (st[col[i]].equals("")) {
                        flag = true;
                        cdata += "\t";
                        break;
                    }

                    //tmp += st[col[i]]+"\t";
                    cdata += st[col[i]] + ctmp;
                }
                if (flag) {
                    break;
                }
                //                vt.add(tmp);
                //                tmp = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //return(vt.toArray());
        return cdata;
    }
    public FilenameFilter getFilesWithExtension(String folderName, final String ext) {
        File f = new File(folderName);
        FilenameFilter textFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(ext)) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        return textFilter;
    }

    public String[] getDataFromFile(String filename, boolean withoutHeader) {
        //Vector vt = new Vector();
        boolean flag = false;
        int lc = lineCount(filename);
        if (withoutHeader) {
            lc = lc - 1;
        }
        if (lc == 0) {
            return null;
        }
        String[] cdata = new String[lc];
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            int c = 0;
            while ((line = br.readLine()) != null && !line.equals("")) {
                if (c == 0 && withoutHeader) {
                    withoutHeader = false;
                    continue;
                }
                cdata[c++] = line.trim();
            }
            br.close();
            br = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Get all col data");
        return cdata;
    }

    public String[] getColumnDataFromFile(int col[], String filename) {
        //Vector vt = new Vector();
        boolean flag = false;
        String[] cdata = new String[lineCount(filename)];
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            String tmp = "";

            int count = 0;
            int scount = 0;
            String ctmp = "";
            if (col.length > 1) {
                ctmp = "\t";
            }
            while ((line = br.readLine()) != null && !line.equals("")) {
                //line.replaceAll("\\t\\t", "\tnull\t");
                String st[] = line.split("\t");
                //System.out.println(line);
                if (st.length - 1 < col[0]) {
                    System.out.println(line);
                    break;
                }
                cdata[count] = "";
                for (int i = 0; i < col.length; i++) {
                    if (st[col[i]].equals("")) {
                        flag = true;
                        cdata[count++] = "";
                        break;
                    }

                    //tmp += st[col[i]]+"\t";
                    cdata[count] += st[col[i]] + ctmp;
                    //System.out.println(cdata[count]);
                }
                // System.out.println(count);
                count++;
                if (flag) {
                    break;
                }
                //                vt.add(tmp);
                //                tmp = "";
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return(vt.toArray());
        return cdata;
    }

    public int lineCount(String file) {
        int count = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
//                if (count == 0) {
//                    this.colCount = line.split("\t").length;
//                }
                count++;
            }
//            this.rowCount = count;
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
     public File[] sortFilesBySize(String resultPath) {
        File sortedFiles[] = new File(resultPath).listFiles(getFilesWithExtension(resultPath, ".txt"));

        Arrays.sort(sortedFiles, new NumberedFileSizeComparator().reversed());
//        System.out.println(sortedFiles[0]+"*******");
        return sortedFiles;
    }
     public class NumberedFileSizeComparator implements Comparator {

        public int compare(Object f1, Object f2) {
            int val = (int) (((File) f1).length() - ((File) f2).length());
            return val;
        }

    }
}
