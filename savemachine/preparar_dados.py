import pandas as pd

url = "https://archive.ics.uci.edu/ml/machine-learning-databases/00601/ai4i2020.csv"
dados = pd.read_csv(url)

dados = dados.rename(columns={
    'Air temperature [K]': 'temperatura_ar',
    'Process temperature [K]': 'temperatura_processo',
    'Rotational speed [rpm]': 'rotacao_rpm',
    'Torque [Nm]': 'torque',
    'Tool wear [min]': 'desgaste_ferramenta',
    'Machine failure': 'falha'
})

colunas_selecionadas = [
    'temperatura_ar',
    'temperatura_processo',
    'rotacao_rpm',
    'torque',
    'desgaste_ferramenta',
    'falha'
]

dados_finais = dados[colunas_selecionadas]
dados_finais.to_csv('dados_maquinas.csv', index=False)
print("Dados reais baixados e salvos como dados_maquinas.csv")