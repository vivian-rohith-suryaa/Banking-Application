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

<div class="employee-data-panel">
	<div class="section-header">
		<h2 id="section-title">Employees</h2>
		<div class="filters">
			<select id="filter-type"></select>
			<div id="filter-value-container">
				<input type="text" id="filter-value" class="filter-value"
					placeholder="Enter value" />
			</div>
			<button id="filter-button">Search</button>
			<button id="clear-button">Clear</button>
			<% if (role >= 3) { %>
			<button id="open-add-modal" class="add-btn">Add</button>
			<% } %>
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

<!-- Add Employee Modal -->
<div id="add-employee-modal" class="modal hidden">
	<div class="modal-content">
		<h3>Add Employee</h3>
		<form id="add-employee-form">
			<label>User ID</label> <input name="employeeId" type="text"
				placeholder="Existing User ID" required
				oninput="this.value = this.value.replace(/[^0-9]/g, '')" /> <label>Branch
				ID</label> <input name="branchId" type="text" value="<%= branchId %>"
				required maxlength="12"
				oninput="this.value = this.value.replace(/[^0-9]/g, '')" /> <label>Role</label>
			<select name="type" required>
				<option value="EMPLOYEE">Employee</option>
			</select>
			<div class="button-row">
				<button type="submit">Add</button>
				<button type="button" id="close-employee-modal">Cancel</button>
			</div>
		</form>
	</div>
</div>

<!-- Edit Modal -->
<div id="form-modal" class="modal hidden">
	<div class="modal-content">
		<h3 id="modal-title">Edit Employee</h3>
		<form id="entity-form"></form>
		<div class="modal-actions">
			<button type="submit" form="entity-form">Submit</button>
			<button type="button" id="close-modal">Cancel</button>
		</div>
	</div>
</div>

<script type="module">
    const contextPath = "<%= request.getContextPath() %>";
    const userId = <%= userId %>;
    const role = <%= role %>;
    const branchId = <%= branchId %>;

    import(`${contextPath}/scripts/employee.js`)
        .then(mod => mod.initEmployeeModule(contextPath, userId, role, branchId));
</script>
