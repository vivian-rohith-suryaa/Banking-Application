export function initProfilePage(contextPath, userId) {
	const loading = document.getElementById("loading");
	const form = document.getElementById("profile-form");
	const profileContainer = document.querySelector(".profile-container");
	const editableInputs = form.querySelectorAll("input:not(.readonly)");
	const updateBtn = document.getElementById("update-btn");
	let editMode = false;

	if (!userId || userId === -1) {
		loading.textContent = "Session expired or invalid.";
		return;
	}

	// Fetch user profile data
	fetch(`${contextPath}/viiva/user/${userId}`, {
		method: 'GET',
		headers: { 'Content-Type': 'application/json' }
	})
		.then(res => {
			if (!res.ok) throw new Error("Failed to fetch user profile");
			return res.json();
		})
		.then(response => {
			const data = response.data;
			loading.style.display = "none";
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
			loading.textContent = "Failed to load profile data.";
			console.error(err);
		});

	// Enable editing
	document.getElementById("edit-btn")?.addEventListener("click", (e) => {
		e.stopPropagation(); // Prevent immediate outside cancel
		editableInputs.forEach(input => {
			input.readOnly = false;
			input.classList.add("editable");
		});

		// Make DOB and gender editable
		form.dob.readOnly = false;
		form.gender.readOnly = false;

		updateBtn.classList.remove("edit-hidden");
		editMode = true;
	});

	// Update profile
	updateBtn?.addEventListener("click", () => {
		if (!validateProfileForm()) return;

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

		fetch(`${contextPath}/viiva/user/${userId}`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' },
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

	// Cancel editing if click outside
	document.addEventListener("click", (e) => {
		if (
			editMode &&
			!profileContainer.contains(e.target)
		) {
			editableInputs.forEach(input => {
				input.readOnly = true;
				input.classList.remove("editable");
			});
			updateBtn.classList.add("edit-hidden");
			editMode = false;
		}
	});
}


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
