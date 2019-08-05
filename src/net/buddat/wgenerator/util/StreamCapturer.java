package net.buddat.wgenerator.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.buddat.wgenerator.MainWindow;

/** Retrieves text from the given print stream and logs it to the interface */
public class StreamCapturer extends OutputStream {

  private StringBuilder buffer;
  private PrintStream old;
  private MainWindow window;

  public StreamCapturer(PrintStream old, MainWindow window) {
    buffer = new StringBuilder(128);
    this.old = old;
    this.window = window;
  }

  @Override
  public void write(int b) throws IOException {
    char c = (char) b;
    String value = Character.toString(c);
    buffer.append(value);
    if (value.equals("\n")) {
      String mes = buffer.toString();
      if (mes.indexOf("xception") > -1 || mes.indexOf("\t") == 0) {
        window.submitError(mes);
      }
      buffer.delete(0, buffer.length());
    }
    old.print(c);
  }
}