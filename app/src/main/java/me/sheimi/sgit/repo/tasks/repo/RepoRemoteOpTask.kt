package me.sheimi.sgit.repo.tasks.repo

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.manichord.mgit.repolist.RepoListActivity
import me.sheimi.android.activities.SheimiFragmentActivity
import me.sheimi.android.utils.Constants
import me.sheimi.sgit.R
import me.sheimi.sgit.SGitApplication
import me.sheimi.sgit.database.models.Repo

/**
 * Super class for Tasks that operate on a git remote
 */

abstract class RepoRemoteOpTask(repo: Repo) : RepoOpTask(repo), SheimiFragmentActivity.OnPasswordEntered {

	abstract val newTask: RepoRemoteOpTask

    protected val placeholderTextTitle = "Performing an indeterminate action"
    protected val placeholderTextInProgress = "Work in progress, please wait"
    protected val placeholderTextComplete = "Work is done"
    protected val placeholderTextAction = "Done"

	override fun onClicked(username: String, password: String, savePassword: Boolean) {
		mRepo.username = username
		mRepo.password = password
		if (savePassword) {
			mRepo.saveCredentials()
		}

		mRepo.removeTask(this)
		newTask.executeTask()
	}

	override fun onCanceled() {

	}

    protected inline fun doWhileShowingIndeterminateNotification(
        textTitle: String = placeholderTextTitle,
        textInProgress: String = placeholderTextInProgress,
        textComplete: String = placeholderTextComplete,
        classToLaunch: Class<*>,
        func: () -> Boolean
    ): Boolean {
        var result = false

        with(SGitApplication.getContext()) {
            val intent = Intent(this, classToLaunch)
            val pendingIntent = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationId = Constants.NOTIFICATION_ID_GIT_OPERATION
            val channelId = getString(R.string.id_git_operations)

            val builder = NotificationCompat.Builder(this, channelId).apply {
                setContentTitle(textTitle)
                setContentText(textInProgress)
                setSmallIcon(android.R.drawable.ic_dialog_info)
                setPriority(NotificationCompat.PRIORITY_DEFAULT)
            }

            NotificationManagerCompat.from(this).apply {
                builder.setProgress(0, 0, true)
                cancel(notificationId)
                notify(notificationId, builder.build())

                result = func.invoke()

                builder.addAction(android.R.drawable.ic_dialog_alert, placeholderTextAction, pendingIntent)
                builder.setContentText(textComplete)
                builder.setProgress(0, 0, false)
                notify(notificationId, builder.build())
            }
        }

        return result
    }
}
