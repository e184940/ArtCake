<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="contact.title"/> - ArtCake</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
        <img src="<c:url value='/images/logo_hvit_nobg.png'/>" alt="ArtCake Studio AS">
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

<main class="contact-section">
    <div class="contact-inner">
        <h1><spring:message code="contact.title"/></h1>

        <div class="contact-grid">
            <div>
                <h3><spring:message code="contact.address"/></h3>
                <p>Art Cake Studio AS<br>Matti Aikios vei 8<br>8019, Bodø</p>
                <br>

                <h3><spring:message code="contact.contact"/></h3>
                <p><spring:message code="contact.email"/>: <a href="mailto:artcake@artcake.no">artcake@artcake.no</a><br>
                    <spring:message code="cart.phone"/>: <a href="tel:+4791794812">+47 917 94 812</a></p>
                <br>

                <h3><spring:message code="contact.follow"/></h3>
                <p>
                    <a href="https://www.facebook.com/profile.php?id=100086608387816" target="_blank">Facebook</a><br>
                    <a href="https://www.instagram.com/art_cake_bodo/" target="_blank">Instagram</a>
                </p>
            </div>

            <div>
                <h3><spring:message code="contact.send_msg"/></h3>

                <c:if test="${not empty contactSent}">
                    <div class="alert-success">
                        <spring:message code="contact.sent"/>
                    </div>
                </c:if>

                <c:if test="${not empty contactError}">
                    <div class="alert-error">
                            ${contactError}
                    </div>
                </c:if>

                <form method="POST" action="/contact/send">
                    <div class="form-group">
                        <label for="name"><spring:message code="contact.name"/></label>
                        <input type="text" id="name" name="name" required>
                    </div>
                    <div class="form-group">
                        <label for="email"><spring:message code="contact.email"/></label>
                        <input type="email" id="email" name="email" required>
                    </div>
                    <div class="form-group">
                        <label for="message"><spring:message code="contact.message"/></label>
                        <textarea id="message" name="message" rows="6" required></textarea>
                    </div>
                    <div style="margin-top: 1rem;">
                        <button type="submit" class="btn-submit"><spring:message code="btn.send_message"/></button>
                    </div>
                </form>
            </div>
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
