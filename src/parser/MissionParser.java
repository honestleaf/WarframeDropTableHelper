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
package parser;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import java.util.ArrayList;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class MissionParser extends GenericParser<HtmlTable, ArrayList<String[]>> {
    

    @Override
    public ArrayList<String[]> parse() {
        ArrayList<String[]> list = new ArrayList<>();
        StringParser sp = new StringParser();
        String[] nodeData;
        String rotation = null, chance, buffer, planet = null, node = null, mtype = null, reward = null;
        for (final HtmlTableRow row : this.table.getRows()) {
            for (final HtmlTableCell cell : row.getCells()) {
                buffer = cell.asText();
                if (cell.getColumnSpan() == 2) {
                    nodeData = sp.parseNode(buffer);
                    rotation = sp.parseRotation(buffer);
                    if (nodeData != null) {
                        planet = nodeData[0];
                        node = nodeData[1];
                        mtype = nodeData[2];
                        rotation = "All";
                    }
                    break;
                } else if (cell.getColumnSpan() == 1) {
                    if (cell.getIndex() == 0) {
                        reward = buffer;
                    } else if (cell.getIndex() == 1) {
                        if (buffer.isEmpty()) {
                            continue;
                        }
                        chance = buffer;
                        list.add(new String[]{planet, node, mtype, rotation,
                            reward, chance});
                    }
                }
            }
        }
        return list;
    }
}
