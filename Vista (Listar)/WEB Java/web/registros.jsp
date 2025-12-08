<%-- 
    Document   : registros
    Created on : 6 dic 2025, 5:15:22
    Author     : march
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Registros de ${tabla}</title>
  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <style>
    .highlight {
      background-color: yellow;
      font-weight: bold;
    }
  </style>
</head>
<body class="container mt-4">
  <h1 class="mb-4">Tabla: ${tabla}</h1>

  <!-- Barra de filtros -->
  <div class="row mb-4">
    <div class="col-md-6">
      <input type="text" id="filterNombre" class="form-control" placeholder="Buscar por nombre">
    </div>
    <div class="col-md-6">
      <input type="text" id="filterCodigo" class="form-control" placeholder="Buscar por código">
    </div>
  </div>

  <!-- Tabla de registros -->
  <table id="tablaRegistros" class="table table-bordered table-striped">
    <thead class="table-dark">
      <tr>
        <c:forEach var="col" items="${columnas}">
          <th>${col}</th>
        </c:forEach>
        <th>Imagen</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="fila" items="${registros}" varStatus="status">
        <tr class="fila">
          <c:forEach var="col" items="${columnas}">
            <td>${fila[col]}</td>
          </c:forEach>
          <td>
            <img src="ImagenServlet?tabla=${tabla}&id=${fila[columnas[0]]}"
                 class="img-thumbnail" style="width:100px;cursor:pointer"
                 data-bs-toggle="modal" data-bs-target="#imageModal"
                 onclick="showImage(${status.index})" />
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <!-- Modal Bootstrap -->
  <div class="modal fade" id="imageModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content bg-dark">
        <div class="modal-header border-0">
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body text-center">
          <img id="modalImg" class="img-fluid rounded" />
        </div>
        <div class="modal-footer justify-content-between border-0">
          <button class="btn btn-outline-light" onclick="changeImage(-1)">&#10094; Anterior</button>
          <button class="btn btn-outline-light" onclick="changeImage(1)">Siguiente &#10095;</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Bootstrap JS + script -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    let currentIndex = 0;
    let images = [];
    let nombreIdx = -1;
    let codigoIdx = -1;

    window.onload = function() {
      // Array de imágenes para el modal
      const thumbs = document.querySelectorAll("img.img-thumbnail");
      images = Array.from(thumbs).map(img => img.src);

      // Detectar índices de columnas por header
      const headers = Array.from(document.querySelectorAll("#tablaRegistros thead th"))
                           .map(th => th.innerText.trim().toLowerCase());
      nombreIdx = headers.indexOf("nombre");
      codigoIdx = headers.indexOf("codigo");

      // Activar filtros en tiempo real
      document.getElementById("filterNombre").addEventListener("input", applyFilters);
      document.getElementById("filterCodigo").addEventListener("input", applyFilters);
    };

    function showImage(index) {
      currentIndex = index;
      document.getElementById("modalImg").src = images[currentIndex];
    }

    function changeImage(step) {
      currentIndex += step;
      if (currentIndex < 0) currentIndex = images.length - 1;
      if (currentIndex >= images.length) currentIndex = 0;
      document.getElementById("modalImg").src = images[currentIndex];
    }

    function applyFilters() {
      const nombre = document.getElementById("filterNombre").value.toLowerCase();
      const codigo = document.getElementById("filterCodigo").value.toLowerCase();

      const rows = document.querySelectorAll("#tablaRegistros tbody tr.fila");
      rows.forEach(row => {
        const cells = row.querySelectorAll("td");

        const nombreText = (nombreIdx >= 0 && cells[nombreIdx]) ? cells[nombreIdx].innerText.toLowerCase() : "";
        const codigoText = (codigoIdx >= 0 && cells[codigoIdx]) ? cells[codigoIdx].innerText.toLowerCase() : "";

        const match =
          (nombre === "" || nombreText.includes(nombre)) &&
          (codigo === "" || codigoText.includes(codigo));

        row.style.display = match ? "" : "none";

        // Resaltado de coincidencias
        if (match) {
          if (nombreIdx >= 0 && cells[nombreIdx]) highlightText(cells[nombreIdx], nombre);
          if (codigoIdx >= 0 && cells[codigoIdx]) highlightText(cells[codigoIdx], codigo);
        } else {
          if (nombreIdx >= 0 && cells[nombreIdx]) removeHighlight(cells[nombreIdx]);
          if (codigoIdx >= 0 && cells[codigoIdx]) removeHighlight(cells[codigoIdx]);
        }
      });
    }

    function highlightText(cell, term) {
      const text = cell.innerText;
      if (term === "") {
        cell.innerHTML = text; // restaurar
        return;
      }
      const regex = new RegExp("(" + term + ")", "gi");
      cell.innerHTML = text.replace(regex, "<span class='highlight'>$1</span>");
    }

    function removeHighlight(cell) {
      cell.innerHTML = cell.innerText; // quitar resaltado
    }
  </script>
</body>
</html>