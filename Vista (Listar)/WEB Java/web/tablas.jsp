<%-- 
    Document   : tablas
    Created on : 6 dic 2025, 5:15:07
    Author     : march
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tablas disponibles</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <style>
    .card-icon { font-size: 2rem; }
  </style>
</head>
<body class="container mt-5">
  <div class="text-center mb-4">
    <h1 class="display-5">Tablas disponibles</h1>
    <p class="text-muted">Selecciona una tabla para explorar sus registros</p>
  </div>

  <div class="row row-cols-1 row-cols-md-3 g-4">
    <c:forEach var="tabla" items="${tablas}">
      <div class="col">
        <div class="card shadow-sm h-100 border-primary">
          <div class="card-body d-flex align-items-center justify-content-between">
            <div>
              <h5 class="card-title mb-1">${tabla.toUpperCase()}</h5>
              <a href="RegistrosServlet?tabla=${tabla}" class="btn btn-sm btn-outline-primary">Ver registros</a>
            </div>
            <div class="card-icon">&#127918;</div> <!-- 🎮 ícono genérico -->
          </div>
        </div>
      </div>
    </c:forEach>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
