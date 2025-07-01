export function initDashboard(contextPath, userId, branchId, role) {
	console.log("Initializing SuperAdmin Dashboard");

	const branchSidebar = document.getElementById("branch-sidebar");
	const branchDetailsContainer = document.getElementById("branch-details");

	const paymentModeChartCanvas = document.getElementById("paymentModeChart");
	const accountsPerBranchChartCanvas = document.getElementById("accountsPerBranchChart");
	const topBranchesChartCanvas = document.getElementById("topBranchesChart");

	const headers = {
		"Content-Type": "application/json",
		"Accept": "application/json"
	};

	// ========== Load Branches ==========
	fetch(`${contextPath}/viiva/branch`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const branches = data.data?.branches || [];
			if (branches.length === 0) {
				branchSidebar.innerHTML = "<p style='padding: 8px;'>No branches found.</p>";
				return;
			}

			branches.forEach((branch, idx) => {
				const tab = document.createElement("div");
				tab.classList.add("branch-tab");
				if (idx === 0) tab.classList.add("active");
				tab.textContent = `Branch ${branch.branchId}`;
				tab.addEventListener("click", () => {
					document.querySelectorAll(".branch-tab").forEach(t => t.classList.remove("active"));
					tab.classList.add("active");
					renderBranchDetails(branch);
				});
				branchSidebar.appendChild(tab);
			});

			renderBranchDetails(branches[0]);
		})
		.catch(err => {
			branchSidebar.innerHTML = "<p style='padding: 8px;'>Failed to load branches.</p>";
			console.error(err);
		});

	function renderBranchDetails(branch) {
		branchDetailsContainer.innerHTML = `
            <div><span class="label">Branch ID:</span> ${branch.branchId}</div>
            <div><span class="label">IFSC:</span> ${branch.ifscCode}</div>
            <div><span class="label">Manager ID:</span> ${branch.managerId}</div>
            <div><span class="label">State:</span> ${branch.state}</div>
            <div><span class="label">District:</span> ${branch.district}</div>
            <div><span class="label">Locality:</span> ${branch.locality}</div>
        `;
	}

	// ========== Payment Mode Frequency Chart ==========
	fetch(`${contextPath}/viiva/transactions?limit=-1`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const txns = data.data?.transactions || [];
			const modeCount = {};

			txns.forEach(tx => {
				const mode = tx.paymentMode;
				modeCount[mode] = (modeCount[mode] || 0) + 1;
			});

			renderPieChart(paymentModeChartCanvas, modeCount);
		})
		.catch(err => console.error("Failed to load payment mode chart", err));

	// ========== Accounts Per Branch Bar Chart ==========
	fetch(`${contextPath}/viiva/account`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const accounts = data.data?.accounts || [];
			const accountCount = {};

			accounts.forEach(acc => {
				const bid = acc.branchId;
				accountCount[bid] = (accountCount[bid] || 0) + 1;
			});

			renderBarChart(accountsPerBranchChartCanvas, accountCount, "Accounts");
		})
		.catch(err => console.error("Failed to load account count chart", err));

	// ========== Top Performing Branches ==========
	Promise.all([
		fetch(`${contextPath}/viiva/account?limit=-1`, { method: "GET", headers }).then(res => res.json()),
		fetch(`${contextPath}/viiva/transactions?limit=-1`, { method: "GET", headers }).then(res => res.json())
	])
		.then(([accountRes, txnRes]) => {
			const accounts = accountRes.data?.accounts || [];
			const transactions = txnRes.data?.transactions || [];

			console.log(`📊 Total transactions: ${transactions.length}`);
			console.log(`🏦 Total accounts: ${accounts.length}`);

			// Build map: accountId → branchId
			const accountToBranch = {};
			accounts.forEach(acc => {
				accountToBranch[acc.accountId] = acc.branchId;
			});

			// Count transactions per branch based on accountId
			const branchTxCount = {};
			let matched = 0;

			transactions.forEach(tx => {
				const accId = tx.accountId;
				const branch = accountToBranch[accId];
				if (branch != null) {
					matched++;
					branchTxCount[branch] = (branchTxCount[branch] || 0) + 1;
				}
			});

			console.log(`✅ Transactions matched with account → branch: ${matched}`);
			console.log("📌 Branch transaction counts:", branchTxCount);

			renderBarChart(topBranchesChartCanvas, branchTxCount, "Transactions");
		})
		.catch(err => {
			console.error("Failed to load top performing branches chart", err);
		});

	// ========== Chart Helpers ==========
	function renderPieChart(canvas, dataMap) {
		new Chart(canvas, {
			type: 'pie',
			data: {
				labels: Object.keys(dataMap),
				datasets: [{
					data: Object.values(dataMap),
					backgroundColor: ["#4CAF50", "#2196F3", "#FFC107", "#FF5722", "#9C27B0", "#00BCD4"]
				}]
			}
		});
	}

	function renderBarChart(canvas, dataMap, label = '') {
		new Chart(canvas, {
			type: 'bar',
			data: {
				labels: Object.keys(dataMap),
				datasets: [{
					label,
					data: Object.values(dataMap),
					backgroundColor: '#2f4f9d'
				}]
			},
			options: {
				responsive: true,
				scales: {
					y: {
						beginAtZero: true
					}
				}
			}
		});
	}

	// ========== Go To Branch Page ==========
	document.getElementById("go-to-branches")?.addEventListener("click", () => {
		window.location.href = `${window.contextPath}/pages/home.jsp?view=branch.jsp`;
	});
}


/*export function initDashboard(contextPath, userId, branchId, role) {
	console.log("Initializing user dashboard with", { contextPath, userId, branchId, role });
	console.log("📦 dashboard-superadmin.js loaded and initDashboard exported");
	const branchSidebar = document.getElementById("branch-sidebar");
	const branchDetailsContainer = document.getElementById("branch-details");

	const paymentModeChartCanvas = document.getElementById("paymentModeChart");
	const accountsPerBranchChartCanvas = document.getElementById("accountsPerBranchChart");

	const headers = {
		"Content-Type": "application/json",
		"Accept": "application/json"
	};

	// Load Branches
	fetch(`${contextPath}/viiva/branch`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const branches = data.data?.branches || [];
			if (branches.length === 0) {
				branchSidebar.innerHTML = "<p style='padding: 8px;'>No branches found.</p>";
				return;
			}

			branches.forEach((branch, idx) => {
				const tab = document.createElement("div");
				tab.classList.add("branch-tab");
				if (idx === 0) tab.classList.add("active");
				tab.textContent = `Branch ${branch.branchId}`;
				tab.addEventListener("click", () => {
					document.querySelectorAll(".branch-tab").forEach(t => t.classList.remove("active"));
					tab.classList.add("active");
					renderBranchDetails(branch);
				});
				branchSidebar.appendChild(tab);
			});

			renderBranchDetails(branches[0]);
		})
		.catch(err => {
			branchSidebar.innerHTML = "<p style='padding: 8px;'>Failed to load branches.</p>";
			console.error(err);
		});

	function renderBranchDetails(branch) {
		branchDetailsContainer.innerHTML = `
            <div><span class="label">Branch ID:</span> ${branch.branchId}</div>
            <div><span class="label">IFSC:</span> ${branch.ifscCode}</div>
            <div><span class="label">Manager ID:</span> ${branch.managerId}</div>
            <div><span class="label">State:</span> ${branch.state}</div>
            <div><span class="label">District:</span> ${branch.district}</div>
            <div><span class="label">Locality:</span> ${branch.locality}</div>
        `;
	}

	// Load Payment Mode Pie Chart
	fetch(`${contextPath}/viiva/transactions?limit=-1`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const txns = data.data?.transactions || [];
			const paymentCounts = {};

			txns.forEach(tx => {
				const mode = tx.paymentMode;
				paymentCounts[mode] = (paymentCounts[mode] || 0) + 1;
			});

			renderPieChart(paymentModeChartCanvas, paymentCounts);
		})
		.catch(err => {
			console.error("Failed to load payment mode chart", err);
		});

	// Load Account Count Bar Chart
	fetch(`${contextPath}/viiva/account`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const accounts = data.data?.accounts || [];
			const branchCounts = {};

			accounts.forEach(acc => {
				const bid = acc.branchId;
				branchCounts[bid] = (branchCounts[bid] || 0) + 1;
			});

			renderBarChart(accountsPerBranchChartCanvas, branchCounts);
		})
		.catch(err => {
			console.error("Failed to load account count chart", err);
		});

	// Chart Helpers
	function renderPieChart(canvas, dataMap) {
		new Chart(canvas, {
			type: 'pie',
			data: {
				labels: Object.keys(dataMap),
				datasets: [{
					data: Object.values(dataMap),
					backgroundColor: ["#4CAF50", "#2196F3", "#FFC107", "#FF5722", "#9C27B0", "#00BCD4"]
				}]
			}
		});
	}

	function renderBarChart(canvas, dataMap) {
		new Chart(canvas, {
			type: 'bar',
			data: {
				labels: Object.keys(dataMap),
				datasets: [{
					label: 'Accounts',
					data: Object.values(dataMap),
					backgroundColor: '#2f4f9d'
				}]
			},
			options: {
				responsive: true,
				scales: {
					y: {
						beginAtZero: true
					}
				}
			}
		});
	}

	document.getElementById("go-to-branches")?.addEventListener("click", () => {
	    window.location.href = `${window.contextPath}/pages/home.jsp?view=branch.jsp`;
	});
}
*/