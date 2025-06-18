document.addEventListener("DOMContentLoaded", () => {
	const forms = document.querySelectorAll(".json-form");
	const toast = document.getElementById("toast");

	// Show toast notification
	function showToast(message, status = "success") {
		toast.textContent = message;

		toast.className = "toast";

		if (status === "success") {
			toast.style.backgroundColor = "#28a745"; // Green
		} else if (status === "error") {
			toast.style.backgroundColor = "#dc3545"; // Red
		} else {
			toast.style.backgroundColor = "#007bff"; // Blue (info)
		}

		toast.classList.add("show");

		setTimeout(() => {
			toast.classList.remove("show");
		}, 4000);
	}


	// Password visibility toggles
	document.querySelectorAll(".toggle-password").forEach((toggle) => {
		toggle.addEventListener("click", () => {
			const input = toggle.previousElementSibling;
			const isVisible = input.type === "text";
			input.type = isVisible ? "password" : "text";
			toggle.innerHTML = isVisible ? "&#128065;" : "&#10006;"; // 👁 to ✖
			toggle.title = isVisible ? "Show Password" : "Hide Password";
		});
	});

	// Form submit handler
	forms.forEach((form) => {
		form.addEventListener("submit", async (e) => {
			e.preventDefault();

			if (form.dataset.allowSubmit !== "true") {
				showToast("Validation failed.", "error");
				return;
			}

			const type = form.dataset.formType;
			const endpoint = form.dataset.endpoint;
			const formData = new FormData(form);

			console.log("🧾 Form Data Entries:");
			for (let [key, value] of formData.entries()) {
				console.log(`${key}: ${value}`);
			}

			const user = {
				email: formData.get("email"),
				password: formData.get("password"),
				name: formData.get("name"),
				phone: formData.get("phone"),
				gender: formData.get("gender"),
			};

			let payload;

			if (type === "signup") {
				const customer = {
					dob: formData.get("dob"),
					aadhar: formData.get("aadhar"),
					pan: formData.get("pan"),
					address: formData.get("address"),
				};

				const confirmPassword = formData.get("confirmPassword");
				if (user.password !== confirmPassword) {
					showToast("Passwords do not match!", "error");
					return;
				}

				payload = { user, customer };
			} else {
				payload = { ...user };
			}

			try {
				const res = await fetch(endpoint, {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify(payload),
				});

				const result = await res.json();

				if (res.ok) {
					showToast(result.data?.message || "Success", "success");
					form.reset();
					
					if (type === "signup") {
						signupTab.classList.remove("active");
						loginTab.classList.add("active");
						signupForm.classList.remove("active");
						loginForm.classList.add("active");
					} else if (type === "login") {
						setTimeout(() => {
							window.location.href = "home.jsp";
						}, 1000);
					}
				} else {
					showToast(result.data?.message || "Failed", "error");
				}
			} catch (err) {
				console.error(err);
				showToast("Server Network Error", "error");
			}
		});
	});
});
