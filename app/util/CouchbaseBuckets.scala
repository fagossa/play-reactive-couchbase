package util

import com.google.inject.Inject
import org.reactivecouchbase.ReactiveCouchbaseDriver
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

class CouchbaseBuckets @Inject()(lifecycle: ApplicationLifecycle) {

  private val driver = ReactiveCouchbaseDriver()

  lazy val defaultBucket = driver.bucket("default")

  val defaultExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  lifecycle.addStopHook { () =>
    Future.successful(defaultBucket.disconnect())
  }

}
