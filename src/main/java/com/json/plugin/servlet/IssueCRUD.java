package com.json.plugin.servlet;


import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.query.Query;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.velocity.VelocityManager;
import com.google.common.collect.Maps;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Scanned
public class IssueCRUD extends HttpServlet{

    private static final String ISSUE= "/servlet.vm";

//    @JiraImport
    private VelocityManager velocityManager;
    private IssueService issueService;
    private JiraAuthenticationContext authenticationContext;


    public IssueCRUD() {
        this.issueService = ComponentAccessor.getIssueService();
        this.velocityManager = ComponentAccessor.getVelocityManager();
        this.authenticationContext = ComponentAccessor.getJiraAuthenticationContext();


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        ApplicationUser user = authenticationContext.getLoggedInUser();
        MutableIssue issue = Optional.ofNullable(issueService.getIssue(user, req.getParameter("key")).getIssue()).orElseThrow(() -> new IllegalArgumentException());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("issue", issue.getKey());
            jsonObject.put("description", issue.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, Object> context = Maps.newHashMap();
        context.put("json",jsonObject);
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().write(this.velocityManager.getEncodedBody("/","servlet.vm","UTF-8", context));
        resp.getWriter().write(req.getContextPath());
        resp.getWriter().close();
    }
}

