export function initDashboard(contextPath, userId, branchId, role) {
	console.log("Initializing employee dashboard", { contextPath, userId, branchId, role });

	const requestList = document.getElementById("request-list");
	const branchOverview = document.getElementById("branch-overview");
	const paymentModeChartCanvas = document.getElementById("paymentModeChart");
	const requestModal = document.getElementById("request-modal");
	const overlay = document.getElementById("modal-overlay");

	const headers = {
		"Content-Type": "application/json",
		"Accept": "application/json"
	};

	// Load Pending Requests
	fetch(`${contextPath}/viiva/request?status=PENDING`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const requests = data.data?.requests?.filter(r => r.branchId === branchId) || [];
			if (requests.length === 0) {
				requestList.innerHTML = "<p>No pending requests.</p>";
				return;
			}
			const maxToShow = 7;
			const limitedRequests = requests.slice(0, maxToShow);
			limitedRequests.forEach(r => {
				const item = document.createElement("div");
				item.classList.add("request-item");
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

	// Load Branch Overview
	fetch(`${contextPath}/viiva/branch/${branchId}`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const branch = data.data;
			branchOverview.innerHTML = `
                <div><span class="label">Branch ID:</span> ${branch.branchId}</div>
				<div><span class="label">Manager ID:</span> ${branch.managerId}</div>
                <div><span class="label">IFSC:</span> ${branch.ifscCode}</div>
                <div><span class="label">State:</span> ${branch.state}</div>
                <div><span class="label">District:</span> ${branch.district}</div>
                <div><span class="label">Locality:</span> ${branch.locality}</div>
            `;
		})
		.catch(err => {
			branchOverview.innerHTML = "<p>Failed to load branch overview.</p>";
			console.error(err);
		});

	// Load Chart Stats
	fetch(`${contextPath}/viiva/transactions?limit=-1`, { method: "GET", headers })
		.then(res => res.json())
		.then(data => {
			const transactions = data.data?.transactions || [];
			const paymentModeMap = {};

			transactions.forEach(tx => {
				paymentModeMap[tx.paymentMode] = (paymentModeMap[tx.paymentMode] || 0) + 1;
			});

			renderPieChart(paymentModeChartCanvas, paymentModeMap);
		});

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

	// Modal Logic
	function openRequestModal(request) {
		const headers = {
			"Content-Type": "application/json",
			"Accept": "application/json"
		};

		// Load user info
		fetch(`${contextPath}/viiva/user?userId=${request.customerId}`, { method: "GET", headers })
			.then(res => res.json())
			.then(data => {
				const user = data.data?.users?.[0];
				const userID = user?.userId || "Invalid User ID";

				requestModal.innerHTML = `
					<div class="modal-box">
						<h3>Update Request</h3>
						<p><strong>User ID:</strong> <span id="userid-value">${userID}</span></p>
						<div>
							<label for="status">Status:</label>
							<select id="status">
								<option value="APPROVED">Approve</option>
								<option value="REJECTED">Reject</option>
							</select>
						</div>
						<div>
							<label for="remark">Remark:</label>
							<input type="text" id="remark" placeholder="Optional remark"/>
						</div>
						<div class="modal-actions" style="display: flex; gap: 10px; margin-top: 12px;">
							<button id="request-update-btn" style="flex: 1;">Update</button>
							<button id="cancel-btn" style="flex: 1;">Cancel</button>
						</div>
					</div>
				`;

				overlay.style.display = "block";
				requestModal.style.display = "flex";

				// Cancel button closes modal
				document.getElementById("cancel-btn").addEventListener("click", closeModal);

				// Update request
				document.getElementById("request-update-btn").addEventListener("click", () => {
					const status = document.getElementById("status").value;
					const remark = document.getElementById("remark").value;

					const payload = {
						requestId: request.requestId,
						customerId: request.customerId,
						branchId: request.branchId,
						balance: request.balance,
						accountType: request.accountType,
						status: status,
						remarks: remark
					};

					fetch(`${contextPath}/viiva/request/${request.requestId}`, {
						method: "PUT",
						headers,
						body: JSON.stringify(payload)
					})
						.then(res => res.json())
						.then(data => {
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
		requestModal.style.display = "none";
		overlay.style.display = "none";
		requestModal.innerHTML = "";
	}

	document.getElementById("view-requests-btn")?.addEventListener("click", () => {
		window.location.href = `${contextPath}/pages/home.jsp?view=employee.jsp&section=requests`;
	});

}

