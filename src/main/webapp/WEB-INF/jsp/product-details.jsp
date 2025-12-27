<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
    <a href="<c:url value='/'/>" class="logo-link">
        <img src="<c:url value='/images/logo_hvit_nobg.png'/>" alt="ArtCake AS">
    </a>
    <div class="topmenu-right">
        <a href="/cart" class="cart-link" title="<spring:message code='menu.cart'/>">
            <img src="<c:url value='/images/handlekurv.png'/>" alt="<spring:message code='menu.cart'/>" class="cart-icon-img">
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
                <a href="/reviews"><spring:message code="menu.reviews"/></a>
                <div class="lang-switch">
                    <spring:message code="menu.language"/>: <a href="?lang=no" class="${pageContext.request.locale.language == 'no' ? 'active' : ''}">NO</a> |
                    <a href="?lang=en" class="${pageContext.request.locale.language == 'en' ? 'active' : ''}">EN</a>
                </div>
            </nav>
            <div class="menu-backdrop"></div>
        </div>
    </div>
</div>

<div class="modal-overlay" id="detailsModal">
    <div class="modal-content">
        <button class="modal-close" onclick="window.history.back()">&times;</button>
        <div class="modal-body">
            <div class="modal-images">
                <img src="<c:url value='${cake.imageUrl}'/>" alt="${cake.name}" class="modal-image">
                <img src="<c:url value='${cake.imageUrl2}'/>" alt="${cake.name}" class="modal-image">
            </div>

            <div class="modal-info-section">
                <h2>${cake.name}</h2>
                <p class="modal-description">${cake.description}</p>
                <p class="modal-price"><spring:message code="cart.price"/>: ${cake.minPrice} kr</p>

                <div class="sizes-section">
                    <h3><spring:message code="cart.details"/>:</h3>
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
                    <h3><spring:message code="terms.6.title"/>:</h3>
                    <ul>
                        <c:forEach var="allergen" items="${cake.allergens}">
                            <li>${allergen.name}</li>
                        </c:forEach>
                    </ul>
                </div>

                <button class="btn-order" onclick="addToCart()"><spring:message code="btn.add_to_cart"/></button>
            </div>
        </div>
    </div>
</div>

<footer>
    <div class="footer-content">
        <a href="/terms"><spring:message code="footer.terms"/></a>
    </div>
</footer>

<script>
    // Hamburger Menu Logic
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

    // Modal Logic
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