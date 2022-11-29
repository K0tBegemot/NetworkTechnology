package Logger;

import java.util.Calendar;
import java.util.logging.*;

// logger class which can have only two instances - exception logger and workflow logger
public class GlobalLogger {
    public enum LoggerType {EXCEPTION_LOGGER, WORKFLOW_LOGGER};
    public enum Mode {ENABLE, DISABLE}  // logger mode (messages will be logging if mode equals 'ENABLE')

    private Logger logger;
    private Mode mode = Mode.ENABLE;    // logger is enabled by default

    // singleton helper
    public static class LoggerCreator {
        private static GlobalLogger exceptionLogger;
        private static GlobalLogger workflowLogger;

        public static GlobalLogger getLogger(LoggerType type) {
            switch (type) {
                case WORKFLOW_LOGGER: {
                    if (workflowLogger == null) {
                        workflowLogger = new GlobalLogger(type);
                    }
                    return workflowLogger;
                }
                case EXCEPTION_LOGGER: {
                    if (exceptionLogger == null) {
                        exceptionLogger = new GlobalLogger(type);
                    }
                    return exceptionLogger;
                }
                default:
                    assert false;
            }
            return null;
        }
    }
    private GlobalLogger(LoggerType type) {
        switch (type) {
            case EXCEPTION_LOGGER: {
                logger = Logger.getLogger("ExceptionLogger");
                logger.setUseParentHandlers(false);
                // config handler
                ConsoleHandler handler = new ConsoleHandler();
                /*
                try {
                    handler = new FileHandler("exceptions_%u.log");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                */
                handler.setFormatter(new SimpleFormatter());
                // add handler to logger
                logger.addHandler(handler);
                break;
            }
            case WORKFLOW_LOGGER: {
                logger = Logger.getLogger("WorkflowLogger");
                logger.setUseParentHandlers(false);
                // config handler
                ConsoleHandler handler = new ConsoleHandler();
                handler.setFormatter(new SimpleFormatter());
                // add handler to logger
                logger.addHandler(handler);
                break;
            }
            default:
                assert false;
        }
    }
    private static class SimpleFormatter extends Formatter {
        @Override
        public String format(LogRecord logRecord) {
            Calendar calendar = Calendar.getInstance(); // set current date and time
            String date = calendar.get(Calendar.DATE) + "." + calendar.get(Calendar.MONTH) +
                    "." + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR) +
                    ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
            // log message represents: date + level + message
            return date + "\n" + logRecord.getLevel() + ": " + logRecord.getMessage();
        }
    }
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    public void log(Level level, String msg) {
        if (this.mode == Mode.ENABLE) {
            logger.log(level, msg);
        }
    }
}
