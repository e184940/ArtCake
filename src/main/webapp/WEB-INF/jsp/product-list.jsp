<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vårt utvalg - ArtCake AS</title>
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

<main class="products-section">
    <h1>Vårt utvalg av kaker</h1>

    <div class="products-grid">
        <c:forEach var="cake" items="${cakes}">
            <div class="product-card">
                <div class="product-image">
                    <img src="${cake.imageUrl}" alt="${cake.name}">
                </div>
                <div class="product-info">
                    <h2>${cake.name}</h2>
                    <p class="product-description">${cake.description}</p>
                    <p class="product-price">${cake.price} kr</p>
                    <a href="/products/${cake.id}" class="btn-details">Se detaljer</a>
                </div>
            </div>
        </c:forEach>
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