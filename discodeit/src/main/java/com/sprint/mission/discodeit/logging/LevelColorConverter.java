package com.sprint.mission.discodeit.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class LevelColorConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

  @Override
  protected String getForegroundColorCode(ILoggingEvent event) {
    return switch (event.getLevel().toInt()) {
      case Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG;
      case Level.WARN_INT -> ANSIConstants.YELLOW_FG;
      case Level.INFO_INT -> ANSIConstants.GREEN_FG;
      case Level.DEBUG_INT -> ANSIConstants.BLUE_FG;
      case Level.TRACE_INT -> ANSIConstants.WHITE_FG;
      default -> ANSIConstants.DEFAULT_FG;
    };
  }
}