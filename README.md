# CronosEstudo v2.0.0

Estrutura Android nativa reconstruída com separação explícita entre:

- `Discipline`: cadastro, cor, descrição, meta semanal e arquivo.
- `ScheduleSlot`: programação independente por dia, hora, tópico e alarme.
- `StudyLog`: histórico real, concluído ou não realizado.
- `AlarmSettings`: som, volume, vibração e adiamento.
- `TimerState`: cronómetro persistente.

## Módulos

- `model/Models.kt`: entidades do domínio.
- `data/AppRepository.kt`: persistência local separada, sem dados de exemplo.
- `service/Alarm.kt`: alarme em ecrã inteiro.
- `service/StudyTimerService.kt`: cronómetro em serviço de primeiro plano.
- `MainActivity.kt`: navegação Agenda, Disciplinas, Cronómetro, Análise e Definições.

## Regras

- A aplicação inicia vazia.
- Uma disciplina pode existir sem horários.
- Cada horário tem ID próprio.
- Adicionar um horário não substitui outro.
- O cronómetro continua ao minimizar ou sair da aplicação.
- O workflow gera `CronosEstudo-v2.0.0-APK`.
