package ao.bedi.cronosestudo.model

data class Discipline(val id:String,val name:String,val color:String="#4F46E5",val description:String="",val targetWeeklyHours:Int=4,val archived:Boolean=false)
data class ScheduleSlot(val id:String,val disciplineId:String,val day:Int,val startTime:String,val endTime:String,val topic:String="",val alarmEnabled:Boolean=true)
data class StudyLog(val id:String,val date:String,val slotId:String?,val disciplineId:String,val status:String,val minutesStudied:Int,val notes:String="",val missedReason:String="",val timestamp:Long=System.currentTimeMillis())
data class AlarmSettings(val soundEnabled:Boolean=true,val notifications:Boolean=true,val soundType:String="chime",val volume:Int=80,val snoozeMinutes:Int=5,val vibration:Boolean=true)
data class TimerState(val disciplineId:String="",val slotId:String?=null,val startedAt:Long=0,val accumulatedMs:Long=0,val running:Boolean=false)
