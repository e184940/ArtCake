<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Handlekurv - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="<c:url value='/'/>" class="logo-link">
        <img src="<c:url value='/images/logo_hvit_nobg.png'/>" alt="ArtCake AS">
    </a>
    <div class="topmenu-right">
        <a href="/cart" class="cart-link" title="Handlekurv">
            <span class="cart-icon">[CART]</span>
        </a>
        <div class="hamburger-menu">
            <div class="hamburger">
                <span></span>
                <span></span>
                <span></span>
            </div>
            <nav class="menu-items">
                <a href="/products">Vårt faste utvalg</a>
                <a href="/custom-cakes">Personlige kaker</a>
                <a href="/contact">Kontakt</a>
            </nav>
        </div>
    </div>
</div>

<main class="cart-section">
    <h1>Handlekurv</h1>

    <c:if test="${empty cartItems}">
        <div class="cart-empty">
            <p>Din handlekurv er tom</p>
            <a href="/products" class="btn-continue">Fortsett shopping</a>
        </div>
    </c:if>

    <c:if test="${not empty cartItems}">
        <table class="cart-table">
            <thead>
                <tr>
                    <th>Produkt</th>
                    <th>Størrelse / Detaljer</th>
                    <th>Pris</th>
                    <th>Ant.</th>
                    <th>Total</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${cartItems}">
                    <tr class="${item.itemType == 'custom' ? 'custom-item' : ''}">
                        <td>
                            <span class="cart-item-name">${item.cakeName}</span>
                            <c:if test="${item.itemType == 'custom'}">
                                <br><small style="color: #999;">Personlig kake</small>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${item.itemType == 'standard'}">
                                ${item.sizeCm} cm
                            </c:if>
                            <c:if test="${item.itemType == 'custom'}">
                                <div class="description-preview" title="${item.customDescription}">
                                    ${item.customDescription}
                                </div>
                            </c:if>
                        </td>
                        <td>${item.price} kr</td>
                        <td>${item.quantity}</td>
                        <td><strong>${item.price.multiply(item.quantity)} kr</strong></td>
                        <td>
                            <button class="remove-btn" onclick="removeItem(${item.id})">Fjern</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="cart-summary">
            <div class="cart-total">
                Total: ${cartTotal} kr
            </div>
        </div>

        <div class="cart-actions">
            <a href="/products" class="btn-continue">Fortsett shopping</a>
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

    // Set minimum delivery date to 3 days from today
    const deliveryDateInput = document.getElementById('deliveryDate');
    if (deliveryDateInput) {
        const today = new Date();
        const minDate = new Date(today);
        minDate.setDate(minDate.getDate() + 3);

        const dateString = minDate.toISOString().split('T')[0];
        deliveryDateInput.setAttribute('min', dateString);
    }

    const hamburger = document.querySelector(".hamburger");
    if (hamburger) {
        hamburger.addEventListener("click", function(){
            this.classList.toggle("active");
            document.querySelector(".menu-items").classList.toggle("active");
        });
    }
</script>
</body>
</html>
