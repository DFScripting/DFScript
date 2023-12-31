package io.github.techstreet.dfscript.util;

import javax.swing.JOptionPane;

public class PopUpUtil {
    public static int messageBox(String title, String message, String[] options) {
        return JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }

    public static String inputBox(String title, String message) {
        return JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
    }
}
