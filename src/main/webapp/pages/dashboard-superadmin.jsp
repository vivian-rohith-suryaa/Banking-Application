<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>

<!-- Stylesheet -->
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/styles/dashboard-superadmin.css">

<!-- Superadmin Dashboard Container -->
<div class="superadmin-dashboard">

	<!-- Header -->
	<header class="dashboard-header">
		<h2>Branch Insights</h2>
		<button class="go-to-branches" id="go-to-branches" title="Go to Branches">
            <img src="<%=request.getContextPath()%>/icons/arrow-right.svg" alt="Go">
        </button>
	</header>

	<!-- Main Panel -->
	<main class="dashboard-main">

		<!-- Branch Section -->
		<div class="branch-info-card">
			<div class="branch-sidebar" id="branch-sidebar"></div>
			<div class="branch-details" id="branch-details"></div>
		</div>

		<!-- Charts Section -->
		<div class="branch-chart-section">
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
    window.contextPath = "<%= request.getContextPath() %>";
    window.sessionUserId = <%= userId %>;
    window.sessionBranchId = <%= branchId %>;
    window.sessionRole = <%= role %>;
</script>




<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/dashboard-superadmin.css">
<div class="superadmin-dashboard">
    <!-- Header -->
    <div class="branch-header">
        <h3>All Branches</h3>
        <button class="go-to-branches" id="go-to-branches" title="Go to Branches">
            <img src="<%=request.getContextPath()%>/icons/arrow-right.svg" alt="Go">
        </button>
    </div>

    <!-- Branch Info Section -->
    <div class="branch-sidecard">
        <div class="branch-sidebar" id="branch-sidebar"></div>
        <div class="branch-details" id="branch-details"></div>
    </div>

    <!-- Chart Section INSIDE superadmin-dashboard now -->
    <div class="branch-chart-section">
        <div class="admin-chart-card">
            <h3>Frequency of Payment Modes</h3>
            <canvas id="paymentModeChart"></canvas>
        </div>

        <div class="admin-chart-card">
            <h3>Number of Accounts per Branch</h3>
            <canvas id="accountsPerBranchChart"></canvas>
        </div>
    </div>
</div>


<script>
    window.contextPath = "<%= request.getContextPath() %>";
    window.sessionUserId = <%= userId %>;
    window.sessionBranchId = <%= branchId %>;
    window.sessionRole = <%= role %>;
</script>
 --%>