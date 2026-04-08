import pandas as pd
import joblib
from datetime import datetime, timedelta

class SaveMachine:
    def __init__(self):
        self.maquinas = {}
        try:
            self.modelo_ia = joblib.load('modelo_savemachine.pkl')
        except FileNotFoundError:
            self.modelo_ia = None

    def adicionar_maquina(self, id_maquina, modelo):
        self.maquinas[id_maquina] = {
            "modelo": modelo,
            "data_proxima_manutencao": None,
            "status_ia": "Sem previsão"
        }
        print(f"Máquina {id_maquina} adicionada.")

    def definir_tempo(self, id_maquina, dias):
        if id_maquina in self.maquinas:
            data_calculada = datetime.now() + timedelta(days=dias)
            self.maquinas[id_maquina]["data_proxima_manutencao"] = data_calculada
            print(f"Tempo definido para a máquina {id_maquina}.")
        else:
            print("Máquina não encontrada.")

    def prever_falha(self, id_maquina, t_ar, t_proc, rot, torq, desg):
        if not self.modelo_ia:
            print("Modelo de IA não encontrado. Treine o modelo primeiro.")
            return
        
        if id_maquina not in self.maquinas:
            print("Máquina não encontrada.")
            return

        dados_entrada = pd.DataFrame({
            'temperatura_ar': [t_ar],
            'temperatura_processo': [t_proc],
            'rotacao_rpm': [rot],
            'torque': [torq],
            'desgaste_ferramenta': [desg]
        })

        probabilidade = self.modelo_ia.predict_proba(dados_entrada)[0][1]

        if probabilidade >= 0.8:
            self.maquinas[id_maquina]["status_ia"] = "Risco Crítico"
            self.maquinas[id_maquina]["data_proxima_manutencao"] = datetime.now()
            print(f"ALERTA: Risco crítico de falha ({probabilidade:.0%}). Manutenção imediata.")
        elif probabilidade >= 0.6:
            self.maquinas[id_maquina]["status_ia"] = "Risco Alto"
            self.maquinas[id_maquina]["data_proxima_manutencao"] = datetime.now() + timedelta(days=2)
            print(f"ALERTA: Risco alto de falha ({probabilidade:.0%}). Fazer manutenção nos próximos 2 dias.")
        elif probabilidade >= 0.4:
            self.maquinas[id_maquina]["status_ia"] = "Risco Moderado"
            self.maquinas[id_maquina]["data_proxima_manutencao"] = datetime.now() + timedelta(days=5)
            print(f"Aviso: Risco moderado de falha ({probabilidade:.0%}). Fazer manutenção nos próximos 5 dias.")
        else:
            self.maquinas[id_maquina]["status_ia"] = "Operação Normal"
            print(f"Máquina {id_maquina} operando normalmente (Risco: {probabilidade:.0%}).")

    def consultar(self):
        agendadas = {k: v for k, v in self.maquinas.items() if v["data_proxima_manutencao"] is not None}
        ordenadas = sorted(agendadas.items(), key=lambda x: x[1]["data_proxima_manutencao"])
        
        print("\nPróximas Manutenções:")
        for id_maq, dados in ordenadas:
            data_formatada = dados["data_proxima_manutencao"].strftime("%d/%m/%Y")
            print(f"ID: {id_maq} | Modelo: {dados['modelo']} | Data Prevista: {data_formatada} | Status: {dados['status_ia']}")
        print()

def iniciar_terminal():
    sistema = SaveMachine()
    
    while True:
        print("\n--- SaveMachine ---")
        print("1. Adicionar Máquina")
        print("2. Definir Tempo de Manutenção (Manual)")
        print("3. Consultar Máquinas Próximas da Manutenção")
        print("4. Prever Falha com IA (Simular Sensores)")
        print("5. Sair")
        
        opcao = input("Escolha uma opção: ")
        
        if opcao == "1":
            id_maq = input("Identificador da Máquina: ")
            modelo = input("Modelo do Maquinário: ")
            sistema.adicionar_maquina(id_maq, modelo)
        elif opcao == "2":
            id_maq = input("Identificador da Máquina: ")
            dias = int(input("Quantidade de dias até a manutenção: "))
            sistema.definir_tempo(id_maq, dias)
        elif opcao == "3":
            sistema.consultar()
        elif opcao == "4":
            id_maq = input("Identificador da Máquina: ")
            t_ar = float(input("Temperatura do ar (K): "))
            t_proc = float(input("Temperatura do processo (K): "))
            rot = float(input("Rotação (RPM): "))
            torq = float(input("Torque (Nm): "))
            desg = float(input("Desgaste da ferramenta (minutos): "))
            sistema.prever_falha(id_maq, t_ar, t_proc, rot, torq, desg)
        elif opcao == "5":
            break
        else:
            print("Opção inválida.")

if __name__ == "__main__":
    iniciar_terminal()