<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>ArtCake Admin</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body { font-family: sans-serif; padding: 20px; background: #f4f4f4; }
        .admin-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .filters a { margin-right: 10px; text-decoration: none; padding: 5px 10px; background: #ddd; border-radius: 4px; color: #333; }
        .filters a:hover { background: #ccc; }
        table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #d4a373; color: white; }
        tr:hover { background-color: #f9f9f9; }
        .status-badge { padding: 4px 8px; border-radius: 4px; font-size: 0.9em; font-weight: bold; }
        .status-Ny { background-color: #ffcccb; color: #d32f2f; }
        .status-Ferdig { background-color: #c8e6c9; color: #388e3c; }
        .status-Påbegynt { background-color: #fff9c4; color: #fbc02d; }
        .action-btn { padding: 4px 8px; cursor: pointer; }
        .custom-tag { color: blue; font-size: 0.9em; margin-left: 5px; }
        .img-link { font-size: 0.8em; display: block; margin-top: 2px; }

        /* Modal styles */
        .image-modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgba(0,0,0,0.8);
        }
        .modal-content {
            margin: 2% auto;
            display: block;
            max-width: 60%;
            max-height: 60vh;
            border-radius: 5px;
        }
        .modal-caption {
            margin: auto;
            display: block;
            width: 80%;
            text-align: center;
            color: #fff;
            padding: 10px 0;
            font-size: 1.5em;
            font-weight: bold;
        }
        .close {
            position: absolute;
            top: 15px;
            right: 35px;
            color: #f1f1f1;
            font-size: 40px;
            font-weight: bold;
            transition: 0.3s;
            cursor: pointer;
        }
        .close:hover,
        .close:focus {
            color: #bbb;
            text-decoration: none;
            cursor: pointer;
        }
    </style>
</head>
<body>

<div id="imageModal" class="image-modal">
  <img class="modal-content" id="modalImg">
  <img class="modal-content" id="modalImg">
  <div id="caption" class="modal-caption"></div>
</div>

<div class="admin-header">
    <h1>Ty bolshaja molodec, ja teba ochen lublu!</h1>
    <div>
        <form action="/logout" method="post" style="display:inline;">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit">Logg ut</button>
        </form>
    </div>
</div>

<div class="filters">
    <a href="/admin">Siste bestilt</a>
    <a href="/admin?filter=delivery">Leveringsfrist</a>
    <a href="/admin?filter=new">Kun 'Ny'</a>
</div>

<br>

<table>
    <thead>
    <tr>
        <th>Dato bestilt</th>
        <th>Kunde</th>
        <th>Leveringsfrist</th>
        <th>Kaker</th>
        <th>Notater</th>
        <th>Total</th>
        <th>Status</th>
        <th>Handling</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="order" items="${orders}">
        <tr>
            <td>
                <%-- Format LocalDateTime to just date --%>
                <c:set var="dateParts" value="${fn:split(order.orderDate, 'T')}" />
                ${dateParts[0]}
            </td>
            <td>
                <div style="font-weight:bold;">${order.customerName}</div>
                <div style="font-size:0.9em; color:#666;">${order.customerPhone}</div>
</div>
            </td>
            <td style="font-weight:bold; color:#d35400;">${order.deliveryDate}</td>
            <td>
                <ul style="margin:0; padding-left:20px;">
                    <c:forEach var="item" items="${order.items}">
                        <li>
                            <span>${item.quantity}x ${item.cakeName}</span>
                            <c:if test="${item.itemType == 'custom'}">
                                <span class="custom-tag">(Custom)</span>
                                <c:if test="${not empty item.customDescription}">
                                    <div style="font-size: 0.9em; color: #555; margin-top: 4px; font-style: italic; background: #f9f9f9; padding: 4px; border-radius: 4px;">
                                        "${item.customDescription}"
                                    </div>
                                </c:if>
                            </c:if>
                            <c:if test="${not empty item.customImageUrl}">
                                <div class="img-link">
                                    <%-- Robust filename extraction: Split by '/' and take the last element --%>
                                    <c:set var="pathParts" value="${fn:split(item.customImageUrl, '/')}" />
                                    <c:set var="filename" value="${pathParts[fn:length(pathParts) - 1]}" />
                                    <%-- Construct the clean URL for ImageController --%>
                                    <c:set var="imgUrl" value="/custom-image/${filename}" />

                                    <a href="#" onclick="openModal('${imgUrl}', '${order.customerName}'); return false;">Se bilde</a>
                                </div>
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </td>
            <td>
                <c:if test="${not empty order.notes}">
                    <div style="background-color: #fffde7; padding: 8px; border: 1px solid #fff9c4; border-radius: 4px; font-size: 0.9em;">
                        <strong>Notat:</strong><br>
                        ${order.notes}
                    </div>
                </c:if>
            </td>
            <td>${order.totalPrice} kr</td>
            <td>
                <span class="status-badge status-${order.status}">${order.status}</span>
            </td>
            <td>
                <form action="/admin/update-status" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="orderId" value="${order.id}"/>
                    <select name="status" onchange="this.form.submit()" class="action-btn">
                        <option value="Ny" ${order.status == 'Ny' ? 'selected' : ''}>Ny</option>
                        <option value="Påbegynt" ${order.status == 'Påbegynt' ? 'selected' : ''}>Påbegynt</option>
                        <option value="Ferdig" ${order.status == 'Ferdig' ? 'selected' : ''}>Ferdig</option>
                    </select>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<script>
    function openModal(imgUrl, customerName) {
        var modal = document.getElementById("imageModal");
        var modalImg = document.getElementById("modalImg");
        var captionText = document.getElementById("caption");

        modal.style.display = "block";
        modalImg.src = imgUrl;
        captionText.innerHTML = "Bestilling fra: " + customerName;
    }

    function closeModal() {
        var modal = document.getElementById("imageModal");
        modal.style.display = "none";
    }

    // Close modal when clicking outside the image
    window.onclick = function(event) {
        var modal = document.getElementById("imageModal");
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
</script>

</body>
</html>
