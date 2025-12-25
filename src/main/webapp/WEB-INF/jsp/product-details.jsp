<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${cake.name} - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
        <img src="/imgs/logo-no-bg.png" alt="ArtCake AS">
    </a>
    <div class="topmenu-right">
        <a href="/cart" class="cart-link" title="Handlekurv">
            <span class="cart-icon">🛒</span>
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
                <a href="/about">Om oss / Kontakt</a>
            </nav>
        </div>
    </div>
</div>

<div class="modal-overlay" id="detailsModal">
    <div class="modal-content">
        <button class="modal-close" onclick="window.history.back()">&times;</button>
        <div class="modal-body">
            <div class="modal-images">
                <img src="${cake.imageUrl}" alt="${cake.name}" class="modal-image">
                <img src="${cake.imageUrl2}" alt="${cake.name}" class="modal-image">
            </div>

            <div class="modal-info-section">
                <h2>${cake.name}</h2>
                <p class="modal-description">${cake.description}</p>
                <p class="modal-price">Fra ${cake.minPrice} kr</p>

                <div class="sizes-section">
                    <h3>Velg størrelse:</h3>
                    <form id="sizeForm">
                        <div class="size-options">
                            <c:forEach var="size" items="${cake.sizes}">
                                <label class="size-option">
                                    <input type="radio" name="selectedSize" value="${size.id}"
                                           data-price="${size.price}" required>
                                    <span>${size.sizeCm} cm - ${size.price} kr</span>
                                </label>
                            </c:forEach>
                        </div>
                    </form>
                </div>

                <div class="allergens">
                    <h3>Allergener:</h3>
                    <ul>
                        <c:forEach var="allergen" items="${cake.allergens}">
                            <li>${allergen.name}</li>
                        </c:forEach>
                    </ul>
                </div>

                <button class="btn-order" onclick="addToCart()">Legg i handlekurv</button>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById("detailsModal").classList.add("show");

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            window.history.back();
        }
    });

    function addToCart() {
        const selectedSize = document.querySelector('input[name="selectedSize"]:checked');
        if (!selectedSize) {
            alert('Vennligst velg en størrelse');
            return;
        }

        const cakeSizeId = selectedSize.value;
        const price = selectedSize.getAttribute('data-price');
        const sizeCm = selectedSize.nextElementSibling.textContent.split(' ')[0];

        fetch('/cart/add-standard', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'cakeId=${cake.id}&cakeName=${cake.name}&cakeSizeId=' + cakeSizeId + '&sizeCm=' + sizeCm + '&price=' + price
        }).then(() => {
            alert('Lagt til handlekurv!');
            window.location.href = '/cart';
        });
    }
</script>
</body>
</html>