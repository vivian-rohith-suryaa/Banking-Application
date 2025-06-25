export function initEmployeeModule(contextPath, userId, role, userBranchId) {
    const sectionMap = {
        users: {
            endpoint: "/viiva/user",
            filters: ["userId", "type"],
            editable: false,
            columnOrder: ["userId", "name", "email", "phone", "gender", "dob", "aadhar", "pan", "type"]
        },
        accounts: {
            endpoint: "/viiva/account",
            filters: ["customerId", "type"],
            editable: false,
            columnOrder: ["accountId", "customerId", "branchId", "accountType", "balance"]
        },
        transactions: {
            endpoint: "/viiva/transactions",
            filters: ["customerId", "accountId", "type", "paymentMode"],
            editable: false,
            columnOrder: ["transactionId", "customerId", "accountId", "transactedAccount", "transactionType", "paymentMode", "amount", "closingBalance", "transactionTime"]
        },
        requests: {
            endpoint: "/viiva/request",
            filters: ["status", "branchId", "customerId"],
            editable: role >= 2,
            columnOrder: ["requestId", "branchId", "customerId", "accountType", "status", "balance", "remarks"]
        },
        employees: {
            endpoint: "/viiva/employee",
            filters: ["type", "branchId", "employeeId"],
            editable: role >= 3,
            columnOrder: ["employeeId", "branchId", "name", "email", "phone", "type"]
        }
    };

    let currentSection = "users";
    let page = 1;
    const limit = 10;
    const headers = { 'Content-Type': 'application/json' };

    const sectionTitle = document.getElementById("section-title");
    const filterType = document.getElementById("filter-type");
    const filterValue = document.getElementById("filter-value");
    const filterBtn = document.getElementById("filter-button");
    const clearBtn = document.getElementById("clear-button");
    const prevBtn = document.getElementById("prev-page");
    const nextBtn = document.getElementById("next-page");
    const pageInfo = document.getElementById("page-info");
    const dataTable = document.getElementById("data-table");
    const dataBody = document.getElementById("data-body");
    const formModal = document.getElementById("form-modal");
    const modalTitle = document.getElementById("modal-title");
    const form = document.getElementById("entity-form");
    const openAddBtn = document.getElementById("open-add-modal");
    const addEmployeeModal = document.getElementById("add-employee-modal");
    const addEmployeeForm = document.getElementById("add-employee-form");
    const closeModal = document.getElementById("close-modal");
    const closeEmployeeModal = document.getElementById("close-employee-modal");

    document.querySelectorAll(".tree-menu li").forEach(li => {
        li.addEventListener("click", () => {
            currentSection = li.dataset.section;
            page = 1;
            filterType.innerHTML = sectionMap[currentSection].filters.map(f => `<option value="${f}">${f}</option>`).join("");
            filterValue.value = "";
            openAddBtn.classList.toggle("hidden", currentSection !== "employees");
            loadSection();
            document.querySelectorAll(".tree-menu li").forEach(i => i.classList.remove("active"));
            li.classList.add("active");
        });
    });

    function buildURL() {
        const section = sectionMap[currentSection];
        const params = new URLSearchParams({ limit, page });
        const key = filterType.value;
        const val = filterValue.value.trim();
        if (key && val) {
            params.set(key, val);
        }
        return `${contextPath}${section.endpoint}?${params.toString()}`;
    }

    function loadSection() {
        sectionTitle.textContent = currentSection.charAt(0).toUpperCase() + currentSection.slice(1);
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
                console.error("Error loading section:", err);
                dataBody.innerHTML = `<tr><td colspan="10">Failed to load data.</td></tr>`;
            });
    }

    function renderTable(list) {
        dataTable.tHead.innerHTML = "";
        dataBody.innerHTML = "";

        const keys = sectionMap[currentSection]?.columnOrder || Object.keys(list[0] || {});
        const theadRow = document.createElement("tr");
        keys.forEach(k => {
            const th = document.createElement("th");
            th.textContent = k.replace(/([A-Z])/g, " $1").replace(/^./, ch => ch.toUpperCase());
            theadRow.appendChild(th);
        });

        if (sectionMap[currentSection].editable) {
            const th = document.createElement("th");
            th.textContent = "Actions";
            theadRow.appendChild(th);
        }

        dataTable.tHead.appendChild(theadRow);

        list.forEach(item => {
            const tr = document.createElement("tr");
            keys.forEach(k => {
                const td = document.createElement("td");
                td.textContent = item[k] ?? "-";
                tr.appendChild(td);
            });

            if (sectionMap[currentSection].editable) {
                const td = document.createElement("td");

                // For requests, only show edit if status is PENDING
                if (currentSection !== "requests" || item.status === "PENDING") {
                    td.innerHTML = `<img src="${contextPath}/icons/edit.svg" alt="Edit" style="width:18px; cursor:pointer;" class="edit-icon">`;
                    td.style.cursor = "pointer";

                    td.querySelector(".edit-icon").addEventListener("click", e => {
                        e.stopPropagation();
                        showEditModal(item);
                    });
                } else {
                    td.textContent = "-";
                }

                tr.appendChild(td);
            }

            dataBody.appendChild(tr);
        });
    }

    function showEditModal(data) {
        form.innerHTML = "";
        formModal.classList.remove("hidden");
        modalTitle.textContent = `Edit ${currentSection}`;

        if (currentSection === "employees") {
            form.innerHTML = `
                <label>Branch ID</label>
                <input name="branchId" type="number" value="${data.branchId}" required />
                <label>Role</label>
                <select name="type" required>
                    <option value="EMPLOYEE" ${data.type === "EMPLOYEE" ? "selected" : ""}>Employee</option>
                    <option value="MANAGER" ${data.type === "MANAGER" ? "selected" : ""}>Manager</option>
                </select>
                <input type="hidden" name="employeeId" value="${data.employeeId}" />
            `;
        }

        if (currentSection === "requests") {
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
    }

    form.addEventListener("submit", e => {
        e.preventDefault();
        const payload = Object.fromEntries(new FormData(form));
        const idKey = payload.employeeId || payload.requestId;
        fetch(`${contextPath}${sectionMap[currentSection].endpoint}/${idKey}`, {
            method: "PUT",
            headers,
            body: JSON.stringify(payload)
        })
            .then(res => res.json())
            .then(() => {
                formModal.classList.add("hidden");
                loadSection();
            });
    });

    addEmployeeForm.addEventListener("submit", e => {
        e.preventDefault();
        const payload = Object.fromEntries(new FormData(addEmployeeForm));
        fetch(`${contextPath}/viiva/employee`, {
            method: "POST",
            headers,
            body: JSON.stringify(payload)
        })
            .then(res => res.json())
            .then(() => {
                addEmployeeModal.classList.add("hidden");
                loadSection();
            });
    });

    openAddBtn.addEventListener("click", () => addEmployeeModal.classList.remove("hidden"));
    closeModal.addEventListener("click", () => formModal.classList.add("hidden"));
    closeEmployeeModal.addEventListener("click", () => addEmployeeModal.classList.add("hidden"));

    filterBtn.addEventListener("click", () => {
        page = 1;
        loadSection();
    });

    clearBtn.addEventListener("click", () => {
        filterValue.value = "";
        page = 1;
        loadSection();
    });

    prevBtn.addEventListener("click", () => { if (page > 1) { page--; loadSection(); } });
    nextBtn.addEventListener("click", () => { page++; loadSection(); });

    document.querySelector(`.tree-menu li[data-section="${currentSection}"]`)?.click();
}
