/*
 * The MIT License
 *
 * Copyright 2017 Honestleaf.
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
package warframedroptablehelper;

import com.owlike.genson.Genson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Honestleaf
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try (XSSFWorkbook wb = new XSSFWorkbook(new File("droptable.xlsx"));
                FileWriter fw = new FileWriter(new File("droptable.json"));) {
            // mission sheet
            XSSFSheet missionSheet = wb.getSheet("mission");
            ArrayList<String[]> dropList = new ArrayList<>();
            Genson genson = new Genson();

            Pattern nodePattern = Pattern.compile("(.+?)/(.+?) \\((.+?)\\) *(.*)");
            Pattern rotationPattern = Pattern.compile("Rotation (A|B|C)");

            String planet = "", node = "", mtype = "", rotation = "", reward = "", chance = "";
            Matcher m;
            for (Row row : missionSheet) {
                for (Cell cell : row) {
                    String s = cell.getStringCellValue();
                    if (cell.getColumnIndex() == 0) {
                        m = nodePattern.matcher(s);
                        if (m.find()) {
                            planet = m.group(1);
                            node = m.group(2);
                            mtype = m.group(4).isEmpty() ? m.group(3) : m.group(3) + " " + m.group(4);
                            rotation = "All";
                            continue;
                        }
                        m = rotationPattern.matcher(s);
                        if (m.find()) {
                            rotation = m.group(1);
                            continue;
                        }
                        reward = s;
                    }
                    if (cell.getColumnIndex() == 1) {
                        if (s.isEmpty()) {
                            continue;
                        }
                        chance = s;
                        dropList.add(new String[]{planet, node, mtype, rotation,
                            reward, chance});
                    }
                }
            }

            // bounty sheet
            XSSFSheet bountySheet = wb.getSheet("bounty");

            Pattern bountyTierPattern = Pattern.compile("Level .+? Bounty");
            Pattern bountyStagePattern = Pattern.compile("Stage .*");

            String stage = "", bountyTier = "";
            planet = "Earth";
            mtype = "Bounty";

            for (Row row : bountySheet) {
                for (Cell cell : row) {
                    String s = cell.getStringCellValue();
                    if (cell.getColumnIndex() == 0) {
                        m = bountyTierPattern.matcher(s);
                        if (m.find()) {
                            bountyTier = m.group(0);
                            rotation = "All";
                            continue;
                        }
                        m = rotationPattern.matcher(s);
                        if (m.find()) {
                            rotation = m.group(1);
                            continue;
                        }
                    }
                    if (cell.getColumnIndex() == 1) {
                        m = bountyStagePattern.matcher(s);
                        if (m.find()) {
                            stage = m.group(0);
                            continue;
                        }
                        reward = s;
                    }
                    if (cell.getColumnIndex() == 2) {
                        if (s.isEmpty()) {
                            continue;
                        }
                        chance = s;
                        dropList.add(new String[]{planet, bountyTier + "<" + stage + ">", mtype, rotation,
                            reward, chance});
                    }
                }
            }

            fw.write(genson.serialize(dropList));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Mod Drop Source
        try (XSSFWorkbook wb = new XSSFWorkbook(new File("droptable.xlsx"));
                FileWriter fw = new FileWriter(new File("modsdrop.json"));) {
            // mission sheet
            XSSFSheet modsSheet = wb.getSheet("mods");
            HashMap<String, ArrayList> dropTable = new HashMap<>();
            Genson genson = new Genson();

            Pattern srcColumnHeadPattern = Pattern.compile("Enemy Name");
            Pattern modChancePattern = Pattern.compile("\\(([0-9\\.]+)%\\)");
            DecimalFormat df = new DecimalFormat("#.####");

            String mod = "", src = "", modDropChance = "", modChance = "";
            Matcher m;
            for (Row row : modsSheet) {
                for (Cell cell : row) {
                    if (cell.getColumnIndex() == 0) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        if (row.getCell(cell.getColumnIndex() + 1) == null) {
                            mod = s;
                            continue;
                        }
                        if (row.getCell(cell.getColumnIndex() + 1).getCellTypeEnum() == CellType.BLANK) {
                            mod = s;
                            continue;
                        }
                        m = srcColumnHeadPattern.matcher(s);
                        if (!m.find()) {
                            src = s;
                            continue;
                        }
                    }
                    if (cell.getColumnIndex() == 1) {
                        if (cell.getCellTypeEnum() != CellType.NUMERIC) {
                            continue;
                        }
                        modDropChance = df.format(cell.getNumericCellValue());
                    }
                    if (cell.getColumnIndex() == 2) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        m = modChancePattern.matcher(s);
                        if (m.find()) {
                            modChance = df.format(Double.parseDouble(m.group(1)) / 100);
                            if (dropTable.get(mod) == null) {
                                ArrayList<String[]> dropList = new ArrayList<>();
                                dropList.add(new String[]{src, modDropChance, modChance});
                                dropTable.put(mod, dropList);
                            } else {
                                ArrayList<String[]> dropList = dropTable.get(mod);
                                dropList.add(new String[]{src, modDropChance, modChance});
                            }
                        }
                    }
                }
            }
            fw.write(genson.serialize(dropTable));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Enemy Mod Drop
        try (XSSFWorkbook wb = new XSSFWorkbook(new File("droptable.xlsx"));
                FileWriter fw = new FileWriter(new File("enemymoddrop.json"));) {
            // mission sheet
            XSSFSheet modsSheet = wb.getSheet("enemy mod");
            HashMap<String, ArrayList> dropTable = new HashMap<>();
            Genson genson = new Genson();

            Pattern chancePattern = Pattern.compile("([0-9\\.]+)%");
            DecimalFormat df = new DecimalFormat("#.####");

            String enemy = "", drop = "", dropChance = "", globalChance = "";
            Matcher m;
            boolean isDuplicate = false;
            for (Row row : modsSheet) {
                for (Cell cell : row) {
                    if (cell.getColumnIndex() == 0) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        enemy = s;
                        continue;
                    }
                    if (cell.getColumnIndex() == 1) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        m = chancePattern.matcher(s);
                        if (m.find()) {
                            globalChance = df.format(Double.parseDouble(m.group(1)) / 100);
                            continue;
                        } else {
                            drop = s;
                        }
                    }
                    if (cell.getColumnIndex() == 2) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        m = chancePattern.matcher(s);
                        if (m.find()) {
                            dropChance = df.format(Double.parseDouble(m.group(1)) / 100);
                            if (dropTable.get(enemy) == null) {
                                ArrayList<String[]> dropList = new ArrayList<>();
                                dropList.add(new String[]{drop, globalChance, dropChance});
                                dropTable.put(enemy, dropList);
                            } else {
                                ArrayList<String[]> dropList = dropTable.get(enemy);
                                for (String[] entry : dropList) {
                                    if (entry[0].equals(drop) && entry[1].equals(globalChance) && entry[2].equals(dropChance)) {
                                        isDuplicate = true;
                                        break;
                                    }
                                }
                                if (isDuplicate) {
                                    isDuplicate = false;
                                } else {
                                    dropList.add(new String[]{drop, globalChance, dropChance});
                                }
                            }
                        }
                    }
                }
            }
            fw.write(genson.serialize(dropTable));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Enemy Blueprint Drop
        try (XSSFWorkbook wb = new XSSFWorkbook(new File("droptable.xlsx"));
                FileWriter fw = new FileWriter(new File("enemybpdrop.json"));) {
            // mission sheet
            XSSFSheet modsSheet = wb.getSheet("enemy bp");
            HashMap<String, ArrayList> dropTable = new HashMap<>();
            Genson genson = new Genson();

            Pattern chancePattern = Pattern.compile("([0-9\\.]+)%");
            DecimalFormat df = new DecimalFormat("#.####");

            String enemy = "", drop = "", dropChance = "", globalChance = "";
            Matcher m;
            for (Row row : modsSheet) {
                for (Cell cell : row) {
                    if (cell.getColumnIndex() == 0) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        enemy = s;
                        continue;
                    }
                    if (cell.getColumnIndex() == 1) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        m = chancePattern.matcher(s);
                        if (m.find()) {
                            globalChance = df.format(Double.parseDouble(m.group(1)) / 100);
                            continue;
                        } else {
                            drop = s;
                        }
                    }
                    if (cell.getColumnIndex() == 2) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        m = chancePattern.matcher(s);
                        if (m.find()) {
                            dropChance = df.format(Double.parseDouble(m.group(1)) / 100);
                            if (dropTable.get(enemy) == null) {
                                ArrayList<String[]> dropList = new ArrayList<>();
                                dropList.add(new String[]{drop, globalChance, dropChance});
                                dropTable.put(enemy, dropList);
                            } else {
                                ArrayList<String[]> dropList = dropTable.get(enemy);
                                dropList.add(new String[]{drop, globalChance, dropChance});
                            }
                        }
                    }
                }
            }
            fw.write(genson.serialize(dropTable));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Enemy Miscellaneous Drop
        try (XSSFWorkbook wb = new XSSFWorkbook(new File("droptable.xlsx"));
                FileWriter fw = new FileWriter(new File("enemymiscdrop.json"));) {
            // mission sheet
            XSSFSheet modsSheet = wb.getSheet("enemy misc");
            HashMap<String, ArrayList> dropTable = new HashMap<>();
            Genson genson = new Genson();

            Pattern chancePattern = Pattern.compile("([0-9\\.]+)%");
            DecimalFormat df = new DecimalFormat("#.####");

            String enemy = "", drop = "", dropChance = "", globalChance = "";
            Matcher m;
            boolean isDuplicate = false;
            for (Row row : modsSheet) {
                for (Cell cell : row) {
                    if (cell.getColumnIndex() == 0) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        enemy = s;
                        continue;
                    }
                    if (cell.getColumnIndex() == 1) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        m = chancePattern.matcher(s);
                        if (m.find()) {
                            globalChance = df.format(Double.parseDouble(m.group(1)) / 100);
                            continue;
                        } else {
                            drop = s;
                        }
                    }
                    if (cell.getColumnIndex() == 2) {
                        String s = cell.getStringCellValue();
                        if (s.isEmpty()) {
                            continue;
                        }
                        m = chancePattern.matcher(s);
                        if (m.find()) {
                            dropChance = df.format(Double.parseDouble(m.group(1)) / 100);
                            if (dropTable.get(enemy) == null) {
                                ArrayList<String[]> dropList = new ArrayList<>();
                                dropList.add(new String[]{drop, globalChance, dropChance});
                                dropTable.put(enemy, dropList);
                            } else {
                                ArrayList<String[]> dropList = dropTable.get(enemy);
                                for (String[] entry : dropList) {
                                    if (entry[0].equals(drop) && entry[1].equals(globalChance) && entry[2].equals(dropChance)) {
                                        isDuplicate = true;
                                        break;
                                    }
                                }
                                if (isDuplicate) {
                                    isDuplicate = false;
                                } else {
                                    dropList.add(new String[]{drop, globalChance, dropChance});
                                }
                            }
                        }
                    }
                }
            }
            fw.write(genson.serialize(dropTable));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
