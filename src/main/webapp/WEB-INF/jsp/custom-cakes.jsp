<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="custom.title"/> - ArtCake AS</title>
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

<main class="custom-section">
    <h1><spring:message code="custom.title"/></h1>
    <p class="custom-intro">
        <spring:message code="custom.intro"/>
    </p>

    <!-- Center the order button -->
    <div class="custom-button-container" style="justify-content:center; margin-bottom: 1.5rem; flex-direction: column; align-items: center;">
        <a href="/order/custom" class="custom-btn-order"><spring:message code="btn.order"/></a>
        <p style="margin-top: 0.5rem; font-size: 0.9rem; color: #555;"><spring:message code="custom.note"/></p>
    </div>

    <!-- Use the same responsive grid structure as the product list; cards contain ONLY images (no text) -->
    <div class="products-grid">
        <!-- 12 example custom cakes (non-interactive) -->

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_0649.jpg" alt="Custom 1">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_1762.jpg" alt="Custom 2">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_2077.jpg" alt="Custom 3">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_2899.jpg" alt="Custom 4">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_3206.jpg" alt="Custom 5">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_3282.JPG" alt="Custom 6">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_3480.jpg" alt="Custom 7">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_3977.jpg" alt="Custom 8">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_5470.jpg" alt="Custom 9">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_6145.jpg" alt="Custom 10">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_7020.jpg" alt="Custom 11">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/images/customs/IMG_9162.jpg" alt="Custom 12">
            </div>
        </div>

    </div>

</main>

<script>
    const hamburger = document.querySelector(".hamburger");
    if (hamburger) {
        hamburger.addEventListener("click", function () {
            this.classList.toggle("active");
            document.querySelector(".menu-items").classList.toggle("active");
        });
    }
</script>
</body>
</html>
