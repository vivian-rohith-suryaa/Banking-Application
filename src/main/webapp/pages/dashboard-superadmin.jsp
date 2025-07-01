<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>

<!-- Stylesheet -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/dashboard-superadmin.css">

<div class="superadmin-dashboard">
    <main class="dashboard-main">

        <!-- LEFT COLUMN -->
        <div class="left-section">
            <!-- Branch Insights Card -->
            <div class="branch-info-card">
                <header class="dashboard-header">
                    <h3>Branch Insights</h3>
                    <button class="go-to-branches" id="go-to-branches" title="Go to Branches">
                        <img src="<%=request.getContextPath()%>/icons/arrow-right.svg" alt="Go">
                    </button>
                </header>
                <div class="branch-overview-layout">
                    <div class="branch-sidebar" id="branch-sidebar"></div>
                    <div class="branch-details" id="branch-details"></div>
                </div>
            </div>

            <!-- Top Performing Branches -->
            <div class="admin-chart-card full-width">
                <h3>Top Performing Branches (by Transaction Volume)</h3>
                <canvas id="topBranchesChart"></canvas>
            </div>
        </div>

        <!-- RIGHT COLUMN -->
        <div class="right-section branch-chart-section">
            <div class="admin-chart-card">
                <h3>Frequency of Payment Modes</h3>
                <canvas id="paymentModeChart"></canvas>
            </div>

            <div class="admin-chart-card">
                <h3>Number of Accounts per Branch</h3>
                <canvas id="accountsPerBranchChart"></canvas>
            </div>
        </div>

    </main>
</div>

<!-- JS Globals -->
<script>
    window.contextPath = "<%=request.getContextPath()%>";
    window.sessionUserId = <%= userId %>;
    window.sessionBranchId = <%= branchId %>;
    window.sessionRole = <%= role %>;
</script>
