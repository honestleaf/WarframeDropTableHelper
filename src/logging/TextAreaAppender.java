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
package logging;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author Honestleaf<Cheng.Ye.HL@hotmail.com>
 */
public class TextAreaAppender extends WriterAppender {

    private static Logger log = Logger.getLogger(TextAreaAppender.class.getName());

    private static volatile TextArea textArea = null;

    /**
     * Set the target TextArea for the logging information to appear.
     *
     * @param textArea
     */
    public static void setTextArea(final TextArea textArea) {
        TextAreaAppender.textArea = textArea;
    }

    /**
     * Format and then append the loggingEvent to the stored TextArea.
     *
     * @param loggingEvent
     */
    @Override
    public void append(final LoggingEvent loggingEvent) {
        String message = this.layout.format(loggingEvent);

        Platform.runLater(() -> {
            try {
                if (textArea != null) {
                    if (textArea.getText().length() == 0) {
                        textArea.setText(message);
                    } else {
                        textArea.selectEnd();
                        textArea.insertText(textArea.getText().length(),
                                message);
                    }
                }
            } catch (final Throwable t) {
                log.warn("Unable to append log to text area", t);
            }
        });

    }
}
