<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>

<link rel="stylesheet" href="<%= request.getContextPath() %>/styles/profile.css">

<div class="profile-sidebar-content">
    <div class="profile-header">
        <h2>My Profile</h2>
        <span class="close-btn" onclick="document.getElementById('profile-sidebar').classList.remove('open')">&times;</span>
    </div>

    <div class="profile-center">
        <img src="<%= request.getContextPath() %>/icons/popup-profile.svg" alt="Profile Icon" class="profile-pic" />
        <h3 id="profile-name">Loading...</h3>
        <p id="profile-id">ID: -</p>
    </div>

    <div class="profile-details">
        <div class="profile-row"><span>Email:</span> <div id="profile-email"></div></div>
        <div class="profile-row"><span>Phone:</span> <div id="profile-phone"></div></div>
        <div class="profile-row"><span>DOB:</span> <div id="profile-dob"></div></div>
        <div class="profile-row"><span>Gender:</span> <div id="profile-gender"></div></div>
        <div class="profile-row address-row"><span>Address:</span> <div id="profile-address"></div></div>
        <div class="profile-row"><span>PAN:</span> <div id="profile-pan"></div></div>
        <div class="profile-row"><span>Aadhaar:</span> <div id="profile-aadhar"></div></div>
    </div>

	<div id="profile-update-section" class="update-section hidden">
        <button id="save-profile-btn">Update</button>
    </div>

    <div class="profile-actions">
        <img src="<%= request.getContextPath() %>/icons/edit.svg" alt="Edit Profile" class="action-icon" id="edit-profile-btn">
        <img src="<%= request.getContextPath() %>/icons/logout.svg" alt="Logout" class="action-icon" id="logout-btn">
    </div>

    
</div>



