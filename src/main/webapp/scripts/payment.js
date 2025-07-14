export function initPaymentForm(contextPath, userId, userRole) {
	
  console.log("Payment Form Init", { contextPath, userId, userRole });
  const form = document.getElementById("payment-form");
  const resultBox = document.getElementById("payment-result");

  const paymentModeSelect = document.getElementById("payment-mode");
  const toAccountInput = document.getElementById("to-account-input");
  const toAccountSelect = document.getElementById("to-account-select");
  const toAccountGroup = document.getElementById("to-account-group");
  const transferTypeRadios = document.getElementsByName("isExternal");
  const ifscGroup = document.getElementById("ifsc-group");
  const bankTransferOptions = document.getElementById("bank-transfer-options");
  const accountSelect = document.getElementById("account-select");

  const pinModal = document.getElementById("pin-modal");
  const pinForm = document.getElementById("pin-confirm-form");
  const closePinModal = document.getElementById("close-pin-modal");

  let transactionPayload = null;

  if (userRole < 2) {
    [...paymentModeSelect.options].forEach(opt => {
      if (["DEPOSIT", "WITHDRAWAL"].includes(opt.value)) {
        opt.remove();
      }
    });
  }

  fetch(`${contextPath}/viiva/user/${userId}/accounts`, {
    headers: { "Content-Type": "application/json" }
  })
    .then(res => res.json())
    .then(data => {
      const accounts = data?.data?.accounts || [];
      accounts.forEach(acc => {
        const opt1 = new Option(acc.accountId, acc.accountId);
        const opt2 = new Option(acc.accountId, acc.accountId);
        accountSelect?.appendChild(opt1);
        toAccountSelect?.appendChild(opt2);
      });
    });

  function toggleFields() {
    const mode = paymentModeSelect.value;
    const isBankTransfer = mode === "BANK_TRANSFER";
    const isSelfTransfer = mode === "SELF_TRANSFER";

    toAccountGroup.classList.toggle("hidden", !(isBankTransfer || isSelfTransfer));
    toAccountInput.classList.toggle("hidden", !isBankTransfer);
    toAccountSelect.classList.toggle("hidden", !isSelfTransfer);
    bankTransferOptions.classList.toggle("hidden", !isBankTransfer);

    const showIfsc = isBankTransfer && document.querySelector("input[name='isExternal']:checked")?.value === "true";
    ifscGroup.classList.toggle("hidden", !showIfsc);

    toAccountInput.required = isBankTransfer;
    toAccountSelect.required = isSelfTransfer;
  }

  paymentModeSelect.addEventListener("change", toggleFields);
  transferTypeRadios.forEach(r => r.addEventListener("change", toggleFields));
  toggleFields();

  form.addEventListener("submit", e => {
    e.preventDefault();
    resultBox.innerHTML = "";

    const data = new FormData(form);
    const accountId = parseInt(data.get("accountId"));
    const paymentMode = data.get("paymentMode");
    const isExternal = document.querySelector("input[name='isExternal']:checked")?.value === "true";
    const amount = parseFloat(data.get("amount"));
    const externalIfscCode = data.get("externalIfscCode")?.trim();

    let transactedAccountRaw = "";
    if (!toAccountInput.classList.contains("hidden")) {
      transactedAccountRaw = toAccountInput.value;
    } else if (!toAccountSelect.classList.contains("hidden")) {
      transactedAccountRaw = toAccountSelect.value;
    }

    const transactedAccount = transactedAccountRaw ? parseInt(transactedAccountRaw) : null;

    const errors = [];
    if (!accountId) errors.push("Account ID is required.");
    if (!paymentMode) errors.push("Select a payment mode.");
    if (!amount || amount < 1 || amount > 100000) errors.push("Amount must be ₹1–₹1,00,000.");

    if ((paymentMode === "SELF_TRANSFER" || paymentMode === "BANK_TRANSFER") && !transactedAccount) {
      errors.push("To Account is required.");
    }

    if (paymentMode === "BANK_TRANSFER" && isExternal && (!externalIfscCode || !/^[A-Z]{4}0[A-Z0-9]{6}$/.test(externalIfscCode))) {
      errors.push("Enter valid 11-character IFSC (format: XXXX0XXXXXX).");
    }

    if (errors.length > 0) {
      resultBox.innerHTML = `<ul class="error-message">${errors.map(e => `<li>${e}</li>`).join("")}</ul>`;
      return;
    }

    const transactionType = (paymentMode === "DEPOSIT" || paymentMode === "SELF_TRANSFER") ? "CREDIT" : "DEBIT";

    transactionPayload = {
      accountId,
      customerId: userId,
      transactedAccount: isExternal ? null : transactedAccount,
      transactionType,
      paymentMode,
      amount,
      isExternalTransfer: isExternal,
      externalAccountId: isExternal ? transactedAccountRaw : null,
      externalIfscCode: isExternal ? externalIfscCode : null
    };

    pinModal.classList.remove("hidden");
  });
  
  pinForm.addEventListener("submit", e => {
    e.preventDefault();

    const authInput = pinForm.querySelector("#auth-input");
    const pinOrPassword = authInput.value.trim();
    const isCustomer = userRole < 2;
	
	console.log(authInput, pinOrPassword, isCustomer, userRole);

    if (isCustomer) {
      if (!/^\d{6}$/.test(pinOrPassword)) {
        alert("PIN must be a 6-digit number.");
        return;
      }
    } else {
      if (!/^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).{8,20}$/.test(pinOrPassword)) {
        alert("Password must be 8–20 characters with 1 uppercase, 1 digit, and 1 special character.");
        return;
      }
    }

    const payload = {
      account: {
        accountId: transactionPayload.accountId,
        customerId: transactionPayload.customerId,
        pin: isCustomer ? pinOrPassword : null,
        
      },
      transaction: transactionPayload,
	  password: isCustomer ? null : pinOrPassword
    };

    fetch(`${contextPath}/viiva/transactions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    })
      .then(async res => {
        const text = await res.text();
        const resp = JSON.parse(text);

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
        resultBox.innerHTML = `<p class="error-message">Transaction failed: ${err.message || "Unknown error."}</p>`;
        pinModal.classList.add("hidden");
      });
  });


/*  pinForm.addEventListener("submit", e => {
    e.preventDefault();
    const pinOrPassword = document.getElementById("auth-input").value.trim();
    const isCustomer = userRole < 2;

    if (isCustomer && !/^\d{6}$/.test(pinOrPassword)) {
      return alert("PIN must be 6 digits.");
    }
    if (!isCustomer && !/^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).{8,20}$/.test(pinOrPassword)) {
      return alert("Password must be 8–20 chars with uppercase, digit, and special character.");
    }

    const payload = {
      account: {
        accountId: transactionPayload.accountId,
        customerId: transactionPayload.customerId,
        pin: isCustomer ? pinOrPassword : null,
        password: isCustomer ? null : pinOrPassword
      },
      transaction: transactionPayload
    };

    fetch(`${contextPath}/viiva/transactions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    })
      .then(res => res.json())
      .then(resp => {
        if (!resp || resp.status !== "success" || !resp.data) throw new Error(resp.message);
        const tx = resp.data;
        resultBox.innerHTML = `
          <div class="success-message tx-summary">
            <h4>Transaction Successful</h4>
            <p><strong>Transaction ID:</strong> ${tx.transactionId}</p>
            <p><strong>Amount:</strong> ₹${tx.amount}</p>
            <p><strong>Balance:</strong> ₹${tx.closingBalance}</p>
          </div>`;
        setTimeout(() => resultBox.innerHTML = "", 4000);
        form.reset(); pinForm.reset(); pinModal.classList.add("hidden"); toggleFields();
      })
      .catch(err => {
        resultBox.innerHTML = `<p class="error-message">Transaction failed: ${err.message}</p>`;
        pinModal.classList.add("hidden");
      });
  });*/

  closePinModal.addEventListener("click", () => pinModal.classList.add("hidden"));
  document.addEventListener("click", (e) => {
    if (!pinModal.classList.contains("hidden") && !pinModal.querySelector(".modal-card").contains(e.target)) {
      pinModal.classList.add("hidden");
    }
  });
}


/*export function initPaymentForm(contextPath, userId, userRole) {
  const form = document.getElementById("payment-form");
  const resultBox = document.getElementById("payment-result");

  const paymentModeSelect = document.getElementById("payment-mode");

  if (userRole < 2) {
    const restrictedModes = ["DEPOSIT", "WITHDRAWAL"];
    [...paymentModeSelect.options].forEach(opt => {
      if (restrictedModes.includes(opt.value)) {
        opt.remove();
      }
    });
  }

  const toAccountGroup = document.getElementById("to-account-group");
  const accountSelect = document.getElementById("account-select");
  const pinModal = document.getElementById("pin-modal");
  const pinForm = document.getElementById("pin-confirm-form");
  const closePinModal = document.getElementById("close-pin-modal");

  const toAccountSelect = document.getElementById("to-account-select");
  const toAccountInput = document.getElementById("to-account-input");
  const transferTypeRadios = document.getElementsByName("isExternal");
  const bankTransferOptions = document.getElementById("bank-transfer-options");
  const ifscGroup = document.getElementById("ifsc-group");

  let transactionPayload = null;

  fetch(`${contextPath}/viiva/user/${userId}/accounts`, {
    headers: { "Content-Type": "application/json" }
  })
    .then(res => res.json())
    .then(data => {
      const accounts = data?.data?.accounts || [];
      accounts.forEach(acc => {
        const opt1 = document.createElement("option");
        opt1.value = acc.accountId;
        opt1.textContent = acc.accountId;
        accountSelect.appendChild(opt1);

        const opt2 = document.createElement("option");
        opt2.value = acc.accountId;
        opt2.textContent = acc.accountId;
        toAccountSelect.appendChild(opt2);
      });
    })
    .catch(err => console.error("Failed to load accounts:", err));

  paymentModeSelect.addEventListener("change", () => {
    const mode = paymentModeSelect.value;

    toAccountGroup.classList.add("hidden");
    toAccountInput.classList.add("hidden");
    toAccountSelect.classList.add("hidden");
    bankTransferOptions.classList.add("hidden");
    ifscGroup.classList.add("hidden");

    if (mode === "SELF_TRANSFER") {
      toAccountGroup.classList.remove("hidden");
      toAccountSelect.classList.remove("hidden");
    } else if (mode === "BANK_TRANSFER") {
      toAccountGroup.classList.remove("hidden");
      toAccountInput.classList.remove("hidden");
      bankTransferOptions.classList.remove("hidden");

      const selectedType = document.querySelector("input[name='isExternal']:checked")?.value;
      if (selectedType === "true") {
        ifscGroup.classList.remove("hidden");
      }
    }
  });

  transferTypeRadios.forEach(radio => {
    radio.addEventListener("change", () => {
      if (radio.value === "true" && radio.checked) {
        ifscGroup.classList.remove("hidden");
      } else {
        ifscGroup.classList.add("hidden");
      }
    });
  });

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

  form.addEventListener("submit", e => {
    e.preventDefault();
    resultBox.innerHTML = "";

    const data = new FormData(form);
    const accountId = parseInt(data.get("accountId"));
    const paymentMode = data.get("paymentMode");

    let transactedAccountRaw = "";
    if (!toAccountInput.classList.contains("hidden")) {
      transactedAccountRaw = toAccountInput.value;
    } else if (!toAccountSelect.classList.contains("hidden")) {
      transactedAccountRaw = toAccountSelect.value;
    }

    const transactedAccount = transactedAccountRaw ? parseInt(transactedAccountRaw) : null;
    const isExternal = document.querySelector("input[name='isExternal']:checked")?.value === "true";
    const externalIfscCode = data.get("externalIfscCode")?.trim();
    const amount = parseFloat(data.get("amount"), 10);

    const errors = [];

    if (!accountId || accountId <= 0) errors.push("Account ID must be valid.");
    if (!paymentMode) errors.push("Payment mode is required.");
    if (!amount || isNaN(amount) || amount <= 0 || amount > 100000) {
      errors.push("Transaction Amount Limit - ₹1 to ₹1,00,000.");
    }

    if ((paymentMode === "SELF_TRANSFER" || paymentMode === "BANK_TRANSFER") &&
        (!transactedAccount || transactedAccount <= 0)) {
      errors.push("To Account ID must be a valid number.");
    }

    if (paymentMode === "BANK_TRANSFER" && isExternal) {
      if (!externalIfscCode || !/^[A-Z]{4}0[A-Z0-9]{6}$/.test(externalIfscCode)) {
        errors.push("Invalid IFSC code. Please enter a valid 11-character code in the format: XXXX0XXXXXX.");
      }
    }

    if (errors.length > 0) {
      resultBox.innerHTML = `<ul class="error-message">${errors.map(e => `<li>${e}</li>`).join("")}</ul>`;
      return;
    }

    const transactionType = (paymentMode === "DEPOSIT" || paymentMode === "SELF_TRANSFER") ? "CREDIT" : "DEBIT";

    transactionPayload = {
      accountId,
      customerId: userId,
      transactedAccount: isExternal ? null : (transactedAccount || null),
      transactionType,
      paymentMode,
      amount,
      isExternalTransfer: isExternal,
      externalAccountId: isExternal ? transactedAccountRaw : null,
      externalIfscCode: isExternal ? externalIfscCode : null
    };

    pinModal.classList.remove("hidden");
  });

  pinForm.addEventListener("submit", e => {
    e.preventDefault();

    const authInput = pinForm.querySelector("#auth-input");
    const pinOrPassword = authInput.value.trim();
    const isCustomer = userRole < 2;

    if (isCustomer) {
      if (!/^\d{6}$/.test(pinOrPassword)) {
        alert("PIN must be a 6-digit number.");
        return;
      }
    } else {
      if (!/^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).{8,20}$/.test(pinOrPassword)) {
        alert("Password must be 8–20 characters with 1 uppercase, 1 digit, and 1 special character.");
        return;
      }
    }

    const payload = {
      account: {
        accountId: transactionPayload.accountId,
        customerId: transactionPayload.customerId,
        pin: isCustomer ? pinOrPassword : null,
        password: isCustomer ? null : pinOrPassword
      },
      transaction: transactionPayload
    };

    fetch(`${contextPath}/viiva/transactions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    })
      .then(async res => {
        const text = await res.text();
        const resp = JSON.parse(text);

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
        resultBox.innerHTML = `<p class="error-message">Transaction failed: ${err.message || "Unknown error."}</p>`;
        pinModal.classList.add("hidden");
      });
  });


  closePinModal.addEventListener("click", () => {
    pinModal.classList.add("hidden");
  });

  document.addEventListener("click", (e) => {
    if (!pinModal.classList.contains("hidden") &&
        !pinModal.querySelector(".modal-card").contains(e.target) &&
        !form.contains(e.target)) {
      pinModal.classList.add("hidden");
    }
  });
}
*/