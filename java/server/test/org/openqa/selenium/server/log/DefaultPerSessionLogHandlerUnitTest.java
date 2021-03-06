package org.openqa.selenium.server.log;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * {@link org.openqa.selenium.server.log.PerSessionLogHandler} unit test class.
 */
public class DefaultPerSessionLogHandlerUnitTest extends TestCase {

  private static final int CAPACITY = 1;

  public void testThreadToSessionMappingOnInitialNullSession()
      throws IOException {
    PerSessionLogHandler handler = createPerSessionLogHandler();
    LogRecord firstRecord = new LogRecord(Level.INFO, "First Log Record");
    LogRecord secondRecord = new LogRecord(Level.INFO, "Second Log Record");

    handler.publish(firstRecord);
    handler.attachToCurrentThread("session-1");
    handler.publish(secondRecord);

    assertMessagesLoggedForSessionId(handler, "session-1",
                                     "First Log Record", "Second Log Record");
  }

  public void testThreadToSessionMappingOnTwoInitialNullSessions()
      throws IOException {
    PerSessionLogHandler handler = createPerSessionLogHandler();
    LogRecord firstRecord = new LogRecord(Level.INFO, "First Log Record");
    LogRecord secondRecord = new LogRecord(Level.INFO, "Second Log Record");

    LogRecord anotherRecord = new LogRecord(Level.INFO,
                                            "Another Log Record");
    LogRecord oneMoreRecord = new LogRecord(Level.INFO,
                                            "One More Log Record");

    handler.publish(firstRecord);
    handler.attachToCurrentThread("session-1");
    handler.publish(secondRecord);
    handler.detachFromCurrentThread();
    handler.publish(anotherRecord);
    handler.attachToCurrentThread("session-2");
    handler.publish(oneMoreRecord);

    assertMessagesLoggedForSessionId(handler, "session-1",
                                     firstRecord.getMessage(), "Second Log Record");
    assertMessagesLoggedForSessionId(handler, "session-2",
                                     "Another Log Record", "One More Log Record");
  }

  public void testThreadToSessionMappingAndClearMapping() throws IOException {
    PerSessionLogHandler handler = createPerSessionLogHandler();
    LogRecord firstSessionLog = new LogRecord(Level.INFO,
                                              "First Session Related Log Record");
    LogRecord secondSessionLog = new LogRecord(Level.INFO,
                                               "Second Session Related Log Record");

    // set logs for session-1
    handler.attachToCurrentThread("session-one");
    handler.publish(firstSessionLog);
    handler.detachFromCurrentThread();

    // set logs for session-2
    handler.attachToCurrentThread("session-two");
    handler.publish(secondSessionLog);
    handler.detachFromCurrentThread();

    assertMessagesLoggedForSessionId(handler, "session-one",
                                     "First Session Related Log Record");
    assertMessagesLoggedForSessionId(handler, "session-two",
                                     "Second Session Related Log Record");
  }

  public void testShouldReturnEmptyLogIfNoRecordHasBeenLogged()
      throws IOException {
    PerSessionLogHandler handler = createPerSessionLogHandler();

    assertNoMessageLoggedForSessionId(handler, "session");
  }

  public void testShouldNotCopyThreadTempLogsToSessionLogsIfNoLogRecordForThreadPresent()
      throws IOException {
    PerSessionLogHandler handler = createPerSessionLogHandler();

    handler.transferThreadTempLogsToSessionLogs("session");

    assertNoMessageLoggedForSessionId(handler, "session");
  }

  private void assertMessagesLoggedForSessionId(PerSessionLogHandler handler,
                                                String sessionId, String... expectedMessages)
      throws IOException {
    StringBuilder expectedLogMessage = new StringBuilder(
        "\n<RC_Logs RC_Session_ID=");
    expectedLogMessage.append(sessionId);
    expectedLogMessage.append(">\n");
    for (String expectedMessage : expectedMessages) {
      expectedLogMessage.append("[FORMATTED] ");
      expectedLogMessage.append(expectedMessage);
    }
    expectedLogMessage.append("\n</RC_Logs>\n");

    String loggedMessage = handler.getLog(sessionId);
    assertEquals("Wrong message logged.", expectedLogMessage.toString(),
                 loggedMessage);
  }

  private void assertNoMessageLoggedForSessionId(
      PerSessionLogHandler handler, String sessionId) throws IOException {
    assertMessagesLoggedForSessionId(handler, sessionId);
  }

  private PerSessionLogHandler createPerSessionLogHandler() {
    return new DefaultPerSessionLogHandler(CAPACITY, Level.INFO,
                                    new FormatterStub());
  }

  static class FormatterStub extends Formatter {

    @Override
    public String format(LogRecord record) {
      return "[FORMATTED] " + record.getMessage();
    }
  }
}
