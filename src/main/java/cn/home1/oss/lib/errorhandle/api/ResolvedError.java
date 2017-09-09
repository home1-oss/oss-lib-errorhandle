package cn.home1.oss.lib.errorhandle.api;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Resolved error.
 *
 * <p>
 * Created by zhanghaolun on 16/7/1.
 * </p>
 */
@XmlRootElement(name = "error") // Jaxb2RootElementHttpMessageConverter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { //
  "error", "exception", "message", "path", "status", "timestamp", "trace", "validationErrors", //
  "datetime", "headers", "localizedMessage", "tracks"})
@JsonInclude(JsonInclude.Include.NON_EMPTY) // for Jackson 2.x
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY) // for Jackson 1.x
@Builder(builderMethodName = "resolvedErrorBuilder")
@AllArgsConstructor(access = PRIVATE)
@ToString
@EqualsAndHashCode(of = {"error", "exception", "path", "status", "timestamp", "localizedMessage"})
@Setter(PRIVATE)
@Getter
@Slf4j
public class ResolvedError implements Serializable {

  public static final String RESOLVED_ERROR_COOKIE = "resolvedErrorCookie";
  public static final String RESOLVED_ERROR_OBJECT = "resolvedError";
  static final String HEADER_RESOLVED_ERROR = "RESOLVED-ERROR";
  private static final long serialVersionUID = 1L;

  // ------------------------------ basic ------------------------------
  @JsonProperty("error")
  private String error;
  @XmlElement
  private String exception;
  private String message;
  private String path;
  private Integer status;
  private Long timestamp;
  private String trace;
  /**
   * Nested XmlElements see: https://github.com/FasterXML/jackson-module-jaxb-annotations/issues/42
   */
  @JsonProperty("validationErrors")
  @XmlElementWrapper(name = "validationErrors")
  @XmlElement(name = "validationError")
  //@XmlElements(@XmlElement(name = "validationError"))
  private ValidationError[] validationErrors;
  // ------------------------------ extended ------------------------------
  /**
   * ISO8601 string.
   */
  private String datetime;
  /**
   * 解析得到的 异常响应头信息.
   */
  @JsonProperty("headers")
  @XmlElementWrapper(name = "headers")
  @XmlElement(name = "header")
  private HttpHeader[] headers;
  /**
   * 解析得到的 错误信息.
   */
  private String localizedMessage;
  /**
   * 解析得到的 异常的调用路径 (RPC).
   */
  @JsonProperty("tracks")
  @XmlElementWrapper(name = "tracks")
  @XmlElement(name = "track")
  private String[] tracks;

  private ResolvedError() {
    this.headers = HttpHeader.fromHttpHeaders(newHttpHeaders());
  }

  public static HttpHeaders newHttpHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HEADER_RESOLVED_ERROR, HEADER_RESOLVED_ERROR);
    return headers;
  }

  public static ResolvedError fromErrorAttributes(final Map<String, Object> errorAttributes) {
    return errorAttributes == null ? null : ResolvedError.resolvedErrorBuilder()
      // basic
      .error((String) errorAttributes.get("error")) //
      .exception((String) errorAttributes.get("exception"))
      .message((String) errorAttributes.get("message")) //
      .path((String) errorAttributes.get("path")) //
      .status((Integer) errorAttributes.get("status")) //
      .timestamp((Long) errorAttributes.get("timestamp")) //
      .trace((String) errorAttributes.get("trace")) //
      .validationErrors((ValidationError[]) errorAttributes.get("validationErrors")) //
      // extended
      .datetime((String) errorAttributes.get("datetime"))
      .headers((HttpHeader[]) errorAttributes.get("headers")) //
      .localizedMessage((String) errorAttributes.get("localizedMessage")) //
      .tracks((String[]) errorAttributes.get("tracks")).build();
  }

  @JsonIgnore
  @org.codehaus.jackson.annotate.JsonIgnore
  public HttpStatus getHttpStatus() {
    HttpStatus result;
    try {
      result = HttpStatus.valueOf(this.status);
    } catch (final Exception ex) {
      result = HttpStatus.INTERNAL_SERVER_ERROR;
      log.debug("error parse http status {}", this.status, ex);
    }
    return result;
  }

  /**
   * before set into cookie, call this method to avoid header size exceed limit.
   *
   * @return this
   */
  public ResolvedError eraseTraces() {
    this.setTracks(null);
    this.setTrace(null);
    return this;
  }

  public ResolvedError trackPrepend(final String track) {
    this.tracks = this.tracks != null ? //
      ArrayUtils.add(this.tracks, 0, track) : //
      new String[]{track};
    return this;
  }

  public Map<String, Object> toErrorAttributes() {
    final Map<String, Object> map = newLinkedHashMap();
    // basic
    map.put("error", this.error);
    map.put("exception", this.exception);
    map.put("message", this.message);
    map.put("path", this.path);
    map.put("status", this.status);
    map.put("timestamp", this.timestamp);
    map.put("trace", this.trace);
    map.put("validationErrors", this.validationErrors);
    // extended
    map.put("datetime", datetime);
    map.put("headers", this.headers);
    map.put("localizedMessage", this.localizedMessage);
    map.put("tracks", this.tracks);

    map.entrySet().removeIf(e -> e.getValue() == null);
    return map;
  }
}
