export function initBranchModule(contextPath, userId, role, userBranchId) {
    const tbody = document.getElementById("branch-body");
    const prevBtn = document.getElementById("prev-page");
    const nextBtn = document.getElementById("next-page");
    const pageInfo = document.getElementById("page-info");
    const filterType = document.getElementById("filter-type");
    const filterValue = document.getElementById("filter-value");
    const filterBtn = document.getElementById("filter-button");
    const clearBtn = document.getElementById("clear-button");
    const updateModal = document.getElementById("update-modal");
    const addModal = document.getElementById("add-modal");

    let page = 1;
    const limit = 10;
    let totalFetched = 0;

    function buildURL() {
        const key = filterType.value.trim();
        const val = filterValue.value.trim();
        const params = new URLSearchParams({ limit, page });
        if (key && val) params.append(key, val);
        return `${contextPath}/viiva/branch?${params.toString()}`;
    }

    function fetchAndRender() {
        tbody.innerHTML = `<tr><td colspan="7">Loading...</td></tr>`;
        pageInfo.textContent = `Page ${page}`;

        fetch(buildURL(), {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then(res => res.json())
            .then(data => {
                const branches = data?.data?.branches || [];
                totalFetched = branches.length;

                if (branches.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="7">No branches found.</td></tr>`;
                    nextBtn.disabled = true;
                    return;
                }

                renderRows(branches);
                prevBtn.disabled = page === 1;
                nextBtn.disabled = branches.length < limit;
            })
            .catch(err => {
                console.error("Failed to load branches:", err);
                tbody.innerHTML = `<tr><td colspan="7">Error loading data.</td></tr>`;
            });
    }

    function renderRows(branches) {
        tbody.innerHTML = "";
        branches.forEach(branch => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${branch.branchId}</td>
                <td>${branch.ifscCode}</td>
                <td>${branch.locality}</td>
                <td>${branch.district}</td>
                <td>${branch.state}</td>
                <td>${branch.managerId || '-'}</td>
            `;

            if (role >= 2) {
                const actionTd = document.createElement("td");
                const canUpdate = (role === 2 && branch.branchId === userBranchId) || role === 3 || role === 4;

                if (canUpdate) {
                    const updateBtn = document.createElement("button");
                    updateBtn.className = "edit-btn";
                    updateBtn.title = "Edit Branch";
                    const img = document.createElement("img");
                    img.src = `${contextPath}/icons/edit.svg`;
                    img.alt = "Edit";
                    img.style.width = "18px";
                    img.style.height = "18px";
                    updateBtn.appendChild(img);

                    updateBtn.addEventListener("click", () => showUpdateModal(branch));
                    actionTd.appendChild(updateBtn);
                }

                row.appendChild(actionTd);
            }

            tbody.appendChild(row);
        });
    }

    function showUpdateModal(branch) {
        updateModal.classList.remove("hidden");
        const form = document.getElementById("update-branch-form");
        form.branchId.value = branch.branchId;
        form.locality.value = branch.locality;
        form.district.value = branch.district;
        form.state.value = branch.state;
        if (role === 4) form.managerId.value = branch.managerId || "";
    }

    document.getElementById("close-modal").addEventListener("click", () => {
        updateModal.classList.add("hidden");
    });

    // ✅ Bind update form submission once
    const updateForm = document.getElementById("update-branch-form");
    updateForm.addEventListener("submit", function (e) {
        e.preventDefault();

        const updated = {
            branchId: updateForm.branchId.value,
            locality: updateForm.locality.value,
            district: updateForm.district.value,
            state: updateForm.state.value,
        };

        if (role === 4) {
            updated.managerId = updateForm.managerId.value;
        }

        fetch(`${contextPath}/viiva/branch/${updated.branchId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updated)
        })
            .then(res => res.json())
            .then(() => {
                updateModal.classList.add("hidden");
                fetchAndRender();
            })
            .catch(err => console.error("Update failed:", err));
    });

    // Pagination
    prevBtn.addEventListener("click", () => {
        if (page > 1) {
            page--;
            fetchAndRender();
        }
    });

    nextBtn.addEventListener("click", () => {
        if (totalFetched === limit) {
            page++;
            fetchAndRender();
        }
    });

    // Filter handlers
    filterBtn.addEventListener("click", () => {
        page = 1;
        fetchAndRender();
    });

    clearBtn.addEventListener("click", () => {
        filterValue.value = "";
        page = 1;
        fetchAndRender();
    });

    // Add branch (for superadmin only)
    const openAdd = document.getElementById("open-add-modal");
    const closeAdd = document.getElementById("close-add-modal");

    if (openAdd && addModal && closeAdd) {
        openAdd.addEventListener("click", () => addModal.classList.remove("hidden"));
        closeAdd.addEventListener("click", () => addModal.classList.add("hidden"));

        const addForm = document.getElementById("add-branch-form");
        addForm.addEventListener("submit", function (e) {
            e.preventDefault();

            const newBranch = {
                locality: addForm.locality.value,
                district: addForm.district.value,
                state: addForm.state.value,
                managerId: addForm.managerId.value
            };

            fetch(`${contextPath}/viiva/branch`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newBranch)
            })
                .then(res => res.json())
                .then(() => {
                    addModal.classList.add("hidden");
                    addForm.reset();
                    fetchAndRender();
                })
                .catch(err => console.error("Add failed:", err));
        });
    }

    fetchAndRender();
}
