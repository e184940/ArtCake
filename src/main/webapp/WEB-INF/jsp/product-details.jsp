<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="modal-body">
    <div class="modal-images">
        <img src="<c:url value='${cake.imageUrl}'/>" alt="${cake.name}" class="modal-image">
        <c:if test="${not empty cake.imageUrl2}">
            <img src="<c:url value='${cake.imageUrl2}'/>" alt="${cake.name}" class="modal-image">
        </c:if>
    </div>

    <div class="modal-info-section">
        <h2>${cake.name}</h2>
        <p class="modal-description">${cake.description}</p>
        <p class="modal-price"><spring:message code="cart.price"/>: ${cake.minPrice} kr</p>

        <div class="sizes-section">
            <h3><spring:message code="cart.details"/>:</h3>
            <form id="sizeForm">
                <div class="size-options">
                    <c:forEach var="size" items="${cake.sizes}">
                        <label class="size-option">
                            <input type="radio" name="selectedSize" value="${size.id}"
                                   data-price="${size.price}" required>
                            <span>${size.sizeCm} cm - ${size.price} kr</span>
                        </label>
                    </c:forEach>
                </div>
            </form>
        </div>

        <div class="allergens">
            <h3><spring:message code="terms.6.title"/>:</h3>
            <ul>
                <c:forEach var="allergen" items="${cake.allergens}">
                    <li>${allergen.name}</li>
                </c:forEach>
            </ul>
        </div>

        <!-- Hidden fields for JS -->
        <input type="hidden" id="detailCakeId" value="${cake.id}">
        <input type="hidden" id="detailCakeName" value="${cake.name}">

        <button class="btn-order" onclick="addToCart()"><spring:message code="btn.add_to_cart"/></button>
    </div>
</div>
