<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
HttpSession sess = request.getSession(false);
long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/styles/dashboard-employee.css">

<div class="dashboard-layout">
	<section class="dashboard-main">
		<!-- Left Side -->
		<div class="left-section">
			<!-- Branch Overview -->
			<div class="branch-card">
				<h3>Branch Overview</h3>
				<div id="branch-overview" class="branch-overview"></div>
			</div>

			<!-- Pending Requests -->
			<div class="requests-card">
				<div class="request-header">
					<h3>Pending Requests</h3>
					<button class="view-requests-btn" id="view-requests-btn"
						title="Go to Requests">
						<img src="<%=request.getContextPath()%>/icons/arrow-right.svg"
							alt="Go">
					</button>
				</div>
				<div id="request-list" class="request-list"></div>
			</div>
		</div>

		<!-- Right Side -->
		<div class="right-section">

			<!-- Payment Mode Frequency -->
			<div class="employee-chart-card">
				<div class="employee-chart-header">
					<h3>Frequency of Payment Modes</h3>
				</div>
				<div class="employee-chart-content">
					<canvas id="paymentModeChart"></canvas>
					<div id="paymentModeLegend" class="custom-legend"></div>
				</div>
			</div>

			<!-- Employee Performance -->
			<div class="employee-chart-card">
				<div class="employee-chart-header">
					<h3>Requests Approved/Rejected by Employees</h3>
				</div>
				<div class="employee-chart-content">
					<canvas id="employeePerformanceChart"></canvas>
				</div>
			</div>

		</div>
	</section>
</div>

<!-- Modal -->
<div id="modal-overlay"></div>
<div id="request-modal"></div>

<!-- JS Variables -->
<script>
    window.contextPath = "<%=request.getContextPath()%>
	";
	window.sessionUserId =
<%=userId%>
	;
	window.sessionBranchId =
<%=branchId%>
	;
	window.sessionRole =
<%=role%>
	;
</script>

<script type="module"
	src="<%=request.getContextPath()%>/scripts/dashboard-employee.js"></script>
