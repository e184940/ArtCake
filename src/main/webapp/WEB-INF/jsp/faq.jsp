<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FAQ - ArtCake AS</title>
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

<main class="faq-section" style="padding: 2rem; max-width: 800px; margin: 0 auto;">
    <h1 style="text-align: center; margin-bottom: 2rem; color: #333;">Ofte stilte spørsmål (FAQ)</h1>

    <div class="faq-content" style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;">Hvor lang tid i forveien må jeg bestille?</h3>
            <p style="color: #555; line-height: 1.6;">For kaker fra vårt faste utvalg trenger vi bestillingen senest 3 dager før. For personlige kaker anbefaler vi å bestille minst 1 uke i forveien, gjerne enda tidligere i høysesonger (mai, juni, desember).</p>
        </div>

        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;">Tilbyr dere glutenfrie eller laktosefrie kaker?</h3>
            <p style="color: #555; line-height: 1.6;">Ja, flere av våre kaker kan lages glutenfrie, laktosefire, og fri for andre diverse allergener. For dette ber vi deg venligst legge igjen en kommentar ved bestilling.</p>
        </div>

        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;">Kan dere levere kaken?</h3>
            <p style="color: #555; line-height: 1.6;">Ja, vi tilbyr levering i Bodø-området mot et tillegg i prisen. Prisen avhenger av leveringsadresse. Dette kan avtales nærmere ved bestilling.</p>
        </div>

        <div class="faq-item" style="margin-bottom: 1.5rem;">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;">Hvordan betaler jeg?</h3>
            <p style="color: #555; line-height: 1.6;">Du betaler ikke ved bestilling på nettsiden. Vi sender deg en faktura eller betalingsinformasjon (Vipps) når bestillingen er bekreftet, og vi er enige om alle detaljer.</p>
        </div>

        <div class="faq-item">
            <h3 style="color: #0b4a6a; margin-bottom: 0.5rem;">Kan jeg endre bestillingen min?</h3>
            <p style="color: #555; line-height: 1.6;">Endringer kan gjøres inntil 3 dager før levering for standardkaker. For større personlige kaker må endringer skje så tidlig som mulig. Kontakt oss på telefon eller e-post for endringer.</p>
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
