<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/statement.css" />

<div class="statement-wrapper">
    <h2 class="statement-title">Transaction History</h2>

    <div class="statement-controls">
        <label for="account-select">Filter by Account:</label>
        <select id="account-select">
            <option value="all">All Accounts</option>
        </select>
    </div>

    <div class="statement-table-container">
        <table class="statement-table">
            <thead>
                <tr>
                    <th>Transaction ID</th>
                    <th>Customer ID</th>
                    <th>Account ID</th>
                    <th>Amount</th>
                    <th>Type</th>
                    <th>Mode</th>
                    <th>Transacted Account</th>
                    <th>Description</th>
                    <th>Balance</th>
                    <th>Time</th>
                </tr>
            </thead>
            <tbody id="statement-body">
            </tbody>
        </table>
    </div>

    <div class="statement-pagination">
        <button id="prev-page" disabled>Prev</button>
        <span id="page-info">Page 1</span>
        <button id="next-page">Next</button>
    </div>
</div>

<script type="module">
    const contextPath = "<%= request.getContextPath() %>";
    const userId = <%= userId %>;

    import(`${contextPath}/scripts/statement.js`)
        .then(module => module.initStatementPage(contextPath, userId))
        .catch(err => console.error("Failed to load statement module:", err));
</script>

