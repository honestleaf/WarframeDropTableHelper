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

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class ModByModParser extends GenericParser<HtmlTable, HashMap<String, ArrayList<Object[]>>> {

    private int listSizeMax = 1;
    private String boundaryKey;

    public String getBoundaryKey() {
        return boundaryKey;
    }

    public int getListSizeMax() {
        return listSizeMax;
    }

    @Override
    public HashMap<String, ArrayList<Object[]>> parse() {
        HashMap<String, ArrayList<Object[]>> dropTable = new HashMap<>();
        ArrayList<Object[]> list;
        StringParser sp = new StringParser();
        String mod = null, src = null, buffer;
        Object[] drop;
        float modDropChance = -1, modChance;
        for (final HtmlTableRow row : this.table.getRows()) {
            INNER:
            for (final HtmlTableCell cell : row.getCells()) {
                buffer = cell.asText();
                if (cell.getColumnSpan() == 3) {
                    mod = buffer;
                } else if (cell.getColumnSpan() == 1) {
                    switch (cell.getIndex()) {
                        case 0:
                            if (buffer.equals("Enemy Name")) {
                                break INNER;
                            } else {
                                src = buffer;
                            }
                            break;
                        case 1:
                            modDropChance = sp.parseChance(buffer);
                            break;
                        case 2:
                            modChance = sp.parseChance(buffer);
                            drop = new Object[]{src, modDropChance, modChance};
                            if (dropTable.get(mod) == null) {
                                list = new ArrayList<>();
                                list.add(drop);
                                dropTable.put(mod, list);
                            } else {
                                list = dropTable.get(mod);
                                final String fSrc = src;
                                if (list.stream().noneMatch(e -> e[0].equals(fSrc))){
                                    list.add(drop);
                                }
                                if (list.size() > this.listSizeMax) {
                                    this.listSizeMax = list.size();
                                    this.boundaryKey = mod;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return dropTable;
    }

}
