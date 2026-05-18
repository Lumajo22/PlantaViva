# ════════════════════════════════════════════════════════════════
#   PlantaViva ML — Model Training Script
#   Models: Random Forest | SVM | Neural Network (TensorFlow)
#   Split:  70% train / 15% validation / 15% test
#   Balancing: SMOTE oversampling
# ════════════════════════════════════════════════════════════════

import numpy as np
import pandas as pd
import joblib
import json
import os
from pathlib import Path

# Scikit-learn
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import (classification_report, confusion_matrix,
                             precision_score, recall_score, f1_score,
                             accuracy_score, roc_auc_score)

# Imbalanced-learn
from imblearn.over_sampling import SMOTE

# TensorFlow
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers

# ─── Paths ──────────────────────────────────────────────────────
BASE_DIR   = Path(__file__).parent
DATA_PATH  = BASE_DIR / 'data' / 'sensor_readings.csv'
MODEL_DIR  = BASE_DIR / 'models'
MODEL_DIR.mkdir(exist_ok=True)

# ─── Features used for training ─────────────────────────────────
FEATURES = [
    'valor',
    'umbral_min',
    'umbral_max',
    'rango',
    'distancia_min',
    'distancia_max',
    'pct_rango',
    'sensor_type_num'
]
TARGET = 'es_anomalia'


def load_and_split(path: Path):
    """Load dataset and split 70 / 15 / 15."""
    print("📂 Loading dataset...")
    df = pd.read_csv(path)
    print(f"   Rows: {len(df)} | Anomalies: {df[TARGET].sum()}")

    X = df[FEATURES].values
    y = df[TARGET].values

    # First split: 85% temp, 15% test
    X_temp, X_test, y_temp, y_test = train_test_split(
        X, y, test_size=0.15, random_state=42, stratify=y
    )

    # Second split: 70% train, 15% validation (from the 85%)
    val_ratio = 0.15 / 0.85
    X_train, X_val, y_train, y_val = train_test_split(
        X_temp, y_temp, test_size=val_ratio, random_state=42, stratify=y_temp
    )

    print(f"\n📊 Split 70 / 15 / 15:")
    print(f"   Train : {len(X_train)} rows ({y_train.sum()} anomalies)")
    print(f"   Val   : {len(X_val)}   rows ({y_val.sum()} anomalies)")
    print(f"   Test  : {len(X_test)}  rows ({y_test.sum()} anomalies)")

    return X_train, X_val, X_test, y_train, y_val, y_test


def apply_smote(X_train, y_train):
    """Balance classes with SMOTE oversampling."""
    print("\n⚖️  Applying SMOTE class balancing...")
    before = dict(zip(*np.unique(y_train, return_counts=True)))
    sm = SMOTE(random_state=42)
    X_res, y_res = sm.fit_resample(X_train, y_train)
    after  = dict(zip(*np.unique(y_res, return_counts=True)))
    print(f"   Before: {before}")
    print(f"   After : {after}")
    return X_res, y_res


def get_metrics(name, y_true, y_pred, y_prob=None):
    """Compute and return standard ML metrics."""
    metrics = {
        'model':     name,
        'accuracy':  round(accuracy_score(y_true, y_pred), 4),
        'precision': round(precision_score(y_true, y_pred, zero_division=0), 4),
        'recall':    round(recall_score(y_true, y_pred, zero_division=0), 4),
        'f1_score':  round(f1_score(y_true, y_pred, zero_division=0), 4),
    }
    if y_prob is not None:
        metrics['roc_auc'] = round(roc_auc_score(y_true, y_prob), 4)
    return metrics


def print_metrics(metrics: dict):
    print(f"   Accuracy : {metrics['accuracy']:.4f}")
    print(f"   Precision: {metrics['precision']:.4f}")
    print(f"   Recall   : {metrics['recall']:.4f}")
    print(f"   F1-Score : {metrics['f1_score']:.4f}")
    if 'roc_auc' in metrics:
        print(f"   ROC-AUC  : {metrics['roc_auc']:.4f}")


# ════════════════════════════════════════════════════════════════
#   MODEL 1 — Random Forest
# ════════════════════════════════════════════════════════════════
def train_random_forest(X_train, y_train, X_val, y_val):
    print("\n" + "═"*50)
    print("🌲 MODEL 1: Random Forest")
    print("═"*50)

    rf = RandomForestClassifier(
        n_estimators=200,
        max_depth=10,
        min_samples_split=5,
        class_weight='balanced',
        random_state=42,
        n_jobs=-1
    )
    rf.fit(X_train, y_train)

    y_pred = rf.predict(X_val)
    y_prob = rf.predict_proba(X_val)[:, 1]
    metrics = get_metrics('RandomForest', y_val, y_pred, y_prob)
    print_metrics(metrics)

    # Feature importance
    importances = dict(zip(FEATURES, rf.feature_importances_.round(4)))
    print(f"\n   Top features: {sorted(importances.items(), key=lambda x: -x[1])[:3]}")

    # Save
    joblib.dump(rf, MODEL_DIR / 'random_forest.pkl')
    print(f"   ✅ Saved: models/random_forest.pkl")
    return metrics


# ════════════════════════════════════════════════════════════════
#   MODEL 2 — SVM (Support Vector Machine)
# ════════════════════════════════════════════════════════════════
def train_svm(X_train, y_train, X_val, y_val, scaler):
    print("\n" + "═"*50)
    print("🔷 MODEL 2: Support Vector Machine (SVM)")
    print("═"*50)

    # SVM needs scaled features
    X_train_s = scaler.transform(X_train)
    X_val_s   = scaler.transform(X_val)

    svm = SVC(
        kernel='rbf',
        C=10,
        gamma='scale',
        probability=True,
        class_weight='balanced',
        random_state=42
    )
    svm.fit(X_train_s, y_train)

    y_pred = svm.predict(X_val_s)
    y_prob = svm.predict_proba(X_val_s)[:, 1]
    metrics = get_metrics('SVM', y_val, y_pred, y_prob)
    print_metrics(metrics)

    # Save
    joblib.dump(svm, MODEL_DIR / 'svm.pkl')
    print(f"   ✅ Saved: models/svm.pkl")
    return metrics


# ════════════════════════════════════════════════════════════════
#   MODEL 3 — Neural Network (TensorFlow / Keras)
# ════════════════════════════════════════════════════════════════
def train_neural_network(X_train, y_train, X_val, y_val, scaler):
    print("\n" + "═"*50)
    print("🧠 MODEL 3: Neural Network (TensorFlow/Keras)")
    print("═"*50)

    X_train_s = scaler.transform(X_train)
    X_val_s   = scaler.transform(X_val)

    n_features = X_train_s.shape[1]

    # ── Architecture ─────────────────────────────────────────────
    model = keras.Sequential([
        layers.Input(shape=(n_features,)),
        layers.Dense(64, activation='relu'),
        layers.BatchNormalization(),
        layers.Dropout(0.3),
        layers.Dense(32, activation='relu'),
        layers.BatchNormalization(),
        layers.Dropout(0.2),
        layers.Dense(16, activation='relu'),
        layers.Dense(1, activation='sigmoid')
    ], name='PlantaVivaAnomalyNet')

    model.compile(
        optimizer=keras.optimizers.Adam(learning_rate=0.001),
        loss='binary_crossentropy',
        metrics=['accuracy',
                 keras.metrics.Precision(name='precision'),
                 keras.metrics.Recall(name='recall')]
    )

    print(f"   Architecture: {n_features} → 64 → 32 → 16 → 1")
    print(f"   Parameters  : {model.count_params():,}")

    # ── Callbacks ─────────────────────────────────────────────────
    callbacks = [
        keras.callbacks.EarlyStopping(
            monitor='val_loss', patience=10,
            restore_best_weights=True, verbose=0
        ),
        keras.callbacks.ReduceLROnPlateau(
            monitor='val_loss', factor=0.5,
            patience=5, verbose=0
        )
    ]

    # Class weights for imbalance
    pos = np.sum(y_train == 1)
    neg = np.sum(y_train == 0)
    class_weight = {0: 1.0, 1: neg / pos}

    history = model.fit(
        X_train_s, y_train,
        validation_data=(X_val_s, y_val),
        epochs=100,
        batch_size=32,
        class_weight=class_weight,
        callbacks=callbacks,
        verbose=0
    )

    epochs_run = len(history.history['loss'])
    print(f"   Trained for : {epochs_run} epochs (early stopping)")

    # Metrics
    y_prob = model.predict(X_val_s, verbose=0).flatten()
    y_pred = (y_prob >= 0.5).astype(int)
    metrics = get_metrics('NeuralNetwork', y_val, y_pred, y_prob)
    print_metrics(metrics)

    # Save
    model.save(MODEL_DIR / 'neural_network.keras')
    print(f"   ✅ Saved: models/neural_network.keras")
    return metrics


# ════════════════════════════════════════════════════════════════
#   FINAL EVALUATION ON TEST SET
# ════════════════════════════════════════════════════════════════
def evaluate_on_test(X_test, y_test, scaler):
    print("\n" + "═"*50)
    print("🧪 FINAL EVALUATION ON TEST SET (15%)")
    print("═"*50)

    X_test_s = scaler.transform(X_test)
    all_metrics = []

    # Random Forest
    rf = joblib.load(MODEL_DIR / 'random_forest.pkl')
    y_pred_rf = rf.predict(X_test)
    y_prob_rf  = rf.predict_proba(X_test)[:, 1]
    m = get_metrics('RandomForest', y_test, y_pred_rf, y_prob_rf)
    all_metrics.append(m)
    print(f"\n🌲 Random Forest  → F1: {m['f1_score']} | AUC: {m.get('roc_auc','N/A')}")

    # SVM
    svm = joblib.load(MODEL_DIR / 'svm.pkl')
    y_pred_svm = svm.predict(X_test_s)
    y_prob_svm  = svm.predict_proba(X_test_s)[:, 1]
    m = get_metrics('SVM', y_test, y_pred_svm, y_prob_svm)
    all_metrics.append(m)
    print(f"🔷 SVM            → F1: {m['f1_score']} | AUC: {m.get('roc_auc','N/A')}")

    # Neural Network
    nn = keras.models.load_model(MODEL_DIR / 'neural_network.keras')
    y_prob_nn  = nn.predict(X_test_s, verbose=0).flatten()
    y_pred_nn  = (y_prob_nn >= 0.5).astype(int)
    m = get_metrics('NeuralNetwork', y_test, y_pred_nn, y_prob_nn)
    all_metrics.append(m)
    print(f"🧠 Neural Network → F1: {m['f1_score']} | AUC: {m.get('roc_auc','N/A')}")

    # Best model
    best = max(all_metrics, key=lambda x: x['f1_score'])
    print(f"\n🏆 Best model: {best['model']} (F1={best['f1_score']})")

    # Save metrics report
    report = {
        'split': {'train': '70%', 'validation': '15%', 'test': '15%'},
        'balancing': 'SMOTE',
        'features': FEATURES,
        'models': all_metrics,
        'best_model': best['model']
    }
    report_path = MODEL_DIR / 'metrics_report.json'
    with open(report_path, 'w') as f:
        json.dump(report, f, indent=2)
    print(f"\n📄 Metrics report saved: models/metrics_report.json")
    return all_metrics


# ════════════════════════════════════════════════════════════════
#   MAIN
# ════════════════════════════════════════════════════════════════
def main():
    print("🌿 PlantaViva — ML Model Training")
    print("   Models: Random Forest | SVM | Neural Network")
    print("   Split : 70% train / 15% val / 15% test")
    print("   Tool  : SMOTE class balancing\n")

    # 1. Load & split
    X_train, X_val, X_test, y_train, y_val, y_test = load_and_split(DATA_PATH)

    # 2. SMOTE balancing on train only
    X_train_bal, y_train_bal = apply_smote(X_train, y_train)

    # 3. Scaler (fit on balanced train, transform others)
    scaler = StandardScaler()
    scaler.fit(X_train_bal)
    joblib.dump(scaler, MODEL_DIR / 'scaler.pkl')
    print(f"\n⚙️  Scaler saved: models/scaler.pkl")

    # 4. Train all 3 models
    train_random_forest(X_train_bal, y_train_bal, X_val, y_val)
    train_svm(X_train_bal, y_train_bal, X_val, y_val, scaler)
    train_neural_network(X_train_bal, y_train_bal, X_val, y_val, scaler)

    # 5. Final evaluation on untouched test set
    evaluate_on_test(X_test, y_test, scaler)

    print("\n✅ Training complete! All models saved in models/")
    print("   Run app.py to start the Flask prediction API.")


if __name__ == '__main__':
    main()