/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.permission;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.ibatis.session.RowBounds;
import org.sonar.db.Dao;
import org.sonar.db.DatabaseUtils;
import org.sonar.db.DbSession;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static org.sonar.db.DatabaseUtils.executeLargeInputs;

public class UserPermissionDao implements Dao {

  /**
   * @see UserPermissionMapper#selectByQuery(PermissionQuery, Collection, RowBounds)
   */
  public List<ExtendedUserPermissionDto> select(DbSession dbSession, PermissionQuery query, @Nullable Collection<String> userLogins) {
    if (userLogins != null) {
      if (userLogins.isEmpty()) {
        return emptyList();
      }
      checkArgument(userLogins.size() <= DatabaseUtils.PARTITION_SIZE_FOR_ORACLE, "Maximum 1'000 users are accepted");
    }

    RowBounds rowBounds = new RowBounds(query.getPageOffset(), query.getPageSize());
    return mapper(dbSession).selectByQuery(query, userLogins, rowBounds);
  }

  /**
   * @see UserPermissionMapper#countUsersByQuery(PermissionQuery, Collection)
   */
  public int countUsers(DbSession dbSession, PermissionQuery query) {
    return mapper(dbSession).countUsersByQuery(query, null);
  }

  /**
   * Count the number of users per permission for a given list of projects
   *
   * @param projectIds a non-null list of project ids to filter on. If empty then an empty list is returned.
   */
  public List<UserCountPerProjectPermission> countUsersByProjectPermission(DbSession dbSession, Collection<Long> projectIds) {
    return executeLargeInputs(projectIds, mapper(dbSession)::countUsersByProjectPermission);
  }

  public void insert(DbSession dbSession, UserPermissionDto dto) {
    mapper(dbSession).insert(dto);
  }

  private static UserPermissionMapper mapper(DbSession dbSession) {
    return dbSession.getMapper(UserPermissionMapper.class);
  }
}
