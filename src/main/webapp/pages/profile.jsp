<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Viiva Banc</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/profile.css">
</head>
<body>
<div class="profile-container">
    <div class="profile-header">
        <h2>User Profile</h2>
        <button id="edit-btn" title="Edit Profile">
            <img src="<%= request.getContextPath() %>/icons/edit.svg" alt="Edit">
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
        <label>Aadhar: <input name="aadhar" readonly class="readonly"></label>

        <label>PAN: <input name="pan" readonly class="readonly"></label>
        <label>Status: <input name="status" readonly class="readonly"></label>

        <button type="button" id="update-btn" class="edit-hidden">Update</button>
    </form>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const editBtn = document.getElementById("edit-btn");
    const updateBtn = document.getElementById("update-btn");
    const form = document.getElementById("profile-form");

    const editableInputs = form.querySelectorAll("input:not(.readonly)");

    form.style.display = "block";

    editBtn.addEventListener("click", () => {
        editableInputs.forEach(input => {
            input.removeAttribute("readonly");
            input.classList.add("editable");
        });

        // Specifically make DOB and gender editable too
        const dob = form.querySelector("input[name='dob']");
        const gender = form.querySelector("input[name='gender']");
        if (dob) dob.removeAttribute("readonly");
        if (gender) gender.removeAttribute("readonly");

        updateBtn.classList.remove("edit-hidden");
    });

    updateBtn.addEventListener("click", () => {
        if (validateProfileForm()) {
            console.log("Validation passed. Proceeding to update...");
        } else {
            console.log("Validation failed.");
        }
    });
});

function validateProfileForm() {
    const form = document.getElementById("profile-form");

    const name = form.name.value.trim();
    const email = form.email.value.trim();
    const phone = form.phone.value.trim();
    const dob = form.dob.value?.trim();
    const address = form.address.value.trim();
    const gender = form.gender.value.trim().toUpperCase();

    let valid = true;
    let messages = [];

    const nameRegex = /^([A-Za-z]{1,})( [A-Za-z]{1,})*$/;
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const phoneRegex = /^\d{10}$/;
    const addressRegex = /^[a-zA-Z0-9\s,.\-/#()]{5,100}$/;

    if (!nameRegex.test(name)) {
        messages.push("Invalid Name.");
        valid = false;
    }
    if (!emailRegex.test(email)) {
        messages.push("Invalid Email.");
        valid = false;
    }
    if (!phoneRegex.test(phone)) {
        messages.push("Phone must be 10 digits.");
        valid = false;
    }
    if (!addressRegex.test(address)) {
        messages.push("Invalid Address.");
        valid = false;
    }
    if (!dob || isNaN(new Date(dob))) {
        messages.push("Date of Birth is required and must be valid.");
        valid = false;
    }
    if (!["MALE", "FEMALE", "OTHERS"].includes(gender)) {
        messages.push("Gender must be MALE, FEMALE, or OTHERS.");
        valid = false;
    }

    if (!valid) {
        alert(messages.join("\n"));
    }

    return valid;
}
</script>


</body>
</html>
