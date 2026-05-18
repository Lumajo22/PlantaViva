# ════════════════════════════════════════════════════════════════
#   PlantaViva ML — Inference Script
#   Loads trained models and predicts anomalies
# ════════════════════════════════════════════════════════════════

import numpy as np
import joblib
import json
from pathlib import Path
from tensorflow import keras

BASE_DIR  = Path(__file__).parent
MODEL_DIR = BASE_DIR / 'models'

SENSOR_TYPE_MAP = {'TEMPERATURA': 0, 'HUMEDAD': 1, 'LUZ': 2}


def build_features(valor: float, umbral_min: float,
                   umbral_max: float, sensor_type: str) -> np.ndarray:
    """Build feature vector from a single reading."""
    rango         = umbral_max - umbral_min
    distancia_min = valor - umbral_min
    distancia_max = umbral_max - valor
    pct_rango     = distancia_min / rango if rango != 0 else 0.0
    sensor_num    = SENSOR_TYPE_MAP.get(sensor_type.upper(), 0)

    return np.array([[
        valor, umbral_min, umbral_max, rango,
        distancia_min, distancia_max, pct_rango, sensor_num
    ]])


class AnomalyPredictor:
    """Loads all 3 models and provides prediction with confidence."""

    def __init__(self):
        self.scaler = joblib.load(MODEL_DIR / 'scaler.pkl')
        self.rf     = joblib.load(MODEL_DIR / 'random_forest.pkl')
        self.svm    = joblib.load(MODEL_DIR / 'svm.pkl')
        self.nn     = keras.models.load_model(MODEL_DIR / 'neural_network.keras')

        with open(MODEL_DIR / 'metrics_report.json') as f:
            self.report = json.load(f)

        print("✅ All models loaded successfully")

    def predict(self, valor: float, umbral_min: float,
                umbral_max: float, sensor_type: str) -> dict:
        """
        Predict whether a reading is anomalous.
        Returns prediction from all 3 models + ensemble vote.
        """
        X_raw = build_features(valor, umbral_min, umbral_max, sensor_type)
        X_scaled = self.scaler.transform(X_raw)

        # ── Individual predictions ───────────────────────────────
        rf_prob  = float(self.rf.predict_proba(X_raw)[0][1])
        svm_prob = float(self.svm.predict_proba(X_scaled)[0][1])
        nn_prob  = float(self.nn.predict(X_scaled, verbose=0)[0][0])

        rf_pred  = int(rf_prob  >= 0.5)
        svm_pred = int(svm_prob >= 0.5)
        nn_pred  = int(nn_prob  >= 0.5)

        # ── Ensemble: majority vote ──────────────────────────────
        votes        = rf_pred + svm_pred + nn_pred
        ensemble_pred = 1 if votes >= 2 else 0
        ensemble_conf = round((rf_prob + svm_prob + nn_prob) / 3, 4)

        return {
            'is_anomaly':       bool(ensemble_pred),
            'confidence':       ensemble_conf,
            'severity':         _severity(ensemble_conf),
            'models': {
                'random_forest':   {'prediction': bool(rf_pred),  'probability': round(rf_prob, 4)},
                'svm':             {'prediction': bool(svm_pred), 'probability': round(svm_prob, 4)},
                'neural_network':  {'prediction': bool(nn_pred),  'probability': round(nn_prob, 4)},
            },
            'input': {
                'valor':       valor,
                'umbral_min':  umbral_min,
                'umbral_max':  umbral_max,
                'sensor_type': sensor_type,
            }
        }


def _severity(confidence: float) -> str:
    if confidence >= 0.85:
        return 'CRITICAL'
    elif confidence >= 0.65:
        return 'HIGH'
    elif confidence >= 0.5:
        return 'MEDIUM'
    else:
        return 'LOW'