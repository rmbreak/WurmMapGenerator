package net.buddat.wgenerator.util;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class StreamCapturer extends OutputStream {

    private StringBuilder buffer;
    private PrintStream old;

    public StreamCapturer(PrintStream old) {
        buffer = new StringBuilder(128);
        this.old = old;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            JOptionPane.showMessageDialog(null, buffer.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            buffer.delete(0, buffer.length());
        }
        old.print(c);
    }
}    