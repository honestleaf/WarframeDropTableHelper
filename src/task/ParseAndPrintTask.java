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
package task;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import javafx.concurrent.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import parser.AdditionalMissionDropParser;
import parser.BountyParser;
import parser.MissionParser;
import parser.EnemyParser;
import parser.ModByModParser;
import parser.SpecialMissionMapParser;
import parser.TransientParser;
import tablewriter.ItemDropWriter;
import tablewriter.MissionDropWriter;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class ParseAndPrintTask extends Task<Integer> {

    private final static int PROGRESS_MAX = 100;
    private final static Logger LOG = Logger.getLogger(ParseAndPrintTask.class.getName());
    private final static String DATA_URL = "https://n8k6e2y6.ssl.hwcdn.net/repos/hnfvc0o3jnfvc873njb03enrf56.html";
    private final static String MR_ID = "missionRewards";
    private final static String BOUNTY_ID = "cetusRewards";
    private final static String OB_BOUNTY_ID = "solarisRewards";
    private final static String TR_ID = "transientRewards";
    private final static String ML_ID = "modByDrop";
    private final static String EMT_ID = "modByAvatar";
    private final static String SR_ID = "sortieRewards";
    private final static String KR_ID = "keyRewards";
    private final static String MI_ID = "resourceByAvatar";
    private final static String EBT_ID = "blueprintByAvatar";
    private final static String ST_ID = "sigilByAvatar";
    private final static String AI_ID = "additionalItemByAvatar";
    private final static String ADDITIONAL_DATA_FILE = "AdditionalData.xlsx";

    private final static String MISSION_DROP_JSON = "MissionDrop.json";
    private final static String MISSION_DROP_XLSX = "MissionDrop.xlsx";

    private final static String MOD_BY_MOD_JSON = "ModDropByMod.json";
    private final static String MOD_BY_MOD_XLSX = "ModDropByMod.xlsx";

    private final static String MOD_BY_ENEMY_JSON = "EnemyModDrop.json";
    private final static String MOD_BY_ENEMY_XLSX = "EnemyModDrop.xlsx";
    
    private final static String PART_BY_ENEMY_JSON = "EnemyPartDrop.json";
    private final static String PART_BY_ENEMY_XLSX = "EnemyPartDrop.xlsx";
    
    private final static String RESOURCE_BY_ENEMY_JSON = "EnemyResourceDrop.json";
    private final static String RESOURCE_BY_ENEMY_XLSX = "EnemyResourceDrop.xlsx";
    
    private final static String SIGIL_BY_ENEMY_JSON = "EnemySigilDrop.json";
    private final static String SIGIL_BY_ENEMY_XLSX = "EnemySigilDrop.xlsx";
    
    @Override
    protected Integer call() throws Exception {

        LOG.info("Start processing.");
        updateProgress(1, PROGRESS_MAX);

        try (final WebClient webClient = new WebClient()) {
            LOG.info("Retrieve web page.");
            final HtmlPage page = webClient.getPage(DATA_URL);
            updateProgress(40, PROGRESS_MAX);

            HtmlTable table;
            ArrayList<String[]> missionDrop = new ArrayList<>(), missionMap;
            HashMap<String, ArrayList<Object[]>> modLocation = null, enemyDropTables = null;

            MissionParser mp = new MissionParser();
            BountyParser bp = new BountyParser();
            TransientParser tp = new TransientParser();
            SpecialMissionMapParser smmp = new SpecialMissionMapParser();
            AdditionalMissionDropParser adp = new AdditionalMissionDropParser();
            ModByModParser mbmp = new ModByModParser();
            EnemyParser ep = new EnemyParser();

            MissionDropWriter mdw = new MissionDropWriter();
            ItemDropWriter idw = new ItemDropWriter();

            LOG.info("Parse \"Missions\" table.");
            table = page.querySelector("#" + MR_ID + "+table");
            if (table != null) {
                mp.setTable(table);
                missionDrop = mp.parse();
            } else {
               LOG.warn("\"Missions\" table is not found.");
            }
            updateProgress(50, PROGRESS_MAX);

            LOG.info("Parse \"Cetus Bounty Rewards\" table.");
            table = page.querySelector("#" + BOUNTY_ID + "+table");
            if (table != null) {
                bp.setTable(table);
                missionDrop.addAll(bp.parse());
            } else {
               LOG.warn("\"Cetus Bounty Rewards\" table is not found.");
            }
            updateProgress(53, PROGRESS_MAX);
            
            LOG.info("Parse \"Orb Vallis Bounty Rewards\" table.");
            table = page.querySelector("#" + OB_BOUNTY_ID + "+table");
            if (table != null) {
                bp.setPlanet("Venus");
                bp.setTable(table);
                missionDrop.addAll(bp.parse());
            } else {
               LOG.warn("\"Orb Vallis Bounty Rewards\" table is not found.");
            }
            updateProgress(55, PROGRESS_MAX);

            missionMap = null;
            try (XSSFWorkbook wb = new XSSFWorkbook(new File(ADDITIONAL_DATA_FILE));) {
                XSSFSheet missionSheet = wb.getSheet("mission map");
                smmp.setTable(missionSheet);
                missionMap = smmp.parse();
            } catch (IOException | InvalidFormatException ex) {
                Logger.getLogger(ParseAndPrintTask.class.getName()).error(null, ex);
            }

            LOG.info("Parse \"Sorties\" table.");
            table = page.querySelector("#" + SR_ID + "+table");
            if (table != null) {
                tp.setTable(table);
                tp.setMissionMap(missionMap);
                missionDrop.addAll(tp.parse());
            } else {
               LOG.warn("\"Sorties\" table is not found.");
            }
            updateProgress(57.5, PROGRESS_MAX);

            LOG.info("Parse \"Dynamic Location Rewards\" table.");
            table = page.querySelector("#" + TR_ID + "+table");
            if (table != null) {
                tp.setTable(table);
                missionDrop.addAll(tp.parse());
            } else {
               LOG.warn("\"Dynamic Location Rewards\" table is not found.");
            }
            updateProgress(60, PROGRESS_MAX);

            LOG.info("Parse \"Keys\" table.");
            table = page.querySelector("#" + KR_ID + "+table");
            if (table != null) {
                tp.setTable(table);
                missionDrop.addAll(tp.parse());
            } else {
               LOG.warn("\"Keys\" table is not found.");
            }
            updateProgress(62.5, PROGRESS_MAX);

            LOG.info("Parse \"AdditionalDrop\" table.");
            try (XSSFWorkbook wb = new XSSFWorkbook(new File(ADDITIONAL_DATA_FILE));) {
                XSSFSheet missionSheet = wb.getSheet("additional drops");
                adp.setTable(missionSheet);
                missionDrop.addAll(adp.parse());
            } catch (IOException | InvalidFormatException ex) {
                Logger.getLogger(ParseAndPrintTask.class.getName()).error(null, ex);
            }
            updateProgress(65, PROGRESS_MAX);

            LOG.info("Generate \"MissionDrop\" files.");
            mdw.setJsonFile(MISSION_DROP_JSON);
            mdw.setXlsxFile(MISSION_DROP_XLSX);
            if (!missionDrop.isEmpty()) {
                mdw.writeJsonFile(missionDrop);
                mdw.writeXlsxFile(missionDrop);
            }
            updateProgress(70, PROGRESS_MAX);

            LOG.info("Parse \"Mod Drops by Mod\" table.");
            table = page.querySelector("#" + ML_ID + "+table");
            if (table != null) {
                mbmp.setTable(table);
                modLocation = mbmp.parse();
            } else {
               LOG.warn("\"Mod Drops by Mod\" table is not found.");
            }
            updateProgress(75, PROGRESS_MAX);

            LOG.info("Generate \"ModDropByMod\" files.");
            idw.setListSizeMax(mbmp.getListSizeMax());
            idw.setBoundaryKey(mbmp.getBoundaryKey());
            idw.setJsonFile(MOD_BY_MOD_JSON);
            idw.setXlsxFile(MOD_BY_MOD_XLSX);
            if (modLocation != null) {
                idw.writeJsonFile(modLocation);
                idw.writeXlsxFile(modLocation);
            }
            updateProgress(80, PROGRESS_MAX);

            LOG.info("Parse \"Mod Drops by Enemy\" table.");
            table = page.querySelector("#" + EMT_ID + "+table");
            if (table != null) {
                ep.setTable(table);
                enemyDropTables = ep.parse();
            } else {
               LOG.warn("\"Mod Drops by Enemy\" table is not found.");
            }
            
            LOG.info("Generate \"EnemyModDrop\" files.");
            idw.setListSizeMax(ep.getListSizeMax());
            idw.setBoundaryKey(ep.getBoundaryKey());
            idw.setKeyTitle("enemyName");
            idw.setRelationTitle("drop");
            idw.setSubTitle1("itemName");
            idw.setSubTitle2("itemDropChance");
            idw.setSubTitle3("itemChance");
            idw.setJsonFile(MOD_BY_ENEMY_JSON);
            idw.setXlsxFile(MOD_BY_ENEMY_XLSX);
            if (enemyDropTables != null) {
                idw.writeJsonFile(enemyDropTables);
                idw.writeXlsxFile(enemyDropTables);
            }
            updateProgress(85, PROGRESS_MAX);

            LOG.info("Parse \"Blueprint/Part Drops by Enemy\" table.");
            table = page.querySelector("#" + EBT_ID + "+table");
            if (table != null) {
                ep.setDropTable(null);
                ep.setTable(table);
                enemyDropTables = ep.parse();
            } else {
               LOG.warn("\"Blueprint/Part Drops by Enemy\" table is not found.");
            }
            
            LOG.info("Parse \"Additional Item Drops by Enemy\" table.");
            table = page.querySelector("#" + AI_ID + "+table");
            if (table != null) {
                ep.setTable(table);
                enemyDropTables = ep.parse();
            } else {
               LOG.warn("\"Additional Item Drops by Enemy\" table is not found.");
            }
            
            LOG.info("Generate \"EnemyPartDrop\" files.");
            idw.setListSizeMax(ep.getListSizeMax());
            idw.setBoundaryKey(ep.getBoundaryKey());
            idw.setJsonFile(PART_BY_ENEMY_JSON);
            idw.setXlsxFile(PART_BY_ENEMY_XLSX);
            if (enemyDropTables != null) {
                idw.writeJsonFile(enemyDropTables);
                idw.writeXlsxFile(enemyDropTables);
            }
            
            updateProgress(90, PROGRESS_MAX);

            LOG.info("Parse \"Resource Drops by Enemy\" table.");
            table = page.querySelector("#" + MI_ID + "+table");
            if (table != null) {
                ep.setDropTable(null);
                ep.setTable(table);
                enemyDropTables = ep.parse();
            } else {
               LOG.warn("\"Resource Drops by Enemy\" table is not found.");
            }
            
            LOG.info("Generate \"EnemyResourceDrop\" files.");
            idw.setListSizeMax(ep.getListSizeMax());
            idw.setBoundaryKey(ep.getBoundaryKey());
            idw.setJsonFile(RESOURCE_BY_ENEMY_JSON);
            idw.setXlsxFile(RESOURCE_BY_ENEMY_XLSX);
            if (enemyDropTables != null) {
                idw.writeJsonFile(enemyDropTables);
                idw.writeXlsxFile(enemyDropTables);
            }
            
            updateProgress(95, PROGRESS_MAX);

            LOG.info("Parse \"Sigil Drops by Enemy\" table.");
            table = page.querySelector("#" + ST_ID + "+table");
            if (table != null) {
                ep.setDropTable(null);
                ep.setTable(table);
                enemyDropTables = ep.parse();
            } else {
               LOG.warn("\"Sigil Drops by Enemy\" table is not found.");
            }
            
            LOG.info("Generate \"EnemySigilDrop\" files.");
            idw.setListSizeMax(ep.getListSizeMax());
            idw.setBoundaryKey(ep.getBoundaryKey());
            idw.setJsonFile(SIGIL_BY_ENEMY_JSON);
            idw.setXlsxFile(SIGIL_BY_ENEMY_XLSX);
            if (enemyDropTables != null) {
                idw.writeJsonFile(enemyDropTables);
                idw.writeXlsxFile(enemyDropTables);
            }
            
            updateProgress(100, PROGRESS_MAX);

            LOG.info("Done.");
        } catch (Exception ex) {
            Logger.getLogger(ParseAndPrintTask.class.getName()).error(null, ex);
        }
        return 0;
    }
}
