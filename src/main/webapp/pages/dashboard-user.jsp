<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/dashboard-user.css">
<div class="dashboard-layout">
    <section class="dashboard-main">
        <div class="account">
            <div class="account-header">
                <h3>Accounts</h3>
                <button class="go-to-accounts" id = "Go to Accounts" title="Go to Accounts">
                    <img src="<%=request.getContextPath()%>/icons/arrow-right.svg" alt="Go">
                </button>
            </div>
             <div class="account-sidecard">
	            <div class="account-sidebar"></div>
	            <div class="account-details" id="account-details"></div>
	         </div>
        </div>

        <div class="charts-section">
            <div class="user-chart-card">
                <h3>Payment Mode Frequency</h3>
                <canvas id="paymentModeChart"></canvas>
            </div>
        </div>
    </section>

    <section class="transactions-section">
        <h3>Recent Transactions</h3>
        <div id="transaction-list" class="transaction-list"></div>
	    <button id="view-transactions-btn">View All Transactions</button>

    </section>
</div>

<script>
    window.contextPath = "<%= request.getContextPath() %>";
    window.sessionUserId = <%= userId %>;
    window.sessionBranchId = <%= branchId %>;
    window.sessionRole = <%= role %>;
</script>
<script type="module" src="<%=request.getContextPath()%>/scripts/dashboard-user.js"></script>
