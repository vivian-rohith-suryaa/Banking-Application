<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/payment.css">

<div class="payment-wrapper">
  <div class="payment-container">
    <h2>Make a Payment</h2>
    <form id="payment-form" class="glass">
      <label>
        Account ID:
        <select name="accountId" id="account-select" required>
          <option value="">-- Select Account --</option>
        </select>
      </label>

      <label>
        Payment Mode:
        <select name="paymentMode" id="payment-mode" required>
          <option value="">-- Select Mode --</option>
          <option value="DEPOSIT">Deposit</option>
          <option value="WITHDRAWAL">Withdrawal</option>
          <option value="SELF_TRANSFER">Self Transfer</option>
          <option value="BANK_TRANSFER">Bank Transfer</option>
        </select>
      </label>

      <div id="to-account-group" class="conditional hidden">
        <label>
          To Account ID:
          <input type="text" name="transactedAccount" id="to-account" maxlength="12"
                 oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                 onkeypress="if(['-','e','E','0'].includes(event.key)) event.preventDefault();" />
        </label>
      </div>

      <label>
        Amount:
        <input type="text" name="amount" maxlength="7" required placeholder="Maximum Input Limit - 7 digits"
               oninput="this.value = this.value.replace(/[^0-9.]/g, '').slice(0, 7)"
               onkeypress="if(['-','e','E'].includes(event.key)) event.preventDefault();" />
      </label>

      <button type="submit">Continue</button>
    </form>

    <div id="payment-result" class="glass message-area"></div>

    <!-- PIN Modal (inside payment-container) -->
    <div id="pin-modal" class="modal-overlay hidden">
      <div class="modal-card">
        <h3>Enter PIN to Confirm</h3>
        <form id="pin-confirm-form">
          <input type="password" name="pin" required maxlength="6"
                 placeholder="Enter 6-digit PIN"
                 pattern="\d{6}"
                 oninput="this.value = this.value.replace(/[^0-9]/g, '')" />
          <div class="modal-actions">
            <button type="submit">Confirm</button>
            <button type="button" id="close-pin-modal">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<script type="module">
  const contextPath = "<%= request.getContextPath() %>";
  const userId = <%= userId %>;

  import(`${contextPath}/scripts/payment.js`)
    .then(module => module.initPaymentForm(contextPath, userId))
    .catch(err => console.error("Failed to load payment module:", err));
</script>
