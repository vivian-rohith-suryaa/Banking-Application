<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/dashboard-employee.css">
<div class="dashboard-layout">
    <section class="dashboard-top">
        <!-- Left Column: Requests -->
        <div class="requests-card">
            <h3>Pending Requests</h3>
            <div id="request-list"></div>
            <button id="view-requests-btn">View All Requests</button>
        </div>

        <!-- Right Column: Branch Overview + Chart -->
        <div class="right-column">
            <div class="branch-card">
                <h3>Branch Overview</h3>
                <div id="branch-overview"></div>
            </div>

            <div class="employee-chart-card">
                <h3>Payment Mode Frequency</h3>
                <canvas id="paymentModeChart"></canvas>
            </div>
        </div>
    </section>
</div>

<!-- Modal -->
<div id="modal-overlay"></div>
<div id="request-modal"></div>

<script>
    window.contextPath = "<%= request.getContextPath() %>";
    window.sessionUserId = <%= userId %>;
    window.sessionBranchId = <%= branchId %>;
    window.sessionRole = <%= role %>;
</script>
<script type="module" src="${pageContext.request.contextPath}/scripts/dashboard-employee.js"></script>
