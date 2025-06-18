<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
HttpSession sess = request.getSession(false);
long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Viiva Banc</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/profile.css">
</head>
<body>

<div class="profile-container">
    <div class="profile-header">
        <h2>User Profile</h2>
        <button id="edit-btn" title="Edit Profile">
            <img src="<%=request.getContextPath()%>/icons/edit.svg" alt="Edit">
        </button>
    </div>

    <div id="loading" class="loading">Loading profile data...</div>

    <form id="profile-form" class="profile-form" style="display: none;">
        <label>User ID: <input name="userId" readonly class="readonly"></label>
        <label>Name: <input name="name" readonly></label>

        <label>Email: <input name="email" readonly></label>
        <label>Phone: <input name="phone" readonly></label>

        <label>Address: <input name="address" readonly></label>
        <label>Date of Birth: <input name="dob" readonly></label>

        <label>Gender: <input name="gender" readonly></label>
        <label>Aadhar: <input name="aadhar" readonly></label>

        <label>PAN: <input name="pan" readonly></label>
        <label>Status: <input name="status" readonly class="readonly"></label>

        <button type="button" id="update-btn" class="edit-hidden">Update</button>
    </form>
</div>

<script>
    const userId = <%=userId%>;

    document.addEventListener("DOMContentLoaded", () => {
        fetch(`<%=request.getContextPath()%>/viiva/user/${userId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(res => {
            if (!res.ok) throw new Error("Failed to fetch user profile");
            return res.json();
        })
        .then(response => {
            const data = response.data;
            document.getElementById("loading").style.display = "none";
            const form = document.getElementById("profile-form");
            form.style.display = "grid";

            form.userId.value = data.userId || '';
            form.name.value = data.name || '';
            form.email.value = data.email || '';
            form.phone.value = data.phone || '';
            form.gender.value = data.gender || '';
            form.status.value = data.status || '';

            form.address.value = data.address || '';
            form.dob.value = data.dob || '';
            form.aadhar.value = data.aadhar || '';
            form.pan.value = data.pan || '';
        })
        .catch(err => {
            document.getElementById("loading").textContent = "Failed to load profile data.";
            console.error(err);
        });
    });

    document.getElementById("edit-btn").addEventListener("click", () => {
        const form = document.getElementById("profile-form");
        const inputs = form.querySelectorAll("input:not(.readonly)");
        const updateBtn = document.getElementById("update-btn");

        inputs.forEach(input => {
            input.readOnly = !input.readOnly;
        });

        updateBtn.classList.toggle("edit-hidden");
    });

    document.getElementById("update-btn").addEventListener("click", () => {
        const form = document.getElementById("profile-form");

        const payload = {
            user: {
                name: form.name.value,
                email: form.email.value,
                phone: form.phone.value,
                gender: form.gender.value
            },
            customer: {
                address: form.address.value,
                dob: form.dob.value,
                aadhar: form.aadhar.value,
                pan: form.pan.value
            }
        };

        fetch(`<%=request.getContextPath()%>/viiva/user/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if (!res.ok) throw new Error("Failed to update profile");
            return res.json();
        })
        .then(response => {
            alert(response.message || "Profile updated successfully.");
            location.reload();
        })
        .catch(err => {
            alert("Failed to update profile.");
            console.error(err);
        });
    });
</script>

</body>
</html>
