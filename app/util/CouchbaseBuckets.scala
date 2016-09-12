package util

import javax.inject.Singleton

import com.google.inject.Inject
import org.reactivecouchbase.ReactiveCouchbaseDriver
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class CouchbaseBuckets @Inject()(lifecycle: ApplicationLifecycle) {

  private val driver = ReactiveCouchbaseDriver()

  lazy val defaultBucket = driver.bucket("default")

  // Replace this with your custom Execution context for couchbase
  val defaultExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  lifecycle.addStopHook { () =>
    Future.successful(defaultBucket.disconnect())
  }

}
