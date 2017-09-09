package cn.home1.oss.lib.errorhandle.api;

/**
 * Created by haolun on 17/1/5.
 */
public final class ResolvedErrorTestScenario {

  private ResolvedErrorTestScenario() {
  }

  public static ResolvedError resolvedErrorScenario() {
    final ValidationError validationError = ValidationError.validationErrorBuilder()
      .field("field")
      .rejected("rejected")
      .message("message")
      .build();

    return ResolvedError.resolvedErrorBuilder()
      .error("error")
      .validationErrors(new ValidationError[]{validationError})
      .exception("exception")
      .message("message")
      .path("path")
      .status(401)
      .timestamp(201701031643L)
      .trace("trace")
      //
      .datetime("datetime")
      .headers(HttpHeader.fromHttpHeaders(ResolvedError.newHttpHeaders()))
      .localizedMessage("localizedMessage")
      .tracks(new String[]{"track1", "track2"})
      .build();
  }
}
