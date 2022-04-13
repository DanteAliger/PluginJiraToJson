package com.json.plugin.servlet;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.velocity.VelocityManager;
import com.google.common.collect.Maps;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

//@Scanned
public class IssueCRUD extends HttpServlet{

    private static final String TEMPLATE= "servlet.vm";

    private final VelocityManager velocityManager;
    private final IssueService issueService;
    private final JiraAuthenticationContext authenticationContext;

    public IssueCRUD()
    {
        this.issueService = ComponentAccessor.getIssueService();
        this.velocityManager = ComponentAccessor.getVelocityManager();
        this.authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        ApplicationUser user = authenticationContext.getLoggedInUser();
        MutableIssue issue = Optional.ofNullable(issueService.getIssue(user, req.getParameter("key")).getIssue()).orElseThrow(IllegalArgumentException::new);
        Map<String, Object> context = Maps.newHashMap();
        context.put("json",toJson(issue));
        resp.getWriter().write(this.velocityManager.getEncodedBody("/",TEMPLATE,"UTF-8", context));
        resp.getWriter().close();
    }

    private JSONObject toJson (MutableIssue issue)
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("issue", issue.getKey());
            jsonObject.put("description", issue.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

