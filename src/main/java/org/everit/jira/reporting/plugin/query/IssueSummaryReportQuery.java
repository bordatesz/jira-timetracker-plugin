/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.jira.reporting.plugin.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.everit.jira.reporting.plugin.dto.IssueSummaryDTO;
import org.everit.jira.reporting.plugin.dto.ReportSearchParam;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLQuery;

/**
 * Query for issue summary report.
 */
public class IssueSummaryReportQuery extends AbstractListReportQuery<IssueSummaryDTO> {

  public IssueSummaryReportQuery(final ReportSearchParam reportSearchParam) {
    super(reportSearchParam);
  }

  @Override
  public List<IssueSummaryDTO> call(final Connection connection, final Configuration configuration)
      throws SQLException {
    SQLQuery<IssueSummaryDTO> query = new SQLQuery<IssueSummaryDTO>(connection, configuration)
        .select(createSelectProjection());

    appendBaseFromAndJoin(query);
    appendBaseWhere(query);

    query.groupBy(createGroupBy());
    return query.fetch();
  }

  private Expression<?>[] createGroupBy() {
    return new Expression<?>[] {
        qProject.pkey,
        qIssue.issuenum,
        qIssue.summary,
        qIssuetype.pname,
        qPriority.pname,
        qIssuestatus.pname,
        qIssue.assignee };
  }

  private QBean<IssueSummaryDTO> createSelectProjection() {
    return Projections.bean(IssueSummaryDTO.class,
        createIssueKeyExpression(qIssue, qProject).as(IssueSummaryDTO.AliasNames.ISSUE_KEY),
        qIssue.summary.as(IssueSummaryDTO.AliasNames.ISSUE_SUMMARY),
        qIssuetype.pname.as(IssueSummaryDTO.AliasNames.ISSUE_TYPE_NAME),
        qPriority.pname.as(IssueSummaryDTO.AliasNames.PRIORITY_NAME),
        qIssuestatus.pname.as(IssueSummaryDTO.AliasNames.STATUS_NAME),
        qIssue.assignee.as(IssueSummaryDTO.AliasNames.ASSIGNEE),
        qIssue.timeoriginalestimate.sum()
            .as(IssueSummaryDTO.AliasNames.ISSUE_ORIGINAL_ESTIMATE_SUM),
        qIssue.timeestimate.sum().as(IssueSummaryDTO.AliasNames.ISSUE_TIME_ESTIMATE_SUM),
        qWorklog.timeworked.sum().as(IssueSummaryDTO.AliasNames.WORKLOGGED_TIME_SUM));
  }

}