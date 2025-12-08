let db;
let currentTable = null;
let currentPage = 0;
const pageSize = 20;
let totalRows = 0;
let totalPages = 0;
let searchTerm = "";

// 🌗 Toggle de tema
function toggleTheme() {
  const body = document.body;
  body.classList.toggle("dark");
  body.classList.toggle("light");
}

// Modal funciones
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

async function start() {
  const SQL = await window.initSqlJs({
    locateFile: file => `https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.6.2/${file}`
  });

  document.getElementById("fileInput").addEventListener("change", async (event) => {
    const file = event.target.files[0];
    const arrayBuffer = await file.arrayBuffer();
    db = new SQL.Database(new Uint8Array(arrayBuffer));

    const res = db.exec("SELECT name FROM sqlite_master WHERE type='table';");
    const tables = res[0].values.map(row => row[0]);

    const container = document.getElementById("buttons");
    container.innerHTML = "";
    tables.forEach(table => {
      const btn = document.createElement("button");
      btn.textContent = table;
      btn.onclick = () => { 
        currentTable = table; 
        currentPage = 0; 
        searchTerm = ""; 
        document.getElementById("searchBox").value = "";
        calculateTotalRows(); 
        showTable(); 
      };
      container.appendChild(btn);
    });
  });
}

function blobToBase64(blob) {
  let binary = '';
  const bytes = new Uint8Array(blob);
  const len = bytes.byteLength;
  for (let i = 0; i < len; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return window.btoa(binary);
}

function calculateTotalRows() {
  let query = `SELECT COUNT(*) as total FROM ${currentTable}`;
  if (searchTerm) query = `SELECT COUNT(*) as total FROM ${currentTable} WHERE ${buildSearchCondition()}`;
  const res = db.exec(query);
  totalRows = res[0].values[0][0];
  totalPages = Math.ceil(totalRows / pageSize);
}

function buildSearchCondition() {
  const res = db.exec(`PRAGMA table_info(${currentTable});`);
  const cols = res[0].values.map(row => row[1]);
  return cols.map(c => `${c} LIKE '%${searchTerm}%'`).join(" OR ");
}

function showTable() {
  const offset = currentPage * pageSize;
  let query = `SELECT * FROM ${currentTable} LIMIT ${pageSize} OFFSET ${offset}`;
  if (searchTerm) query = `SELECT * FROM ${currentTable} WHERE ${buildSearchCondition()} LIMIT ${pageSize} OFFSET ${offset}`;
  const res = db.exec(query);
  if (res.length === 0) {
    document.getElementById("data").innerHTML = "<p>No hay registros en esta página.</p>";
    document.getElementById("paginationTop").innerHTML = "";
    document.getElementById("paginationBottom").innerHTML = "";
    return;
  }

  const columns = res[0].columns;
  const values = res[0].values;

  let html = "<table><thead><tr>";
  columns.forEach(col => html += `<th>${col}</th>`);
  html += "</tr></thead><tbody>";

  values.forEach(row => {
    html += "<tr>";
    row.forEach((cell, idx) => {
      const colName = columns[idx];
      if (colName.toLowerCase() === "imagen" && cell) {
        const codigoIndex = columns.findIndex(c => c.toLowerCase() === "codigo");
        const plataformaIndex = columns.findIndex(c => c.toLowerCase() === "plataforma");
        const codigo = codigoIndex >= 0 ? row[codigoIndex] : null;
        const plataforma = plataformaIndex >= 0 ? row[plataformaIndex] : null;

        if (codigo && plataforma) {
          const imgPath = `IMG/${plataforma}/${codigo}.jpg`;
          html += `<td><img class="thumbnail" src="${imgPath}" 
                   onerror="this.src='data:image/jpeg;base64,${blobToBase64(cell)}'" 
                   onclick="openModal(this.src)" alt="imagen"/></td>`;
        } else {
          const base64 = blobToBase64(cell);
          html += `<td><img class="thumbnail" src="data:image/jpeg;base64,${base64}" 
                   onclick="openModal(this.src)" alt="imagen"/></td>`;
        }
      } else {
        html += `<td>${cell}</td>`;
      }
    });
    html += "</tr>";
  });

  html += "</tbody></table>";
  document.getElementById("data").innerHTML = html;

  const pagHtml = buildPagination();
  document.getElementById("paginationTop").innerHTML = pagHtml;
  document.getElementById("paginationBottom").innerHTML = pagHtml;

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function buildPagination() {
  let pagHtml = "";
  if (currentPage > 0) pagHtml += `<button onclick="prevPage()">Anterior</button>`;
  pagHtml += ` Página ${currentPage + 1} de ${totalPages} (Total registros: ${totalRows}) `;
  if (currentPage < totalPages - 1) pagHtml += `<button onclick="nextPage()">Siguiente</button>`;
  return pagHtml;
}

function nextPage() {
  if (currentPage < totalPages - 1) {
    currentPage++;
    showTable();
  }
}

function prevPage() {
  if (currentPage > 0) {
    currentPage--;
    showTable();
  }
}

function applySearch() {
  searchTerm = document.getElementById("searchBox").value.trim();
  currentPage = 0;
  calculateTotalRows();
  showTable();
}

// Inicializar
start();