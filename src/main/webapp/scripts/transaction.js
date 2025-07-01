export function initTransactionModule(contextPath, userId, role, branchId) {
	const endpoint = "/viiva/transactions";
	const filters = ["customerId", "accountId", "type", "paymentMode"];
	const columnOrder = [
		"transactionId",
		"customerId",
		"accountId",
		"transactedAccount",
		"transactionType",
		"paymentMode",
		"amount",
		"closingBalance",
		"transactionTime"
	];
	const limit = 10;

	let page = 1;
	const headers = { "Content-Type": "application/json" };

	// DOM Elements
	const sectionTitle = document.getElementById("section-title");
	const filterType = document.getElementById("filter-type");
	let filterValueInput = document.getElementById("filter-value");
	const filterContainer = filterValueInput.parentElement;

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
		const val = filterValueInput.value.trim();
		if (key && val) params.set(key, val);
		return `${contextPath}${endpoint}?${params.toString()}`;
	}

	function loadTransactions() {
		sectionTitle.textContent = "Transactions";
		fetch(buildURL(), { method: "GET", headers })
			.then(res => res.json())
			.then(data => {
				const list = Array.isArray(data.data)
					? data.data
					: Object.values(data.data || {}).find(v => Array.isArray(v)) || [];
				pageInfo.textContent = `Page ${page}`;
				renderTable(list);
				prevBtn.disabled = page === 1;
				nextBtn.disabled = list.length < limit;
			})
			.catch(err => {
				console.error("Error loading transactions:", err);
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

	// Filter setup
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
			["", "CREDIT", "DEBIT"].forEach(opt => {
				const option = document.createElement("option");
				option.value = opt;
				option.textContent = opt || "--Select Account Type--";
				newInput.appendChild(option);
			});
		} else if (key === "paymentMode") {
			newInput = document.createElement("select");
			["", "DEPOSIT", "WITHDRAWAL", "BANK_TRANSFER", "SELF_TRANSFER"].forEach(opt => {
				const option = document.createElement("option");
				option.value = opt;
				option.textContent = opt || "--Select Payment Mode--";
				newInput.appendChild(option);
			});
		} else {
			newInput = document.createElement("input");
			newInput.type = "text";
			newInput.placeholder = "Enter value";
		}

		newInput.id = "filter-value";
		newInput.className = "filter-value";
		filterValueInput = newInput;
		filterContainer.appendChild(newInput);
	});

	// Event listeners
	filterBtn.addEventListener("click", () => {
		page = 1;
		loadTransactions();
	});

	clearBtn.addEventListener("click", () => {
		filterValueInput.value = "";
		page = 1;
		loadTransactions();
	});

	prevBtn.addEventListener("click", () => {
		if (page > 1) {
			page--;
			loadTransactions();
		}
	});

	nextBtn.addEventListener("click", () => {
		page++;
		loadTransactions();
	});

	// Initial load
	loadTransactions();
}
