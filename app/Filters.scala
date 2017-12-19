import javax.inject._

import filters.ExampleFilter
import play.api._
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter
import play.filters.hosts.AllowedHostsFilter

/**
  * This class configures filters that run on every request. This
  * class is queried by Play to get a list of filters.
  *
  * Play will automatically use filters from any class called
  * `Filters` that is placed the root package. You can load filters
  * from a different class by adding a `play.http.filters` setting to
  * the `application.conf` configuration file.
  *
  * @param env           Basic environment settings for the current application.
  * @param exampleFilter A demonstration filter that adds a header to
  *                      each response.
  */
@Singleton
class Filters @Inject()(
                         env: Environment,
                         exampleFilter: ExampleFilter,
                         corsFilter: CORSFilter,
                         allowedHostsFilter: AllowedHostsFilter
                       ) extends DefaultHttpFilters(corsFilter, allowedHostsFilter) {

  //  override val filters = {
  //    // Use the example filter if we're running development mode. If
  //    // we're running in production or test mode then don't use any
  //    // filters at all.
  //    if (env.mode == Mode.Dev) Seq(exampleFilter) else Seq.empty
  //  }

}