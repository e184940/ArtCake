<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="faq.title"/> - ArtCake AS</title>
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

<main class="faq-section" style="padding: 2rem; max-width: 800px; margin: 0 auto;">
    <h1 style="text-align: center; margin-bottom: 2rem; color: #333;"><spring:message code="faq.title"/></h1>

    <div class="faq-content" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;"><spring:message code="faq.q1"/></h3>
            <p style="color: #555; line-height: 1.6;"><spring:message code="faq.a1"/></p>
        </div>

        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;"><spring:message code="faq.q2"/></h3>
            <p style="color: #555; line-height: 1.6;"><spring:message code="faq.a2"/></p>
        </div>

        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;"><spring:message code="faq.q3"/></h3>
            <p style="color: #555; line-height: 1.6;"><spring:message code="faq.a3"/></p>
        </div>

        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;"><spring:message code="faq.q4"/></h3>
            <p style="color: #555; line-height: 1.6;"><spring:message code="faq.a4"/></p>
        </div>

        <div class="faq-item">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;"><spring:message code="faq.q5"/></h3>
            <p style="color: #555; line-height: 1.6;"><spring:message code="faq.a5"/></p>
        </div>
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
