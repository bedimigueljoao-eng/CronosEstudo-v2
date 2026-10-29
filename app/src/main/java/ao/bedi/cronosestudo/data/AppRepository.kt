package ao.bedi.cronosestudo.data
import android.content.Context
import ao.bedi.cronosestudo.model.*
import org.json.*
class AppRepository(private val c:Context){private val p=c.getSharedPreferences("cronos_v2",0)
 fun disciplines()=read("disciplines") { o->Discipline(o.getString("id"),o.getString("name"),o.optString("color","#4F46E5"),o.optString("description"),o.optInt("target",4),o.optBoolean("archived")) }
 fun saveDisciplines(x:List<Discipline>)=write("disciplines",x){d->JSONObject().put("id",d.id).put("name",d.name).put("color",d.color).put("description",d.description).put("target",d.targetWeeklyHours).put("archived",d.archived)}
 fun slots()=read("slots"){o->ScheduleSlot(o.getString("id"),o.getString("disciplineId"),o.getInt("day"),o.getString("start"),o.getString("end"),o.optString("topic"),o.optBoolean("alarm",true))}
 fun saveSlots(x:List<ScheduleSlot>)=write("slots",x){s->JSONObject().put("id",s.id).put("disciplineId",s.disciplineId).put("day",s.day).put("start",s.startTime).put("end",s.endTime).put("topic",s.topic).put("alarm",s.alarmEnabled)}
 fun logs()=read("logs"){o->StudyLog(o.getString("id"),o.getString("date"),o.optString("slotId").ifBlank{null},o.getString("disciplineId"),o.getString("status"),o.getInt("minutes"),o.optString("notes"),o.optString("reason"),o.optLong("timestamp"))}
 fun saveLogs(x:List<StudyLog>)=write("logs",x){l->JSONObject().put("id",l.id).put("date",l.date).put("slotId",l.slotId?:"").put("disciplineId",l.disciplineId).put("status",l.status).put("minutes",l.minutesStudied).put("notes",l.notes).put("reason",l.missedReason).put("timestamp",l.timestamp)}
 private fun <T> read(k:String,f:(JSONObject)->T):List<T>{val a=JSONArray(p.getString(k,"[]"));return List(a.length()){f(a.getJSONObject(it))}}
 private fun <T> write(k:String,x:List<T>,f:(T)->JSONObject){val a=JSONArray();x.forEach{a.put(f(it))};p.edit().putString(k,a.toString()).apply()}
}
