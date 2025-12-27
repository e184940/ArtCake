<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kundeomtaler - ArtCake AS</title>
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
                <a href="/faq">FAQ</a>
                <a href="/reviews">Kundeomtaler</a>
                <div class="lang-switch">
                    <a href="?lang=no" class="active">NO</a> | <a href="?lang=en">EN</a>
                </div>
            </nav>
            <div class="menu-backdrop"></div>
        </div>
    </div>
</div>

<main class="reviews-section" style="padding: 2rem; max-width: 1000px; margin: 0 auto;">
    <h1 style="text-align: center; margin-bottom: 2rem; color: #333;">Hva våre kunder sier</h1>

    <div class="reviews-grid" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 2rem;">

        <div class="review-card" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div class="stars" style="color: #ffc107; font-size: 1.2rem; margin-bottom: 1rem;">★★★★★</div>
            <p style="font-style: italic; color: #555; margin-bottom: 1.5rem;"> "omtale" </p>
            <p style="font-weight: bold; color: #0b4a6a;">- kunde</p>
        </div>

        <div class="review-card" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div class="stars" style="color: #ffc107; font-size: 1.2rem; margin-bottom: 1rem;">★★★★★</div>
            <p style="font-style: italic; color: #555; margin-bottom: 1.5rem;">"omtale"</p>
            <p style="font-weight: bold; color: #0b4a6a;">- kunde</p>
        </div>

        <div class="review-card" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div class="stars" style="color: #ffc107; font-size: 1.2rem; margin-bottom: 1rem;">★★★★★</div>
            <p style="font-style: italic; color: #555; margin-bottom: 1.5rem;">"omtale"</p>
            <p style="font-weight: bold; color: #0b4a6a;">- kunde</p>
        </div>

        <div class="review-card" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div class="stars" style="color: #ffc107; font-size: 1.2rem; margin-bottom: 1rem;">★★★★★</div>
            <p style="font-style: italic; color: #555; margin-bottom: 1.5rem;">"omtale"</p>
            <p style="font-weight: bold; color: #0b4a6a;">- kunde</p>
        </div>

        <div class="review-card" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div class="stars" style="color: #ffc107; font-size: 1.2rem; margin-bottom: 1rem;">★★★★☆</div>
            <p style="font-style: italic; color: #555; margin-bottom: 1.5rem;">"omtale"</p>
            <p style="font-weight: bold; color: #0b4a6a;">- kunde</p>
        </div>

        <div class="review-card" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div class="stars" style="color: #ffc107; font-size: 1.2rem; margin-bottom: 1rem;">★★★★☆</div>
            <p style="font-style: italic; color: #555; margin-bottom: 1.5rem;">"omtale"</p>
            <p style="font-weight: bold; color: #0b4a6a;">- kunde</p>
        </div>
    </div>
</main>

<footer>
    <div class="footer-content">
        <a href="/terms">Vilkår og betingelser for bestilling</a>
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
