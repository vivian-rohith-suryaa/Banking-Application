document.addEventListener("DOMContentLoaded", () => {
	const menuItems = document.querySelectorAll(".menu-item");
	const mainContent = document.getElementById("main-content");

	const contextPath = window.contextPath || "";
	const defaultPage = "dashboard.jsp";

	function loadPage(page) {
		fetch(`${contextPath}/pages/${page}`)
			.then(res => {
				if (!res.ok) throw new Error("Page load failed");
				return res.text();
			})
			.then(html => {
				mainContent.innerHTML = html;
				window.history.pushState({ page }, "", `?view=${page}`);

				if (page === "dashboard.jsp") {
					const fragment = sessionRole === 1
						? "dashboard-user.jsp"
						: sessionRole === 4
							? "dashboard-superadmin.jsp"
							: "dashboard-employee.jsp";

					fetch(`${contextPath}/pages/${fragment}`)
						.then(res => {
							if (!res.ok) throw new Error("Failed to load dashboard fragment");
							return res.text();
						})
						.then(fragmentHtml => {
							mainContent.innerHTML = fragmentHtml;

							const scriptName = sessionRole === 1
								? "dashboard-user.js"
								: sessionRole === 4
									? "dashboard-superadmin.js"
									: "dashboard-employee.js";

							import(`${contextPath}/scripts/${scriptName}`)
								.then(mod => {
									if (typeof mod.initDashboard === "function") {
										mod.initDashboard(contextPath, sessionUserId, sessionBranchId, sessionRole);
									}
								})
								.catch(err => console.error("Dashboard script load failed:", err));
						})
						.catch(err => {
							mainContent.innerHTML = "<p>Unable to load dashboard fragment.</p>";
							console.error(err);
						});
				}

				if (page === "account.jsp") {
					import(`${contextPath}/scripts/account.js`)
						.then(mod => {
							if (typeof mod.initAccountModule === "function") {
								mod.initAccountModule(contextPath, sessionUserId);
							}
						})
						.catch(err => {
							console.error("Failed to load account module", err);
						});
				}
				if (page === "payment.jsp") {
					import(`${contextPath}/scripts/payment.js`)
						.then(mod => {
							if (typeof mod.initPaymentForm === "function") {
								mod.initPaymentForm(contextPath, sessionUserId);
							}
						})
						.catch(err => {
							console.error("Failed to load payment module", err);
						});
				}
				if (page === "statement.jsp") {
					import(`${contextPath}/scripts/statement.js`)
						.then(mod => {
							if (typeof mod.initStatementPage === "function") {
								mod.initStatementPage(contextPath, sessionUserId);
							}
						})
						.catch(err => {
							console.error("Failed to load statement module", err);
						});
				}
				if (page === "branch.jsp") {
					import(`${contextPath}/scripts/branch.js`)
						.then(mod => {
							if (typeof mod.initBranchModule === "function") {
								mod.initBranchModule(contextPath, sessionUserId, sessionRole, sessionBranchId);
							}
						})
						.catch(err => {
							console.error("Failed to load branch module", err);
						});
				}
				if (page === "employee.jsp") {
					import(`${contextPath}/scripts/employee.js`)
						.then(mod => {
							if (typeof mod.initEmployeeModule === "function") {
								mod.initEmployeeModule(contextPath, sessionUserId, sessionRole, sessionBranchId);
							}
						})
						.catch(err => {
							console.error("Failed to load employee module", err);
						});
				}
				
				if (page === "users.jsp") {
					import(`${contextPath}/scripts/users.js`)
						.then(mod => {
							if (typeof mod.initUsersModule === "function") {
								mod.initUsersModule(contextPath, sessionUserId, sessionRole, sessionBranchId);
							}
						})
						.catch(err => {
							console.error("Failed to load users module", err);
						});
				}
				
				if (page === "allaccounts.jsp") {
									import(`${contextPath}/scripts/allaccounts.js`)
										.then(mod => {
											if (typeof mod.initAccountsModule === "function") {
												mod.initAccountsModule(contextPath, sessionUserId, sessionRole, sessionBranchId);
											}
										})
										.catch(err => {
											console.error("Failed to load accounts module", err);
										});
								}
								
								if (page === "transaction.jsp") {
													import(`${contextPath}/scripts/transaction.js`)
														.then(mod => {
															if (typeof mod.initTransactionModule === "function") {
																mod.initTransactionModule(contextPath, sessionUserId, sessionRole, sessionBranchId);
															}
														})
														.catch(err => {
															console.error("Failed to load transaction module", err);
														});
												}
												
												
												if (page === "requests.jsp") {
																	import(`${contextPath}/scripts/requests.js`)
																		.then(mod => {
																			if (typeof mod.initRequestsModule === "function") {
																				mod.initRequestsModule(contextPath, sessionUserId, sessionRole, sessionBranchId);
																			}
																		})
																		.catch(err => {
																			console.error("Failed to load requests module", err);
																		});
																}
				
			})
			.catch(err => {
				mainContent.innerHTML = "<p>Unable to load content.</p>";
				console.error(err);
			});
	}

	function setActive(item) {
		menuItems.forEach(i => i.classList.remove("active"));
		item.classList.add("active");
	}

	menuItems.forEach(item => {
		item.addEventListener("click", () => {
			const page = item.getAttribute("data-page");
			loadPage(page);
			setActive(item);
		});
	});

	const urlParams = new URLSearchParams(window.location.search);
	const initialPage = urlParams.get("view") || defaultPage;
	const defaultItem = Array.from(menuItems).find(item => item.dataset.page === initialPage);
	if (defaultItem) setActive(defaultItem);
	loadPage(initialPage);

	window.addEventListener("popstate", e => {
		const page = e.state?.page || defaultPage;
		const item = Array.from(menuItems).find(i => i.dataset.page === page);
		if (item) setActive(item);
		loadPage(page);
	});
});
