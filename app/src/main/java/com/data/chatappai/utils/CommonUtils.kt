package com.data.chatappai.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CommonUtils {

    companion object {
        object DateUtils {
            fun formatTimestamp(timestamp: Long): String {
                // Format for today: just the time
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                // Format for non-today dates: shortest format
                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

                val date = Date(timestamp)
                return if (isToday(date)) {
                    timeFormat.format(date) // Show time if it's today
                } else {
                    dateFormat.format(date) // Show date if it's not today
                }
            }

            private fun isToday(date: Date): Boolean {
                val calendar = Calendar.getInstance()
                val today = Calendar.getInstance()

                calendar.time = date

                return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            }
        }

    }


}