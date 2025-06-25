<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/branch.css">

<div class="branch-container">
    <div class="branch-header">
        <h2>Branch</h2>
        <% if (role == 4) { %>
        <button id="open-add-modal" class="add-btn">Add Branch</button>
        <% } %>
    </div>

    <!-- Filters -->
    <div class="branch-filters">
        <select id="filter-type">
            <option value="ifsc_code">IFSC Code</option>
            <option value="branchId">Branch ID</option>
            <option value="locality">Locality</option>
            <option value="district">District</option>
            <option value="state">State</option>
        </select>

        <input type="text" id="filter-value" placeholder="Enter value to search">
        <button id="filter-button">Search</button>
        <button id="clear-button">Clear</button>
    </div>

    <!-- Table -->
    <div class="branch-table-wrapper">
        <table id="branch-table">
            <thead>
                <tr>
                    <th>Branch ID</th>
                    <th>IFSC</th>
                    <th>Locality</th>
                    <th>District</th>
                    <th>State</th>
                    <th>Manager ID</th>
                    <% if (role >= 2) { %><th>Actions</th><% } %>
                </tr>
            </thead>
            <tbody id="branch-body">
                <!-- Populated dynamically -->
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <div class="branch-pagination">
        <button id="prev-page">Previous</button>
        <span id="page-info">Page 1</span>
        <button id="next-page">Next</button>
    </div>
</div>

<!-- Add Branch Modal (SuperAdmin only) -->
<% if (role == 4) { %>
<div id="add-modal" class="modal hidden">
    <div class="modal-content">
        <h3>Add New Branch</h3>
        <form id="add-branch-form">
            <input type="text" name="locality" placeholder="Locality" required required oninput="this.value = this.value.replace(/[^A-Za-z ]/g, '')"
							pattern="^[A-Za-z]+(?: [A-Za-z]+)*$" maxlength="30">
            <input type="text" name="district" placeholder="District" required required oninput="this.value = this.value.replace(/[^A-Za-z ]/g, '')"
							pattern="^[A-Za-z]+(?: [A-Za-z]+)*$" maxlength="30">
            <input type="text" name="state" placeholder="State" required required oninput="this.value = this.value.replace(/[^A-Za-z ]/g, '')"
							pattern="^[A-Za-z]+(?: [A-Za-z]+)*$" maxlength="30">
            <input type="text" name="managerId" placeholder="Manager ID" required maxlength="12"
                 oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                 onkeypress="if(['-','e','E','0'].includes(event.key)) event.preventDefault();" >
            <div class="modal-actions">
                <button type="submit">Add</button>
                <button type="button" id="close-add-modal">Cancel</button>
            </div>
        </form>
    </div>
</div>
<% } %>

<div id="update-modal" class="modal hidden">
    <div class="modal-content">
        <h3>Update Branch</h3>
        <form id="update-branch-form">
            <input type="hidden" name="branchId" maxlength="12">
            <input type="text" name="locality" placeholder="Locality" required oninput="this.value = this.value.replace(/[^A-Za-z ]/g, '')"
							pattern="^[A-Za-z]+(?: [A-Za-z]+)*$" maxlength="30">
            <input type="text" name="district" placeholder="District" required oninput="this.value = this.value.replace(/[^A-Za-z ]/g, '')"
							pattern="^[A-Za-z]+(?: [A-Za-z]+)*$" maxlength="30">
            <input type="text" name="state" placeholder="State" required oninput="this.value = this.value.replace(/[^A-Za-z ]/g, '')"
							pattern="^[A-Za-z]+(?: [A-Za-z]+)*$" maxlength="30">
            <% if (role == 4) { %>
            <input type="number" name="managerId" placeholder="Manager ID" required>
            <% } %>
            <div class="modal-actions">
                <button type="submit">Update</button>
                <button type="button" id="close-modal">Cancel</button>
            </div>
        </form>
    </div>
</div>

<script type="module">
    const contextPath = "<%= request.getContextPath() %>";
    const userId = <%= userId %>;
    const role = Number("<%= role %>");
    const branchId = Number("<%= branchId %>");

    import(`${contextPath}/scripts/branch.js`)
        .then(mod => mod.initBranchModule(contextPath, userId, role, branchId))
        .catch(err => console.error("Failed to load branch module:", err));
</script>
