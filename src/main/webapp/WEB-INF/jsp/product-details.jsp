<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="no">
<head>
    <link href="/css/style.css" rel="stylesheet">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${cake.name} - ArtCake AS</title>
</head>
<body>
<div class="topmenu">
    <a href="/" class="logo-link">
        <img src="/imgs/logo-no-bg.png" alt="ArtCake AS">
    </a>
</div>

<div class="modal-overlay" id="detailsModal">
    <div class="modal-content">
        <button class="modal-close" onclick="window.history.back()">&times;</button>
        <div class="modal-body">
            <img src="${cake.imageUrl}" alt="${cake.name}" class="modal-image">
            <h2>${cake.name}</h2>
            <p class="modal-description">${cake.description}</p>
            <p class="modal-price">${cake.price} kr</p>

            <div class="allergens">
                <h3>Allergener:</h3>
                <ul>
                    <c:forEach var="allergen" items="${cake.allergens}">
                        <li>${allergen.name}</li>
                    </c:forEach>
                </ul>
            </div>
            <a href="/order/${cake.id}" class="btn-order">Gå til bestilling</a>
        </div>
    </div>
</div>

<script>
    document.getElementById("detailsModal").classList.add("show");
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            window.history.back();
        }
    });
</script>
</body>
</html>
