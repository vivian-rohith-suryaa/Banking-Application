export function initPaymentForm(contextPath, userId) {
	const form = document.getElementById("payment-form");
	const resultBox = document.getElementById("payment-result");
	const paymentModeSelect = document.getElementById("payment-mode");
	const toAccountGroup = document.getElementById("to-account-group");
	const toAccountInput = toAccountGroup.querySelector("input");
	const accountSelect = document.getElementById("account-select");

	const pinModal = document.getElementById("pin-modal");
	const pinForm = document.getElementById("pin-confirm-form");
	const closePinModal = document.getElementById("close-pin-modal");

	let transactionPayload = null;

	// Fetch user's accounts
	fetch(`${contextPath}/viiva/user/${userId}/accounts`, {
		headers: { "Content-Type": "application/json" }
	})
	.then(async res => {
		const text = await res.text();
		try {
			const data = JSON.parse(text);
			const accounts = data.data.accounts || [];
			accounts.forEach(acc => {
				const opt = document.createElement("option");
				opt.value = acc.accountId;
				opt.textContent = acc.accountId;
				accountSelect.appendChild(opt);
			});
		} catch (err) {
			console.error("Failed to parse account JSON:", text);
			resultBox.innerHTML = `<p class="error-message">Unable to load accounts. Please try again.</p>`;
		}
	})
	.catch(err => {
		console.error("Failed to fetch accounts:", err);
		resultBox.innerHTML = `<p class="error-message">Error fetching accounts.</p>`;
	});

	// Show/hide "To Account ID" for transfer types
	paymentModeSelect.addEventListener("change", () => {
		const mode = paymentModeSelect.value;
		const show = (mode === "SELF_TRANSFER" || mode === "BANK_TRANSFER");
		toAccountGroup.classList.toggle("hidden", !show);
		if (show) {
			toAccountInput.setAttribute("required", "true");
		} else {
			toAccountInput.removeAttribute("required");
		}
	});

	// Submit form: store payload and show PIN modal
	form.addEventListener("submit", e => {
		e.preventDefault();
		resultBox.innerHTML = "";

		const data = new FormData(form);
		const accountId = parseInt(data.get("accountId"));
		const paymentMode = data.get("paymentMode");
		const transactedAccountRaw = data.get("transactedAccount");
		const transactedAccount = transactedAccountRaw ? parseInt(transactedAccountRaw) : null;
		const amount = parseFloat(data.get("amount"));

		const errors = [];

		if (!accountId || accountId <= 0) errors.push("Account ID must be selected.");
		if (!paymentMode) errors.push("Payment mode is required.");
		if (!amount || isNaN(amount) || amount <= 0 || amount > 9999999) {
			errors.push("Amount must be a number between 0 and 9,999,999.");
		}
		if ((paymentMode === "SELF_TRANSFER" || paymentMode === "BANK_TRANSFER") &&
			(!transactedAccount || transactedAccount <= 0)) {
			errors.push("To Account ID must be a valid number.");
		}

		if (errors.length > 0) {
			resultBox.innerHTML = `<ul class="error-message">${errors.map(e => `<li>${e}</li>`).join("")}</ul>`;
			return;
		}

		const transactionType = (paymentMode === "DEPOSIT" || paymentMode === "SELF_TRANSFER") ? "CREDIT" : "DEBIT";

		transactionPayload = {
			accountId,
			customerId: userId,
			transactedAccount: transactedAccount || null,
			transactionType,
			paymentMode,
			amount
		};

		pinModal.classList.remove("hidden");
	});

	// Submit PIN form
	pinForm.addEventListener("submit", e => {
		e.preventDefault();
		const pin = pinForm.elements["pin"].value.trim();

		if (!/^\d{6}$/.test(pin)) {
			alert("PIN must be a 6-digit number.");
			return;
		}

		const payload = {
			account: {
				accountId: transactionPayload.accountId,
				customerId: transactionPayload.customerId,
				pin
			},
			transaction: transactionPayload
		};

		console.log("Sending payload:", JSON.stringify(payload, null, 2));

		fetch(`${contextPath}/viiva/transactions`, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(payload)
		})
		.then(async res => {
			const text = await res.text();
			console.log("Raw response text:", text);

			let resp;
			try {
				resp = JSON.parse(text);
			} catch (err) {
				throw new Error("Server did not return valid JSON.");
			}

			if (!resp || resp.status !== "success" || !resp.data) {
				throw new Error(resp.message || "Unexpected response format");
			}

			const tx = resp.data;

			resultBox.innerHTML = `
				<div class="success-message tx-summary">
					<h4>Transaction Successful</h4>
					<p><strong>Transaction ID:</strong> ${tx.transactionId}</p>
					<p><strong>Amount:</strong> ₹${tx.amount}</p>
					<p><strong>Balance:</strong> ₹${tx.closingBalance}</p>
				</div>`;

			setTimeout(() => {
				const box = resultBox.querySelector(".success-message");
				if (box) box.classList.add("fade-out");
				setTimeout(() => resultBox.innerHTML = "", 500);
			}, 3500);

			form.reset();
			toAccountGroup.classList.add("hidden");
			pinModal.classList.add("hidden");
			pinForm.reset();
		})
		.catch(err => {
			console.error("Transaction failed:", err);
			resultBox.innerHTML = `<p class="error-message">Transaction failed: ${err.message || "Unknown error."}</p>`;
			pinModal.classList.add("hidden");
		});
	});

	// Close modal by button
	closePinModal.addEventListener("click", () => {
		pinModal.classList.add("hidden");
	});

	// Outside click hides modal
	document.addEventListener("click", (e) => {
		if (!pinModal.classList.contains("hidden") &&
			!pinModal.querySelector(".modal-card").contains(e.target) &&
			!form.contains(e.target)) {
			pinModal.classList.add("hidden");
		}
	});
}
