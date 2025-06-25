<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/account.css">
<div class="account-wrapper">
    <div class="account-header">
        <h3>View Accounts</h3>
        <button id="request-account-btn" title="Request New Account">
            <img src="<%= request.getContextPath() %>/icons/add-account.svg" alt="Request Account">
        </button>
    </div>

    <div id="view-section" class="account-section active">
        <select id="account-select">
            <option value="all">Select All</option>
        </select>
        <div id="account-result" class="account-display"></div>
    </div>
</div>

<!-- Request Modal -->
<div id="request-modal" class="modal-overlay hidden">
    <div class="modal-card">
        <h3>Request New Account</h3>
        <form id="account-request-form">
            <label>Account Type:
                <select name="accountType" required>
                    <option value="SAVINGS">Savings</option>
                    <option value="FIXED_DEPOSIT">Fixed Deposit</option>
                    <option value="CURRENT">Current</option>
                </select>
            </label>

            <label>Initial Balance:
                <input type="text" name="balance" required
                    oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/^0+(\d)/, '$1')"
                    onkeypress="if(['-','e','E'].includes(event.key)) event.preventDefault();">
            </label>

            <label>Branch ID:
                <input type="text" name="branchId" required
                    oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                    onkeypress="if(['-','e','E'].includes(event.key)) event.preventDefault();">
            </label>

            <div class="modal-actions">
                <button type="submit">Submit</button>
                <button type="button" id="close-modal-btn">Cancel</button>
            </div>
        </form>
    </div>
</div>

<script type="module">
    const contextPath = "<%= request.getContextPath() %>";
    const userId = <%= userId %>;

    import(`${contextPath}/scripts/account.js`)
        .then(module => module.initAccountModule(contextPath, userId))
        .catch(err => console.error("Failed to load account module:", err));
</script>
