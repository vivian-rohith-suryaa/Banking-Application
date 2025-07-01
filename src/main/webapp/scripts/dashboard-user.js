export function initDashboard(contextPath, userId, branchId, role) {
    console.log("Initializing user dashboard with", { contextPath, userId, branchId, role });

    const accountSidebar = document.querySelector(".account-sidebar");
    const accountDetailsContainer = document.getElementById("account-details");
    const transactionList = document.getElementById("transaction-list");
    const paymentModeChartCanvas = document.getElementById("paymentModeChart");

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

            // Initially show first account
            renderAccountDetails(accounts[0]);
        })
        .catch(err => {
            accountSidebar.innerHTML = "<p style='padding: 8px;'>Failed to load accounts.</p>";
            console.error(err);
        });

    function renderAccountDetails(account) {
        accountDetailsContainer.innerHTML = `
            <div class="balance" style="color: green;font-size: 25px; font-weight: 600">Balance: ₹${account.balance.toFixed(2)}</div>
            <div><span class="label">Account No:</span> <span class="value">${account.accountId}</span></div>
            <div><span class="label">Branch Id:</span> <span class="value">${account.branchId}</span></div>
            <div><span class="label">Type:</span> <span class="value">${account.accountType}</span></div>
            <div><span class="label">Status:</span> <span class="value">${account.status}</span></div>
        `;

        // Load transactions and chart for selected account
        loadAccountTransactions(account.accountId);
    }

    function loadAccountTransactions(accountId) {

        fetch(`${contextPath}/viiva/account/${accountId}/transactions?limit=-1`, { method: "GET", headers })
            .then(res => res.json())
            .then(data => {
                const allTransactions = data.data?.transactions || [];
                const accountTransactions = allTransactions.filter(tx => tx.accountId === accountId);
                const recentTransactions = accountTransactions.slice(0, 5);

                transactionList.innerHTML = "";

                if (recentTransactions.length === 0) {
                    transactionList.innerHTML = "<p>No recent transactions for this account.</p>";
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

                // Prepare chart data (sum amount by paymentMode)
                const paymentSummary = {};
                accountTransactions.forEach(tx => {
                    const mode = tx.paymentMode;
                    const amt = tx.amount;
                    paymentSummary[mode] = (paymentSummary[mode] || 0) + amt;
                });

                renderPaymentModeChart(paymentSummary);
            })
            .catch(err => {
                transactionList.innerHTML = "<p>Failed to load transactions.</p>";
                console.error(err);
            });
    }

    function renderPaymentModeChart(summaryMap) {
        const ctx = paymentModeChartCanvas.getContext('2d');
        if (window.paymentChartInstance) {
            window.paymentChartInstance.destroy();
        }

        const modes = Object.keys(summaryMap);
        const amounts = Object.values(summaryMap);
        const colors = ["#3F51B5", "#009688", "#E91E63", "#FF9800", "#9C27B0", "#4CAF50"];

        window.paymentChartInstance = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: modes,
                datasets: [{
                    data: amounts,
                    backgroundColor: colors.slice(0, modes.length)
                }]
            },
            options: {
                responsive: false,
                plugins: {
                    legend: { display: false }
                }
            }
        });

        // Generate custom legend
        const legendContainer = document.getElementById("chart-legend");
        legendContainer.innerHTML = "";
        modes.forEach((mode, i) => {
            const row = document.createElement("div");
            row.className = "chart-legend-item";
            row.innerHTML = `
                <div class="chart-legend-color" style="background-color: ${colors[i % colors.length]}"></div>
                <div>${mode}: ₹${summaryMap[mode].toFixed(2)}</div>
            `;
            legendContainer.appendChild(row);
        });
    }

    // Navigation handlers
    document.getElementById("Go to Accounts")?.addEventListener("click", () => {
        window.location.href = `${window.contextPath}/pages/home.jsp?view=account.jsp`;
    });

    document.getElementById("view-transactions-btn")?.addEventListener("click", () => {
        window.location.href = `${window.contextPath}/pages/home.jsp?view=statement.jsp`;
    });
}
