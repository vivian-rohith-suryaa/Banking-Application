<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
%>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/styles/employee.css">

<div class="employee-container">
	<div class="tree-menu">
		<ul>
			<li data-section="users">Users</li>
			<li data-section="accounts">Accounts</li>
			<li data-section="transactions">Transactions</li>
			<li data-section="requests">Requests</li>
			<li data-section="employees">Employees</li>
		</ul>
	</div>

	<div class="employee-data-panel">
		<div class="section-header">
			<h2 id="section-title">Users</h2>
			<div class="filters">
				<select id="filter-type"></select> <input type="text"
					id="filter-value" placeholder="Enter value" />
				<button id="filter-button">Search</button>
				<button id="clear-button">Clear</button>
				<button id="open-add-modal" class="add-btn hidden">Add</button>
			</div>
		</div>

		<div class="data-table-wrapper">
			<table id="data-table">
				<thead></thead>
				<tbody id="data-body"></tbody>
			</table>
		</div>

		<div class="data-pagination">
			<button id="prev-page">Previous</button>
			<span id="page-info">Page 1</span>
			<button id="next-page">Next</button>
		</div>
	</div>
</div>

<!-- Edit Modal for Requests/Employees -->
<div id="form-modal" class="modal hidden">
	<div class="modal-content">
		<h3 id="modal-title">Edit</h3>
		<form id="entity-form"></form>
		<div class="modal-actions">
			<button type="submit" form="entity-form">Submit</button>
			<button type="button" id="close-modal">Cancel</button>
		</div>
	</div>
</div>

<!-- Modal for Adding Employee -->
<div id="add-employee-modal" class="modal hidden">
	<div class="modal-content">
		<h3>Add Employee</h3>
		<form id="add-employee-form">
			<label>User ID</label>
			 <input name="employeeId" type="text"placeholder="Existing User ID" required oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                 onkeypress="if(['-','e','E','0'].includes(event.key)) event.preventDefault();" /> 
			 <label>Branch ID</label>
			<input name="branchId" type="text" value="<%= branchId %>" required oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                 onkeypress="if(['-','e','E','0'].includes(event.key)) event.preventDefault();"/>

			<label>Role</label> <select name="type" required>
				<option value="EMPLOYEE">Employee</option>
			</select>

			<div class="button-row">
				<button type="submit">Add Employee</button>
				<button type="button" id="close-employee-modal">Cancel</button>
			</div>
		</form>
	</div>
</div>

<!-- Modal for Request Approval -->
<div id="request-modal" class="modal hidden"></div>

<!-- Generic Custom Modal (optional usage) -->
<div id="custom-modal" class="modal hidden">
	<div id="custom-modal-content" class="modal-content"></div>
</div>

<script type="module">
    const contextPath = "<%= request.getContextPath() %>";
    const userId = <%= userId %>;
    const role = Number("<%= role %>");
    const branchId = Number("<%= branchId %>");

    import(`${contextPath}/scripts/employee.js`)
        .then(mod => mod.initEmployeeModule(contextPath, userId, role, branchId))
        .catch(err => console.error("Failed to load employee module:", err));
</script>
