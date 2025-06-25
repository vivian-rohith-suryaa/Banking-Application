export function initAccountModule(ctxPath, userId) {
    const result = document.getElementById("account-result");
    const requestBtn = document.getElementById("request-account-btn");
    const requestModal = document.getElementById("request-modal");
    const closeModalBtn = document.getElementById("close-modal-btn");
    const requestForm = document.getElementById("account-request-form");

    // Fetch and display accounts
    function fetchAccounts() {
        fetch(`${ctxPath}/viiva/user/${userId}/accounts`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        })
        .then(res => res.json())
        .then(data => {
            const accounts = data.data.accounts || [];
            renderAccounts(accounts);
        })
        .catch(err => {
            result.innerHTML = "<p>Failed to load accounts.</p>";
            console.error(err);
        });
    }

    fetchAccounts();

    // Modal open/close
    requestBtn.addEventListener("click", () => {
        requestModal.classList.remove("hidden");
    });

    closeModalBtn.addEventListener("click", () => {
        requestModal.classList.add("hidden");
    });

    // Request form submit
    requestForm.addEventListener("submit", e => {
        e.preventDefault();

        const accountType = requestForm.elements["accountType"].value;
        const balanceStr = requestForm.elements["balance"].value.trim();
        const branchIdStr = requestForm.elements["branchId"].value.trim();

        const balance = parseFloat(balanceStr);
        const branchId = parseInt(branchIdStr);
        const errors = [];

        if (!/^[0-9]+(\.[0-9]{1,2})?$/.test(balanceStr) || isNaN(balance) || balance < 0) {
            errors.push("Initial balance must be a valid number ≥ 0.");
        }

        if (!/^\d+$/.test(branchIdStr) || isNaN(branchId) || branchId < 0) {
            errors.push("Branch ID must be a valid integer ≥ 0.");
        }

        if (errors.length > 0) {
            alert(errors.join("\n"));
            return;
        }

        const payload = {
            request: {
                customerId: userId,
                branchId: branchId,
                accountType: accountType,
                balance: balance
            }
        };

        fetch(`${ctxPath}/viiva/account`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(resp => {
            alert(resp.message || "Account request submitted.");
            requestForm.reset();
            requestModal.classList.add("hidden");
            fetchAccounts();
        })
        .catch(() => alert("Failed to submit account request."));
    });

    // Render account cards
    function renderAccounts(accounts) {
        result.innerHTML = accounts.map(acc => `
            <div class="account-card glass" data-account-id="${acc.accountId}">
                <p><strong>ID:</strong> ${acc.accountId}</p>
                <p><strong>Branch ID:</strong> ${acc.branchId}</p>
                <p><strong>Type:</strong> ${acc.accountType}</p>
                <p><strong>Balance:</strong> ₹${acc.balance.toFixed(2)}</p>
                <p><strong>Status:</strong> ${acc.status}</p>
                <button class="pin-edit-btn" title="Update PIN">🔐</button>
                <form class="pin-update-form hidden">
                    <input type="password" name="pin" placeholder="New 6-digit PIN" maxlength="6" pattern="\\d{6}" required />
                    <button type="submit">Update</button>
                </form>
            </div>
        `).join("");

        attachPinListeners();
    }

    // Handle PIN form logic
    function attachPinListeners() {
        result.querySelectorAll(".pin-edit-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                const card = btn.closest(".account-card");
                const form = card.querySelector(".pin-update-form");
                form.classList.toggle("hidden");
            });
        });

        result.querySelectorAll(".pin-update-form").forEach(form => {
            form.addEventListener("submit", e => {
                e.preventDefault();
                const pin = form.elements["pin"].value.trim();
                const accountId = form.closest(".account-card").dataset.accountId;

                if (!/^\d{6}$/.test(pin)) {
                    alert("PIN must be a 6-digit number.");
                    return;
                }
				
				console.log("Sending PIN update for account:", accountId, "with payload:", {
				    account: { pin }
				});

				/*const payload = {
				    account: {
				        accountId: accountId,
				        pin: pin
				    }
				};*/
				fetch(`${ctxPath}/viiva/account/${accountId}`, {
				    method: "PUT",
				    headers: { "Content-Type": "application/json" },
				    body: JSON.stringify({
				        account: {
				            pin: pin
				        }
				    })
				})
                .then(res => res.json())
                .then(resp => {
                    alert(resp.message || "PIN updated successfully.");
                    form.reset();
                    form.classList.add("hidden");
                })
                .catch(() => alert("Failed to update PIN."));
            });
        });
    }

    // Click-outside detection
    document.addEventListener("click", (e) => {
        // Hide all PIN forms if clicked outside
        document.querySelectorAll(".account-card").forEach(card => {
            const pinBtn = card.querySelector(".pin-edit-btn");
            const pinForm = card.querySelector(".pin-update-form");

            if (
                !pinBtn.contains(e.target) &&
                !pinForm.contains(e.target)
            ) {
                pinForm.classList.add("hidden");
            }
        });

        // Close request modal if clicking outside modal-card
        const modalCard = requestModal.querySelector(".modal-card");
        if (
            !requestModal.classList.contains("hidden") &&
            !modalCard.contains(e.target) &&
            !requestBtn.contains(e.target)
        ) {
            requestModal.classList.add("hidden");
        }
    });
}
