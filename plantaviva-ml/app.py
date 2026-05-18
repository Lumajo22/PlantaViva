# ════════════════════════════════════════════════════════════════
#   PlantaViva ML — Flask API
#   Endpoints:
#     POST /predict        → predict single reading
#     POST /predict/batch  → predict multiple readings
#     GET  /health         → service health check
#     GET  /models/info    → model metrics summary
# ════════════════════════════════════════════════════════════════

from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
import os
import json
from pathlib import Path

load_dotenv()

app = Flask(__name__)
CORS(app)

# Lazy-load models (only when first request arrives)
_predictor = None

def get_predictor():
    global _predictor
    if _predictor is None:
        from inference import AnomalyPredictor
        _predictor = AnomalyPredictor()
    return _predictor


# ════════════════════════════════════════════════════════════════
#   GET /health
# ════════════════════════════════════════════════════════════════
@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        'status':  'ok',
        'service': 'PlantaViva ML API',
        'version': '1.0.0'
    }), 200


# ════════════════════════════════════════════════════════════════
#   GET /models/info
# ════════════════════════════════════════════════════════════════
@app.route('/models/info', methods=['GET'])
def models_info():
    report_path = Path(__file__).parent / 'models' / 'metrics_report.json'
    if not report_path.exists():
        return jsonify({'error': 'Models not trained yet. Run train_model.py first.'}), 404

    with open(report_path) as f:
        report = json.load(f)
    return jsonify(report), 200


# ════════════════════════════════════════════════════════════════
#   POST /predict
#   Body: { "valor": 35.0, "umbral_min": 15.0,
#           "umbral_max": 30.0, "sensor_type": "TEMPERATURA" }
# ════════════════════════════════════════════════════════════════
@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json(silent=True)
    if not data:
        return jsonify({'error': 'Request body must be JSON'}), 400

    required = ['valor', 'umbral_min', 'umbral_max', 'sensor_type']
    missing  = [f for f in required if f not in data]
    if missing:
        return jsonify({'error': f'Missing fields: {missing}'}), 400

    try:
        result = get_predictor().predict(
            valor       = float(data['valor']),
            umbral_min  = float(data['umbral_min']),
            umbral_max  = float(data['umbral_max']),
            sensor_type = str(data['sensor_type']).upper()
        )
        return jsonify(result), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500


# ════════════════════════════════════════════════════════════════
#   POST /predict/batch
#   Body: { "readings": [ {...}, {...} ] }
# ════════════════════════════════════════════════════════════════
@app.route('/predict/batch', methods=['POST'])
def predict_batch():
    data = request.get_json(silent=True)
    if not data or 'readings' not in data:
        return jsonify({'error': 'Body must contain "readings" array'}), 400

    results = []
    predictor = get_predictor()

    for reading in data['readings']:
        try:
            result = predictor.predict(
                valor       = float(reading['valor']),
                umbral_min  = float(reading['umbral_min']),
                umbral_max  = float(reading['umbral_max']),
                sensor_type = str(reading['sensor_type']).upper()
            )
            results.append(result)
        except Exception as e:
            results.append({'error': str(e), 'input': reading})

    return jsonify({
        'total':     len(results),
        'anomalies': sum(1 for r in results if r.get('is_anomaly')),
        'results':   results
    }), 200


# ════════════════════════════════════════════════════════════════
#   MAIN
# ════════════════════════════════════════════════════════════════
if __name__ == '__main__':
    port = int(os.getenv('FLASK_PORT', 5000))
    print(f"🌿 PlantaViva ML API starting on port {port}")
    print(f"   POST /predict       → single prediction")
    print(f"   POST /predict/batch → batch predictions")
    print(f"   GET  /health        → service health")
    print(f"   GET  /models/info   → metrics report")
    app.run(host='0.0.0.0', port=port, debug=True)