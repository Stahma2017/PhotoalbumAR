package com.example.photoalbum.app.base

import android.content.Context
import androidx.annotation.StringRes
import com.example.photoalbum.App
import com.example.photoalbum.R
import com.example.photoalbum.ext.ContextAware
import com.example.photoalbum.server.base.HttpCodeHandler
import com.example.photoalbum.server.base.HttpResponseCode
import com.example.photoalbum.server.base.RequestErrorHandler
import com.example.photoalbum.server.base.RequestResult
import com.example.photoalbum.unclassified.CompositeJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.dmdev.rxpm.PresentationModel
import org.greenrobot.eventbus.EventBus
import org.kodein.di.KodeinAware
import kotlin.coroutines.CoroutineContext

open class BasePresentationModel : PresentationModel(), ContextAware,
    RequestErrorHandler, KodeinAware, CoroutineScope {
    private val compositeJob = CompositeJob()
    protected val eventBus: EventBus = EventBus.getDefault()
    override val kodein by lazy { App.appInstance.kodein }
    private val errorHandler = BaseErrorHandler()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    val showMsgByIdCommand = Command<Int>(bufferSize = 1)
    val showMsgCommand = Command<String>(bufferSize = 1)

    val okDialogCommand = Command<OkDialogCommand>(bufferSize = 1)
    val okCancelDialogCommand = Command<OkCancelDialogCommand>(bufferSize = 1)

    val requestPermissionCommand = Command<RequestPermissionCommand>(bufferSize = 1)

    val stopProcessAction = Action<Unit>()//Остановить процесс

    val loading = State(false)
    val emptyDataSetMsgVisible = State(false)//видимость сообщения о ненайденых результатах
    val nextPageLoading = State(false) //идет догрузка следующих данных
    val dataRefreshing = State(false)//обновление данных
    val loadMoreEnabled = State(false) //существует возможность догрузить с сервера еще оду страницу данных

    override fun onDestroy() {
        compositeJob.cancel()
        job.cancel()
        super.onDestroy()
    }

    override fun getContext(): Context = App.appInstance

    protected fun startLoading() = loading.update(true)
    protected fun stopLoading() = loading.update(false)

    protected fun toggleLoadMoreAvailability(enabled: Boolean) =
        if (enabled) loadMoreEnabled.update(true) else loadMoreEnabled.update(false)

    protected fun startLoadingNextPage() = nextPageLoading.update(true)
    protected fun stopLoadingNextPage() = nextPageLoading.update(false)

    protected fun startDataRefreshing() = dataRefreshing.update(true)
    protected fun stopDataRefreshing() = dataRefreshing.update(false)

    protected fun toggleEmptyDataSetMsgVisibility(visible: Boolean) =
        if (visible) emptyDataSetMsgVisible.update(true) else emptyDataSetMsgVisible.update(false)

    protected fun showMsg(@StringRes text: Int) = showMsgByIdCommand.send(text)
    protected fun showMsg(text: String) = showMsgCommand.send(text)

    protected fun showOkDialog(command: OkDialogCommand) = okDialogCommand.send(command)
    protected fun showOkCancelDialog(command: OkCancelDialogCommand) = okCancelDialogCommand.send(command)

    override fun processError(throwable: RequestResult.Error) = errorHandler.processError(throwable)

    data class OkDialogCommand(
        @StringRes val titleId: Int? = null, @StringRes val contentId: Int? = null,
        @StringRes val okTextId: Int? = null, val okListener: (() -> Unit)? = null,
        val dismissListener: (() -> Unit)? = null
    )

    data class OkCancelDialogCommand(
        @StringRes val titleId: Int? = null, @StringRes val contentId: Int? = null,
        @StringRes val okTextId: Int? = null, @StringRes val cancelTextId: Int? = null,
        val okListener: (() -> Unit)? = null, val cancelListener: (() -> Unit)? = null,
        val dismissListener: (() -> Unit)? = null
    )

    data class RequestPermissionCommand(val permissions: Array<String>, val requestCode: Int)

    inner class BaseErrorHandler(private vararg val httpHttpCodeHandlers: HttpCodeHandler) :
        RequestErrorHandler {
        override fun processError(throwable: RequestResult.Error) {
            when (throwable) {
                is RequestResult.Error.HttpCode400 -> {
                    if (httpHttpCodeHandlers.find { it.first == HttpResponseCode.CODE_400 }?.second?.invoke(
                            throwable.errorMapWithList,
                            throwable.errorMap,
                            throwable.errorList
                        ) != true
                    ) {
                        when {
                            throwable.errorMap.isNotEmpty() -> showMsg(throwable.errorMap.toList().first().second)
                            throwable.errorMapWithList.isNotEmpty() -> showMsg(throwable.errorMapWithList.toList().first().second.first())
                            throwable.errorList.isNotEmpty() -> showMsg(throwable.errorList.first())
                            else -> showMsg(R.string.server_error)
                        }
                    }
                }

                is RequestResult.Error.HttpCode404 -> {
                    if (httpHttpCodeHandlers.find { it.first == HttpResponseCode.CODE_404 }?.second?.invoke(
                            throwable.errorMapWithList,
                            throwable.errorMap,
                            throwable.errorList
                        ) != true
                    ) {
                        when {
                            throwable.errorMap.isNotEmpty() -> showMsg(throwable.errorMap.toList().first().second)
                            throwable.errorMapWithList.isNotEmpty() -> showMsg(throwable.errorMapWithList.toList().first().second.first())
                            throwable.errorList.isNotEmpty() -> showMsg(throwable.errorList.first())
                            else -> showMsg(R.string.server_error)
                        }
                    }
                }
                is RequestResult.Error.HttpCodeAnother -> showMsg(R.string.server_error)
                is RequestResult.Error.UnknownHost -> showMsg(R.string.no_internet_connection)
                is RequestResult.Error.SocketTimeout -> showMsg(R.string.server_error)
                is RequestResult.Error.Another -> throwable.throwable.printStackTrace()
            }
        }
    }

    protected fun Job.untilDestroy() {
        compositeJob.add(this)
    }

    protected fun <T> State<T>.refresh() = consumer.accept(value)

    protected fun <T> State<T>.update(data: T) = consumer.accept(data)

    protected fun Action<Unit>.call() = consumer.accept(Unit)

    protected fun <T> Action<T>.call(data: T) = consumer.accept(data)

    protected fun <T> Command<T>.send(data: T) = consumer.accept(data)
}