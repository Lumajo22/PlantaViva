# ════════════════════════════════════════════════════════════════
#   PlantaViva ML — Dataset Generator
#   Generates realistic IoT sensor readings for training
#   Sensors: TEMPERATURA (°C), HUMEDAD (%), LUZ (lux)
# ════════════════════════════════════════════════════════════════

import numpy as np
import pandas as pd
from pathlib import Path

np.random.seed(42)

# ─── Sensor configurations (mirrors DB umbral values) ───────────
SENSORS = {
    'TEMPERATURA': {
        'normal_mean': 22.0,
        'normal_std':  3.0,
        'umbral_min':  15.0,
        'umbral_max':  30.0,
        'unit': '°C'
    },
    'HUMEDAD': {
        'normal_mean': 60.0,
        'normal_std':  8.0,
        'umbral_min':  40.0,
        'umbral_max':  85.0,
        'unit': '%'
    },
    'LUZ': {
        'normal_mean': 2500.0,
        'normal_std':  600.0,
        'umbral_min':  500.0,
        'umbral_max':  5000.0,
        'unit': 'lux'
    }
}

def generate_readings(sensor_type: str, n_normal: int, n_anomaly: int) -> pd.DataFrame:
    """Generate normal and anomalous readings for a sensor type."""
    cfg = SENSORS[sensor_type]
    rows = []

    # ── Normal readings (within thresholds) ─────────────────────
    for _ in range(n_normal):
        value = np.random.normal(cfg['normal_mean'], cfg['normal_std'])
        # Clip to stay inside thresholds
        value = np.clip(value, cfg['umbral_min'] + 0.5, cfg['umbral_max'] - 0.5)
        rows.append({
            'sensor_type':  sensor_type,
            'valor':        round(value, 2),
            'umbral_min':   cfg['umbral_min'],
            'umbral_max':   cfg['umbral_max'],
            'rango':        cfg['umbral_max'] - cfg['umbral_min'],
            'distancia_min': round(value - cfg['umbral_min'], 2),
            'distancia_max': round(cfg['umbral_max'] - value, 2),
            'pct_rango':    round((value - cfg['umbral_min']) /
                                  (cfg['umbral_max'] - cfg['umbral_min']), 4),
            'es_anomalia':  0
        })

    # ── Anomalous readings (outside thresholds) ──────────────────
    for _ in range(n_anomaly):
        # Alternate between too-low and too-high anomalies
        if np.random.random() < 0.5:
            # Below min
            value = cfg['umbral_min'] - abs(np.random.normal(0, cfg['normal_std']))
        else:
            # Above max
            value = cfg['umbral_max'] + abs(np.random.normal(0, cfg['normal_std']))

        value = round(value, 2)
        rows.append({
            'sensor_type':  sensor_type,
            'valor':        value,
            'umbral_min':   cfg['umbral_min'],
            'umbral_max':   cfg['umbral_max'],
            'rango':        cfg['umbral_max'] - cfg['umbral_min'],
            'distancia_min': round(value - cfg['umbral_min'], 2),
            'distancia_max': round(cfg['umbral_max'] - value, 2),
            'pct_rango':    round((value - cfg['umbral_min']) /
                                  (cfg['umbral_max'] - cfg['umbral_min']), 4),
            'es_anomalia':  1
        })

    return pd.DataFrame(rows)


def main():
    print("🌿 PlantaViva — Generating IoT sensor dataset...")
    all_dfs = []

    # Generate ~2000 normal, ~500 anomaly per sensor type
    for sensor_type in SENSORS:
        df = generate_readings(sensor_type, n_normal=2000, n_anomaly=500)
        all_dfs.append(df)
        print(f"  ✅ {sensor_type}: {len(df)} readings "
              f"({df['es_anomalia'].sum()} anomalies)")

    # Combine and shuffle
    dataset = pd.concat(all_dfs, ignore_index=True)
    dataset = dataset.sample(frac=1, random_state=42).reset_index(drop=True)

    # Add sensor type as numeric (for models that need it)
    dataset['sensor_type_num'] = dataset['sensor_type'].map(
        {'TEMPERATURA': 0, 'HUMEDAD': 1, 'LUZ': 2}
    )

    # Save
    out_path = Path(__file__).parent / 'sensor_readings.csv'
    dataset.to_csv(out_path, index=False)

    print(f"\n📊 Dataset summary:")
    print(f"   Total rows   : {len(dataset)}")
    print(f"   Normal (0)   : {(dataset['es_anomalia'] == 0).sum()}")
    print(f"   Anomaly (1)  : {(dataset['es_anomalia'] == 1).sum()}")
    print(f"   Class ratio  : {(dataset['es_anomalia'] == 0).sum() / len(dataset):.1%} normal")
    print(f"\n✅ Saved to: {out_path}")


if __name__ == '__main__':
    main()