package io.github.techstreet.dfscript.util;

import javax.swing.*;

public class PopUpUtil {

    public static int messageBox(String title, String message, String[] options) {
        final JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        return JOptionPane.showOptionDialog(dialog, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }

    public static String inputBox(String title, String message) {
        final JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        return JOptionPane.showInputDialog(dialog, message, title, JOptionPane.PLAIN_MESSAGE);
    }
}
