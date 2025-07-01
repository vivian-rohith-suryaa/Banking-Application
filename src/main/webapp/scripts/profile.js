export function initProfilePage(contextPath, userId) {
	console.log("Initializing profile for user:", userId);

	const nameField = document.getElementById("profile-name");
	const idField = document.getElementById("profile-id");
	const sidebar = document.getElementById("profile-sidebar");

	const fields = {
		email: document.getElementById("profile-email"),
		phone: document.getElementById("profile-phone"),
		dob: document.getElementById("profile-dob"),
		gender: document.getElementById("profile-gender"),
		address: document.getElementById("profile-address"),
		pan: document.getElementById("profile-pan"),
		aadhar: document.getElementById("profile-aadhar"),
		name: document.getElementById("profile-name")
	};

	const saveBtn = document.getElementById("save-profile-btn");
	const updateSection = document.getElementById("profile-update-section");

	let originalData = {};
	let editMode = false;

	if (!userId || userId === -1) {
		nameField.textContent = "Invalid session";
		return;
	}

	// Load profile data
	fetch(`${contextPath}/viiva/user/${userId}`, {
		method: "GET",
		headers: { "Content-Type": "application/json" }
	})
		.then(res => res.json())
		.then(res => {
			const data = res.data || {};
			nameField.textContent = data.name || "Unnamed";
			idField.textContent = `ID: ${data.userId || userId}`;

			Object.entries(fields).forEach(([key, el]) => {
				originalData[key] = data[key] || "";
				el.textContent = originalData[key];
			});
		})
		.catch(err => {
			console.error("Failed to load profile:", err);
			nameField.textContent = "Failed to load";
		});

	// Edit button handler
	document.getElementById("edit-profile-btn")?.addEventListener("click", () => {
		editMode = true;

		Object.entries(fields).forEach(([key, el]) => {
			if (!["pan", "aadhar", "name"].includes(key)) {
				el.contentEditable = "true";
				el.classList.add("editable");
			}
		});

		updateSection?.classList.remove("hidden");
	});

	// Save button handler
	saveBtn?.addEventListener("click", () => {
		const payload = {
			user: {
				name: originalData.name,
				email: fields.email.textContent.trim(),
				phone: fields.phone.textContent.trim(),
				gender: fields.gender.textContent.trim().toUpperCase()
			},
			customer: {
				aadhar: originalData.aadhar,
				pan: originalData.pan,
				address: fields.address.textContent.trim(),
				dob: fields.dob.textContent.trim()
			}
		};

		if (!validateProfilePayload(payload)) return;

		fetch(`${contextPath}/viiva/user/${userId}`, {
			method: "PUT",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(payload)
		})
			.then(res => res.json())
			.then(resp => {
				alert(resp.message || "Profile updated successfully.");
				location.reload();
			})
			.catch(err => {
				console.error("Update failed", err);
				alert("Failed to update profile.");
			});
	});

	// Logout handler
	document.getElementById("logout-btn")?.addEventListener("click", () => {
		fetch(`${contextPath}/logout`, {
			method: "POST",
			headers: { "Content-Type": "application/json" }
		})
			.then(() => window.location.href = `${contextPath}/pages/signin.jsp`)
			.catch(err => console.error("Logout failed", err));
	});

	// Close sidebar on outside click
	document.addEventListener("click", e => {
		if (!sidebar || !sidebar.classList.contains("open")) return;
		if (!sidebar.contains(e.target) && e.target.id !== "profile-icon") {
			sidebar.classList.remove("open");

			if (editMode) {
				Object.entries(fields).forEach(([key, el]) => {
					el.contentEditable = "false";
					el.classList.remove("editable");
					el.textContent = originalData[key];
				});
				editMode = false;
				updateSection?.classList.add("hidden");
			}
		}
	});
}

// Validation
function validateProfilePayload({ user, customer }) {
	const messages = [];

	if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(user.email))
		messages.push("Invalid email address.");

	if (!/^\d{10}$/.test(user.phone))
		messages.push("Phone must be 10 digits.");

	if (!["MALE", "FEMALE", "OTHER", "OTHERS"].includes(user.gender))
		messages.push("Gender must be MALE, FEMALE, or OTHER(S).");

	if (!customer.dob || isNaN(new Date(customer.dob)))
		messages.push("Invalid or empty DOB.");

	if (!/^[a-zA-Z0-9\s,.\-/#()]{5,100}$/.test(customer.address))
		messages.push("Invalid address format.");

	if (messages.length > 0) {
		alert("Invalid Input(s) found:\n\n" + messages.join("\n"));
		return false;
	}

	return true;
}
