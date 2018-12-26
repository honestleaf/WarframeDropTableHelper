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

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class EnemyParser extends GenericParser<HtmlTable, HashMap<String, ArrayList<Object[]>>> {

    private int listSizeMax = 1;
    private String boundaryKey;
    private HashMap<String, ArrayList<Object[]>> dropTable;

    public int getListSizeMax() {
        return listSizeMax;
    }
    
    public String getBoundaryKey() {
        return boundaryKey;
    }
    
    public void setDropTable(HashMap<String, ArrayList<Object[]>> table) {
        this.dropTable = table;
    }

    @Override
    public HashMap<String, ArrayList<Object[]>> parse() {
        if (this.dropTable == null) {
            this.dropTable = new HashMap<>();
            this.listSizeMax = 1;
            this.boundaryKey = null;
        }
        ArrayList<Object[]> list;
        StringParser sp = new StringParser();
        String item = null, enemy = null, buffer;
        float itemDropChance = -1, itemChance;
        Object[] drop;
        for (final HtmlTableRow row : this.table.getRows()) {
            INNER:
            for (final HtmlTableCell cell : row.getCells()) {
                buffer = cell.asText();
                if (cell.getColumnSpan() == 2) {
                    itemDropChance = sp.parseChance(buffer);
                } else if (cell.getColumnSpan() == 1) {
                    switch (cell.getIndex()) {
                        case 0:
                            if (!buffer.isEmpty()) {
                                enemy = buffer;
                            }
                            break;
                        case 1:
                            item = buffer;
                            break;
                        case 2:
                            itemChance = sp.parseChance(buffer);
                            drop = new Object[]{item, itemDropChance, itemChance};
                            if (this.dropTable.get(enemy) == null) {
                                list = new ArrayList<>();
                                list.add(drop);
                                this.dropTable.put(enemy, list);
                            } else {
                                list = this.dropTable.get(enemy);
                                final String fSrc = item;
                                if (list.stream().noneMatch(e -> e[0].equals(fSrc))){
                                    list.add(drop);
                                }
                                if (list.size() > this.listSizeMax) {
                                    this.listSizeMax = list.size();
                                    this.boundaryKey = enemy;
                                }
                                if (this.boundaryKey == null) {
                                    this.boundaryKey = enemy;
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
