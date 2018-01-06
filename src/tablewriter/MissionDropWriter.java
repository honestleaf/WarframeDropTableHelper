/*
 * The MIT License
 *
 * Copyright 2017 Honestleaf<Cheng.Ye.HL@hotmail.com>.
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
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class MissionDropWriter extends GenericDropWriter<ArrayList<String[]>> {
    
    @Override
    public void writeJsonFile(ArrayList<String[]> dropList) {
        if (this.jsonFile == null) {
            Logger.getLogger(MissionDropWriter.class.getName()).warn("Json file not set.");
            return;
        }
        try (FileWriter fw = new FileWriter(this.jsonFile);) {
            Genson g = new Genson();
            fw.write(g.serialize(dropList));
        } catch (IOException ex) {
            Logger.getLogger(MissionDropWriter.class.getName()).error(null, ex);
        }
    }

    @Override
    public void writeXlsxFile(ArrayList<String[]> dropList) {
        if (this.xlsxFile == null) {
            Logger.getLogger(MissionDropWriter.class.getName()).warn("Xlsx file not set.");
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(this.xlsxFile);) {
            XSSFWorkbook nwb = new XSSFWorkbook();
            XSSFSheet nSheet = nwb.createSheet();
            Row firstRow = nSheet.createRow(0);
            firstRow.createCell(0).setCellValue("planet");
            firstRow.createCell(1).setCellValue("node");
            firstRow.createCell(2).setCellValue("mtype");
            firstRow.createCell(3).setCellValue("rotation");
            firstRow.createCell(4).setCellValue("reward");
            firstRow.createCell(5).setCellValue("chance");
            int row = 1, cell;
            Row cRow;
            for (String[] drop : dropList) {
                cell = 0;
                cRow = nSheet.createRow(row++);
                for (String v : drop) {
                    cRow.createCell(cell).setCellValue(drop[cell++]);
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
