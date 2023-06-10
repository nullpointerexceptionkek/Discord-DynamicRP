/*
 *
 * MIT License
 *
 * Copyright (c) 2023 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.dynamicrp.exceptions;

import lee.aspect.dev.dynamicrp.Launch;
import lee.aspect.dev.dynamicrp.application.core.Script;
import lee.aspect.dev.dynamicrp.json.loader.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static String getSystemInfo(String thread) {
        return "********** system Information ***********\n" +
                "Client Version: " + Launch.VERSION + "\n" +
                "Operating system: " + System.getProperty("os.name") + "\n" +
                "Operating system Version: " + System.getProperty("os.version") + "\n" +
                "Java Version: " + System.getProperty("java.version") + "\n" +
                "Java Vendor: " + System.getProperty("java.vendor") + '\n' +
                "Thread: " + thread +
                "\n***************************************\n";
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        //there is no need to every touch this code again unless for gui improvements
        //the code below is for grabbing the uncaught exception and displaying it in a gui
        try{
            FileManager.saveAllFiles();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        e.printStackTrace();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = getSystemInfo(t.getName()) + "\n" + sw;

        JTextArea textArea = new JTextArea(stackTrace);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener((event) -> {
            StringSelection selection = new StringSelection(stackTrace);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(null, "Error message copied to clipboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton exitButton = new JButton("Exit Application");
        exitButton.addActionListener((event) -> System.exit(1));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(exitButton);
        buttonPanel.add(copyButton);

        JLabel messageLabel = new JLabel("<html><body style='text-align:center;'>" +
                "Sorry for the inconvenience.<br>" +
                "An &quot;" + e.getClass().getSimpleName() + "&quot; has occurred at Thread " + t.getName() + "." +
                "</body></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(scrollPane, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.SOUTH);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, messagePanel, "Error", JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }

}