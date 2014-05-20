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
package org.sonar.server.search;

import javax.annotation.CheckForNull;
import java.util.Map;

/**
 * Base implementation for business objects based on elasticsearch document
 */
public abstract class BaseDoc {

  private final Map<String,Object> fields;

  protected BaseDoc(Map<String,Object> fields) {
    this.fields = fields;
  }

  public String keyField() {
    return (String)fields.get("key");
  }

  @CheckForNull
  public <K> K getField(String key) {
    if (!fields.containsKey(key)) {
      throw new IllegalStateException(String.format("Field %s not specified in query options", key));
    }
    return (K)fields.get(key);
  }
}
