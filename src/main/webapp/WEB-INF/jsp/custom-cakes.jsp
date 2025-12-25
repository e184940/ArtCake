<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personlige kaker - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
        <img src="/imgs/logo_hvit_nobg.png" alt="ArtCake AS">
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

<main class="custom-section">
    <h1>Personlige Kaker</h1>
    <p class="custom-intro">
        Vis din kreativitet! Bestill en helt personlig kake som passer dine ønsker og smaksanledninger.
        Fra bursdager til bryllupper, vi lager den perfekte kaken for deg.
    </p>

    <!-- Center the order button -->
    <div class="custom-button-container" style="justify-content:center; margin-bottom: 1.5rem;">
        <a href="/order/custom" class="custom-btn-order">Bestill personlig kake</a>
    </div>

    <!-- Use the same responsive grid structure as the product list; cards contain ONLY images (no text) -->
    <div class="products-grid">
        <!-- 12 example custom cakes (non-interactive) -->

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_0649.jpg" alt="Custom 1">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_1762.jpg" alt="Custom 2">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_2077.jpg" alt="Custom 3">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_2899.jpg" alt="Custom 4">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_3206.jpg" alt="Custom 5">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_3282.JPG" alt="Custom 6">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_3480.jpg" alt="Custom 7">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_3977.jpg" alt="Custom 8">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_5470.jpg" alt="Custom 9">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_6145.jpg" alt="Custom 10">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_7020.jpg" alt="Custom 11">
            </div>
        </div>

        <div class="product-card custom-card">
            <div class="product-image">
                <img src="/imgs/customs/IMG_9162.jpg" alt="Custom 12">
            </div>
        </div>

    </div>

</main>

<script>
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
