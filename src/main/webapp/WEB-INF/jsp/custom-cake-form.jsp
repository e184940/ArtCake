<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="custom.form.title"/> - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
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

<main class="custom-form-section">
    <h1><spring:message code="custom.form.title"/></h1>

    <div class="info-text">
        <spring:message code="custom.form.intro"/>
    </div>

    <form id="customCakeForm" onsubmit="submitCustomCake(event)">
        <div class="form-group">
            <label for="description"><spring:message code="custom.form.desc"/></label>
            <textarea id="description" name="description" required
                      placeholder="<spring:message code='custom.form.desc_placeholder'/>"></textarea>
        </div>

        <div class="form-group">
            <label for="inspiration"><spring:message code="custom.form.image"/></label>
            <input type="file" id="inspiration" name="inspiration" accept="image/*">
            <img id="imagePreview" class="preview-image" src="" alt="Preview image" style="display: none;">
        </div>

        <div class="form-group">
            <label for="estimatedPrice"><spring:message code="custom.form.budget"/></label>
            <input type="number" id="estimatedPrice" name="estimatedPrice"
                   placeholder="F.eks. 500" min="0">
        </div>

        <div class="form-actions">
            <button type="button" class="btn btn-cancel" onclick="window.history.back()"><spring:message code="custom.form.cancel"/></button>
            <button type="submit" class="btn btn-submit"><spring:message code="btn.add_to_cart"/></button>
        </div>
    </form>
</main>

<footer>
    <div class="footer-content">
        <a href="/terms"><spring:message code="footer.terms"/></a>
    </div>
</footer>

<script>
    // Preview image and validate size
    document.getElementById('inspiration').addEventListener('change', function (e) {
        const file = e.target.files[0];
        if (file) {
            // Check file size (10MB limit)
            if (file.size > 10 * 1024 * 1024) {
                alert('Bildet er for stort! Maks størrelse er 10MB.');
                this.value = ''; // Clear the input
                document.getElementById('imagePreview').style.display = 'none';
                return;
            }

            const reader = new FileReader();
            reader.onload = function (event) {
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
