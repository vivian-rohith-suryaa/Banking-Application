document.addEventListener("DOMContentLoaded", () => {
	const profileIcon = document.getElementById("profile-icon");

	profileIcon?.addEventListener("click", () => {
		const sidebar = document.getElementById("profile-sidebar");
		if (!sidebar) return;

		const contextPath = window.contextPath || "";
		const userId = window.sessionUserId || -1;

		if (!sidebar.classList.contains("loaded")) {
			// First time: fetch HTML and module
			fetch(`${contextPath}/pages/profile.jsp`)
				.then(res => res.text())
				.then(html => {
					sidebar.innerHTML = html;
					sidebar.classList.add("loaded", "open");

					import(`${contextPath}/scripts/profile.js`)
						.then(mod => {
							if (typeof mod.initProfilePage === "function") {
								mod.initProfilePage(contextPath, userId);
							}
						})
						.catch(err => console.error("Failed to load profile.js module:", err));
				})
				.catch(err => console.error("Failed to load profile.jsp:", err));
		} else {
			// Already loaded: reopen and re-init to refresh data
			sidebar.classList.add("open");

			import(`${contextPath}/scripts/profile.js`)
				.then(mod => {
					if (typeof mod.initProfilePage === "function") {
						mod.initProfilePage(contextPath, userId);
					}
				})
				.catch(err => console.error("Re-loading profile.js failed:", err));
		}
	});
});
