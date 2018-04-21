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

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class TransientParser extends GenericParser<HtmlTable, ArrayList<String[]>> {

    @Override
    public ArrayList<String[]> parse() {
        ArrayList<String[]> list = new ArrayList<>();
        StringParser sp = new StringParser();
        String rotation = null, temp, chance, buffer, node = null, reward = null, planet = "Any", mtype = "Any";
        boolean skip = false, hasMAVA = false;
        for (final HtmlTableRow row : this.table.getRows()) {
            if (skip) {
                if (row.getAttribute("class").contains("blank-row")) {
                    skip = false;
                } else {
                    continue;
                }
            }
            INNER:
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
                    switch (buffer) {
                        case "Derelict Vault":
                            planet = "Derelict";
                            break;
                        case "Phorid Assassination":
                            mtype = "Assassination";
                            break;
                        case "Fomorian Sabotage":
                            mtype = "Sabotage";
                            break;
                        case "Plains of Eidolon Incursions":
                            planet = "Earth";
                            node = "Plains of Eidolon";
                            mtype = "Incursion";
                            break;
                        case "The Law Of Retribution":
                            planet = "Earth";
                            mtype = "Trial";
                            break;
                        case "The Law Of Retribution (Nightmare)":
                            planet = "Earth";
                            mtype = "Trial";
                            break;
                        case "The Jordas Verdict":
                            planet = "Eris";
                            mtype = "Trial";
                            break;
                        case "Jordas Golem Assassinate":
                            planet = "Eris";
                            mtype = "Assassination";
                            break;
                        case "Vay Hek Frequency Triangulator":
                            planet = "Earth";
                            mtype = "Assassination";
                            break;
                        case "Void Onslaught (Easy)":
                            planet = "Sanctuary (Cephalon Simaris)";
                            mtype = "Void Onslaught";
                            break;
                        case "Void Onslaught (Hard)":
                            planet = "Sanctuary (Cephalon Simaris)";
                            mtype = "Void Onslaught";
                            break;
                        case "Mutalist Alad V Assassinate":
                            if (hasMAVA) {
                                skip = true;
                                break INNER;
                            } else {
                                planet = "Eris";
                                mtype = "Assassination";
                                hasMAVA = true;
                                break;
                            }
                        case "Orokin Derelict Defense":
                        case "Orokin Derelict Assassinate":
                        case "Orokin Derelict Survival":
                        case "Help Clem Retrieve The Relic":
                            skip = true;
                            break INNER;
                        default:
                            planet = "Any";
                            mtype = "Any";
                            break;
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
