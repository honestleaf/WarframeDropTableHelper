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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class StringParser {

    private final Pattern nodePattern = Pattern.compile("(.+?)/(.+?) \\((.+?)\\) *(.*)");
    private final Pattern rotationPattern = Pattern.compile("Rotation (A|B|C)");
    private final Pattern chancePattern = Pattern.compile("([0-9\\.]+)%");
    private final Pattern bountyTierPattern = Pattern.compile("Level .+? Bounty");
    private final Pattern bountyStagePattern = Pattern.compile("Stage .*");

    /**
     *
     * @param s
     * @return String[]{planet, node, mtype}
     */
    public String[] parseNode(String s) {
        Matcher m = nodePattern.matcher(s);
        String planet, node, mtype;
        if (m.find()) {
            planet = m.group(1);
            node = m.group(2);
            mtype = m.group(4).isEmpty() ? m.group(3) : m.group(3) + " " + m.group(4);
            return new String[]{planet, node, mtype};
        } else {
            return null;
        }
    }

    public String parseRotation(String s) {
        Matcher m = rotationPattern.matcher(s);
        String rotation;
        if (m.find()) {
            rotation = m.group(1);
            return rotation;
        } else {
            return null;
        }
    }

    public float parseChance(String s) {
        Matcher m = chancePattern.matcher(s);
        BigDecimal bd;
        float dropChance;
        if (m.find()) {
            bd = new BigDecimal(m.group(1));
            dropChance = bd.divide(BigDecimal.valueOf(100)).floatValue();
            return dropChance;
        } else {
            return -1;
        }
    }

    public String parseBountyTier(String s) {
        Matcher m = bountyTierPattern.matcher(s);
        String bountyTier;
        if (m.find()) {
            bountyTier = m.group(0);
            return bountyTier;
        } else {
            return null;
        }
    }

    public String parseBountyStage(String s) {
        Matcher m = bountyStagePattern.matcher(s);
        String bountyStage;
        if (m.find()) {
            bountyStage = m.group(0);
            return bountyStage;
        } else {
            return null;
        }
    }
}
