from flask import Flask, render_template, request
import sqlite3, base64, os

app = Flask(__name__)
DATABASE = "../../Juegos.sqlite"
PAGE_SIZE = 20

def get_db_connection():
    conn = sqlite3.connect(DATABASE)
    conn.row_factory = sqlite3.Row
    return conn

@app.route("/")
def index():
    conn = get_db_connection()
    tables = conn.execute("SELECT name FROM sqlite_master WHERE type='table';").fetchall()
    conn.close()
    return render_template("index.html", tables=[t["name"] for t in tables])

@app.route("/table/<string:table_name>")
def show_table(table_name):
    page = int(request.args.get("page", 1))
    search = request.args.get("search", "").strip()
    offset = (page - 1) * PAGE_SIZE

    conn = get_db_connection()
    cursor = conn.cursor()

    if search:
        cols = cursor.execute(f"PRAGMA table_info({table_name});").fetchall()
        conditions = " OR ".join([f"{col['name']} LIKE ?" for col in cols])
        query = f"SELECT * FROM {table_name} WHERE {conditions} LIMIT ? OFFSET ?"
        params = [f"%{search}%"] * len(cols) + [PAGE_SIZE, offset]
        rows = cursor.execute(query, params).fetchall()
        total = cursor.execute(
            f"SELECT COUNT(*) as total FROM {table_name} WHERE {conditions}",
            [f"%{search}%"] * len(cols)
        ).fetchone()["total"]
    else:
        query = f"SELECT * FROM {table_name} LIMIT ? OFFSET ?"
        rows = cursor.execute(query, (PAGE_SIZE, offset)).fetchall()
        total = cursor.execute(f"SELECT COUNT(*) as total FROM {table_name}").fetchone()["total"]

    juegos = []
    for row in rows:
        codigo = row["codigo"] if "codigo" in row.keys() else None
        plataforma = row["plataforma"] if "plataforma" in row.keys() else table_name
        imagen_path = f"img/{plataforma}/{codigo}.jpg" if codigo else None

        if imagen_path and os.path.exists(os.path.join("static", imagen_path)):
            imagen = imagen_path
        elif "imagen" in row.keys() and row["imagen"]:
            imagen = f"data:image/jpeg;base64,{base64.b64encode(row['imagen']).decode('utf-8')}"
        else:
            imagen = "img/fallback.jpg"

        juegos.append({**dict(row), "imagen": imagen})

    conn.close()
    total_pages = (total // PAGE_SIZE) + (1 if total % PAGE_SIZE else 0)

    return render_template("table.html",
                           table_name=table_name,
                           juegos=juegos,
                           page=page,
                           total_pages=total_pages,
                           total=total,
                           search=search)

# 👇 Bloque de arranque
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5001)
