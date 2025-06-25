document.addEventListener("DOMContentLoaded", () => {
    const profileIcon = document.getElementById("profile-icon");
    const popup = document.getElementById("profile-popup");
    const logoutBtn = document.getElementById("logout-btn");
    const editBtn = document.getElementById("edit-profile");
    const accountBtn = document.getElementById("goto-accounts");
    const paymentBtn = document.getElementById("goto-payments");

    const contextPath = window.contextPath || "";
    const sessionUserId = window.sessionUserId || -1;

    profileIcon?.addEventListener("click", async () => {
        if (popup.style.display === "none" || popup.style.display === "") {
            try {
                const res = await fetch(`${contextPath}/viiva/user/${sessionUserId}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    }
                });

                if (!res.ok) throw new Error(`HTTP error ${res.status}`);

                const result = await res.json();
                const user = result?.data;

                if (!user) throw new Error("Invalid response");

                document.getElementById("popup-name").textContent = user.name || "User";
                document.getElementById("popup-id").textContent = `ID: ${user.userId || ""}`;
                document.getElementById("popup-email").textContent = user.email || "";

                popup.style.display = "block";
            } catch (err) {
                console.error("Profile fetch error:", err);
                alert("Unable to load profile. Please try again.");
            }
        } else {
            popup.style.display = "none";
        }
    });

    logoutBtn?.addEventListener("click", () => {
        fetch(`${contextPath}/viiva/auth/logout`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        })
        .then(res => {
            if (res.ok) {
                window.location.href = `${contextPath}/pages/signin.jsp`;
            } else {
                return res.json().then(data => {
                    alert("Logout failed: " + (data.message || "Unknown error"));
                });
            }
        })
        .catch(err => {
            console.error("Logout failed:", err);
            alert("Logout error. Please try again.");
        });
    });

    editBtn?.addEventListener("click", () => {
        window.location.href = `${contextPath}/pages/home.jsp?view=profile.jsp`;
        popup.style.display = "none";
    });

    accountBtn?.addEventListener("click", () => {
        window.location.href = `${contextPath}/pages/home.jsp?view=account.jsp`;
        popup.style.display = "none";
    });

    paymentBtn?.addEventListener("click", () => {
        window.location.href = `${contextPath}/pages/home.jsp?view=payment.jsp`;
        popup.style.display = "none";
    });

    document.addEventListener("click", (e) => {
        if (!popup.contains(e.target) && e.target !== profileIcon) {
            popup.style.display = "none";
        }
    });
});
