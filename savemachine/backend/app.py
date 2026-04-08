from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import pandas as pd
from datetime import datetime, timedelta
import json, os, hashlib

app = Flask(__name__)
CORS(app)

# ── Carrega o modelo treinado ──────────────────────────────────────────────
MODEL_PATH = os.path.join(os.path.dirname(__file__), '..', 'modelo_savemachine.pkl')
try:
    modelo = joblib.load(MODEL_PATH)
    print("✅ Modelo carregado com sucesso.")
except FileNotFoundError:
    modelo = None
    print("⚠️  modelo_savemachine.pkl não encontrado. Rode treinar.py primeiro.")

# ── Banco de dados simples em JSON ─────────────────────────────────────────
DB_PATH = os.path.join(os.path.dirname(__file__), 'db.json')

def load_db():
    if not os.path.exists(DB_PATH):
        return {"users": [], "machines": [], "predictions": []}
    with open(DB_PATH, 'r') as f:
        return json.load(f)

def save_db(db):
    with open(DB_PATH, 'w') as f:
        json.dump(db, f, indent=2, default=str)

def hash_password(password):
    return hashlib.sha256(password.encode()).hexdigest()

def init_db():
    db = load_db()
    if not db["users"]:
        db["users"].append({
            "id": 1, "name": "Admin",
            "email": "admin@savemachine.com",
            "password": hash_password("admin123")
        })
        save_db(db)

init_db()

# ═══════════════════════════════════════════════════════════════
# AUTH
# ═══════════════════════════════════════════════════════════════

@app.route('/auth/login', methods=['POST'])
def login():
    data = request.get_json()
    email = data.get('email', '').strip()
    password = data.get('password', '')
    db = load_db()
    user = next((u for u in db["users"]
                 if u["email"] == email and u["password"] == hash_password(password)), None)
    if not user:
        return jsonify({"error": "Email ou senha inválidos"}), 401
    return jsonify({
        "id": user["id"], "name": user["name"], "email": user["email"],
        "token": f"token_{user['id']}_{hash_password(email)[:8]}"
    })

@app.route('/auth/register', methods=['POST'])
def register():
    data = request.get_json()
    name = data.get('name', '').strip()
    email = data.get('email', '').strip()
    password = data.get('password', '')
    if not name or not email or not password:
        return jsonify({"error": "Todos os campos são obrigatórios"}), 400
    db = load_db()
    if any(u["email"] == email for u in db["users"]):
        return jsonify({"error": "Email já cadastrado"}), 409
    new_id = max((u["id"] for u in db["users"]), default=0) + 1
    db["users"].append({"id": new_id, "name": name, "email": email, "password": hash_password(password)})
    save_db(db)
    return jsonify({"message": "Usuário criado com sucesso"}), 201

# ═══════════════════════════════════════════════════════════════
# MÁQUINAS
# ═══════════════════════════════════════════════════════════════

@app.route('/machines', methods=['GET'])
def list_machines():
    db = load_db()
    return jsonify(db["machines"])

@app.route('/machines', methods=['POST'])
def add_machine():
    data = request.get_json()
    machine_id = data.get('machine_id', '').strip()
    model_name = data.get('model', '').strip()
    if not machine_id or not model_name:
        return jsonify({"error": "ID e modelo são obrigatórios"}), 400
    db = load_db()
    if any(m["machine_id"] == machine_id for m in db["machines"]):
        return jsonify({"error": "Máquina já cadastrada"}), 409
    machine = {
        "machine_id": machine_id, "model": model_name,
        "status": "Normal", "risk": 0.0,
        "next_maintenance": None, "created_at": datetime.now().isoformat()
    }
    db["machines"].append(machine)
    save_db(db)
    return jsonify(machine), 201

@app.route('/machines/<machine_id>', methods=['DELETE'])
def delete_machine(machine_id):
    db = load_db()
    before = len(db["machines"])
    db["machines"] = [m for m in db["machines"] if m["machine_id"] != machine_id]
    if len(db["machines"]) == before:
        return jsonify({"error": "Máquina não encontrada"}), 404
    save_db(db)
    return jsonify({"message": "Máquina removida"})

# ═══════════════════════════════════════════════════════════════
# PREDIÇÃO
# ═══════════════════════════════════════════════════════════════

@app.route('/predict', methods=['POST'])
def predict():
    if not modelo:
        return jsonify({"error": "Modelo de IA não disponível"}), 503
    data = request.get_json()
    machine_id = data.get('machine_id')
    t_ar       = data.get('temperatura_ar')
    t_proc     = data.get('temperatura_processo')
    rotacao    = data.get('rotacao_rpm')
    torque     = data.get('torque')
    desgaste   = data.get('desgaste_ferramenta')
    if None in [machine_id, t_ar, t_proc, rotacao, torque, desgaste]:
        return jsonify({"error": "Todos os parâmetros são obrigatórios"}), 400

    entrada = pd.DataFrame([{
        'temperatura_ar': float(t_ar), 'temperatura_processo': float(t_proc),
        'rotacao_rpm': float(rotacao), 'torque': float(torque),
        'desgaste_ferramenta': float(desgaste)
    }])
    prob = float(modelo.predict_proba(entrada)[0][1])

    if prob >= 0.8:
        status, dias, mensagem = "Risco Crítico", 0, "Manutenção IMEDIATA necessária!"
    elif prob >= 0.6:
        status, dias, mensagem = "Risco Alto", 2, "Agendar manutenção em até 2 dias."
    elif prob >= 0.4:
        status, dias, mensagem = "Risco Moderado", 5, "Agendar manutenção em até 5 dias."
    else:
        status, dias, mensagem = "Normal", 30, "Máquina operando normalmente."

    next_m = (datetime.now() + timedelta(days=dias)).strftime("%d/%m/%Y")

    db = load_db()
    for m in db["machines"]:
        if m["machine_id"] == machine_id:
            m["status"] = status
            m["risk"] = round(prob * 100, 1)
            m["next_maintenance"] = next_m
            break

    record = {
        "id": len(db["predictions"]) + 1, "machine_id": machine_id,
        "temperatura_ar": t_ar, "temperatura_processo": t_proc,
        "rotacao_rpm": rotacao, "torque": torque, "desgaste_ferramenta": desgaste,
        "probabilidade": round(prob * 100, 1), "status": status,
        "next_maintenance": next_m, "timestamp": datetime.now().isoformat()
    }
    db["predictions"].append(record)
    save_db(db)

    return jsonify({
        "machine_id": machine_id, "probabilidade": round(prob * 100, 1),
        "status": status, "mensagem": mensagem, "proxima_manutencao": next_m
    })

# ═══════════════════════════════════════════════════════════════
# HISTÓRICO
# ═══════════════════════════════════════════════════════════════

@app.route('/history', methods=['GET'])
def get_history():
    machine_id = request.args.get('machine_id')
    db = load_db()
    preds = db["predictions"]
    if machine_id:
        preds = [p for p in preds if p["machine_id"] == machine_id]
    return jsonify(list(reversed(preds[-50:])))

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
