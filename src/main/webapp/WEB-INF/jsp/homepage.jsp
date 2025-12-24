<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <title>Hjem - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
        <img src="/imgs/logo-no-bg.png" alt="ArtCake AS">
    </a>
    <div class="hamburger-menu">
        <div class="hamburger">
            <span></span>
            <span></span>
            <span></span>
        </div>
        <nav class="menu-items">
            <a href="/products">Vårt utvalg</a>
            <a href="/about">Om oss</a>
            <a href="/contact">Kontakt</a>

        </nav>
    </div>
</div>

<main class="welcome-section">
    <div class="hero">
        <h1>Velkommen til ArtCake</h1>
        <p>Hvor hver kake er et mesterverk</p>
    </div>

    <div class="description-section">
        <p><strong>Art Cake studio</strong> er et lite lokalt konditori</p>
        <p>Vi bruker lokale ingredienser, og følger lengestående, og tradisjonelle familie-oppskrifter.</p>
        <p>Alt vi gjør, gjør vi med formål om å lage de <strong>deiligste kakene</strong> du noensinne har smakt!</p>
        <p>Vi lager alt fra store saftige honningkaker, til små deilige sjokoloadekaker.</p>
        <p>Fine kaker med personlige meldinger eller pynt ordner vi selvsagt også!</p>
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