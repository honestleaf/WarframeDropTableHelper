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
package parser;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class SpecialMissionMapParser extends GenericParser<XSSFSheet, ArrayList<String[]>> {

    private final static Logger LOG = Logger.getLogger(SpecialMissionMapParser.class.getName());

    @Override
    public ArrayList<String[]> parse() {
        ArrayList<String[]> list = new ArrayList<>();
        StringParser sp = new StringParser();
        String[] missionData;
        String mission, planet, node, mtype, firstOnly, skip, buffer;
        boolean first = true;
        for (Row row : this.table) {
            if (first) {
                first = false;
                continue;
            }
            mission = planet = mtype = node = firstOnly = skip = null;
            for (Cell cell : row) {
                try {
                    buffer = cell.getStringCellValue();
                    switch (cell.getColumnIndex()) {
                        case 0:
                            mission = buffer;
                            break;
                        case 1:
                            planet = buffer;
                            break;
                        case 2:
                            mtype = buffer;
                            break;
                        case 3:
                            node = buffer;
                            break;
                        case 4:
                            firstOnly = buffer;
                            break;
                        case 5:
                            skip = buffer;
                            break;
                        default:
                            break;
                    }
                } catch (Exception ex) {
                    LOG.warn("Invalid MissionMap cell value.", ex);
                }
            }
            list.add(new String[]{mission, planet, mtype, node, firstOnly, skip});
        }
        return list;
    }

}
