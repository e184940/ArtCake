<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personlig kake - ArtCake AS</title>
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
            </nav>
        </div>
    </div>
</div>

<main class="custom-form-section">
    <h1>Bestill Personlig Kake</h1>

    <div class="info-text">
        <strong>Fortell oss om drømmekaken din!</strong> Beskriv hva du ønsker, og last gjerne opp et inspirasjonsbilde.
        Vi tar kontakt for å diskutere detaljer og pris.
    </div>

    <form id="customCakeForm" onsubmit="submitCustomCake(event)">
        <div class="form-group">
            <label for="description">Beskrivelse av kaken *</label>
            <textarea id="description" name="description" required
                      placeholder="Beskriv drømmekaken din... (smak, design, tema, osv.)"></textarea>
        </div>

        <div class="form-group">
            <label for="inspiration">Inspirasjonsbilde (valgfritt)</label>
            <input type="file" id="inspiration" name="inspiration" accept="image/*">
            <img id="imagePreview" class="preview-image" style="display: none;">
        </div>

        <div class="form-group">
            <label for="estimatedPrice">Estimert budsjett (kr)</label>
            <input type="number" id="estimatedPrice" name="estimatedPrice"
                   placeholder="F.eks. 500" min="0" step="50">
        </div>

        <div class="form-actions">
            <button type="button" class="btn btn-cancel" onclick="window.history.back()">Avbryt</button>
            <button type="submit" class="btn btn-submit">Legg i handlekurv</button>
        </div>
    </form>
</main>

<script>
    // Preview image
    document.getElementById('inspiration').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(event) {
                const preview = document.getElementById('imagePreview');
                preview.src = event.target.result;
                preview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    });

    function submitCustomCake(event) {
        event.preventDefault();

        const description = document.getElementById('description').value;
        const estimatedPrice = document.getElementById('estimatedPrice').value || '500';
        const fileInput = document.getElementById('inspiration');
        const submitBtn = document.querySelector('button[type="submit"]');

        // Disable submit button and show loading
        submitBtn.disabled = true;
        submitBtn.textContent = 'Laster opp...';

        const formData = new FormData();
        formData.append('description', description);
        formData.append('price', parseFloat(estimatedPrice));

        if (fileInput.files.length > 0) {
            console.log('Uploading file:', fileInput.files[0].name, 'Size:', fileInput.files[0].size);
            formData.append('inspirationImage', fileInput.files[0]);
        }

        fetch('/cart/add-custom', {
            method: 'POST',
            body: formData
        }).then(response => response.text())
        .then(result => {
            console.log('Server response:', result);
            if (result === 'added') {
                alert('Personlig kakebestilling lagt til handlekurv!');
                window.location.href = '/cart';
            } else if (result.startsWith('error:')) {
                alert('Feil ved bestilling: ' + result.substring(6));
                // Re-enable button
                submitBtn.disabled = false;
                submitBtn.textContent = 'Legg i handlekurv';
            } else {
                alert('Feil ved bestilling: ' + result);
                // Re-enable button
                submitBtn.disabled = false;
                submitBtn.textContent = 'Legg i handlekurv';
            }
        }).catch(err => {
            console.error('Network error:', err);
            alert('Feil ved bestilling: ' + err);
            // Re-enable button
            submitBtn.disabled = false;
            submitBtn.textContent = 'Legg i handlekurv';
        });
    }

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
