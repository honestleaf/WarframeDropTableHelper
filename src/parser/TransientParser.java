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
public class TransientParser extends GenericParser<HtmlTable, ArrayList<String[]>> {

    private HashMap<String, String[]> missionMap = null;

    public void setMissionMap(ArrayList<String[]> list) {
        missionMap = new HashMap<>();
        for (String[] sa : list) {
            if (sa[0] != null) {
                missionMap.put(sa[0], new String[]{sa[1], sa[2], sa[3], sa[4], sa[5]});
            }
        }
    }

    @Override
    public ArrayList<String[]> parse() {
        ArrayList<String[]> list = new ArrayList<>();
        StringParser sp = new StringParser();
        String rotation = null, temp, chance, buffer, node = null, reward = null, planet = "Any", mtype = "Any";
        boolean skip = false;
        ArrayList<String> singleList = new ArrayList<>();
        for (final HtmlTableRow row : this.table.getRows()) {
            if (skip) {
                if (row.getAttribute("class").contains("blank-row")) {
                    skip = false;
                } else {
                    continue;
                }
            }
            for (final HtmlTableCell cell : row.getCells()) {
                buffer = cell.asText();
                if (cell.getColumnSpan() == 2) {
                    temp = sp.parseRotation(buffer);
                    if (temp != null) {
                        rotation = temp;
                    } else {
                        node = buffer;
                        rotation = "All";
                    }
                    planet = "Any";
                    mtype = "Any";
                    if (missionMap != null) {
                        if (missionMap.containsKey(buffer)) {
                            if (missionMap.get(buffer)[3] != null) {
                                if (singleList.contains(buffer)) {
                                    skip = true;
                                    break;
                                } else {
                                    singleList.add(buffer);
                                }
                            }
                            if (missionMap.get(buffer)[4] != null) {
                                skip = true;
                                break;
                            }
                            planet = missionMap.get(buffer)[0] != null ? missionMap.get(buffer)[0] : planet;
                            mtype = missionMap.get(buffer)[1] != null ? missionMap.get(buffer)[1] : mtype;
                            node = missionMap.get(buffer)[2] != null ? missionMap.get(buffer)[2] : node;
                        }
                    }
                } else if (cell.getColumnSpan() == 1) {
                    if (cell.getIndex() == 0) {
                        temp = sp.parseRotation(buffer);
                        if (temp != null) {
                            rotation = temp;
                        } else {
                            reward = buffer;
                        }
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
