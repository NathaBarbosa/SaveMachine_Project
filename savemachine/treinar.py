import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from imblearn.over_sampling import SMOTE
import joblib

def treinar_modelo(caminho_dados):
    dados = pd.read_csv(caminho_dados)
    
    X = dados.drop('falha', axis=1)
    y = dados['falha']
    
    X_treino, X_teste, y_treino, y_teste = train_test_split(X, y, test_size=0.2, random_state=42)
    
    smote = SMOTE(random_state=42)
    X_treino_balanceado, y_treino_balanceado = smote.fit_resample(X_treino, y_treino)
    
    modelo = RandomForestClassifier(n_estimators=100, random_state=42)
    modelo.fit(X_treino_balanceado, y_treino_balanceado)
    
    previsoes = modelo.predict(X_teste)
    precisao = accuracy_score(y_teste, previsoes)
    
    print(f"Precisão (Accuracy) geral: {precisao:.4f}\n")
    print("Matriz de Confusão:")
    print(confusion_matrix(y_teste, previsoes))
    print("\nRelatório de Classificação Detalhado:")
    print(classification_report(y_teste, previsoes))
    
    joblib.dump(modelo, 'modelo_savemachine.pkl')
    
    return modelo

if __name__ == "__main__":
    treinar_modelo('dados_maquinas.csv')