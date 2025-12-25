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

<main class="custom-section">
    <h1>Personlige Kaker</h1>
    <p class="custom-intro">
        Vis din kreativitet! Bestill en helt personlig kake som passer dine ønsker og smaksanledninger.
        Fra bursdager til bryllupper – vi lager den perfekte kaken for deg.
    </p>

    <div class="custom-grid">
        <div class="custom-card">
            <div class="custom-image">
                <img src="https://via.placeholder.com/280x280?text=Bryllupskake" alt="Bryllupskake">
            </div>
        </div>

        <div class="custom-card">
            <div class="custom-image">
                <img src="https://via.placeholder.com/280x280?text=Bursdagskake" alt="Bursdagskake">
            </div>
        </div>

        <div class="custom-card">
            <div class="custom-image">
                <img src="https://via.placeholder.com/280x280?text=Tematisk+Kake" alt="Tematisk Kake">
            </div>
        </div>
    </div>

    <div class="custom-button-container">
        <a href="/order/custom" class="custom-btn-order">Bestill personlig kake</a>
    </div>
</main>

<script>
    document.querySelector(".hamburger").addEventListener("click", function(){
        this.classList.toggle("active");
        document.querySelector(".menu-items").classList.toggle("active");
    });
</script>
</body>
</html>

