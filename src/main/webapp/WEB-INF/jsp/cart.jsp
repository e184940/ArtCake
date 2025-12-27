<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="cart.title"/> - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
        <img src="<c:url value='/images/logo_hvit_nobg.png'/>" alt="ArtCake AS">
    </a>
    <div class="topmenu-right">
        <a href="/cart" class="cart-link" title="<spring:message code='menu.cart'/>">
            <img src="<c:url value='/images/handlekurv.png'/>" alt="<spring:message code='menu.cart'/>"
                 class="cart-icon-img">
        </a>
        <div class="hamburger-menu">
            <div class="hamburger">
                <span></span>
                <span></span>
                <span></span>
            </div>
            <nav class="menu-items">
                <a href="/products"><spring:message code="menu.products"/></a>
                <a href="/custom-cakes"><spring:message code="menu.custom"/></a>
                <a href="/contact"><spring:message code="menu.contact"/></a>
                <a href="/faq"><spring:message code="menu.faq"/></a>
                <%-- <a href="/reviews"><spring:message code="menu.reviews"/></a> --%>
                <div class="lang-switch">
                    <spring:message code="menu.language"/>: <a href="?lang=no"
                                                               class="${pageContext.request.locale.language == 'no' ? 'active' : ''}">NO</a>
                    |
                    <a href="?lang=en" class="${pageContext.request.locale.language == 'en' ? 'active' : ''}">EN</a>
                </div>
            </nav>
            <div class="menu-backdrop"></div>
        </div>
    </div>
</div>

<main class="cart-section">
    <h1><spring:message code="cart.title"/></h1>

    <c:if test="${empty cartItems}">
        <div class="cart-empty">
            <p><spring:message code="cart.empty"/></p>
            <a href="/products" class="btn-continue"><spring:message code="btn.continue"/></a>
        </div>
    </c:if>

    <c:if test="${not empty cartItems}">
        <div class="cart-table-wrapper">
            <table class="cart-table">
                <thead>
                <tr>
                    <th><spring:message code="cart.product"/></th>
                    <th><spring:message code="cart.details"/></th>
                    <th><spring:message code="cart.price"/></th>
                    <th><spring:message code="cart.qty"/></th>
                    <th><spring:message code="cart.total"/></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cartItems}">
                    <tr class="${item.itemType == 'custom' ? 'custom-item' : ''}">
                        <td data-label="<spring:message code='cart.product'/>">
                            <span class="cart-item-name">${item.cakeName}</span>
                            <c:if test="${item.itemType == 'custom'}">
                                <br><small style="color: #999;"><spring:message code="menu.custom"/></small>
                            </c:if>
                        </td>
                        <td data-label="<spring:message code='cart.details'/>">
                            <c:if test="${item.itemType == 'standard'}">
                                ${item.sizeCm} cm
                            </c:if>
                            <c:if test="${item.itemType == 'custom'}">
                                <div class="description-preview" title="${item.customDescription}">
                                        ${item.customDescription}
                                </div>
                            </c:if>
                        </td>
                        <td data-label="<spring:message code='cart.price'/>">${item.price} kr</td>
                        <td data-label="<spring:message code='cart.qty'/>">
                            <div class="qty-controls">
                                <button class="qty-btn" onclick="updateQuantity(${item.id}, ${item.quantity - 1})">-</button>
                                <input type="text" class="qty-input" value="${item.quantity}" readonly>
                                <button class="qty-btn" onclick="updateQuantity(${item.id}, ${item.quantity + 1})">+</button>
                            </div>
                        </td>
                        <td data-label="<spring:message code='cart.total'/>"><strong>${item.price.multiply(item.quantity)} kr</strong></td>
                        <td class="action-cell">
                            <button class="remove-btn" onclick="removeItem(${item.id})"><spring:message
                                    code="cart.remove"/></button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="cart-summary">
            <div class="cart-total">
                <spring:message code="cart.total"/>: ${cartTotal} kr
            </div>
        </div>

        <div class="cart-actions">
            <a href="/products" class="btn-continue"><spring:message code="btn.continue"/></a>
        </div>

        <div class="checkout-form">
            <h3>Fullfør bestilling</h3>
            <form method="POST" action="/cart/checkout">
                <div class="form-row">
                    <div class="form-group">
                        <label for="customerName">Navn *</label>
                        <input type="text" id="customerName" name="customerName" required>
                    </div>
                    <div class="form-group">
                        <label for="customerEmail">Epost *</label>
                        <input type="email" id="customerEmail" name="customerEmail" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="customerPhone">Telefon *</label>
                        <input type="tel" id="customerPhone" name="customerPhone" required>
                    </div>
                    <div class="form-group">
                        <label for="deliveryDate">Ønsket leveringsdato *</label>
                        <input type="date" id="deliveryDate" name="deliveryDate" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="notes">Ekstra ønsker / Notater</label>
                    <textarea id="notes" name="notes" placeholder="F.eks. spesielle ønsker, allergier, etc."></textarea>
                </div>

                <button type="submit" class="btn-checkout">Bekreft og send bestilling</button>
            </form>
        </div>
    </c:if>
</main>

<footer>
    <div class="footer-content">
        <a href="/terms">Vilkår og betingelser for bestilling</a>
    </div>
</footer>

<script>
    function removeItem(itemId) {
        fetch('/cart/remove', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'itemId=' + itemId
        }).then(() => {
            window.location.reload();
        });
    }

    function updateQuantity(itemId, newQuantity) {
        if (newQuantity < 1) {
            if (confirm('Vil du fjerne varen fra handlekurven?')) {
                removeItem(itemId);
            }
            return;
        }

        fetch('/cart/update-quantity', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'itemId=' + itemId + '&quantity=' + newQuantity
        }).then(() => {
            window.location.reload();
        });
    }

    // Set minimum delivery date based on cart contents
    const deliveryDateInput = document.getElementById('deliveryDate');
    if (deliveryDateInput) {
        const today = new Date();
        const minDate = new Date(today);

        // Check if there are any custom cakes in the cart
        let hasCustomCake = false;
        <c:forEach items="${cartItems}" var="item">
        <c:if test="${item.itemType == 'custom'}">
        hasCustomCake = true;
        </c:if>
        </c:forEach>

        // Set min date: 7 days for custom cakes, 3 days for standard
        if (hasCustomCake) {
            minDate.setDate(minDate.getDate() + 7);
        } else {
            minDate.setDate(minDate.getDate() + 3);
        }

        const dateString = minDate.toISOString().split('T')[0];
        deliveryDateInput.setAttribute('min', dateString);
    }

    const hamburger = document.querySelector(".hamburger");
    const menuItems = document.querySelector(".menu-items");
    const backdrop = document.querySelector(".menu-backdrop");

    function toggleMenu() {
        hamburger.classList.toggle("active");
        menuItems.classList.toggle("active");
        if (backdrop) backdrop.classList.toggle("active");
    }

    if (hamburger) {
        hamburger.addEventListener("click", toggleMenu);
    }

    if (backdrop) {
        backdrop.addEventListener("click", toggleMenu);
    }
</script>
</body>
</html>
