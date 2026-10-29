package ao.bedi.cronosestudo
import android.Manifest
import android.content.*
import android.os.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ao.bedi.cronosestudo.data.AppRepository
import ao.bedi.cronosestudo.model.*
import ao.bedi.cronosestudo.service.StudyTimerService
class MainActivity:ComponentActivity(){private val ask=registerForActivityResult(ActivityResultContracts.RequestPermission()){};override fun onCreate(b:Bundle?){super.onCreate(b);if(Build.VERSION.SDK_INT>=33)ask.launch(Manifest.permission.POST_NOTIFICATIONS);setContent{App(this)}}}
@Composable fun App(c:Context){val repo=remember{AppRepository(c)};var disc by remember{mutableStateOf(repo.disciplines())};var slots by remember{mutableStateOf(repo.slots())};var logs by remember{mutableStateOf(repo.logs())};var tab by remember{mutableIntStateOf(0)};var addDisc by remember{mutableStateOf(false)};var addSlot by remember{mutableStateOf(false)}
 MaterialTheme{Scaffold(bottomBar={NavigationBar{listOf("Agenda" to Icons.Default.CalendarMonth,"Disciplinas" to Icons.Default.MenuBook,"Cronómetro" to Icons.Default.Timer,"Análise" to Icons.Default.BarChart,"Definições" to Icons.Default.Settings).forEachIndexed{i,v->NavigationBarItem(tab==i,{tab=i},{Icon(v.second,null)},{Text(v.first,fontSize=9.sp)})}}},floatingActionButton={if(tab==0||tab==1)FloatingActionButton({if(tab==1)addDisc=true else addSlot=true}){Icon(Icons.Default.Add,null)}}){p->Box(Modifier.padding(p)){when(tab){0->Agenda(disc,slots){addSlot=true};1->Disciplines(disc,slots,{addDisc=true}){d->disc=disc.filterNot{it.id==d.id};repo.saveDisciplines(disc)};2->Timer(c,disc);3->Analytics(disc,logs);else->Settings()}}};if(addDisc)DisciplineDialog({addDisc=false}){d->disc=disc+d;repo.saveDisciplines(disc);addDisc=false};if(addSlot)SlotDialog(disc,{addSlot=false}){s->slots=slots+s;repo.saveSlots(slots);addSlot=false}}
}
@Composable fun Title(t:String){Text(t,fontSize=28.sp,fontWeight=FontWeight.Bold,modifier=Modifier.fillMaxWidth().background(Color.White).padding(20.dp))}
@Composable fun Agenda(d:List<Discipline>,s:List<ScheduleSlot>,add:()->Unit){Column{Title("Agenda semanal");LazyColumn{items((1..7).toList()){day->Text(listOf("Segunda","Terça","Quarta","Quinta","Sexta","Sábado","Domingo")[day-1],Modifier.padding(14.dp),fontWeight=FontWeight.Bold);s.filter{it.day==day}.sortedBy{it.startTime}.forEach{slot->val name=d.find{it.id==slot.disciplineId}?.name?:"Disciplina";Card(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=4.dp)){Column(Modifier.padding(14.dp)){Text(name,fontWeight=FontWeight.Bold);Text("${slot.startTime} - ${slot.endTime}");if(slot.topic.isNotBlank())Text(slot.topic,color=Color.Gray)}}}};item{Button(add,Modifier.fillMaxWidth().padding(16.dp)){Text("Adicionar horário")}}}}}
@Composable fun Disciplines(d:List<Discipline>,s:List<ScheduleSlot>,add:()->Unit,del:(Discipline)->Unit){Column{Title("Disciplinas e metas");LazyColumn{items(d){x->Card(Modifier.fillMaxWidth().padding(10.dp)){Column(Modifier.padding(16.dp)){Text(x.name,fontWeight=FontWeight.Bold);Text("Meta: ${x.targetWeeklyHours}h/semana");Text("${s.count{it.disciplineId==x.id}} horários");Row{TextButton(add){Text("Adicionar horário")};TextButton({del(x)}){Text("Arquivar",color=Color.Red)}}}}};item{Button(add,Modifier.fillMaxWidth().padding(16.dp)){Text("Nova disciplina")}}}}}
@Composable fun Timer(c:Context,d:List<Discipline>){var name by remember{mutableStateOf("")};Column(Modifier.padding(20.dp)){Title("Cronómetro persistente");OutlinedTextField(name,{name=it},label={Text("Disciplina")},modifier=Modifier.fillMaxWidth());Button({c.startForegroundService(Intent(c,StudyTimerService::class.java).setAction("START").putExtra("discipline",name.ifBlank{"Estudo"}))},Modifier.fillMaxWidth().padding(top=12.dp)){Text("Iniciar")};OutlinedButton({c.startService(Intent(c,StudyTimerService::class.java).setAction("PAUSE"))},Modifier.fillMaxWidth()){Text("Pausar")};OutlinedButton({c.startService(Intent(c,StudyTimerService::class.java).setAction("RESUME"))},Modifier.fillMaxWidth()){Text("Continuar")};OutlinedButton({c.startService(Intent(c,StudyTimerService::class.java).setAction("STOP"))},Modifier.fillMaxWidth()){Text("Terminar")};Text("O cronómetro continua ao minimizar ou sair da aplicação.",Modifier.padding(top=12.dp),color=Color.Gray)}}
@Composable fun Analytics(d:List<Discipline>,l:List<StudyLog>){Column{Title("Análise");val done=l.filter{it.status=="completed"};Text("Total estudado: ${done.sumOf{it.minutesStudied}} min",Modifier.padding(16.dp),fontSize=20.sp,fontWeight=FontWeight.Bold);Text("Sessões concluídas: ${done.size}",Modifier.padding(horizontal=16.dp));Text("Não estudadas: ${l.count{it.status=="missed"}}",Modifier.padding(16.dp))}}
@Composable fun Settings(){Column{Title("Definições");Text("Som, volume, adiamento, backup e restauração serão armazenados separadamente.",Modifier.padding(20.dp))}}
@Composable fun DisciplineDialog(close:()->Unit,save:(Discipline)->Unit){var n by remember{mutableStateOf("")};var h by remember{mutableStateOf("4")};AlertDialog(onDismissRequest=close,title={Text("Nova disciplina")},text={Column{OutlinedTextField(n,{n=it},label={Text("Nome")});OutlinedTextField(h,{h=it.filter(Char::isDigit)},label={Text("Meta semanal")})}},confirmButton={Button({if(n.isNotBlank())save(Discipline("disc-${System.currentTimeMillis()}",n,targetWeeklyHours=h.toIntOrNull()?:4))}){Text("Criar")}},dismissButton={TextButton(close){Text("Cancelar")}})}
@Composable fun SlotDialog(d:List<Discipline>,close:()->Unit,save:(ScheduleSlot)->Unit){var id by remember{mutableStateOf(d.firstOrNull()?.id?:"")};var day by remember{mutableStateOf("1")};var st by remember{mutableStateOf("09:00")};var en by remember{mutableStateOf("10:00")};AlertDialog(onDismissRequest=close,title={Text("Novo horário")},text={Column{Text("Disciplina: ${d.find{it.id==id}?.name?:"Crie uma disciplina primeiro"}");OutlinedTextField(day,{day=it.filter(Char::isDigit)},label={Text("Dia 1-7")});OutlinedTextField(st,{st=it},label={Text("Início")});OutlinedTextField(en,{en=it},label={Text("Fim")})}},confirmButton={Button({if(id.isNotBlank())save(ScheduleSlot("slot-${System.currentTimeMillis()}",id,(day.toIntOrNull()?:1).coerceIn(1,7),st,en))}){Text("Adicionar")}},dismissButton={TextButton(close){Text("Cancelar")}})}
