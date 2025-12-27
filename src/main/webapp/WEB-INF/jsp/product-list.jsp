<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="menu.products"/> - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
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
                <%-- <a href="/reviews"><spring:message code="menu.reviews"/></a> --%>
                <div class="lang-switch">
                    <spring:message code="menu.language"/>: <a href="?lang=no" class="${pageContext.request.locale.language == 'no' ? 'active' : ''}">NO</a> |
                    <a href="?lang=en" class="${pageContext.request.locale.language == 'en' ? 'active' : ''}">EN</a>
                </div>
            </nav>
            <div class="menu-backdrop"></div>
        </div>
    </div>
</div>

<main class="products-section">
    <h1><spring:message code="menu.products"/></h1>

    <div class="products-grid">
        <c:forEach var="cake" items="${cakes}">
            <div class="product-card">
                <div class="product-image">
                    <img src="<c:url value='${cake.imageUrl}'/>" alt="${cake.name}">
                </div>
                <div class="product-info">
                    <h2>${cake.name}</h2>
                    <p class="product-description">${cake.description}</p>
                    <p class="product-price"><spring:message code="cart.price"/>: ${cake.minPrice} kr</p>
                    <button class="btn-details" onclick="openModal(${cake.id})"><spring:message code="btn.details"/></button>
                </div>
            </div>
        </c:forEach>
    </div>
</main>

<footer>
    <div class="footer-content">
        <a href="/terms"><spring:message code="footer.terms"/></a>
    </div>
</footer>

<!-- Modal Structure -->
<div id="productModal" class="modal-overlay">
    <div class="modal-content">
        <span class="modal-close" onclick="closeModal()">&times;</span>
        <div id="modalBody">
            <!-- Content loaded via AJAX -->
        </div>
    </div>
</div>

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
    const modal = document.getElementById("productModal");

    function openModal(cakeId) {
        fetch('/products/' + cakeId + '/details')
            .then(response => response.text())
            .then(html => {
                document.getElementById('modalBody').innerHTML = html;
                modal.style.display = "block";
                document.body.style.overflow = "hidden"; // Prevent scrolling
            });
    }

    function closeModal() {
        modal.style.display = "none";
        document.body.style.overflow = "auto"; // Enable scrolling
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            closeModal();
        }
    }

    function addToCart() {
        const selectedSize = document.querySelector('input[name="selectedSize"]:checked');
        if (!selectedSize) {
            alert('Vennligst velg en størrelse');
            return;
        }

        const cakeId = document.getElementById('detailCakeId').value;
        const cakeName = document.getElementById('detailCakeName').value;
        const cakeSizeId = selectedSize.value;
        const price = selectedSize.getAttribute('data-price');
        const sizeCm = selectedSize.nextElementSibling.textContent.split(' ')[0];

        fetch('/cart/add-standard', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'cakeId=' + cakeId + '&cakeName=' + encodeURIComponent(cakeName) + '&cakeSizeId=' + cakeSizeId + '&sizeCm=' + sizeCm + '&price=' + price
        }).then(() => {
            alert('Lagt til handlekurv!');
            window.location.href = '/cart';
        });
    }
</script>
</body>
</html>