const API_URL = "http://127.0.0.1:5000"; // URL del backend Flask
let currentTable = null;
let currentPage = 1;
const pageSize = 20;
let totalPages = 0;
let searchTerm = "";

// 🌗 Cambiar tema
function toggleTheme() {
  document.body.classList.toggle("dark");
  document.body.classList.toggle("light");
}

// 📸 Modal imágenes
function openModal(src) {
  const modal = document.getElementById("imgModal");
  const modalImg = document.getElementById("modalImage");
  modal.style.display = "block";
  modalImg.src = src;
}
function closeModal() {
  document.getElementById("imgModal").style.display = "none";
}
window.onclick = function(event) {
  const modal = document.getElementById("imgModal");
  if (event.target === modal) modal.style.display = "none";
};

// 🚀 Inicializar: obtener tablas desde backend
async function start() {
  try {
    const res = await fetch(`${API_URL}/tables`);
    const tables = await res.json();

    const container = document.getElementById("buttons");
    container.innerHTML = "";
    tables.forEach(table => {
      const btn = document.createElement("button");
      btn.textContent = table;
      btn.onclick = () => {
        currentTable = table;
        currentPage = 1;
        searchTerm = "";
        document.getElementById("searchBox").value = "";
        showTable();
      };
      container.appendChild(btn);
    });
  } catch (err) {
    console.error("Error al conectar con el backend:", err);
  }
}

// 📊 Mostrar tabla desde backend
async function showTable() {
  let url = `${API_URL}/table/${currentTable}?page=${currentPage}&page_size=${pageSize}`;
  if (searchTerm) url += `&search=${encodeURIComponent(searchTerm)}`;

  try {
    const res = await fetch(url);
    const data = await res.json();

    totalPages = Math.ceil(data.total / pageSize);

    const columns = Object.keys(data.data[0] || {});
    let html = "<table><thead><tr>";
    columns.forEach(col => html += `<th>${col}</th>`);
    html += "</tr></thead><tbody>";

    data.data.forEach(row => {
      html += "<tr>";
      columns.forEach(col => {
        if (col.toLowerCase() === "imagen" && row[col]) {
          html += `<td><img class="thumbnail" src="data:image/jpeg;base64,${row[col]}" onclick="openModal(this.src)" alt="imagen"/></td>`;
        } else {
          html += `<td>${row[col]}</td>`;
        }
      });
      html += "</tr>";
    });

    html += "</tbody></table>";
    document.getElementById("data").innerHTML = html;

    const pagHtml = buildPagination(data.total);
    document.getElementById("paginationTop").innerHTML = pagHtml;
    document.getElementById("paginationBottom").innerHTML = pagHtml;
  } catch (err) {
    console.error("Error al cargar tabla:", err);
  }
}

// 🔁 Paginación
function buildPagination(total) {
  let pagHtml = "";
  if (currentPage > 1) {
    pagHtml += `<button onclick="prevPage()">Anterior</button>`;
  }
  pagHtml += ` Página ${currentPage} de ${totalPages} (Total registros: ${total}) `;
  if (currentPage < totalPages) {
    pagHtml += `<button onclick="nextPage()">Siguiente</button>`;
  }
  return pagHtml;
}

function nextPage() {
  if (currentPage < totalPages) {
    currentPage++;
    showTable();
  }
}

function prevPage() {
  if (currentPage > 1) {
    currentPage--;
    showTable();
  }
}

function applySearch() {
  searchTerm = document.getElementById("searchBox").value.trim();
  currentPage = 1;
  showTable();
}

// 🚀 Iniciar
start();