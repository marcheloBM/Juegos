from flask import Flask, jsonify, request
from flask_cors import CORS
import sqlite3, base64

app = Flask(__name__)
CORS(app)   # habilita CORS

DATABASE = "../../Juegos.sqlite"

def get_db_connection():
    conn = sqlite3.connect(DATABASE)
    conn.row_factory = sqlite3.Row
    return conn

@app.route("/")
def home():
    return "API de Juegos.sqlite funcionando. Usa /tables o /table/<nombre>."

@app.route("/tables", methods=["GET"])
def list_tables():
    conn = get_db_connection()
    tables = conn.execute("SELECT name FROM sqlite_master WHERE type='table';").fetchall()
    conn.close()
    return jsonify([t["name"] for t in tables])

@app.route("/table/<string:table_name>", methods=["GET"])
def get_table(table_name):
    page = int(request.args.get("page", 1))
    page_size = int(request.args.get("page_size", 20))
    search = request.args.get("search", "")

    offset = (page - 1) * page_size
    conn = get_db_connection()

    if search:
        cols = conn.execute(f"PRAGMA table_info({table_name});").fetchall()
        conditions = " OR ".join([f"{col['name']} LIKE ?" for col in cols])
        query = f"SELECT * FROM {table_name} WHERE {conditions} LIMIT ? OFFSET ?"
        params = [f"%{search}%"] * len(cols) + [page_size, offset]
        rows = conn.execute(query, params).fetchall()
        total = conn.execute(
            f"SELECT COUNT(*) as total FROM {table_name} WHERE {conditions}",
            [f"%{search}%"] * len(cols)
        ).fetchone()["total"]
    else:
        query = f"SELECT * FROM {table_name} LIMIT ? OFFSET ?"
        rows = conn.execute(query, (page_size, offset)).fetchall()
        total = conn.execute(f"SELECT COUNT(*) as total FROM {table_name}").fetchone()["total"]

    conn.close()

    data = []
    for row in rows:
        d = {}
        for key in row.keys():
            value = row[key]
            if isinstance(value, bytes):  # convertir BLOB a base64
                d[key] = base64.b64encode(value).decode("utf-8")
            else:
                d[key] = value
        data.append(d)

    return jsonify({
        "page": page,
        "page_size": page_size,
        "total": total,
        "data": data
    })

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)