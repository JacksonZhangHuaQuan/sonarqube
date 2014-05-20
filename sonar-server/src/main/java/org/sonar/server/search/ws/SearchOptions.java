/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.search.ws;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.server.search.Result;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Generic options for search web services
 */
public class SearchOptions {

  public static final String PARAM_TEXT_QUERY = "q";
  public static final String PARAM_PAGE = "p";
  public static final String PARAM_PAGE_SIZE = "ps";
  public static final String PARAM_FIELDS = "f";
  public static final String PARAM_SORT = "s";
  public static final String PARAM_ASCENDING = "asc";

  private int pageSize;
  private int page;
  private List<String> fields;

  public int pageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * 1-based page id
   */
  public int page() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  @CheckForNull
  public List<String> fields() {
    return fields;
  }

  public void setFields(@Nullable List<String> fields) {
    this.fields = fields;
  }

  public boolean hasField(String key) {
    return fields == null || fields.contains(key);
  }

  public SearchOptions writeStatistics(JsonWriter json, Result searchResult) {
    json.prop("total", searchResult.getTotal());
    json.prop(PARAM_PAGE, page);
    json.prop(PARAM_PAGE_SIZE, pageSize);
    return this;
  }

  public static SearchOptions create(Request request) {
    SearchOptions options = new SearchOptions();
    options.setPage(request.mandatoryParamAsInt(PARAM_PAGE));
    options.setPageSize(request.mandatoryParamAsInt(PARAM_PAGE_SIZE));
    options.setFields(request.paramAsStrings(PARAM_FIELDS));
    return options;
  }

  public static void defineGenericParameters(WebService.NewAction action, Object... possibleValues) {
    WebService.NewParam newParam = action
      .createParam(PARAM_FIELDS)
      .setDescription("Comma-separated list of the fields to be returned in response. All the fields are returned by default.")
      .setPossibleValues(possibleValues);
    if (newParam.possibleValues() != null && newParam.possibleValues().size() > 1) {
      Iterator<String> it = newParam.possibleValues().iterator();
      newParam.setExampleValue(String.format("%s,%s", it.next(), it.next()));
    }

    action
      .createParam(PARAM_PAGE)
      .setDescription("1-based page number")
      .setExampleValue("42")
      .setDefaultValue("1");

    action
      .createParam(PARAM_PAGE_SIZE)
      .setDescription("Page size. Must be greater than 0.")
      .setExampleValue("10")
      .setDefaultValue("25");
  }
}
