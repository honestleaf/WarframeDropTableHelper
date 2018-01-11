/*
 * The MIT License
 *
 * Copyright 2018 Honestleaf<Cheng.Ye.HL@hotmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tablewriter;

import com.owlike.genson.Genson;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class ItemDropWriter extends GenericDropWriter<HashMap<String, ArrayList<Object[]>>> {

    private int listSizeMax = 1;
    private String keyTitle = "modName";
    private String subTitle1 = "enemyName";
    private String subTitle2 = "modDropChance";
    private String subTitle3 = "modChance";
    private String relationTitle = "from";
    private String boundaryKey;

    public void setListSizeMax(int listSizeMax) {
        this.listSizeMax = listSizeMax;
    }
    
    public void setBoundaryKey(String bk) {
        this.boundaryKey = bk;
    }
    
    public void setKeyTitle(String title) {
        this.keyTitle = title;
    }
    
    public void setRelationTitle(String title) {
        this.relationTitle = title;
    }
    
    public void setSubTitle1(String title) {
        this.subTitle1 = title;
    }
    
    public void setSubTitle2(String title) {
        this.subTitle2 = title;
    }
    
    public void setSubTitle3(String title) {
        this.subTitle3 = title;
    }

    @Override
    public void writeJsonFile(HashMap<String, ArrayList<Object[]>> drops) {
        if (this.jsonFile == null) {
            Logger.getLogger(MissionDropWriter.class.getName()).warn("Json file not set.");
            return;
        }
        try (FileWriter fw = new FileWriter(this.jsonFile);) {
            Genson g = new Genson();
            fw.write(g.serialize(drops));
        } catch (IOException ex) {
            Logger.getLogger(MissionDropWriter.class.getName()).error(null, ex);
        }
    }

    @Override
    public void writeXlsxFile(HashMap<String, ArrayList<Object[]>> drops) {
        if (this.xlsxFile == null) {
            Logger.getLogger(MissionDropWriter.class.getName()).warn("Xlsx file not set.");
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(this.xlsxFile);) {
            XSSFWorkbook nwb = new XSSFWorkbook();
            XSSFSheet nSheet = nwb.createSheet();
            Row firstRow = nSheet.createRow(0);
            firstRow.createCell(0).setCellValue(this.keyTitle);
            int start, index;
            for (int i = 1; i <= listSizeMax; i++) {
                start = (i - 1) * 3;
                index = (i - 1);
                firstRow.createCell(start + 1).setCellValue(this.relationTitle + "[" + index + "]." + this.subTitle1);
                firstRow.createCell(start + 2).setCellValue(this.relationTitle + "[" + index + "]." + this.subTitle2);
                firstRow.createCell(start + 3).setCellValue(this.relationTitle + "[" + index + "]." + this.subTitle3);
            }
            int row = 1, cell = 0;
            Row cRow;
            Set<String> keySet = drops.keySet();
            List<String> keys = keySet.stream().collect(Collectors.toList());
            if (boundaryKey != null) {
                keys.remove(boundaryKey);
                keys.add(0, boundaryKey);
            }
            for (String key : keys) {
                cell = 0;
                cRow = nSheet.createRow(row++);
                cRow.createCell(cell++).setCellValue(key);
                for (Object[] v : drops.get(key)) {
                    cRow.createCell(cell++).setCellValue((String) v[0]);
                    cRow.createCell(cell++).setCellValue(Float.toString((float) v[1]));
                    cRow.createCell(cell++).setCellValue(Float.toString((float) v[2]));
                }
            }
            nwb.write(fos);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MissionDropWriter.class.getName()).error(null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MissionDropWriter.class.getName()).error(null, ex);
        }
    }

}
