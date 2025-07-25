export function initEmployeeModule(contextPath, userId, role, userBranchId) {
	if (role < 2) return; // Only managers and superadmins

	const headers = { "Content-Type": "application/json" };
	const endpoint = `${contextPath}/viiva/employee`;
	const filters = ["employeeId", "branchId", "type"];
	const columns = ["employeeId", "branchId", "name", "email", "phone", "type"];

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

	const addBtn = document.getElementById("open-add-modal");
	const addModal = document.getElementById("add-employee-modal");
	const addForm = document.getElementById("add-employee-form");
	const closeAddModal = document.getElementById("close-employee-modal");

	let page = 1;
	const limit = 10;

	// Setup filters (uppercase display names)
	filterType.innerHTML = filters
			.map(f => `<option value="${f}">${f.replace(/([A-Z])/g, " $1").toUpperCase()}</option>`)
			.join("");


	filterType.addEventListener("change", () => {
		const key = filterType.value;
		const existing = document.getElementById("filter-value");
		if (existing) existing.remove();

		let newInput;

		if (key === "type") {
			newInput = document.createElement("select");
			newInput.id = "filter-value";
			newInput.className = "filter-value";

			const options = [
				{ label: "--Select Role--", value: "" },
				{ label: "Employee", value: "2" },
				{ label: "Manager", value: "3" },
				{ label: "Superadmin", value: "4" }
			];

			options.forEach(({ label, value }) => {
				const option = document.createElement("option");
				option.value = value;
				option.textContent = label;
				newInput.appendChild(option);
			});
		} else {
			newInput = document.createElement("input");
			newInput.type = "text";
			newInput.placeholder = "Enter value";
			newInput.id = "filter-value";
			newInput.className = "filter-value";
		}

		filterValueInput = newInput;
		filterContainer.appendChild(newInput);
	});

	function buildURL() {
		const params = new URLSearchParams({ page, limit });
		const key = filterType.value;
		const val = filterValueInput.value.trim();
		if (key && val) params.set(key, val);
		return `${endpoint}?${params.toString()}`;
	}

	function loadEmployees() {
		fetch(buildURL(), { method: "GET", headers })
			.then(res => res.json())
			.then(data => {
				const list = Array.isArray(data.data.employees) ? data.data.employees : [];
				renderTable(list);
				pageInfo.textContent = `Page ${page}`;
				prevBtn.disabled = page === 1;
				nextBtn.disabled = list.length < limit;
			})
			.catch(() => {
				tbody.innerHTML = `<tr><td colspan="10">Failed to load employees.</td></tr>`;
			});
	}

	function renderTable(list) {
		table.tHead.innerHTML = "";
		tbody.innerHTML = "";

		const thead = document.createElement("tr");
		columns.forEach(col => {
			const th = document.createElement("th");
			th.textContent = col.replace(/([A-Z])/g, " $1").replace(/^./, ch => ch.toUpperCase());
			thead.appendChild(th);
		});
		thead.appendChild(document.createElement("th")).textContent = "Actions";
		table.tHead.appendChild(thead);

		list.forEach(emp => {
			const tr = document.createElement("tr");

			columns.forEach(col => {
				const td = document.createElement("td");
				td.textContent = emp[col] ?? "-";
				tr.appendChild(td);
			});

			const actionTd = document.createElement("td");
			const canEdit = role === 4 || (role === 3 && emp.branchId == userBranchId);

			if (canEdit) {
				const icon = document.createElement("img");
				icon.src = `${contextPath}/icons/edit.svg`;
				icon.alt = "Edit";
				icon.style.cursor = "pointer";
				icon.style.width = "18px";
				icon.classList.add("edit-icon");
				icon.addEventListener("click", e => {
					e.stopPropagation();
					openEditModal(emp);
				});
				actionTd.appendChild(icon);
			} else {
				actionTd.textContent = "-";
			}

			tr.appendChild(actionTd);
			tbody.appendChild(tr);
		});
	}

	function openEditModal(emp) {
		formModal.classList.remove("hidden");
		modalTitle.textContent = "Edit Employee";

		form.innerHTML = `
			<label>Branch ID</label>
			<input type="number" name="branchId" value="${emp.branchId}" required />
			<label>Role</label>
			<select name="type" required>
				<option value="2" ${emp.type === "EMPLOYEE" ? "selected" : ""}>Employee</option>
				<option value="3" ${emp.type === "MANAGER" ? "selected" : ""}>Manager</option>
			</select>
			<input type="hidden" name="employeeId" value="${emp.employeeId}" />
		`;
	}

	form.addEventListener("submit", e => {
		e.preventDefault();
		const payload = Object.fromEntries(new FormData(form));
		const id = payload.employeeId;

		fetch(`${endpoint}/${id}`, {
			method: "PUT",
			headers,
			body: JSON.stringify(payload)
		})
			.then(res => res.json())
			.then(() => {
				formModal.classList.add("hidden");
				loadEmployees();
			});
	});

	addForm.addEventListener("submit", e => {
		e.preventDefault();
		const payload = Object.fromEntries(new FormData(addForm));
		payload.type = "2"; // force EMPLOYEE (value 2) on add

		fetch(endpoint, {
			method: "POST",
			headers,
			body: JSON.stringify(payload)
		})
			.then(res => res.json())
			.then(() => {
				addModal.classList.add("hidden");
				loadEmployees();
			});
	});

	// Event listeners
	closeModal.addEventListener("click", () => formModal.classList.add("hidden"));
	closeAddModal.addEventListener("click", () => addModal.classList.add("hidden"));
	addBtn.addEventListener("click", () => addModal.classList.remove("hidden"));

	filterBtn.addEventListener("click", () => {
		page = 1;
		loadEmployees();
	});

	clearBtn.addEventListener("click", () => {
		filterValueInput.value = "";
		page = 1;
		loadEmployees();
	});

	prevBtn.addEventListener("click", () => {
		if (page > 1) {
			page--;
			loadEmployees();
		}
	});

	nextBtn.addEventListener("click", () => {
		page++;
		loadEmployees();
	});

	// Initial Load
	loadEmployees();
}
