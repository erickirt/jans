/*
 * oxd-server
 * oxd-server
 *
 * OpenAPI spec version: 4.0
 * Contact: yuriyz@gluu.org
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * UmaRsCheckAccessParams
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-02-13T06:19:07.893Z[GMT]")
public class UmaRsCheckAccessParams {
  @SerializedName("oxd_id")
  private String oxdId = null;

  @SerializedName("rpt")
  private String rpt = null;

  @SerializedName("path")
  private String path = null;

  @SerializedName("http_method")
  private String httpMethod = null;

  @SerializedName("scopes")
  private List<String> scopes = null;

  public UmaRsCheckAccessParams oxdId(String oxdId) {
    this.oxdId = oxdId;
    return this;
  }

   /**
   * Get oxdId
   * @return oxdId
  **/
  @Schema(example = "bcad760f-91ba-46e1-a020-05e4281d91b6", required = true, description = "")
  public String getOxdId() {
    return oxdId;
  }

  public void setOxdId(String oxdId) {
    this.oxdId = oxdId;
  }

  public UmaRsCheckAccessParams rpt(String rpt) {
    this.rpt = rpt;
    return this;
  }

   /**
   * Get rpt
   * @return rpt
  **/
  @Schema(required = true, description = "")
  public String getRpt() {
    return rpt;
  }

  public void setRpt(String rpt) {
    this.rpt = rpt;
  }

  public UmaRsCheckAccessParams path(String path) {
    this.path = path;
    return this;
  }

   /**
   * Get path
   * @return path
  **/
  @Schema(required = true, description = "")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public UmaRsCheckAccessParams httpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
    return this;
  }

   /**
   * Get httpMethod
   * @return httpMethod
  **/
  @Schema(required = true, description = "")
  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public UmaRsCheckAccessParams scopes(List<String> scopes) {
    this.scopes = scopes;
    return this;
  }

  public UmaRsCheckAccessParams addScopesItem(String scopesItem) {
    if (this.scopes == null) {
      this.scopes = new ArrayList<String>();
    }
    this.scopes.add(scopesItem);
    return this;
  }

   /**
   * Get scopes
   * @return scopes
  **/
  @Schema(example = "[\"http://photoz.example.com/dev/actions/all\",\"http://photoz.example.com/dev/actions/add\"]", description = "")
  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UmaRsCheckAccessParams umaRsCheckAccessParams = (UmaRsCheckAccessParams) o;
    return Objects.equals(this.oxdId, umaRsCheckAccessParams.oxdId) &&
        Objects.equals(this.rpt, umaRsCheckAccessParams.rpt) &&
        Objects.equals(this.path, umaRsCheckAccessParams.path) &&
        Objects.equals(this.httpMethod, umaRsCheckAccessParams.httpMethod) &&
        Objects.equals(this.scopes, umaRsCheckAccessParams.scopes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oxdId, rpt, path, httpMethod, scopes);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UmaRsCheckAccessParams {\n");
    
    sb.append("    oxdId: ").append(toIndentedString(oxdId)).append("\n");
    sb.append("    rpt: ").append(toIndentedString(rpt)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    httpMethod: ").append(toIndentedString(httpMethod)).append("\n");
    sb.append("    scopes: ").append(toIndentedString(scopes)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
