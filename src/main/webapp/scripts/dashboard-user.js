export function initDashboard(contextPath, userId, branchId, role) {
    console.log("Initializing user dashboard with", { contextPath, userId, branchId, role });

    const accountSidebar = document.querySelector(".account-sidebar");
    const accountDetailsContainer = document.getElementById("account-details");
    const transactionList = document.getElementById("transaction-list");
    const paymentModeChartCanvas = document.getElementById("paymentModeChart");

    let paymentModeMap = {};

    const headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    };

    // Load Accounts
    fetch(`${contextPath}/viiva/user/${userId}/accounts`, { method: "GET", headers })
        .then(res => res.json())
        .then(data => {
            const accounts = data.data?.accounts || [];
            if (accounts.length === 0) {
                accountSidebar.innerHTML = "<p style='padding: 8px;'>No accounts found.</p>";
                return;
            }

            accounts.forEach((account, idx) => {
                const tab = document.createElement("div");
                tab.classList.add("account-tab");
                if (idx === 0) tab.classList.add("active");
                tab.textContent = `Account ${idx + 1}`;
                tab.addEventListener("click", () => {
                    document.querySelectorAll(".account-tab").forEach(t => t.classList.remove("active"));
                    tab.classList.add("active");
                    renderAccountDetails(account);
                });
                accountSidebar.appendChild(tab);
            });

            renderAccountDetails(accounts[0]);
        })
        .catch(err => {
            accountSidebar.innerHTML = "<p style='padding: 8px;'>Failed to load accounts.</p>";
            console.error(err);
        });

    function renderAccountDetails(account) {
        accountDetailsContainer.innerHTML = `
            <div class="balance" style="color: green;">Balance: ₹${account.balance.toFixed(2)}</div>
            <div><span class="label">Account No:</span> <span class="value">${account.accountId}</span></div>
            <div><span class="label">Branch Id:</span> <span class="value">${account.branchId}</span></div>
            <div><span class="label">Type:</span> <span class="value">${account.accountType}</span></div>
            <div><span class="label">Status:</span> <span class="value">${account.status}</span></div>
        `;
    }

    // Load Transactions
    fetch(`${contextPath}/viiva/user/${userId}/transactions`, { method: "GET", headers })
        .then(res => res.json())
        .then(data => {
            const allTransactions = data.data?.transactions || [];

            const recentTransactions = allTransactions.slice(0, 5);
            if (recentTransactions.length === 0) {
                transactionList.innerHTML = "<p>No recent transactions.</p>";
            } else {
                recentTransactions.forEach(tx => {
                    const item = document.createElement("div");
                    item.classList.add("transaction-item");
                    const date = new Date(tx.transactionTime).toLocaleDateString();
                    const amountColor = tx.transactionType.toUpperCase() === 'CREDIT' ? 'green' : 'red';
                    item.innerHTML = `
                        <div><strong>${tx.transactionType}</strong> - ₹<span style="color:${amountColor}">${tx.amount}</span></div>
                        <div><span>Account ID:</span> ${tx.accountId}</div>
                        <div><span>To/From:</span> ${tx.transactedAccount || '-'}</div>
                        <div><span>Mode:</span> ${tx.paymentMode}</div>
                        <div><span>Date:</span> ${date}</div>
                    `;
                    transactionList.appendChild(item);
                });
            }

            // Build full chart data from all transactions
            allTransactions.forEach(tx => {
                paymentModeMap[tx.paymentMode] = (paymentModeMap[tx.paymentMode] || 0) + 1;
            });

            renderPaymentModeChart();
        })
        .catch(err => {
            transactionList.innerHTML = "<p>Failed to load transactions.</p>";
            console.error(err);
        });

    function renderPaymentModeChart() {
        new Chart(paymentModeChartCanvas, {
            type: 'pie',
            data: {
                labels: Object.keys(paymentModeMap),
                datasets: [{
                    data: Object.values(paymentModeMap),
                    backgroundColor: ["#3F51B5", "#009688", "#E91E63", "#FF9800"]
                }]
            }
        });
    }

	document.getElementById("Go to Accounts")?.addEventListener("click", () => {
	    window.location.href = `${window.contextPath}/pages/home.jsp?view=account.jsp`;
	});
	
	document.getElementById("view-transactions-btn")?.addEventListener("click", () => {
	    window.location.href = `${window.contextPath}/pages/home.jsp?view=statement.jsp`;
	});
}

