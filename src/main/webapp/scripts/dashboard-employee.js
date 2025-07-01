export function initDashboard(contextPath, userId, branchId, role) {
	console.log("Initializing employee dashboard", { contextPath, userId, branchId, role });

	const headers = {
		"Content-Type": "application/json",
		"Accept": "application/json"
	};

	const requestList = document.getElementById("request-list");
	const branchOverview = document.getElementById("branch-overview");
	const paymentModeChartCanvas = document.getElementById("paymentModeChart");
	const paymentModeLegend = document.getElementById("paymentModeLegend");
	const requestModal = document.getElementById("request-modal");
	const overlay = document.getElementById("modal-overlay");
	const employeePerformanceChartCanvas = document.getElementById("employeePerformanceChart");

	// Branch Overview
	fetch(`${contextPath}/viiva/branch/${branchId}`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const branch = data.data;
			branchOverview.innerHTML = `
				<div class="branch-info"><span class="label">Branch ID:</span> ${branch.branchId}</div>
				<div class="branch-info"><span class="label">Manager ID:</span> ${branch.managerId}</div>
				<div class="branch-info"><span class="label">IFSC:</span> ${branch.ifscCode}</div>
				<div class="branch-info"><span class="label">State:</span> ${branch.state}</div>
				<div class="branch-info"><span class="label">District:</span> ${branch.district}</div>
				<div class="branch-info"><span class="label">Locality:</span> ${branch.locality}</div>
			`;
		})
		.catch(err => {
			branchOverview.innerHTML = "<p>Failed to load branch overview.</p>";
			console.error(err);
		});

	// Pending Requests
	fetch(`${contextPath}/viiva/request?status=PENDING`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const requests = data.data?.requests?.filter(r => r.branchId === branchId) || [];
			if (requests.length === 0) {
				requestList.innerHTML = "<p>No pending requests.</p>";
				return;
			}
			requestList.innerHTML = "";
			requests.slice(0, 7).forEach(r => {
				const item = document.createElement("div");
				item.className = "request-item";
				item.innerHTML = `
					<div><span class="label">Request ID:</span> ${r.requestId}</div>
					<div><span class="label">Customer ID:</span> ${r.customerId}</div>
					<div><span class="label">Type:</span> ${r.accountType}</div>
					<div><span class="label">Status:</span> ${r.status}</div>
				`;
				item.addEventListener("click", () => openRequestModal(r));
				requestList.appendChild(item);
			});
		})
		.catch(err => {
			requestList.innerHTML = "<p>Failed to load requests.</p>";
			console.error(err);
		});

	// Payment Mode Pie Chart
	fetch(`${contextPath}/viiva/transactions?limit=-1`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const transactions = data.data?.transactions || [];
			const paymentModeMap = {};

			for (const tx of transactions) {
				paymentModeMap[tx.paymentMode] = (paymentModeMap[tx.paymentMode] || 0) + 1;
			}

			renderDonutChart(paymentModeChartCanvas, paymentModeMap);
			if (paymentModeLegend) renderLegend(paymentModeMap);
		})
		.catch(err => {
			console.error("Failed to load transaction data:", err);
		});

	// Requests Approved/Rejected Chart
	fetch(`${contextPath}/viiva/request?limit=-1`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const requests = data.data?.requests || [];

			const approvedMap = {};
			const rejectedMap = {};

			requests.forEach(req => {
				const empId = req.modifiedBy;
				if (!empId) return;

				if (req.status === "APPROVED") {
					approvedMap[empId] = (approvedMap[empId] || 0) + 1;
				} else if (req.status === "REJECTED") {
					rejectedMap[empId] = (rejectedMap[empId] || 0) + 1;
				}
			});

			const allEmpIds = Array.from(new Set([
				...Object.keys(approvedMap),
				...Object.keys(rejectedMap)
			]));
			const labels = allEmpIds.map(id => `Emp ${id}`);
			const approvedValues = allEmpIds.map(id => approvedMap[id] || 0);
			const rejectedValues = allEmpIds.map(id => rejectedMap[id] || 0);

			console.log("Performance Labels:", labels);
			console.log("Approved Values:", approvedValues);
			console.log("Rejected Values:", rejectedValues);

			new Chart(employeePerformanceChartCanvas, {
				type: 'bar',
				data: {
					labels,
					datasets: [
						{
							label: 'Approved',
							data: approvedValues,
							backgroundColor: '#4CAF50'
						},
						{
							label: 'Rejected',
							data: rejectedValues,
							backgroundColor: '#F44336'
						}
					]
				},
				options: {
					responsive: true,
					scales: {
						y: {
							beginAtZero: true
						}
					},
					plugins: {
						legend: {
							display: true,
							position: 'top'
						}
					}
				}
			});
		})
		.catch(err => {
			console.error("Failed to load employee performance chart", err);
		});

	function renderDonutChart(canvas, dataMap) {
		const labels = Object.keys(dataMap);
		const values = Object.values(dataMap);
		const total = values.reduce((a, b) => a + b, 0);
		const colors = [
			"#4CAF50", "#2196F3", "#FFC107", "#FF5722",
			"#9C27B0", "#00BCD4", "#795548", "#607D8B"
		];

		new Chart(canvas, {
			type: 'doughnut',
			data: {
				labels,
				datasets: [{
					label: 'Payment Modes',
					data: values,
					backgroundColor: colors.slice(0, labels.length)
				}]
			},
			options: {
				responsive: true,
				cutout: '60%',
				plugins: {
					legend: { display: false },
					tooltip: {
						callbacks: {
							label: function (ctx) {
								const label = ctx.label || '';
								const value = ctx.parsed;
								const percent = ((value / total) * 100).toFixed(1);
								return `${label}: ${value} (${percent}%)`;
							}
						}
					}
				}
			}
		});
	}

	function renderLegend(dataMap) {
		const colors = [
			"#4CAF50", "#2196F3", "#FFC107", "#FF5722",
			"#9C27B0", "#00BCD4", "#795548", "#607D8B"
		];
		const entries = Object.entries(dataMap);
		paymentModeLegend.innerHTML = entries.map(([key, value], idx) => `
			<div class="custom-legend-item">
				<span class="custom-legend-color" style="background-color: ${colors[idx]};"></span>
				${key}: ${value}
			</div>
		`).join('');
	}

	// Modal to approve/reject request
	function openRequestModal(request) {
		fetch(`${contextPath}/viiva/user?userId=${request.customerId}`, { method: "GET", headers })
			.then(res => res.json())
			.then(data => {
				const user = data.data?.users?.[0];
				requestModal.innerHTML = `
					<div class="modal-box">
						<h3>Update Request</h3>
						<p><strong>User ID:</strong> <span>${user?.userId || 'N/A'}</span></p>
						<label for="status">Status:</label>
						<select id="status">
							<option value="APPROVED">Approve</option>
							<option value="REJECTED">Reject</option>
						</select>
						<label for="remark">Remark:</label>
						<input type="text" id="remark" placeholder="Optional remark">
						<div class="modal-actions">
							<button id="request-update-btn">Update</button>
							<button id="cancel-btn">Cancel</button>
						</div>
					</div>
				`;
				overlay.style.display = "block";
				requestModal.style.display = "flex";

				document.getElementById("cancel-btn").addEventListener("click", closeModal);
				document.getElementById("request-update-btn").addEventListener("click", () => {
					const status = document.getElementById("status").value;
					const remark = document.getElementById("remark").value;
					const payload = { ...request, status, remarks: remark };

					fetch(`${contextPath}/viiva/request/${request.requestId}`, {
						method: "PUT",
						headers,
						body: JSON.stringify(payload)
					})
						.then(res => res.json())
						.then(() => {
							alert("Request updated successfully!");
							closeModal();
							location.reload();
						})
						.catch(err => {
							alert("Failed to update request.");
							console.error(err);
						});
				});
			});
	}

	function closeModal() {
		overlay.style.display = "none";
		requestModal.style.display = "none";
		requestModal.innerHTML = "";
	}

	document.getElementById("view-requests-btn")?.addEventListener("click", () => {
		window.location.href = `${contextPath}/pages/home.jsp?view=employee.jsp&section=requests`;
	});
}
