package org.openqa.selenium.server.commands;

import org.openqa.selenium.internal.Base64Encoder;
import org.openqa.selenium.server.RobotRetriever;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Captures a full screen shot of the current screen using the java.awt.Robot class. and returns it
 * as a base 64 encoded PNG image.
 */
public class CaptureScreenshotToStringCommand {

  public static final String ID = "captureScreenshotToString";
  private static final Logger log = Logger.getLogger(CaptureScreenshotToStringCommand.class
      .getName());


  public String execute() {
    try {
      return "OK," + captureAndEncodeSystemScreenshot();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Problem capturing a screenshot to string", e);
      return "ERROR: Problem capturing a screenshot to string: " + e.getMessage();
    }
  }


  public String captureAndEncodeSystemScreenshot() throws InterruptedException, ExecutionException,
      TimeoutException, IOException {
    final ByteArrayOutputStream outStream;
    final BufferedImage bufferedImage;
    final Rectangle captureSize;
    final byte[] encodedData;
    final Robot robot;

    robot = RobotRetriever.getRobot();
    captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    bufferedImage = robot.createScreenCapture(captureSize);
    outStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "png", outStream);

    return new Base64Encoder().encode(outStream.toByteArray());
  }
}
