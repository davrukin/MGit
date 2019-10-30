package me.sheimi.android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import me.sheimi.sgit.R

object NotificationUtils {

	object Channels {

		@JvmStatic
		fun instantiateNotificationChannelForGitOperations(context: Context) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				val channelId = context.getString(R.string.id_git_operations)
				val channelTitle = context.getString(R.string.title_git_operations)
				val channelDescription = context.getString(R.string.message_git_operations)
				val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

				val channel = NotificationChannel(channelId, channelTitle, channelImportance)
				channel.description = channelDescription

				val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
				notificationManager.createNotificationChannel(channel)
			}
		}

	}


}
