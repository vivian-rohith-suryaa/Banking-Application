document.addEventListener("DOMContentLoaded", () => {
	const form = document.getElementById("signupForm");
	const messageBox = document.getElementById("signup-message");

	const pwd = form.querySelector('input[name="password"]');
	const confirmPwd = form.querySelector('input[name="confirmPassword"]');

	// Confirm password real-time validation
	confirmPwd.addEventListener("input", () => {
		if (confirmPwd.value !== pwd.value) {
			confirmPwd.setCustomValidity("Passwords do not match");
		} else {
			confirmPwd.setCustomValidity("");
		}
	});

	form.addEventListener("submit", async (e) => {
		e.preventDefault();

		// HTML5 validation check
		if (!form.checkValidity()) {
			form.reportValidity();
			return;
		}

		// Prepare form data
		const formData = new FormData(form);
		const user = {
			name: formData.get("name"),
			email: formData.get("email"),
			phone: formData.get("phone"),
			gender: formData.get("gender"),
			password: formData.get("password")
		};
		const customer = {
			dob: formData.get("dob"),
			aadhar: formData.get("aadhar"),
			pan: formData.get("pan"),
			address: formData.get("address")
		};
		const accountType = formData.get("accountType");
		const branchId = parseInt(formData.get("branchId"));

		const payload = { user, customer,branchId, accountType };

		try {
			const res = await fetch(form.dataset.endpoint, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(payload)
			});

			const result = await res.json();
			const message = result?.data?.message|| result?.message || "Signup failed.";

			// Reset message classes
			messageBox.classList.remove("success", "error");
			messageBox.textContent = message;
			messageBox.classList.add(res.ok ? "success" : "error");

			if (res.ok) {
				setTimeout(() => {
					window.location.href = "signin.jsp";
				}, 2000);
			}
		} catch (err) {
			messageBox.textContent = "Server error. Please try again.";
			messageBox.classList.remove("success");
			messageBox.classList.add("error");
		}
	});

	// Password visibility toggle
	document.querySelectorAll(".toggle-password").forEach(toggle => {
		toggle.addEventListener("click", () => {
			const input = toggle.previousElementSibling;
			const show = input.type === "password";
			input.type = show ? "text" : "password";
			toggle.innerHTML = show ? "&#10006;" : "&#128065;";
		});
	});
});
