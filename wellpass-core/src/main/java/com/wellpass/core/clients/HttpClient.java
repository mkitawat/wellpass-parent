package com.wellpass.core.clients;

import com.wellpass.core.exceptions.http.HttpNotAuthorizedException;
import com.wellpass.core.exceptions.http.HttpResponseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for calling HTTP services, e.g. for web callbacks.
 */
public class HttpClient {
  private final RequestConfig httpConfig;

  public HttpClient() {
    httpConfig = RequestConfig.custom()
        .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
        .build();
  }

  public static boolean isOkStatus(CloseableHttpResponse response) {
    int statusCode = response.getStatusLine().getStatusCode();
    return (statusCode >= 200 && statusCode < 300);
  }

  public String callUrl(String url, Map<String, String> paramMap) throws IOException {
    HttpPost request = new HttpPost(url);
    request.setConfig(httpConfig);

    List<BasicNameValuePair> params = paramMap.entrySet()
        .stream().map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
    request.setEntity(new UrlEncodedFormEntity(params));

    //request.setHeader("user-agent", "SOME HEADER");
    CloseableHttpResponse response = HttpClients.createDefault().execute(request);
    String body = EntityUtils.toString(response.getEntity(), "UTF-8");
    response.close();

    if (!isOkStatus(response)) {
      throw new IOException(response.getStatusLine().getStatusCode() + " " + body);
    }

    return body;
  }

  public String postJson(String url, String json) throws IOException {
    HttpPost request = new HttpPost(url);

    StringEntity se = new StringEntity(json);
    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    request.setEntity(se);

    CloseableHttpResponse response = HttpClients.createDefault().execute(request);
    String body = EntityUtils.toString(response.getEntity(), "UTF-8");
    response.close();

    if (!isOkStatus(response)) {
      throw new IOException(response.getStatusLine().getStatusCode() + " " + body);
    }

    return body;
  }

  public String putJson(String url, String json) throws IOException {
    HttpPut request = new HttpPut(url);

    StringEntity se = new StringEntity(json);
    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    request.setEntity(se);

    CloseableHttpResponse response = HttpClients.createDefault().execute(request);
    String body = EntityUtils.toString(response.getEntity(), "UTF-8");
    response.close();

    if (!isOkStatus(response)) {
      throw new IOException(response.getStatusLine().getStatusCode() + " " + body);
    }

    return body;
  }

  public CloseableHttpResponse get(String uri) throws IOException {
    return get(URI.create(uri));
  }

  public CloseableHttpResponse get(URI uri) throws IOException {
    return HttpClients.createDefault().execute(new HttpGet(uri));
  }

  public InputStream getContentAsInputStream(URI uri) throws IOException {
    CloseableHttpResponse response = get(uri);
    if (response.getEntity() == null || response.getEntity().getContent() == null) {
      int statusCode = response.getStatusLine().getStatusCode();
      String reasonPhrase = response.getStatusLine().getReasonPhrase();
      String messageBody = response.getEntity().toString();
      switch (statusCode) {
        case 401:
          throw new HttpNotAuthorizedException("Not authorized", reasonPhrase, messageBody);
        default:
          throw new HttpResponseException("An error occurred", statusCode, reasonPhrase, messageBody);
      }
    }

    return new HttpContentInputStream(response);
  }

  public Reader getContentAsReader(URI uri) throws IOException {
    return getContentAsReader(uri, null);
  }

  public Reader getContentAsReader(URI uri, Charset overideCharset) throws IOException {
    HttpContentInputStream is = (HttpContentInputStream)getContentAsInputStream(uri);
    if (overideCharset == null) {
      overideCharset = is.getCharset();
    }
    return overideCharset != null ? new InputStreamReader(is, overideCharset) : new InputStreamReader(is);
  }

  private static class HttpContentInputStream extends FilterInputStream {
    private CloseableHttpResponse wrappedResponse;
    private ContentType contentType;

    HttpContentInputStream(CloseableHttpResponse response) throws IOException {
      super(response.getEntity().getContent());
      this.wrappedResponse = response;
      this.contentType = ContentType.get(wrappedResponse.getEntity());
    }

    public String getContentType() {
      if (contentType == null) return null;
      return contentType.getMimeType();
    }

    public Charset getCharset() {
      if (contentType == null) return null;
      return contentType.getCharset();
    }
  }

}
