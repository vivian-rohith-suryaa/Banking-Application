document.addEventListener("DOMContentLoaded", () => {
	const form = document.getElementById("loginForm");
	const messageBox = document.getElementById("signin-message");

	form.addEventListener("submit", async (e) => {
		e.preventDefault();

		const data = Object.fromEntries(new FormData(form).entries());

		try {
			const res = await fetch(form.dataset.endpoint, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(data)
			});

			const result = await res.json();
			const message = result?.data?.message || result?.message || "Something went wrong";

			messageBox.textContent = message;
			messageBox.classList.remove("success", "error");
			messageBox.classList.add(res.ok ? "success" : "error");

			// Auto-hide message after 3 seconds
			setTimeout(() => {
				messageBox.textContent = "";
				messageBox.classList.remove("success", "error");
			}, 2000);

			if (res.ok) {
				setTimeout(() => {
					window.location.href = "home.jsp";
				}, 900);
			}
		} catch (err) {
			messageBox.textContent = "Server error. Please try again.";
			messageBox.className = "form-message error";

			// Auto-hide error after 3 seconds
			setTimeout(() => {
				messageBox.textContent = "";
				messageBox.classList.remove("error");
			}, 2000);
		}
	});

	document.querySelectorAll(".toggle-password").forEach(toggle => {
		toggle.addEventListener("click", () => {
			const input = toggle.previousElementSibling;
			const show = input.type === "password";
			input.type = show ? "text" : "password";
			toggle.innerHTML = show ? "&#10006;" : "&#128065;";
		});
	});
});

