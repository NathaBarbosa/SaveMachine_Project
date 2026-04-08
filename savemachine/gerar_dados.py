import pandas as pd
import numpy as np

quantidade_registros = 1000
np.random.seed(42)

dados_simulados = {
    'temperatura_media': np.random.normal(70, 15, quantidade_registros),
    'nivel_vibracao': np.random.normal(10, 3, quantidade_registros),
    'horas_trabalhadas': np.random.randint(100, 8000, quantidade_registros),
    'falha': np.random.choice([0, 1], quantidade_registros, p=[0.8, 0.2])
}

tabela = pd.DataFrame(dados_simulados)
tabela.to_csv('dados_maquinas.csv', index=False)