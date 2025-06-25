export function initStatementPage(contextPath, userId) {
    const accountSelect = document.getElementById("account-select");
    const tableBody = document.getElementById("statement-body");
    const prevBtn = document.getElementById("prev-page");
    const nextBtn = document.getElementById("next-page");
    const pageInfo = document.getElementById("page-info");

    let page = 1;
    const limit = 10;
    let selectedAccount = "all";
    let totalFetched = 0;

    // Fetch accounts and populate dropdown
    fetch(`${contextPath}/viiva/user/${userId}/accounts`, {
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(res => res.json())
    .then(data => {
        const accounts = data?.data?.accounts || [];
        accounts.forEach(acc => {
            const opt = document.createElement("option");
            opt.value = acc.accountId;
            opt.textContent = acc.accountId;
            accountSelect.appendChild(opt);
        });
    })
    .catch(err => console.error("Failed to load accounts:", err));

    // Event listener for account filter
    accountSelect.addEventListener("change", () => {
        selectedAccount = accountSelect.value;
        page = 1;
        fetchAndRender();
    });

    // Pagination controls
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

    function fetchAndRender() {
        tableBody.innerHTML = `<tr><td colspan="9">Loading...</td></tr>`;
        pageInfo.textContent = `Page ${page}`;

        const url =
            selectedAccount === "all"
                ? `${contextPath}/viiva/user/${userId}/transactions?limit=${limit}&page=${page}`
                : `${contextPath}/viiva/account/${selectedAccount}/transactions?limit=${limit}&page=${page}`;

        fetch(url, {
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(res => res.json())
        .then(data => {
            const txns = data?.data?.transactions || [];
            totalFetched = txns.length;

            if (txns.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="9">No transactions found.</td></tr>`;
                nextBtn.disabled = true;
                return;
            }

            renderRows(txns);
            prevBtn.disabled = page === 1;
            nextBtn.disabled = txns.length < limit;
        })
        .catch(err => {
            console.error("Failed to load transactions:", err);
            tableBody.innerHTML = `<tr><td colspan="9">Error loading transactions.</td></tr>`;
        });
    }

    function renderRows(transactions) {
        tableBody.innerHTML = "";
        transactions.forEach(tx => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${tx.transactionId}</td>
                <td>${tx.customerId}</td>
                <td>${tx.accountId}</td>
                <td>₹${tx.amount}</td>
                <td class="${tx.transactionType === "DEBIT" ? "debit" : "credit"}">${tx.transactionType}</td>
                <td>${tx.paymentMode}</td>
                <td>${tx.transactedAccount || "-"}</td>
                <td>₹${tx.closingBalance}</td>
                <td>${new Date(tx.transactionTime).toLocaleString()}</td>
            `;

            tableBody.appendChild(row);
        });
    }

    // Initial load
    fetchAndRender();
}
