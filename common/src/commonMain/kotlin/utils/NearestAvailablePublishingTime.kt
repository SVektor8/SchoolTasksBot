package center.sciprog.tasks_bot.common.utils

import com.soywiz.klock.DateTime
import com.soywiz.klock.minutes

fun nearestAvailableTimerTime() = (DateTime.now() + 1.minutes).copyDayOfMonth(
    milliseconds = 0,
    seconds = 0
)
