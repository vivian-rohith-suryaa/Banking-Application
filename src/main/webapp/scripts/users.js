export function initUsersModule(contextPath, userId, role, branchId) {
	const endpoint = "/viiva/user";
	const filters = ["userId", "type"];
	const columnOrder = ["userId", "name", "email", "phone", "gender", "dob", "aadhar", "pan", "type"];
	const limit = 10;

	let page = 1;
	const headers = { "Content-Type": "application/json" };

	const sectionTitle = document.getElementById("section-title");
	const filterType = document.getElementById("filter-type");
	let filterValue = document.getElementById("filter-value"); // Will be replaced dynamically
	const filterBtn = document.getElementById("filter-button");
	const clearBtn = document.getElementById("clear-button");
	const prevBtn = document.getElementById("prev-page");
	const nextBtn = document.getElementById("next-page");
	const pageInfo = document.getElementById("page-info");
	const dataTable = document.getElementById("data-table");
	const dataBody = document.getElementById("data-body");

	function buildURL() {
		const params = new URLSearchParams({ limit, page });
		const key = filterType.value;
		const val = filterValue.value.trim();
		if (key && val) params.set(key, val);
		return `${contextPath}${endpoint}?${params.toString()}`;
	}

	function loadUsers() {
		sectionTitle.textContent = "Users";
		fetch(buildURL(), { method: "GET", headers })
			.then(res => res.json())
			.then(data => {
				const list = Array.isArray(data.data) ? data.data : Object.values(data.data || {}).find(v => Array.isArray(v)) || [];
				pageInfo.textContent = `Page ${page}`;
				renderTable(list);
				prevBtn.disabled = page === 1;
				nextBtn.disabled = list.length < limit;
			})
			.catch(err => {
				console.error("Error loading users:", err);
				dataBody.innerHTML = `<tr><td colspan="10">Failed to load data.</td></tr>`;
			});
	}

	function renderTable(list) {
		dataTable.tHead.innerHTML = "";
		dataBody.innerHTML = "";

		const theadRow = document.createElement("tr");
		columnOrder.forEach(k => {
			const th = document.createElement("th");
			th.textContent = k.replace(/([A-Z])/g, " $1").replace(/^./, ch => ch.toUpperCase());
			theadRow.appendChild(th);
		});
		dataTable.tHead.appendChild(theadRow);

		list.forEach(item => {
			const tr = document.createElement("tr");
			columnOrder.forEach(k => {
				const td = document.createElement("td");
				td.textContent = item[k] ?? "-";
				tr.appendChild(td);
			});
			dataBody.appendChild(tr);
		});
	}

	function updateFilterInput() {
		const container = filterValue.parentElement;
		let newInput;

		if (filterType.value === "type") {
			newInput = document.createElement("select");
			newInput.innerHTML = `
				<option value="">-- Select Role --</option>
				<option value="1">Customer</option>
				<option value="2">Employee</option>
				<option value="3">Manager</option>
				<option value="4">SuperAdmin</option>
			`;
		} else {
			newInput = document.createElement("input");
			newInput.type = "text";
			newInput.placeholder = "Enter filter value";
		}

		newInput.id = "filter-value";
		newInput.className = filterValue.className;
		filterValue.replaceWith(newInput);
		filterValue = newInput;
	}

	// Initial Filter Setup
	filterType.innerHTML = filters
			.map(f => `<option value="${f}">${f.replace(/([A-Z])/g, " $1").toUpperCase()}</option>`)
			.join("");

	updateFilterInput(); // set default input

	// Event Listeners
	filterType.addEventListener("change", updateFilterInput);

	filterBtn.addEventListener("click", () => {
		page = 1;
		loadUsers();
	});

	clearBtn.addEventListener("click", () => {
		filterValue.value = "";
		page = 1;
		loadUsers();
	});

	prevBtn.addEventListener("click", () => {
		if (page > 1) {
			page--;
			loadUsers();
		}
	});

	nextBtn.addEventListener("click", () => {
		page++;
		loadUsers();
	});

	// Initial load
	loadUsers();
}
