<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
HttpSession sess = request.getSession(false);
long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/payment.css">

<div class="payment-wrapper">
  <div class="payment-container">
    <h2>Payment</h2>
    <form id="payment-form" class="glass" novalidate>
      <label> Account ID:
        <% if (role < 2) { %>
        <select name="accountId" id="account-select" required>
          <option value="">-- Select Account --</option>
        </select>
        <% } else { %>
        <input type="text" name="accountId" id="account-id-input" maxlength="12" required
               placeholder="Enter Account ID"
               oninput="this.value = this.value.replace(/[^0-9]/g, '')"
               onkeypress="if(['-','e','E','0'].includes(event.key)) event.preventDefault();" />
        <% } %>
      </label>

      <label> Payment Mode:
        <select name="paymentMode" id="payment-mode" required>
          <option value="">-- Select Mode --</option>
          <% if (role >= 2) { %>
          <option value="DEPOSIT">Deposit</option>
          <option value="WITHDRAWAL">Withdrawal</option>
          <% } %>
          <option value="SELF_TRANSFER">Self Transfer</option>
          <option value="BANK_TRANSFER">Bank Transfer</option>
        </select>
      </label>

      <div id="bank-transfer-options" class="hidden">
        <label for="isExternal">External Transfer:</label>
        <div id="transfer-type">
          <label><input type="radio" name="isExternal" value="true"> Yes</label>
          <label><input type="radio" name="isExternal" value="false" checked> No</label>
        </div>
      </div>

      <div id="to-account-group" class="hidden">
        <label id="to-account-label"> To Account ID:
          <select name="transactedAccount" id="to-account-select" class="hidden">
            <option value="">-- Select Account --</option>
          </select>
          <input type="text" name="transactedAccount" id="to-account-input" maxlength="12"
                 class="hidden" placeholder="Enter Target Account ID"
                 oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                 onkeypress="if(['-','e','E'].includes(event.key)) event.preventDefault();" />
        </label>
      </div>

      <div id="ifsc-group" class="conditional hidden">
        <label> IFSC Code:
          <input type="text" name="externalIfscCode" id="ifsc-code" maxlength="11"
                 placeholder="Enter Target IFSC"
                 oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')" />
        </label>
      </div>

      <label> Amount:
        <input type="text" name="amount" maxlength="6" required
               placeholder="Maximum Amount Limit - 1,00,000"
               oninput="this.value = this.value.replace(/[^0-9]/g, '').slice(0, 6)"
               onkeypress="if(['-','e','E','.'].includes(event.key)) event.preventDefault();" />
      </label>

      <button type="submit">Continue</button>
    </form>

    <div id="payment-result" class="glass message-area"></div>

    <div id="pin-modal" class="modal-overlay hidden">
      <div class="modal-card">
        <h3 id="auth-label"><%= (role < 2 ? "Enter PIN to Confirm" : "Enter Password to Confirm") %></h3>
        <form id="pin-confirm-form">
          <% if (role < 2) { %>
          <input type="password" name="pin" id="auth-input" maxlength="6"
                 placeholder="Enter 6-digit PIN"
                 oninput="this.value = this.value.replace(/[^0-9]/g, '')" />
          <% } else { %>
          <input type="password" name="password" id="auth-input" maxlength="20"
                 pattern="^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).{8,20}$"
                 oninput="this.value = this.value.replace(/[^A-Za-z\d@$!%*?&#]/g, '')"
                 onpaste="return false" oncopy="return false"
                 title="8–20 characters, at least 1 uppercase letter, 1 digit, and 1 special character"
                 placeholder="Enter your Password">
          <% } %>
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
  const contextPath = "<%=request.getContextPath()%>";
  const userId = <%=userId%>;
  const userRole = <%= String.valueOf(role) %>;

	console.log("Initializing payment module with:", {
    contextPath,
    userId,
    userRole: parseInt(userRole)
  });

  import(`${contextPath}/scripts/payment.js`)
    .then(module => module.initPaymentForm(contextPath, userId,  parseInt(userRole)))
    .catch(err => console.error("Failed to load payment module:", err));
</script>
