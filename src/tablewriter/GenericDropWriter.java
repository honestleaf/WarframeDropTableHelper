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

import java.io.File;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public abstract class GenericDropWriter<T> {

    protected File jsonFile = null;
    protected File xlsxFile = null;

    public GenericDropWriter() {

    }

    public GenericDropWriter(String jsonFilePath, String xlsxFilePath) {
        this.jsonFile = new File(jsonFilePath);
        this.xlsxFile = new File(xlsxFilePath);
    }

    public void setJsonFile(String jsonFilePath) {
        this.jsonFile = new File(jsonFilePath);
    }

    public void setXlsxFile(String xlsxFilePath) {
        this.xlsxFile = new File(xlsxFilePath);
    }

    public abstract void writeJsonFile(T drops);

    public abstract void writeXlsxFile(T drops);

}
