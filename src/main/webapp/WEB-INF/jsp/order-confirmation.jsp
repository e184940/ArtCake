<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bestilling mottatt - ArtCake AS</title>
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

<main class="confirmation-section">
    <div class="confirmation-box">
        <h1>✓ Bestilling mottatt!</h1>
        <p>Takk for din bestilling!</p>

        <div class="confirmation-highlight">
            <p>Vi har mottatt bestillingen din og vil kontakte deg snart for å bekrefte detaljer og pris.</p>
            <p><strong>Sjekk eposten din</strong> for å se kopien av bestillingen.</p>
        </div>

        <p style="color: #999; font-size: 0.95rem;">
            En av våre kakemestere vil ta kontakt med deg innen kort tid.
        </p>

        <a href="/" class="btn-home">Tilbake til forsiden</a>
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

