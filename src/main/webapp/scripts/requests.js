export function initRequestsModule(contextPath, userId, role, branchId) {
	if (role < 2) return;

	const headers = { "Content-Type": "application/json" };
	const endpoint = `${contextPath}/viiva/request`;

	const filterType = document.getElementById("filter-type");
	let filterValueInput = document.getElementById("filter-value");
	const filterContainer = filterValueInput.parentElement;

	const filterBtn = document.getElementById("filter-button");
	const clearBtn = document.getElementById("clear-button");
	const prevBtn = document.getElementById("prev-page");
	const nextBtn = document.getElementById("next-page");
	const pageInfo = document.getElementById("page-info");

	const table = document.getElementById("data-table");
	const tbody = document.getElementById("data-body");

	const formModal = document.getElementById("form-modal");
	const modalTitle = document.getElementById("modal-title");
	const form = document.getElementById("entity-form");
	const closeModal = document.getElementById("close-modal");

	let page = 1;
	const limit = 10;

	const filters = role > 3 ? ["branchId", "status", "customerId"] : ["customerId", "status"] ;

	const columns = ["requestId", "branchId", "customerId", "accountType", "status", "balance", "remarks"];

	// Initialize filter dropdown
	filterType.innerHTML = filters
	  .map(f => `<option value="${f}">${f.replace(/([A-Z])/g, " $1").toUpperCase()}</option>`)
	  .join("");

	filterType.addEventListener("change", () => {
	  const key = filterType.value;

	  const existing = document.getElementById("filter-value");
	  if (existing) existing.remove();

	  let newInput;

	  if (key === "status") {
	    newInput = document.createElement("select");
	    ["", "PENDING", "APPROVED", "REJECTED"].forEach(opt => {
	      const option = document.createElement("option");
	      option.value = opt;
	      option.textContent = opt || "--Select Status--";
	      newInput.appendChild(option);
	    });
	  } else {
	    newInput = document.createElement("input");
	    newInput.type = "text";
	    newInput.placeholder = "Enter 12-digit ID";
	    newInput.maxLength = 12;
	    newInput.inputMode = "numeric"; // for mobile support

	    // Accept only digits
	    newInput.addEventListener("input", (e) => {
	      e.target.value = e.target.value.replace(/[^0-9]/g, "").slice(0, 12);
	    });

	    newInput.addEventListener("keypress", (e) => {
	      if (!/[0-9]/.test(e.key)) {
	        e.preventDefault();
	      }
	    });
	  }

	  newInput.id = "filter-value";
	  newInput.className = "filter-value styled-input"; // Add styling class
	  filterContainer.appendChild(newInput);
	});

	function buildURL() {
		const params = new URLSearchParams({ page, limit });
		const key = filterType.value;
		const val = filterValueInput.value.trim();
		if (key && val) params.set(key, val);
		return `${endpoint}?${params.toString()}`;
	}

	function loadRequests() {
		fetch(buildURL(), { method: "GET", headers })
			.then(res => res.json())
			.then(data => {
				const list = Array.isArray(data.data?.requests) ? data.data.requests : [];
				if (list.length === 0) {
					tbody.innerHTML = `
						<tr>
							<td colspan="10" style="
								text-align: center;
								padding: 20px;
								font-size: 16px;
								font-weight: 500;
								color: #555;
								background-color: #f9f9f9;
								border: 1px solid #ddd;
							">
								🚫 No requests found
							</td>
						</tr>`;
					table.tHead.innerHTML = "";
					pageInfo.textContent = `Page ${page}`;
					prevBtn.disabled = page === 1;
					nextBtn.disabled = true;
					return;
				}
				renderTable(list);
				pageInfo.textContent = `Page ${page}`;
				prevBtn.disabled = page === 1;
				nextBtn.disabled = list.length < limit;
			})


			.catch(() => {
				tbody.innerHTML = `<tr><td colspan="10">Failed to load data</td></tr>`;
			});
	}

	function renderTable(list) {
		table.tHead.innerHTML = "";
		tbody.innerHTML = "";

		const thead = document.createElement("tr");
		columns.forEach(col => {
			const th = document.createElement("th");
			th.textContent = col;
			thead.appendChild(th);
		});
		thead.appendChild(document.createElement("th")).textContent = "Actions";
		table.tHead.appendChild(thead);

		list.forEach(item => {
			const tr = document.createElement("tr");

			columns.forEach(col => {
				const td = document.createElement("td");
				td.textContent = item[col] ?? "-";
				tr.appendChild(td);
			});

			const actionTd = document.createElement("td");
			if (item.status === "PENDING") {
				const editIcon = document.createElement("img");
				editIcon.src = `${contextPath}/icons/edit.svg`;
				editIcon.alt = "Edit";
				editIcon.classList.add("edit-icon");
				editIcon.style.cursor = "pointer";
				editIcon.style.width = "18px";
				editIcon.addEventListener("click", e => {
					e.stopPropagation();
					openEditModal(item);
				});
				actionTd.appendChild(editIcon);
			} else {
				actionTd.textContent = "-";
			}
			tr.appendChild(actionTd);

			tbody.appendChild(tr);
		});
	}

	function openEditModal(data) {
		formModal.classList.remove("hidden");
		modalTitle.textContent = "Update Request";
		form.innerHTML = `
			<label>Request ID</label>
			<input type="text" name="requestId" value="${data.requestId}" readonly />
			<label>Customer ID</label>
			<input type="text" name="customerId" value="${data.customerId}" readonly />
			<label>Branch ID</label>
			<input type="text" name="branchId" value="${data.branchId}" readonly />
			<label>Account Type</label>
			<input type="text" name="accountType" value="${data.accountType}" readonly />
			<label>Balance</label>
			<input type="number" name="balance" value="${data.balance}" readonly />
			<label>Status</label>
			<select name="status" required>
				<option value="PENDING" ${data.status === "PENDING" ? "selected" : ""}>Pending</option>
				<option value="APPROVED" ${data.status === "APPROVED" ? "selected" : ""}>Approved</option>
				<option value="REJECTED" ${data.status === "REJECTED" ? "selected" : ""}>Rejected</option>
			</select>
			<label>Remarks</label>
			<input type="text" name="remarks" value="${data.remarks || ""}" />
		`;
	}

	form.addEventListener("submit", e => {
		e.preventDefault();
		const payload = Object.fromEntries(new FormData(form));
		const id = payload.requestId;

		fetch(`${endpoint}/${id}`, {
			method: "PUT",
			headers,
			body: JSON.stringify(payload)
		})
			.then(res => res.json())
			.then(() => {
				formModal.classList.add("hidden");
				loadRequests();
			});
	});

	closeModal.addEventListener("click", () => formModal.classList.add("hidden"));

	filterBtn.addEventListener("click", () => {
		page = 1;
		loadRequests();
	});

	clearBtn.addEventListener("click", () => {
		filterValueInput.value = "";
		page = 1;
		loadRequests();
	});

	prevBtn.addEventListener("click", () => {
		if (page > 1) {
			page--;
			loadRequests();
		}
	});

	nextBtn.addEventListener("click", () => {
		page++;
		loadRequests();
	});

	// Initial load
	loadRequests();
}
