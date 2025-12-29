<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="confirm.title"/> - ArtCake AS</title>
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
                    <spring:message code="menu.language"/>: <a href="?lang=no" class="${currentLang == 'no' ? 'active' : ''}">NO</a> |
                    <a href="?lang=en" class="${currentLang == 'en' ? 'active' : ''}">EN</a>
                </div>
            </nav>
            <div class="menu-backdrop"></div>
        </div>
    </div>
</div>

<main class="confirmation-section">
    <div class="confirmation-box">
        <h1><spring:message code="confirm.title"/></h1>
        <p><spring:message code="confirm.subtitle"/></p>

        <div class="confirmation-highlight">
            <p><spring:message code="confirm.text1"/></p>
            <p><strong><spring:message code="confirm.text2"/></strong></p>
        </div>

        <p style="color: #999; font-size: 0.95rem;">
            <spring:message code="confirm.text3"/>
        </p>

        <a href="/" class="btn-home"><spring:message code="confirm.back"/></a>
    </div>
</main>

<footer>
    <div class="footer-content">
        <a href="/terms"><spring:message code="footer.terms"/></a>
    </div>
</footer>

<script>
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
