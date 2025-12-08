function toggleTheme() {
  document.body.classList.toggle("dark");
  document.body.classList.toggle("light");
}

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