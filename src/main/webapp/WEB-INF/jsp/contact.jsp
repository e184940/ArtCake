<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kontakt - ArtCake AS</title>
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

<main class="contact-section" style="padding:2rem; max-width:900px; margin:0 auto;">
    <h1>Kontakt oss</h1>

    <div style="display:grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; align-items:start;">
        <div>
            <h3>Adresse</h3>
            <p>Art Cake Studio AS<br>Matti Aikios vei 8<br>8019, Bodø</p>

            <h3>Kontakt</h3>
            <p>Epost: <a href="mailto:artcake@artcake.no">artcake@artcake.no</a><br>
               Telefon: <a href="tel:+4712345678">+47 12 34 56 78</a></p>

            <h3>Følg oss</h3>
            <p>
                <a href="https://www.facebook.com/artcake" target="_blank">Facebook</a><br>
                <a href="https://www.instagram.com/artcake" target="_blank">Instagram</a>
            </p>
        </div>

        <div>
            <h3>Send en melding</h3>
            <form method="POST" action="/contact/send">
                <div class="form-group">
                    <label for="name">Navn</label>
                    <input type="text" id="name" name="name" required>
                </div>
                <div class="form-group">
                    <label for="email">Epost</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="message">Melding</label>
                    <textarea id="message" name="message" rows="6" required></textarea>
                </div>
                <div style="margin-top: 1rem;">
                    <button type="submit" class="btn-submit">Send melding</button>
                </div>
            </form>
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

